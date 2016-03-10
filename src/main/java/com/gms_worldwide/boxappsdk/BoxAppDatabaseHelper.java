package com.gms_worldwide.boxappsdk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */

/**
 * Created by Andrew Kochura.
 */
class BoxAppDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = "com.gms_worldwide.boxappsdk.BoxAppDatabaseHelper";

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "BoxAppSDKStorage.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    // the DAO object we use to access the BoxAppMessageDBModel table
    private Dao<BoxAppMessageDBModel, Integer> messageDao = null;
    private RuntimeExceptionDao<BoxAppMessageDBModel, Integer> messageRuntimeDao = null;
    // the DAO object we use to access the BoxAppMessageUpdateDBModel table
    private Dao<BoxAppMessageUpdateDBModel, Integer> messageUpdateDao = null;
    private RuntimeExceptionDao<BoxAppMessageUpdateDBModel, Integer> messageUpdateRuntimeDao = null;
    // the DAO object we use to access the CurrentUserDBModel table
    private Dao<BoxAppCurrentUserDBModel, Integer> currentUserDao = null;
    private RuntimeExceptionDao<BoxAppCurrentUserDBModel, Integer> currentUserRuntimeDao = null;

    // the DAO object we use to access the BoxAppAlphaNameDBModel table
    private Dao<BoxAppAlphaNameDBModel, Integer> alphasDao = null;
    private RuntimeExceptionDao<BoxAppAlphaNameDBModel, Integer> alphasRuntimeDao = null;

    /**
     * Instantiates a new Box app database helper.
     *
     * @param context the context
     */
    public BoxAppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, BoxAppMessageDBModel.class);
            TableUtils.createTable(connectionSource, BoxAppMessageUpdateDBModel.class);
            TableUtils.createTable(connectionSource, BoxAppCurrentUserDBModel.class);
            TableUtils.createTable(connectionSource, BoxAppAlphaNameDBModel.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save incoming message.
     *
     * @param from    the from
     * @param message the message
     * @param time    the time
     * @param type    the type
     * @param owner   the owner
     */
    public void saveIncomingMessage(String from, String message, long time, int type, String owner) {
        // here we try inserting data in the on-create as a test
        RuntimeExceptionDao<BoxAppMessageDBModel, Integer> dao = getMessageDataDao();

        BoxAppMessageDBModel messageDBModel =
                new BoxAppMessageDBModel(from, message, time, type, owner);
        dao.create(messageDBModel);
    }

    /**
     * Update messages of day list.
     *
     * @param messageModels the message models
     * @return the list
     */
    public List<BoxAppMessageModel> updateMessagesOfDay(
            List<BoxAppMessageModel> messageModels) {
        List<BoxAppMessageModel> addingMessageModels = new ArrayList<>();
        try {
            for (BoxAppMessageModel messageModel : messageModels) {
                BoxAppMessageDBModel messageDBModel =
                        getMessageDao().queryBuilder()
                                .where().eq("time", messageModel.getTime())
                                .and().eq("type", messageModel.getType())
                                .and().eq("owner", messageModel.getOwner())
                                .queryForFirst();
                if (messageDBModel == null) {
                    addingMessageModels.add(messageModel);
                    BoxAppMessageDBModel model = new BoxAppMessageDBModel(messageModel.getFrom(),
                            messageModel.getMessage(), messageModel.getTime(),
                            messageModel.getType(), messageModel.getOwner());
                    getMessageDao().create(model);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addingMessageModels;
    }

    /**
     * Add message update info.
     *
     * @param time        the time
     * @param type        the type
     * @param is_fool_day the is fool day
     */
    public void addMessageUpdateInfo(long time, int type, boolean is_fool_day) {
        try {
            BoxAppMessageUpdateDBModel messageUpdateDBModel =
                    getMessageUpdateDao().queryBuilder().where().eq("time", time).queryForFirst();
            if (messageUpdateDBModel == null) {
                messageUpdateDBModel =
                        new BoxAppMessageUpdateDBModel(time, type, is_fool_day);
                getMessageUpdateDao().create(messageUpdateDBModel);
            } else {
                messageUpdateDBModel.setTime(time);
                messageUpdateDBModel.setType(type);
                messageUpdateDBModel.setIs_fool_day(is_fool_day);
                getMessageUpdateDao().update(messageUpdateDBModel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Is message update for fool day boolean.
     *
     * @param time the time
     * @param type the type
     * @return the boolean
     */
    public boolean isMessageUpdateForFoolDay(long time, int type) {
        BoxAppMessageUpdateDBModel messageUpdateDBModel = null;
        try {
            messageUpdateDBModel = getMessageUpdateDao().queryBuilder()
                    .where().eq("time", time)
                    .and().eq("type", type)
                    .queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageUpdateDBModel != null && messageUpdateDBModel.is_fool_day();
    }

    /**
     * Update alphas.
     *
     * @param alphaNames the alpha names
     */
    public void updateAlphas(List<String> alphaNames) {
        try {
            TableUtils.clearTable(connectionSource, BoxAppAlphaNameDBModel.class);
            List<BoxAppAlphaNameDBModel> dbModels = new ArrayList<>();
            for (String name : alphaNames) {
                dbModels.add(new BoxAppAlphaNameDBModel(name));
            }
            if (dbModels.size() > 0) {
                getAlphasDao().create(dbModels);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Is alpha name valid boolean.
     *
     * @param name the name
     * @return the boolean
     */
    public boolean isAlphaNameValid(String name) {
        List<BoxAppAlphaNameDBModel> dbModels =
                null;
        try {
            dbModels = getAlphasDao().queryBuilder().where().eq("name", name).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dbModels != null && dbModels.size() > 0;
    }

    /**
     * Clean user data.
     */
    public void cleanUserData() {
        try {
            TableUtils.clearTable(connectionSource, BoxAppMessageDBModel.class);
            TableUtils.clearTable(connectionSource, BoxAppMessageUpdateDBModel.class);
            TableUtils.clearTable(connectionSource, BoxAppCurrentUserDBModel.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, BoxAppMessageDBModel.class, true);
            TableUtils.dropTable(connectionSource, BoxAppMessageUpdateDBModel.class, true);
            TableUtils.dropTable(connectionSource, BoxAppCurrentUserDBModel.class, true);
            TableUtils.dropTable(connectionSource, BoxAppAlphaNameDBModel.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets messages.
     *
     * @param year  the year
     * @param month the month
     * @param day   the day
     * @param types the types
     * @return the messages
     */
    public List<BoxAppMessageModel> getMessages(int year, int month, int day, List<Integer> types) {
        List<BoxAppMessageModel> boxAppMessageModels = new ArrayList<>();
        try {
            QueryBuilder<BoxAppMessageDBModel, Integer> queryBuilder = getMessageDao().queryBuilder();
            queryBuilder.orderBy("time", false);
            queryBuilder.where().between("time",
                    BoxAppTools.getStartOfDayUtcTime(year, month, day),
                    BoxAppTools.getEndOfDayUtcTime(year, month, day))
                    .and().in("type", types)
                    .and().eq("deleted", false);

            List<BoxAppMessageDBModel> messageDBModels = queryBuilder.query();
            for (BoxAppMessageDBModel dbModel : messageDBModels) {
                boxAppMessageModels.add(new BoxAppMessageModel(dbModel));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boxAppMessageModels;
    }

    /**
     * Gets messages.
     *
     * @param dateFrom  the time un UTC format
     * @param dateTo  the time un UTC format
     * @param types the types
     * @return the messages
     */
    public List<BoxAppMessageModel> getMessages(long dateFrom, long dateTo, List<Integer> types) {
        List<BoxAppMessageModel> boxAppMessageModels = new ArrayList<>();
        try {
            QueryBuilder<BoxAppMessageDBModel, Integer> queryBuilder = getMessageDao().queryBuilder();
            queryBuilder.orderBy("time", false);
            queryBuilder.where()
                    .between("time", dateFrom, dateTo)
                    .and().in("type", types)
                    .and().eq("deleted", false);

            List<BoxAppMessageDBModel> messageDBModels = queryBuilder.query();
            for (BoxAppMessageDBModel dbModel : messageDBModels) {
                boxAppMessageModels.add(new BoxAppMessageModel(dbModel));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boxAppMessageModels;
    }

    /**
     * Gets all messages.
     *
     * @param types the types
     * @return the messages
     */
    public List<BoxAppMessageModel> getAllMessages(List<Integer> types) {
        List<BoxAppMessageModel> boxAppMessageModels = new ArrayList<>();
        try {
            QueryBuilder<BoxAppMessageDBModel, Integer> queryBuilder = getMessageDao().queryBuilder();
            queryBuilder.orderBy("time", false);
            queryBuilder.where().in("type", types)
                    .and().eq("deleted", false);

            List<BoxAppMessageDBModel> messageDBModels = queryBuilder.query();
            for (BoxAppMessageDBModel dbModel : messageDBModels) {
                boxAppMessageModels.add(new BoxAppMessageModel(dbModel));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boxAppMessageModels;
    }

    /**
     * Delete message.
     *
     * @param id the id
     */
    public void deleteMessage(int id) {
        try {
            BoxAppMessageDBModel messageDBModel = getMessageDao().queryForId(id);
            if (messageDBModel != null) {
                messageDBModel.setDeleted(true);
            }
            getMessageDao().update(messageDBModel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clear deleted messages.
     */
    public void clearDeletedMessages() {
        try {
            DeleteBuilder<BoxAppMessageDBModel, Integer> deleteBuilder = getMessageDao().deleteBuilder();
            deleteBuilder.where().eq("deleted", true);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets current user phone.
     *
     * @return the current user phone
     */
    public long getCurrentUserPhone() {
        return getCurrentUser().getPhone();
    }

    /**
     * Gets current user unique id.
     *
     * @return the current user unique id
     */
    public long getCurrentUserUniqueId() {
        return getCurrentUser().getUniqAppDeviceId();
    }

    /**
     * Gets current user.
     *
     * @return the current user
     */
    public BoxAppCurrentUserDBModel getCurrentUser() {
        try {
            List<BoxAppCurrentUserDBModel> userDBModels = getCurrentUserDao().queryForAll();
            if (userDBModels.size() == 1) {
                return userDBModels.get(0);
            } else {
                TableUtils.clearTable(connectionSource, BoxAppCurrentUserDBModel.class);
                getCurrentUserDao().create(new BoxAppCurrentUserDBModel());
                return getCurrentUserDao().queryForAll().get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update current user box app current user db model.
     *
     * @param userDBModel the user db model
     * @return the box app current user db model
     */
    public BoxAppCurrentUserDBModel updateCurrentUser(BoxAppCurrentUserDBModel userDBModel) {
        try {
            getCurrentUserDao().update(userDBModel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getCurrentUser();
    }

    /**
     * Returns the Database Access Object (DAO) for our MessageDBModel class. It will create it or just give the cached
     * value.
     *
     * @return the message dao
     * @throws SQLException the sql exception
     */
    public Dao<BoxAppMessageDBModel, Integer> getMessageDao() throws SQLException {
        if (messageDao == null) {
            messageDao = getDao(BoxAppMessageDBModel.class);
        }
        return messageDao;
    }

    /**
     * Returns the Database Access Object (DAO) for our MessageDBModel class. It will create it or just give the cached
     * value.
     *
     * @return the message update dao
     * @throws SQLException the sql exception
     */
    public Dao<BoxAppMessageUpdateDBModel, Integer> getMessageUpdateDao() throws SQLException {
        if (messageUpdateDao == null) {
            messageUpdateDao = getDao(BoxAppMessageUpdateDBModel.class);
        }
        return messageUpdateDao;
    }

    /**
     * Returns the Database Access Object (DAO) for our BoxAppCurrentUserDBModel class. It will create it or just give the cached
     * value.
     *
     * @return the current user dao
     * @throws SQLException the sql exception
     */
    public Dao<BoxAppCurrentUserDBModel, Integer> getCurrentUserDao() throws SQLException {
        if (currentUserDao == null) {
            currentUserDao = getDao(BoxAppCurrentUserDBModel.class);
        }
        return currentUserDao;
    }

    /**
     * Returns the Database Access Object (DAO) for our BoxAppAlphaNameDBModel class. It will create it or just give the cached
     * value.
     *
     * @return the alphas dao
     * @throws SQLException the sql exception
     */
    public Dao<BoxAppAlphaNameDBModel, Integer> getAlphasDao() throws SQLException {
        if (alphasDao == null) {
            alphasDao = getDao(BoxAppAlphaNameDBModel.class);
        }
        return alphasDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our MessageDBModel class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    private RuntimeExceptionDao<BoxAppMessageDBModel, Integer> getMessageDataDao() {
        if (messageRuntimeDao == null) {
            messageRuntimeDao = getRuntimeExceptionDao(BoxAppMessageDBModel.class);
        }
        return messageRuntimeDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our MessageDBModel class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    private RuntimeExceptionDao<BoxAppMessageUpdateDBModel, Integer> getMessageUpdateDataDao() {
        if (messageUpdateRuntimeDao == null) {
            messageUpdateRuntimeDao = getRuntimeExceptionDao(BoxAppMessageUpdateDBModel.class);
        }
        return messageUpdateRuntimeDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our CurrentUserDBModel class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     *
     * @return the current user data dao
     */
    public RuntimeExceptionDao<BoxAppCurrentUserDBModel, Integer> getCurrentUserDataDao() {
        if (currentUserRuntimeDao == null) {
            currentUserRuntimeDao = getRuntimeExceptionDao(BoxAppCurrentUserDBModel.class);
        }
        return currentUserRuntimeDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our CurrentUserDBModel class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     *
     * @return the alphas data dao
     */
    public RuntimeExceptionDao<BoxAppAlphaNameDBModel, Integer> getAlphasDataDao() {
        if (alphasRuntimeDao == null) {
            alphasRuntimeDao = getRuntimeExceptionDao(BoxAppAlphaNameDBModel.class);
        }
        return alphasRuntimeDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        messageDao = null;
        messageRuntimeDao = null;
        messageUpdateDao = null;
        messageUpdateRuntimeDao = null;
        currentUserDao = null;
        currentUserRuntimeDao = null;
        alphasDao = null;
        alphasRuntimeDao = null;
    }
}