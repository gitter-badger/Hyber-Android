package com.gms_worldwide.boxappsdk;

/**
 * Created by Andrew Kochura.
 */
class GetUserProfileRequestModel {
    private long uniqAppDeviceId;

    /**
     * Instantiates a new Get user profile request model.
     *
     * @param uniqAppDeviceId the uniq app device id
     */
    public GetUserProfileRequestModel(long uniqAppDeviceId) {
        this.uniqAppDeviceId = uniqAppDeviceId;
    }

    /**
     * Gets uniq app device id.
     *
     * @return the uniq app device id
     */
    public long getUniqAppDeviceId() {
        return uniqAppDeviceId;
    }
}
