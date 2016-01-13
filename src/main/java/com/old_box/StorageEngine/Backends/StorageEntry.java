package com.old_box.StorageEngine.Backends;


/**
 * Class that wraps data and its metadata into one object.
 */
public class StorageEntry {

        private String key;
        private byte[] data;
        private String contentType;


    StorageEntry(String k, byte[] d, String ct){
        this.key = k;
        this.contentType = ct;
        this.data = d;
    }

    public String getKey() {
        return key;
    }

    public byte[] getData() {
        return data;
    }

    public String getContentType() {
        return contentType;
    }

}
