package com.gms_worldwide.boxappsdk;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Andrew Kochura.
 */
class BoxAppGcmTokenSender {

    /**
     * The constant TAG.
     */
    public static final String TAG = "com.gms_worldwide.BoxAppGcmTokenSender";

    /**
     * Persist registration to third-party servers.
     * <p>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     * @throws SQLException the sql exception
     */
    public static void sendRegistrationToServer(final String token) throws SQLException {

        BoxAppPlugins.get().setGcmID(token);

        BoxAppCurrentUserDBModel currentUserDBModel =
                BoxAppPlugins.get().getDatabaseHelper().getCurrentUser();

        if (currentUserDBModel == null)
            currentUserDBModel = new BoxAppCurrentUserDBModel();

        if (currentUserDBModel.getUniqAppDeviceId() > 0 &&
                (currentUserDBModel.getPhone() > 0 ||
                        !currentUserDBModel.getEmail().equals("")) &&
                !token.equals("")){
            if (!currentUserDBModel.getGcmTokenId().equals(token)){


                final BoxAppCurrentUserDBModel finalCurrentUserDBModel = currentUserDBModel;
                BoxAppPlugins.get().restClient().updateTokenObservable(
                        currentUserDBModel.getUniqAppDeviceId(), token)
                        .timeout(BoxAppApiClient.STANDARD_TIMEOUT, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(Schedulers.newThread())
                        .subscribe(new Subscriber<BaseResponseModel>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                // If an exception happens while fetching the new token or updating our registration data
                                // on a third-party server, this ensures that we'll attempt the update at a later time.
                            }

                            @Override
                            public void onNext(BaseResponseModel baseResponseModel) {
                                if (baseResponseModel.isSuccess()){
                                    finalCurrentUserDBModel.setGcmTokenId(token);
                                    BoxAppPlugins.get().getDatabaseHelper().updateCurrentUser(finalCurrentUserDBModel);

                                    // You should store a boolean that indicates whether the generated token has been
                                    // sent to your server. If the boolean is false, send the token to your server,
                                    // otherwise your server should have already received the token.

                                }
                            }
                        });
            }
        }
    }

}
