package com.old_box.StorageEngine;


public interface StorageBackend {

    public void store(String key, byte[] value);
    public byte[] retrieve(String key);
}
