package com.example.bllocapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.text.TextUtils;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    private TextView txtView;
    private NotificationReceiver nReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Text view to be updated to latest notification
        txtView = (TextView) findViewById(R.id.textView);

        AlertDialog enableNotificationListenerAlertDialog;

        // Checks if the notification listener is on, if not then the user
        // is prompted to do turn it on
        if(!checkPermission()){
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }
        /*nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver,filter);*/
    }

    // Set the text in the app to the text from the notification
     void changeText(String message) {
        txtView.setText(message);

        // Sending the data using intent
         Intent sendIntent = new Intent();
         sendIntent.setAction(Intent.ACTION_SEND);
         sendIntent.putExtra(Intent.EXTRA_TEXT, message);
         sendIntent.setType("text/plain");

         Intent shareIntent = Intent.createChooser(sendIntent, null);
         startActivity(shareIntent);
    }

    // This method checks if the notification listener is enabled
    private boolean checkPermission() {
        String pname = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pname, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra("notification_event") + "\n" + txtView.getText();
            txtView.setText(temp);
        }
    }*/

    // Creates the alert for user to enable the notification
    private AlertDialog buildNotificationServiceAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("notification_listener_service");
        alertDialogBuilder.setMessage("notification_listener_service_explanation");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton("no",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                        System.out.println("App wont work properly");
                    }
                });
        return(alertDialogBuilder.create());
    }


}
