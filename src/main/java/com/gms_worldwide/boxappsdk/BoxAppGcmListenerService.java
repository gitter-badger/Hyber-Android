package com.gms_worldwide.boxappsdk;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Andrew Kochura.
 */
public class BoxAppGcmListenerService extends GcmListenerService {

    private static final String TAG = "com.gms_worldwide.boxappsdk.BoxAppGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param from the from
     * @param data GCM message received.
     */
    public static void sendNotification(String from, Bundle data) {

        //Bundle notification = data.getBundle("notification");
        String title = data.getString("title");
        String text = data.getString("body");
        long msg_gms_uniq_id = Long.valueOf(data.getString("msg_gms_uniq_id"));

        BoxAppCurrentUserDBModel currentUserDBModel = BoxAppPlugins.get().getDatabaseHelper().getCurrentUser();
        String owner = "";
        if (currentUserDBModel.getPhone() > 0) {
            owner = String.valueOf(currentUserDBModel.getPhone());
        } else if (currentUserDBModel.getEmail() != null && !currentUserDBModel.getEmail().isEmpty()){
            owner = currentUserDBModel.getEmail();
        }
        BoxAppPlugins.get().getDatabaseHelper().saveIncomingMessage(
                title, text, BoxAppTools.getUtcTime(),
                BoxAppConstants.PUSH_TYPE, owner
        );
        BoxApp.getMessageHelper().newMessage(
                new BoxAppMessageModel(title, text, BoxAppTools.getUtcTime(),
                        BoxAppConstants.PUSH_TYPE, owner)
        );
        sendDeliveryReport(currentUserDBModel.getUniqAppDeviceId(), msg_gms_uniq_id);
    }

    private static void sendDeliveryReport(long getUniqAppDeviceId, long msg_gms_uniq_id) {
        BoxAppPlugins.get().restClient().deliveryReportObservable(
                getUniqAppDeviceId, msg_gms_uniq_id, 1)
                .timeout(BoxAppApiClient.STANDARD_TIMEOUT, TimeUnit.SECONDS)
                //.retry(5)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }
                });
    }

}