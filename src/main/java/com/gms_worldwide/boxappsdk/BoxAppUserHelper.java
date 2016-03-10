package com.gms_worldwide.boxappsdk;

import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Andrew Kochura.
 */
public class BoxAppUserHelper {

    private static final String TAG = "com.gms_worldwide.boxappsdk.BoxAppUserHelper";

    private static BoxAppUserHelper instance = null;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static BoxAppUserHelper getInstance() {
        if (instance == null) {
            instance = new BoxAppUserHelper();
        }
        return instance;
    }

    /**
     * Instantiates a new Box app user helper.
     */
    protected BoxAppUserHelper() {

    }

    /**
     * This method send location data to cloud user storage
     *
     * @param latitude  the latitude
     * @param longitude the longitude
     * @return true if operation executed with success signature.
     */
    public Observable<Boolean> updateUserLocation(final double latitude, final double longitude) {
        return BoxAppPlugins.get().restClient()
                .abonentLocationObservable(getCurrentUser().getUniqAppDeviceId(),
                        latitude, longitude)
                .timeout(BoxAppApiClient.STANDARD_TIMEOUT, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<BaseResponseModel, Boolean>() {
                    @Override
                    public Boolean call(BaseResponseModel baseResponseModel) {
                        if (baseResponseModel.isSuccess()) {
                            BoxAppCurrentUserDBModel userDBModel =
                                    BoxAppPlugins.get().getDatabaseHelper().getCurrentUser();
                            userDBModel.setLatitude(latitude);
                            userDBModel.setLongitude(longitude);
                            BoxAppPlugins.get().getDatabaseHelper()
                                    .updateCurrentUser(userDBModel);
                        }
                        return baseResponseModel.isSuccess();
                    }
                });
    }

    /**
     * Update user data in cloud.
     * You must send all user data, not only updated fields.
     * Because cloud make update for full user data package.
     *
     * @param sex                        the sex
     * @param city                       the city
     * @param region                     the region
     * @param age                        the age
     * @param interests                  you may associate integers with your own list of interests.
     * @param ads_source                 call to GMS Worldwide for more info.
     * @param latitude                   the latitude
     * @param longitude                  the longitude
     * @param allow_use_current_location the allow use current location
     * @return true if operation executed with success signature.
     */
    public Observable<Boolean> updateUserProfile(final int sex, final String city, final String region, final String fio,
                                                 final int age, final int[] interests, final int[] ads_source,
                                                 final double latitude, final double longitude,
                                                 final boolean allow_use_current_location) {
        return BoxAppPlugins.get().restClient()
                .abonentProfileObservable(getCurrentUser().getUniqAppDeviceId(),
                        sex, city, region, fio, age, interests, ads_source, latitude, longitude)
                .timeout(BoxAppApiClient.STANDARD_TIMEOUT, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<BaseResponseModel, Boolean>() {
                    @Override
                    public Boolean call(BaseResponseModel baseResponseModel) {
                        if (baseResponseModel.isSuccess()) {
                            BoxAppCurrentUserDBModel userDBModel =
                                    BoxAppPlugins.get().getDatabaseHelper().getCurrentUser();
                            userDBModel.setSex(sex);
                            userDBModel.setCity(city);
                            userDBModel.setRegion(region);
                            userDBModel.setFio(fio);
                            userDBModel.setAge(age);
                            userDBModel.setInterests(BoxAppTools.integerArrayToString(interests));
                            userDBModel.setAds_source(BoxAppTools.integerArrayToString(ads_source));
                            userDBModel.setLatitude(latitude);
                            userDBModel.setLongitude(longitude);
                            userDBModel.setAllow_use_current_location(allow_use_current_location);
                            BoxAppPlugins.get().getDatabaseHelper()
                                    .updateCurrentUser(userDBModel);
                        }
                        return baseResponseModel.isSuccess();
                    }
                });
    }

    /**
     * Check user login in app or not/
     *
     * @return true if user login.
     */
    public boolean isUserLogin() {
        if (BoxAppPlugins.get().getDatabaseHelper().getCurrentUser() != null &&
                BoxAppPlugins.get().getDatabaseHelper().getCurrentUser().getUniqAppDeviceId() > 0)
            return true;
        return false;
    }

    /**
     * Return user profile from SDK storage.
     *
     * @return current user
     */
    public UserProfileModel getCurrentUser() {
        BoxAppCurrentUserDBModel userDBModel = BoxAppPlugins.get().getDatabaseHelper().getCurrentUser();
        if (userDBModel != null) {
            return userDBModel.getUserProfileModel();
        } else {
            return null;
        }
    }

    /**
     * Change email for current user.
     *
     * @param email the email
     * @return true if operation executed with success signature.
     */
    public Observable<Boolean> updateUserEmail(final String email) {
        BoxAppCurrentUserDBModel userDBModel =
                BoxAppPlugins.get().getDatabaseHelper().getCurrentUser();
        return BoxAppPlugins.get().restClient()
                .updatePhoneEmailObservable(userDBModel.getUniqAppDeviceId(), 0, email)
                .timeout(BoxAppApiClient.STANDARD_TIMEOUT, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<BaseResponseModel, Boolean>() {
                    @Override
                    public Boolean call(BaseResponseModel baseResponseModel) {
                        if (baseResponseModel.isSuccess()) {
                            BoxAppCurrentUserDBModel userDBModel =
                                    BoxAppPlugins.get().getDatabaseHelper().getCurrentUser();
                            userDBModel.setEmail(email);
                            BoxAppPlugins.get().getDatabaseHelper()
                                    .updateCurrentUser(userDBModel);
                        }
                        return baseResponseModel.isSuccess();
                    }
                });
    }

    /**
     * Change phone for current user.
     *
     * @param phone the phone
     * @return true if operation executed with success signature.
     */
    public Observable<Boolean> updateUserPhone(final long phone) {
        BoxAppCurrentUserDBModel userDBModel =
                BoxAppPlugins.get().getDatabaseHelper().getCurrentUser();
        return BoxAppPlugins.get().restClient()
                .updatePhoneEmailObservable(userDBModel.getUniqAppDeviceId(), phone, null)
                .timeout(BoxAppApiClient.STANDARD_TIMEOUT, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<BaseResponseModel, Boolean>() {
                    @Override
                    public Boolean call(BaseResponseModel baseResponseModel) {
                        if (baseResponseModel.isSuccess()) {
                            BoxAppCurrentUserDBModel userDBModel =
                                    BoxAppPlugins.get().getDatabaseHelper().getCurrentUser();
                            userDBModel.setPhone(phone);
                            BoxAppPlugins.get().getDatabaseHelper()
                                    .updateCurrentUser(userDBModel);
                        }
                        return baseResponseModel.isSuccess();
                    }
                });
    }

    /**
     * Authorize your user in app
     * One of field must be not empty (phone == 0 is empty)
     *
     * @param phone the phone
     * @param email the email
     * @return if user logged in, your retrieve current user profile model.
     * @throws NoSuchFieldException the no such field exception
     */
    public Observable<UserProfileModel> loginUser(long phone, String email) throws NoSuchFieldException {
        if (phone == 0 && TextUtils.isEmpty(email)) {
            new NoSuchFieldException();
        }
        return BoxAppPlugins.get().restClient()
                .registrationObservable(phone, email, BoxAppPlugins.get().getLastKownGcmID())
                .timeout(BoxAppApiClient.STANDARD_TIMEOUT, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Func1<BaseResponseModel, Observable<UserProfileModel>>() {
                    @Override
                    public Observable<UserProfileModel> call(BaseResponseModel responseModel) {
                        /**
                         * После удачной авторизации, получаем с сервера
                         * информацию о профиле пользователя.
                         */
                        return BoxAppPlugins.get().restClient()
                                .getUserProfileObservable(responseModel.getUniqAppDeviceId())
                                .timeout(BoxAppApiClient.STANDARD_TIMEOUT, TimeUnit.SECONDS)
                                .map(new Func1<UserProfileResponseModel, UserProfileModel>() {
                                    @Override
                                    public UserProfileModel call(UserProfileResponseModel profileResponseModel) {
                                        /**
                                         * Обновляем профиль пользователя в базе данных.
                                         */
                                        BoxAppCurrentUserDBModel userDBModel =
                                                BoxAppPlugins.get().getDatabaseHelper().getCurrentUser();
                                        userDBModel.updateUserInfo(profileResponseModel);
                                        BoxAppPlugins.get().getDatabaseHelper()
                                                .updateCurrentUser(userDBModel);
                                        return profileResponseModel.getUserProfileModel();
                                    }
                                });
                    }
                });
    }

    /**
     * When current user make logout,
     * call this method for clean user data from the SDK storage.
     *
     * @return true if operation executed with success signature.
     */
    public Observable<Boolean> logOutUser() {
        return BoxAppPlugins.get().restClient()
                .allowRecievePushObservable(BoxAppPlugins.get().getDatabaseHelper()
                        .getCurrentUser().getUniqAppDeviceId(), false)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<BaseResponseModel, Boolean>() {
                    @Override
                    public Boolean call(BaseResponseModel baseResponseModel) {
                        //if (baseResponseModel.isSuccess())
                            BoxAppPlugins.get().getDatabaseHelper().cleanUserData();
                        return true;//baseResponseModel.isSuccess();
                    }
                });
    }

}
