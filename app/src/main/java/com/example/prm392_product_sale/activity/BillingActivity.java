package com.example.prm392_product_sale.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.adapter.BillingAdapter;
import com.example.prm392_product_sale.databinding.ActivityBillingBinding;
import com.example.prm392_product_sale.model.CartItem;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

public class BillingActivity extends AppCompatActivity {

    private static final String TAG = "BillingActivity";
    public static final int PAYPAL_REQUEST_CODE = 123;
    private static final int REQUEST_SELECT_ADDRESS = 1;
    RecyclerView rvBilling;
    TextView tvSubtotal, tvShipping, tvTax, tvTotal;
    Button btnCheckout;
    ActivityBillingBinding binding;

    private List<CartItem> cartItemList;
    private BillingAdapter billingAdapter;
    private PayPalConfiguration config;
    private String selectedAddress;
    TextView tvAddress;
    private boolean isSelectingProvince = true;
    private boolean isSelectingDistrict = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBillingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().hasExtra("cartItemList")) {
            cartItemList = (List<CartItem>) getIntent().getSerializableExtra("cartItemList");
        }

        config = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK) // Use ENVIRONMENT_PRODUCTION for real payments
                .clientId("AcXxfYc61WN4hF8lX0YjKDbo9Za9fb14qh78q12ozbLQXiYGeHFAv841X5IOVMW5EfVCiJt2mW7Fqx2C");

        rvBilling = binding.rvBilling;
        tvSubtotal = binding.tvSubtotalBilling;
        tvShipping = binding.tvFeeDeliveryBilling;
        tvTax = binding.tvTaxBilling;
        tvTotal = binding.tvTotalBilling;
        btnCheckout = binding.btnCheckoutBilling;

        billingAdapter = new BillingAdapter(cartItemList, this);
        rvBilling.setLayoutManager(new LinearLayoutManager(this));
        rvBilling.setAdapter(billingAdapter);

        Float subtotal = Float.parseFloat(getIntent().getStringExtra("totalPrice"));
        String shipping = "Free";
        Float tax = Float.parseFloat(getIntent().getStringExtra("totalPrice")) * 0.1f;
        Float total = subtotal + tax;

        tvSubtotal.setText(String.format("%.2f $", Float.parseFloat(getIntent().getStringExtra("totalPrice"))));
        tvShipping.setText(shipping);
        tvTax.setText(String.format("%.2f $", Float.parseFloat(getIntent().getStringExtra("totalPrice")) * 0.1f));
        tvTotal.setText(String.format("%.2f $", total));

        Toolbar toolbar = findViewById(R.id.tb_billing);
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Check out");
        }

        Button btnSelectAddress = findViewById(R.id.btn_select_address);
        btnSelectAddress.setOnClickListener(v -> {
            isSelectingProvince = true;
            isSelectingDistrict = true;
            AddressSelectionActivity("TINHTHANH");
        });

        // Start PayPal service
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);


        btnCheckout.setOnClickListener(v -> {
            if (selectedAddress == null) {
                Toast.makeText(this, "Please select an address first", Toast.LENGTH_SHORT).show();
            } else {
                processPayment(String.format("%.2f", total));

            }
        });
    }


    private final ActivityResultLauncher<Intent> startAddressSelection = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    tvAddress = binding.tvAddressBilling;
                    Intent data = result.getData();
                    String addressPart = data.getStringExtra("selected_address");
                    String TINHTHANH_ID = data.getStringExtra("selected_address_id");
                    if (selectedAddress == null) {
                        selectedAddress = addressPart;
                    } else {
                        selectedAddress = selectedAddress + ", " + addressPart;
                    }
                    tvAddress.setText(selectedAddress);
                    Toast.makeText(this, selectedAddress, Toast.LENGTH_SHORT).show();

                    if (isSelectingProvince) {
                        isSelectingProvince = false;
                        AddressSelectionActivity("QUANHUYEN", TINHTHANH_ID);

                    }
                    else if (isSelectingDistrict) {
                        isSelectingDistrict = false;
                        AddressSelectionActivity("PHUONGXA", TINHTHANH_ID, isSelectingDistrict);}
                }
            }
    );

    private void AddressSelectionActivity(String API_TYPE) {
        Intent intent = new Intent(this, AddressSelectionActivity.class);
        intent.putExtra("API_TYPE", API_TYPE);
        startAddressSelection.launch(intent);
    }

    private void AddressSelectionActivity(String API_TYPE, String TINHTHANH_ID) {
        Intent intent = new Intent(this, AddressSelectionActivity.class);
        intent.putExtra("API_TYPE", API_TYPE);
        intent.putExtra("TINHTHANH_ID", TINHTHANH_ID);
        startAddressSelection.launch(intent);
    }

    private void AddressSelectionActivity(String API_TYPE, String QUANHUYEN_ID, boolean isSelectingDistrict) {
        Intent intent = new Intent(this, AddressSelectionActivity.class);
        intent.putExtra("API_TYPE", API_TYPE);
        intent.putExtra("QUANHUYEN_ID", QUANHUYEN_ID);
        startAddressSelection.launch(intent);
    }

    private void processPayment(String amount) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(amount), "USD",
                "Sample Item", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYPAL_REQUEST_CODE) {
            Intent resultIntent = new Intent();
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        JSONObject jsonDetails = new JSONObject(paymentDetails);

                        // Extract necessary information from paymentDetails
                        String paymentId = jsonDetails.getJSONObject("response").getString("id");
                        String paymentState = jsonDetails.getJSONObject("response").getString("state");

                        // Send back payment details
                        resultIntent.putExtra("paymentState", paymentState);
                        resultIntent.putExtra("paymentId", paymentId);

                        // Verify payment on the client side (not recommended for production)
                        if (paymentState.equals("approved")) {
                            // Payment was successful
                            setResult(Activity.RESULT_OK, resultIntent);
                            Log.i(TAG, "Payment successful. Payment ID: " + paymentId);
                        } else {
                            // Payment was not successful
                            setResult(Activity.RESULT_CANCELED, resultIntent);
                            Log.i(TAG, "Payment failed. Payment ID: " + paymentId);
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                setResult(Activity.RESULT_CANCELED, resultIntent);
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                setResult(Activity.RESULT_CANCELED, resultIntent);
                Log.i(TAG, "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
            finish();
        }
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}