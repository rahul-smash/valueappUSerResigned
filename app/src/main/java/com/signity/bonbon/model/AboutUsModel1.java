package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 19/10/15.
 */
public class AboutUsModel1 {



    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;

    public AboutUsModel2 getAboutUsModel2() {
        return aboutUsModel2;
    }

    public void setAboutUsModel2(AboutUsModel2 aboutUsModel2) {
        this.aboutUsModel2 = aboutUsModel2;
    }

    @SerializedName("data")
    @Expose
    private AboutUsModel2 aboutUsModel2;

    /**
     *
     * @return
     * The success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     *
     * @param success
     * The success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }


}
