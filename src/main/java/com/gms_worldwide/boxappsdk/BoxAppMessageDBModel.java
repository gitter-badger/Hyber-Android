package com.gms_worldwide.boxappsdk;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by Andrew Kochura.
 */
class BoxAppMessageDBModel implements Serializable {

    /**
     * The Id.
     */
// id is generated by the database and set on the object automatically
    @DatabaseField(generatedId = true)
    public int id;
    /**
     * The Unique id.
     */
    @DatabaseField
    public long uniqueId;
    @DatabaseField
    private String from;
    @DatabaseField
    private String message;
    @DatabaseField
    private long time;
    @DatabaseField
    private int type;
    @DatabaseField
    private String owner;
    @DatabaseField
    private boolean deleted;

    /**
     * Instantiates a new Box app message db model.
     */
    public BoxAppMessageDBModel() {

    }

    /**
     * Instantiates a new Box app message db model.
     *
     * @param from    the from
     * @param message the message
     * @param time    the time
     * @param type    the type
     * @param owner   the owner
     */
    public BoxAppMessageDBModel(String from, String message, long time, int type, String owner) {
        this.from = from;
        this.message = message;
        this.time = time;
        this.type = type;
        this.owner = owner;
        this.deleted = false;
    }

    /**
     * Sets deleted.
     *
     * @param deleted the deleted
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets unique id.
     *
     * @return the unique id
     */
    public long getUniqueId() {
        return uniqueId;
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
     * Is deleted boolean.
     *
     * @return the boolean
     */
    public boolean isDeleted() {
        return deleted;
    }
}
