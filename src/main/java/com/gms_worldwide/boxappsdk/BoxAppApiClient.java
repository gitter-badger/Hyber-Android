package com.gms_worldwide.boxappsdk;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Andrew Kochura.
 */
class BoxAppApiClient {

    private static final String API_BASE_URL = BoxAppConstants.BASE_URL;

    /**
     * The constant STANDARD_TIMEOUT.
     */
    public static final int STANDARD_TIMEOUT = 20;  // seconds

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    private MboxWebService mboxWebService;

    /**
     * Instantiates a new Box app api client.
     */
    public BoxAppApiClient(){

        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        mboxWebService = createService(MboxWebService.class);

    }

    /**
     * Create service s.
     *
     * @param <S>          the type parameter
     * @param serviceClass the service class
     * @return the s
     */
    public static <S> S createService(Class<S> serviceClass) {

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header(HttpHeaders.CONTENT_TYPE, "applicaton/json")
                        .header(HttpHeaders.AUTHORIZATION, "app_key=" + BoxAppPlugins.get().getClientKey())
                        .method(original.method(), original.body())
                        .build();

                Response response = chain.proceed(request);

                // Customize or return the response
                return response;
            }
        });

        httpClient.networkInterceptors().add(interceptor);

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(serviceClass);
    }

    private interface MboxWebService {

        /**
         * Delivery report observable observable.
         *
         * @param o the o
         * @return the observable
         */
        @POST (BoxAppConstants.OTT_URL_PATH + "deliveryReport")
        Observable<Void> deliveryReportObservable(@Body Object o);

        /**
         * Gets messages observable.
         *
         * @param channel                    the channel
         * @param boxAppMessagesRequestModel the box app messages request model
         * @return the messages observable
         */
        @POST(BoxAppConstants.OTT_URL_PATH + "requestMessages/{channel}")
        Observable<BoxAppMessagesEnvelope> getMessagesObservable(@Path("channel") String channel, @Body BoxAppMessagesRequestModel boxAppMessagesRequestModel);

        /**
         * Add abonent observable observable.
         *
         * @param o the o
         * @return the observable
         */
        @POST (BoxAppConstants.MAIN_URL_PATH + "lib_add_abonent")
        Observable<BaseResponseModel> addAbonentObservable(@Body Object o);

        /**
         * Update token observable observable.
         *
         * @param o the o
         * @return the observable
         */
        @POST (BoxAppConstants.MAIN_URL_PATH + "lib_update_token")
        Observable<BaseResponseModel> updateTokenObservable(@Body Object o);

        /**
         * Abonent profile observable observable.
         *
         * @param o the o
         * @return the observable
         */
        @POST (BoxAppConstants.MAIN_URL_PATH + "abonent_profile")
        Observable<BaseResponseModel> abonentProfileObservable(@Body Object o);

        /**
         * Abonent location observable observable.
         *
         * @param o the o
         * @return the observable
         */
        @POST (BoxAppConstants.MAIN_URL_PATH + "location")
        Observable<BaseResponseModel> abonentLocationObservable(@Body Object o);

        /**
         * Update phone email observable observable.
         *
         * @param o the o
         * @return the observable
         */
        @POST (BoxAppConstants.MAIN_URL_PATH + "lib_update_phone_email")
        Observable<BaseResponseModel> updatePhoneEmailObservable(@Body Object o);

        /**
         * Allow recieve push observable observable.
         *
         * @param o the o
         * @return the observable
         */
        @POST (BoxAppConstants.MAIN_URL_PATH + "lib_alow_recieve_push")
        Observable<BaseResponseModel> allowRecievePushObservable(@Body Object o);

        /**
         * Gets user profile observable.
         *
         * @param o the o
         * @return the user profile observable
         */
        @POST (BoxAppConstants.MAIN_URL_PATH + "get_profile")
        Observable<UserProfileResponseModel> getUserProfileObservable(@Body Object o);

    }

    /**
     * Delivery report observable observable.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param msg_gms_uniq_id the msg gms uniq id
     * @param status          the status
     * @return the observable
     */
    public Observable<Void> deliveryReportObservable(long uniqAppDeviceId, long msg_gms_uniq_id,
                                                       int status){
        DeliveryReportModel deliveryReportModel =
                new DeliveryReportModel(uniqAppDeviceId, msg_gms_uniq_id, status);
        return mboxWebService.deliveryReportObservable(deliveryReportModel);
    }

    /**
     * Registration observable observable.
     *
     * @param phone    the phone
     * @param email    the email
     * @param gcmToken the gcm token
     * @return the observable
     */
    public Observable<BaseResponseModel> registrationObservable(
            long phone, String email, String gcmToken){
        String p = null;
        if (phone > 0)
            p = String.valueOf(phone);
        if (email.equals(""))
            email = null;
        RegistrationPushModelWithNull registrationPushModel =
                new RegistrationPushModelWithNull(p, email, gcmToken);
        return mboxWebService.addAbonentObservable(registrationPushModel);
    }

    /**
     * Update token observable observable.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param gcmToken        the gcm token
     * @return the observable
     */
    public Observable<BaseResponseModel> updateTokenObservable(
            long uniqAppDeviceId, String gcmToken){
        return mboxWebService.updateTokenObservable(
                new UpdateTokenModel(uniqAppDeviceId, gcmToken));
    }

    /**
     * Abonent profile observable observable.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param sex             the sex
     * @param city            the city
     * @param region          the region
     * @param age             the age
     * @param interests       the interests
     * @param ads_source      the ads source
     * @param latitude        the latitude
     * @param longitude       the longitude
     * @return the observable
     */
    public Observable<BaseResponseModel> abonentProfileObservable(
            long uniqAppDeviceId, int sex, String city,
            String region, String fio, int age, int[] interests,
            int[] ads_source, double latitude, double longitude){
        UserInfoModel userInfoModel =
                new UserInfoModel(uniqAppDeviceId, sex, city, region, fio,
                        age, interests, ads_source, latitude, longitude);
        return mboxWebService.abonentProfileObservable(userInfoModel);
    }

    /**
     * Abonent location observable observable.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param latitude        the latitude
     * @param longitude       the longitude
     * @return the observable
     */
    public Observable<BaseResponseModel> abonentLocationObservable(
            long uniqAppDeviceId, double latitude, double longitude){
        UserLocationModel userLocationModel =
                new UserLocationModel(uniqAppDeviceId, latitude, longitude);
        return mboxWebService.abonentLocationObservable(userLocationModel);
    }

    /**
     * Update phone email observable observable.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param phone           the phone
     * @param email           the email
     * @return the observable
     */
    public Observable<BaseResponseModel> updatePhoneEmailObservable(
            long uniqAppDeviceId, long phone, String email){
        String p = null;
        if (phone > 0)
            p = String.valueOf(phone);
        if (email.equals(""))
            email = null;
        UpdatePhoneEmailModel updatePhoneEmailModel =
                new UpdatePhoneEmailModel(uniqAppDeviceId, p, email);
        return mboxWebService.updatePhoneEmailObservable(updatePhoneEmailModel);
    }

    /**
     * Allow recieve push observable observable.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param isOn            the is on
     * @return the observable
     */
    public Observable<BaseResponseModel> allowRecievePushObservable(long uniqAppDeviceId, boolean isOn){
        BoxAppAllowRecievePushModel model =
                new BoxAppAllowRecievePushModel(uniqAppDeviceId, (isOn) ? 1 : 0);
        return mboxWebService.allowRecievePushObservable(model);
    }

    /**
     * Get user profile observable observable.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @return the observable
     */
    public Observable<UserProfileResponseModel> getUserProfileObservable(long uniqAppDeviceId){
        GetUserProfileRequestModel model =
                new GetUserProfileRequestModel(uniqAppDeviceId);
        return mboxWebService.getUserProfileObservable(model);
    }

    /**
     * Get viber messages observable observable.
     *
     * @param phone           the phone
     * @param uniqAppDeviceId the uniq app device id
     * @param date_utc        the date utc
     * @return the observable
     */
    public Observable<BoxAppMessagesEnvelope> getViberMessagesObservable(
            long phone, long uniqAppDeviceId, long date_utc){
        return mboxWebService.getMessagesObservable(BoxAppConstants.VIBER_CHANNEL,
                new BoxAppMessagesRequestModel(uniqAppDeviceId, phone, date_utc));
    }

}
