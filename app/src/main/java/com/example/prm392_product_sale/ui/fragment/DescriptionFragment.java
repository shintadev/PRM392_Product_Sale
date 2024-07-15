package com.example.prm392_product_sale.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.prm392_product_sale.R;

public class DescriptionFragment extends Fragment {
    private TextView descriptionTextView;
    private String description;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.description_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        descriptionTextView = view.findViewById(R.id.descriptionTxt);
        setDescription(description);
    }

    public void setDescription(String description) {
        this.description = description;
        if (descriptionTextView != null) {
            descriptionTextView.setText(description);
        }
    }
}