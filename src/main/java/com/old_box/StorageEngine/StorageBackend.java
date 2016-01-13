package com.old_box.StorageEngine;


import com.old_box.StorageEngine.Backends.StorageEntry;


/***
 * Describes what methods a compatible backend needs.
 */
public interface StorageBackend {

    public void store(String key, byte[] value, String contentType);
    public StorageEntry retrieve(String key);
}
