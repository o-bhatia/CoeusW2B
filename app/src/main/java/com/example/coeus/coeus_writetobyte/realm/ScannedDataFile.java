package com.example.coeus.coeus_writetobyte.realm;

import io.realm.RealmObject;

/**
 * Description: This class contains all methods that are associated with
 * the scanned data file and realm, used to display in My Files.
 *
 * Author: Ojas Bhatia
 *
 * Last updated: January 10, 2019
 */

public class ScannedDataFile extends RealmObject {

    //declaring Strings associated with each scanned file
    private String fileName;
    private String creationDate;
    private String filePath;

    //accessors and mutators below

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
