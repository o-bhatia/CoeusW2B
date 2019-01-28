package com.example.coeus.coeus_writetobyte.models;

import android.net.Uri;

import java.io.Serializable;

/**
 * Description: This class contains accessors and mutators
 * used for the GmailUser object.
 *
 * Author: Ojas Bhatia
 *
 * Last updated: January 10, 2019
 */

public class GmailUser implements Serializable {

   //declaring Strings associated with each logged in user
    private String personName;
    private String personGivenName;
    private String personFamilyName;
    private String personEmail;
    private String personId;

    private String uriString;

    //constructor
    public GmailUser(String personName, String personGivenName, String personFamilyName, String personEmail, String personId,
                     String uriString) {
        this.personName = personName;
        this.personGivenName = personGivenName;
        this.personFamilyName = personFamilyName;
        this.personEmail = personEmail;
        this.personId = personId;
        this.uriString = uriString;
    }

    //all accessors and mutators are below (not commented due to redundancy)

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonGivenName() {
        return personGivenName;
    }

    public void setPersonGivenName(String personGivenName) {
        this.personGivenName = personGivenName;
    }

    public String getPersonFamilyName() {
        return personFamilyName;
    }

    public void setPersonFamilyName(String personFamilyName) {
        this.personFamilyName = personFamilyName;
    }

    public String getPersonEmail() {
        return personEmail;
    }

    public void setPersonEmail(String personEmail) {
        this.personEmail = personEmail;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getUriString() {
        return uriString;
    }

    public void setUriString(String uriString) {
        this.uriString = uriString;
    }
}
