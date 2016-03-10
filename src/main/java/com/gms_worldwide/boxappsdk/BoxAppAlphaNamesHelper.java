package com.gms_worldwide.boxappsdk;

import java.util.List;

/**
 * Created by Andrew Kochura.
 */
public class BoxAppAlphaNamesHelper {

    private static final String TAG = "com.gms_worldwide.boxappsdk.BoxAppAlphaNamesHelper";

    private static BoxAppAlphaNamesHelper instance = null;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static BoxAppAlphaNamesHelper getInstance() {
        if (instance == null) {
            instance = new BoxAppAlphaNamesHelper();
        }
        return instance;
    }

    /**
     * Instantiates a new Box app alpha names helper.
     */
    protected BoxAppAlphaNamesHelper() {

    }

    /**
     * Add the all your registered operator alpha names
     *
     * @param alphaNames the alpha names
     */
    public void addAlphaName(List<String> alphaNames) {
        BoxAppPlugins.get().getDatabaseHelper().updateAlphas(alphaNames);
    }

}
