package edu.ualr.cpsc4367.customreceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class OutgoingReceiver extends BroadcastReceiver {
    public static final String CUSTOM_INTENT = "edu.ualr.cpsc4367.intent.action.TEST";

    private NotificationManager mNotifyMgr;
    private int mNotificationID = 001;
    private NotificationCompat.Builder mBuilder;

    public OutgoingReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("OutgoingReceiver")
                .setContentText("Hit Outgoing Reciever")
                .setSmallIcon(R.drawable.ic_priority_high_black_24dp);
        mNotifyMgr.notify(mNotificationID, mBuilder.build());

        Log.println(Log.ASSERT, "OutgoingReceiver", "HIT OUTGOING");
        Intent i = new Intent();
        i.setAction(CUSTOM_INTENT);
        context.sendBroadcast(i);
    }
}
