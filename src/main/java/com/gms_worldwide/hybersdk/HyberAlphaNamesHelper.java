package com.gms_worldwide.hybersdk;

import java.util.List;

/**
 * Created by Andrew Kochura.
 */
public class HyberAlphaNamesHelper {

    private static final String TAG = "com.gms_worldwide.hybersdk.HyberAlphaNamesHelper";

    private static HyberAlphaNamesHelper instance = null;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static HyberAlphaNamesHelper getInstance() {
        if (instance == null) {
            instance = new HyberAlphaNamesHelper();
        }
        return instance;
    }

    /**
     * Instantiates a Hyber alpha names helper.
     */
    protected HyberAlphaNamesHelper() {

    }

    /**
     * Add the all your registered operator alpha names
     *
     * @param alphaNames the alpha names
     */
    public void addAlphaName(List<String> alphaNames) {
        HyberPlugins.get().getDatabaseHelper().updateAlphas(alphaNames);
    }

}
