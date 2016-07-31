package com.example.meghana.testing;

/**
 * Created by darshan on 31/07/16.
 */
public class DataModel {

    String image, text, type, filename, teachersName;

    public DataModel(String image, String text, String type, String filename, String teachersName) {
        this.image = image;
        this.text = text;
        this.type = type;
        this.filename = filename;
        this.teachersName = teachersName;
    }

    public String getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public String getFilename() {
        return filename;
    }

    public String getTeachersName() {
        return teachersName;
    }
}
