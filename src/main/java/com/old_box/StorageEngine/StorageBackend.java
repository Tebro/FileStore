package com.old_box.StorageEngine;


import com.old_box.StorageEngine.Backends.StorageEntry;
import com.sun.net.httpserver.Headers;


/***
 * Describes what methods a compatible backend needs.
 */
public interface StorageBackend {
    String TTL_HEADER_NAME = "File-Store-TTL";
    String CONTENT_TYPE_HEADER_NAME = "Content-Type";

    public void store(String key, byte[] value, Headers headers);
    public StorageEntry retrieve(String key);
}
