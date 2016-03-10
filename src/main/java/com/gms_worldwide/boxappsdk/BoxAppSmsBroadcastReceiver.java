package com.gms_worldwide.boxappsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrew Kochura.
 */
public class BoxAppSmsBroadcastReceiver extends BroadcastReceiver {

    /**
     * The Tag.
     */
    String TAG = "com.gms_worldwide.boxappsdk.BoxAppSmsBroadcastReceiver";

    private static final String SMS_BUNDLE = "pdus";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Map<String, String> msg = RetrieveMessages(intent);

        /**
         * Tested with max sms length == 2001 characters
         */

        if (BoxApp.getUserHelper().isUserLogin() && msg != null) {
            BoxAppCurrentUserDBModel currentUserDBModel =
                    BoxAppPlugins.get().getDatabaseHelper().getCurrentUser();
            // send all SMS via XMPP by sender
            for (String sender : msg.keySet()) {
                if (BoxAppPlugins.get().getDatabaseHelper().isAlphaNameValid(sender)) {
                    //this will update the UI with message
                    String owner = "";
                    if (currentUserDBModel.getPhone() > 0) {
                        owner = String.valueOf(currentUserDBModel.getPhone());
                    } else if (currentUserDBModel.getEmail() != null && !currentUserDBModel.getEmail().isEmpty()){
                        owner = currentUserDBModel.getEmail();
                    }
                    BoxAppPlugins.get().getDatabaseHelper().saveIncomingMessage(sender, msg.get(sender),
                            BoxAppTools.getUtcTime(), BoxAppConstants.SMS_TYPE, owner);
                    BoxApp.getMessageHelper().newMessage(
                            new BoxAppMessageModel(sender, msg.get(sender), BoxAppTools.getUtcTime(),
                                    BoxAppConstants.SMS_TYPE, owner));

                }
            }
        }
    }

    private static Map<String, String> RetrieveMessages(Intent intent) {
        Map<String, String> msg = null;
        SmsMessage[] msgs;
        Bundle bundle = intent.getExtras();

        if (bundle != null && bundle.containsKey(SMS_BUNDLE)) {
            Object[] pdus = (Object[]) bundle.get(SMS_BUNDLE);

            if (pdus != null) {
                int nbrOfpdus = pdus.length;
                msg = new HashMap<String, String>(nbrOfpdus);
                msgs = new SmsMessage[nbrOfpdus];

                // There can be multiple SMS from multiple senders, there can be a maximum of nbrOfpdus different senders
                // However, send long SMS of same sender in one message
                for (int i = 0; i < nbrOfpdus; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

                    String originatinAddress = msgs[i].getOriginatingAddress();

                    // Check if index with number exists
                    if (!msg.containsKey(originatinAddress)) {
                        // Index with number doesn't exist
                        // Save string into associative array with sender number as index
                        msg.put(msgs[i].getOriginatingAddress(), msgs[i].getMessageBody());

                    } else {
                        // Number has been there, add content but consider that
                        // msg.get(originatinAddress) already contains sms:sndrNbr:previousparts of SMS,
                        // so just add the part of the current PDU
                        String previousparts = msg.get(originatinAddress);
                        String msgString = previousparts + msgs[i].getMessageBody();
                        msg.put(originatinAddress, msgString);
                    }
                }
            }
        }

        return msg;
    }

}
