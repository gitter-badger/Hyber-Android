package com.gms_worldwide.boxappsdk;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Andrew Kochura.
 */
public class BoxAppGcmBroadcastReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "com.gms_worldwide.boxappsdk.BoxAppGcmBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        BoxAppGcmListenerService.sendNotification(intent.getExtras().getString("title"), intent.getExtras());
    }
}
