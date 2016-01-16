package com.old_box.StorageEngine.Backends;

import com.old_box.StorageEngine.StorageBackend;
import com.sun.net.httpserver.Headers;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/***
 * A simple class for storing data in memory.
 */
public class MemoryBackend implements StorageBackend {
    HashMap<String, StorageEntry> storage = new HashMap<String, StorageEntry>();


    public MemoryBackend(){
        new Thread(new TTLWatcher(this)).start();
    }

    @Override
    public void store(String key, byte[] value, Headers headers) {
        StorageEntry e = new StorageEntry(key, value);
        e.addHeader(CONTENT_TYPE_HEADER_NAME, headers.getFirst(CONTENT_TYPE_HEADER_NAME));
        String ttlEntry = headers.getFirst(TTL_HEADER_NAME);
        if(ttlEntry != null){
            Long ttlTime = new Date().getTime() + (Long.parseLong(ttlEntry) * 1000);
            e.addHeader(TTL_HEADER_NAME, String.valueOf(ttlTime));
        }

        this.storage.put(key, e);
    }

    @Override
    public StorageEntry retrieve(String key) {
        return this.storage.get(key);
    }

    public HashMap<String, StorageEntry> getStorage(){
        return this.storage;
    }

    public void rmEntry(String key){
        this.storage.remove(key);
    }


    private class TTLWatcher implements Runnable{

        MemoryBackend parent;

        TTLWatcher(MemoryBackend parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            while(true){
                Date now = new Date();

                for(Map.Entry<String, StorageEntry> pair : parent.getStorage().entrySet()){
                    StorageEntry entry = pair.getValue();
                    String ttlString = entry.getHeader(StorageBackend.TTL_HEADER_NAME);
                    if(ttlString == null) continue;
                    long ttlTime = Long.parseLong(ttlString);
                    if (ttlTime < 1) continue;
                    Date ttlDate = new Date(ttlTime);
                    if(now.after(ttlDate)){
                        this.parent.rmEntry((String)pair.getKey());
                    }
                }

                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
