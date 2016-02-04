package com.signity.bonbon.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 20/10/15.
 */
public class ContactModel1 {

    @SerializedName("success")
    @Expose
    private Boolean success;

    public ContactModel2 getContactModel2() {
        return contactModel2;
    }

    public void setContactModel2(ContactModel2 contactModel2) {
        this.contactModel2 = contactModel2;
    }

    @SerializedName("store")
    @Expose
    private ContactModel2 contactModel2;

    /**
     * @return The success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * @param success The success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
