package com.gms_worldwide.boxappsdk;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Andrew Kochura.
 */
public class BoxAppMessageResponseModel {

    private long id;
    private String from;
    private String message;
    @SerializedName("deliveredDate")
    private long time;
    private int type;
    @SerializedName("to")
    private String owner;

    /**
     * Instantiates a new Box app message response model.
     *
     * @param from    the from
     * @param message the message
     * @param time    the time
     * @param type    the type
     * @param owner   the owner
     */
    public BoxAppMessageResponseModel(String from, String message, long time, int type, String owner) {
        this.from = from;
        this.message = message;
        this.time = time;
        this.type = type;
        this.owner = owner;
    }

    /**
     * Sets from.
     *
     * @param from the from
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets time.
     *
     * @param time the time
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Sets owner.
     *
     * @param owner the owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Gets from.
     *
     * @return the from
     */
    public String getFrom() {
        return from;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets time.
     *
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * Gets owner.
     *
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Gets type desc.
     *
     * @return the type desc
     */
    public String getTypeDesc() {
        switch (type) {
            case BoxAppConstants.PUSH_TYPE:
                return "PUSH";
            case BoxAppConstants.VIBER_TYPE:
                return "VIBER";
            case BoxAppConstants.SMS_TYPE:
                return "SMS";
            default: return "unexpected";
        }
    }

}
