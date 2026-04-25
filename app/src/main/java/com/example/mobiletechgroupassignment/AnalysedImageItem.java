package com.example.mobiletechgroupassignment;

import android.net.Uri;

public class AnalysedImageItem {
    private String key;
    private String reader;
    private String text;
    private String filename;
    private Uri imageUri;

    public AnalysedImageItem(String key, String reader, String text, String filename, Uri imageUri) {
        this.key = key;
        this.reader = reader;
        this.text = text;
        this.filename = filename;
        this.imageUri = imageUri;
    }

    public String getKey() { return key; }
    public String getReader() { return reader; }
    public String getText() { return text; }
    public String getFilename() { return filename; }
    public Uri getImageUri() { return imageUri; }
    public void setImageUri(Uri imageUri) { this.imageUri = imageUri; }
}