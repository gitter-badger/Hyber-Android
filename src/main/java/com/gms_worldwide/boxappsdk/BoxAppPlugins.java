package com.gms_worldwide.boxappsdk;

import android.content.Context;
import android.net.SSLSessionCache;

/**
 * Created by Andrew Kochura.
 */
class BoxAppPlugins {

    private static final Object LOCK = new Object();
    private static BoxAppPlugins instance;

    /**
     * Initialize.
     *
     * @param projectId the project id
     * @param clientKey the client key
     */
// TODO(grantland): Move towards a Config/Builder parameter pattern to allow other configurations
    // such as path (disabled for Android), etc.
    static void initialize(String projectId, String clientKey) {
        BoxAppPlugins.set(new BoxAppPlugins(projectId, clientKey));
    }

    /**
     * Set.
     *
     * @param plugins the plugins
     */
    static void set(BoxAppPlugins plugins) {
        synchronized (LOCK) {
            if (instance != null) {
                throw new IllegalStateException("BoxAppPlugins is already initialized");
            }
            instance = plugins;
        }
    }

    /**
     * Get box app plugins.
     *
     * @return the box app plugins
     */
    static BoxAppPlugins get() {
        synchronized (LOCK) {
            return instance;
        }
    }

    /**
     * Reset.
     */
    static void reset() {
        synchronized (LOCK) {
            instance = null;
        }
    }

    /**
     * The Lock.
     */
    final Object lock = new Object();
    private final String projectId;
    private final String clientKey;

    private BoxAppApiClient restClient;
    private BoxAppDatabaseHelper databaseHelper;
    private String gcmID = "";

    private BoxAppPlugins(String projectId, String clientKey) {
        this.projectId = projectId;
        this.clientKey = clientKey;
    }

    /**
     * Gets project id.
     *
     * @return the project id
     */
    String getProjectId() {
        return projectId;
    }

    /**
     * Gets client key.
     *
     * @return the client key
     */
    String getClientKey() {
        return clientKey;
    }

    /**
     * New rest api client box app api client.
     *
     * @return the box app api client
     */
    BoxAppApiClient newRestApiClient() {
        return new BoxAppApiClient();
    }

    /**
     * New database helper box app database helper.
     *
     * @return the box app database helper
     */
    BoxAppDatabaseHelper newDatabaseHelper() {
        return new BoxAppDatabaseHelper(BoxApp.getApplicationContext());
    }

    /**
     * Rest client box app api client.
     *
     * @return the box app api client
     */
    BoxAppApiClient restClient() {
        synchronized (lock) {
            if (restClient == null) {
                restClient = newRestApiClient();
            }
            return restClient;
        }
    }

    /**
     * Gets database helper.
     *
     * @return the database helper
     */
    BoxAppDatabaseHelper getDatabaseHelper() {
        synchronized (lock) {
            if (databaseHelper == null) {
                databaseHelper = newDatabaseHelper();
            }
            return databaseHelper;
        }
    }

    /**
     * Sets gcm id.
     *
     * @param gcm_id the gcm id
     */
    void setGcmID(String gcm_id) {
        synchronized (lock) {
            gcmID = gcm_id;
        }
    }

    /**
     * Gets last kown gcm id.
     *
     * @return the last kown gcm id
     */
    String getLastKownGcmID() {
        synchronized (lock) {
            return gcmID;
        }
    }

    /**
     * The type Android.
     */
    static class Android extends BoxAppPlugins {
        /**
         * Initialize.
         *
         * @param context   the context
         * @param projectId the project id
         * @param clientKey the client key
         */
        static void initialize(Context context, String projectId, String clientKey) {
            BoxAppPlugins.set(new Android(context, projectId, clientKey));
        }

        /**
         * Get box app plugins . android.
         *
         * @return the box app plugins . android
         */
        static BoxAppPlugins.Android get() {
            return (BoxAppPlugins.Android) BoxAppPlugins.get();
        }

        private final Context applicationContext;

        private Android(Context context, String projectId, String clientKey) {
            super(projectId, clientKey);
            applicationContext = context.getApplicationContext();
        }

        /**
         * Application context context.
         *
         * @return the context
         */
        Context applicationContext() {
            return applicationContext;
        }

        @Override
        public BoxAppApiClient newRestApiClient() {
            SSLSessionCache sslSessionCache = new SSLSessionCache(applicationContext);
            return new BoxAppApiClient();
        }

    }
}