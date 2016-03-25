package com.gms_worldwide.boxappsdk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew Kochura.
 */
class BoxAppMessagesEnvelope {

    private boolean status;
    private String response;
    private List<BoxAppMessageResponseModel> messages;

    /**
     * Instantiates a new Box app messages envelope.
     *
     * @param messages the messages
     */
    public BoxAppMessagesEnvelope(List<BoxAppMessageResponseModel> messages) {
        this.messages = messages;
    }

    /**
     * Gets messages.
     *
     * @return the messages
     */
    public List<BoxAppMessageModel> getMessages() {
        List<BoxAppMessageModel> appMessageModels = new ArrayList<>();
        for (BoxAppMessageResponseModel responseModel : messages) {
            appMessageModels.add(new BoxAppMessageModel(responseModel.getId(), responseModel.getFrom(),
                    responseModel.getMessage(), responseModel.getTime(),
                    responseModel.getType(), responseModel.getOwner(), false));
        }
        return appMessageModels;
    }

    /**
     * Is status boolean.
     *
     * @return the boolean
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Gets response.
     *
     * @return the response
     */
    public String getResponse() {
        return response;
    }
}
