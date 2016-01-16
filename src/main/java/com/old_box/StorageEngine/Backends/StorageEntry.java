package com.old_box.StorageEngine.Backends;


import com.sun.net.httpserver.Headers;

import java.util.HashMap;

/**
 * Class that wraps data and its metadata into one object.
 */
public class StorageEntry {

        private String key;
        private byte[] data;
        private HashMap<String, String> headers;


    StorageEntry(String k, byte[] d){
        this.key = k;
        this.data = d;
        this.headers = new HashMap<>();
    }

    public void addHeader(String name, String value){
        this.headers.put(name, value);
    }

    public String getKey() {
        return key;
    }

    public byte[] getData() {
        return data;
    }

    public HashMap<String, String> getHeaders(){
        return this.headers;
    }

    public String getHeader(String name){
        return this.headers.get(name);
    }

}
