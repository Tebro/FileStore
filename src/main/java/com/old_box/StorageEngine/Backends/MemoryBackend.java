package com.old_box.StorageEngine.Backends;

import com.old_box.StorageEngine.StorageBackend;

import java.util.HashMap;


/***
 * A simple class for storing data in memory.
 */
public class MemoryBackend implements StorageBackend {
    HashMap<String, StorageEntry> storage = new HashMap<String, StorageEntry>();


    @Override
    public void store(String key, byte[] value, String contentType) {
        StorageEntry e = new StorageEntry(key, value, contentType);
        storage.put(key, e);
    }

    @Override
    public StorageEntry retrieve(String key) {
        return this.storage.get(key);
    }


}
