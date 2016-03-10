package com.gms_worldwide.boxappsdk;

/**
 * Created by Andrew Kochura.
 */
class BoxAppMessagesRequestModel {

    private long uniqAppDeviceId;
    private long phone;
    private long date_utc;

    /**
     * Instantiates a new Box app messages request model.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param phone           the phone
     * @param date_utc        the date utc
     */
    public BoxAppMessagesRequestModel(long uniqAppDeviceId, long phone, long date_utc) {
        this.uniqAppDeviceId = uniqAppDeviceId;
        this.phone = phone;
        this.date_utc = date_utc;
    }

    /**
     * Gets uniq app device id.
     *
     * @return the uniq app device id
     */
    public long getUniqAppDeviceId() {
        return uniqAppDeviceId;
    }

    /**
     * Gets phone.
     *
     * @return the phone
     */
    public long getPhone() {
        return phone;
    }

    /**
     * Gets date utc.
     *
     * @return the date utc
     */
    public long getDate_utc() {
        return date_utc;
    }

}
