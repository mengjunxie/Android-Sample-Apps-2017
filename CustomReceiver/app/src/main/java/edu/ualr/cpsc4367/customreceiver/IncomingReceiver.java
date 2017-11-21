package edu.ualr.cpsc4367.customreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class IncomingReceiver extends BroadcastReceiver {
    public IncomingReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(OutgoingReceiver.CUSTOM_INTENT)) {
            Log.println(Log.ASSERT, "IncomingReceiver", "Got the custom intent");
        }
    }
}
