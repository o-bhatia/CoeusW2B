package com.example.coeus.coeus_writetobyte.realm;

import io.realm.RealmObject;

/**
 * Created by victor on 1/11/19.
 */

public class ScannedDataFile extends RealmObject {

    private String fileName;
    private String creationDate;
    private String filePath;

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
