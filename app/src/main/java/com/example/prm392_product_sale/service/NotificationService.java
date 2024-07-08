package com.example.prm392_product_sale.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.prm392_product_sale.R;

public class NotificationService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int cartItemCount = intent.getIntExtra("cartItemCount", 0);
        showNotification(cartItemCount);
        return START_STICKY;
    }

    private void showNotification(int cartItemCount) {
        Notification notification = new NotificationCompat.Builder(this, "cart_channel")
                .setContentTitle("Cart Items")
                .setContentText("You have " + cartItemCount + " items in your cart.")
                .setSmallIcon(R.drawable.ic_cart_black_24dp)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
