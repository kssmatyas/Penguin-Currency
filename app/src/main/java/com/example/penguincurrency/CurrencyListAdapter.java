package com.example.penguincurrency;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.List;

public class CurrencyListAdapter extends ArrayAdapter<CurrencyDocument> {
    private final Context context;
    private final List<CurrencyDocument> currencyDocuments;
    private final FirestoreHelper firestoreHelper;
    private final UpdateDeleteListener updateDeleteListener;
    private static final String CHANNEL_ID = "delete_notification_channel";
    private static final String CHANNEL_NAME = "Delete Notification Channel";
    private static final int DELETE_NOTIFICATION_ID = 1;

    public CurrencyListAdapter(Context context, List<CurrencyDocument> currencyDocuments, FirestoreHelper firestoreHelper, UpdateDeleteListener updateDeleteListener) {
        super(context, R.layout.list_item_currency, currencyDocuments);
        this.context = context;
        this.currencyDocuments = currencyDocuments;
        this.firestoreHelper = firestoreHelper;
        this.updateDeleteListener = updateDeleteListener;
        createNotificationChannel();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_currency, parent, false);
        }

        TextView currencyTextView = convertView.findViewById(R.id.currencyTextView);
        Button updateButton = convertView.findViewById(R.id.updateButton);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);

        CurrencyDocument currencyDocument = currencyDocuments.get(position);
        CurrencyModel currency = currencyDocument.getCurrency();

        currencyTextView.setText(currency.getName() + ": " + currency.getAmount());

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDeleteListener.onUpdate(currencyDocument);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestoreHelper.deleteCurrency(currencyDocument.getId());
                currencyDocuments.remove(position);
                notifyDataSetChanged();
                updateDeleteListener.onUpdateCurrencies();
                showDeleteNotification(currencyDocument);
            }
        });

        return convertView;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for delete actions");

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void showDeleteNotification(CurrencyDocument currencyDocument) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_delete) // Android beépített törlés ikon
                .setContentTitle("Törlés sikeres")
                .setContentText(currencyDocument.getCurrency().getName() + " valuta sikeresen törölve.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(DELETE_NOTIFICATION_ID, builder.build());
        }
    }

    public interface UpdateDeleteListener {
        void onUpdate(CurrencyDocument currencyDocument);
        void onUpdateCurrencies();
    }
}
