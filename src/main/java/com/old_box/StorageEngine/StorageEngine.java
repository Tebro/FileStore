package com.old_box.StorageEngine;


import com.old_box.StorageEngine.Backends.FileBackend;
import com.old_box.fileStore.Environment;

public class StorageEngine {

    StorageBackend backend;

    public StorageEngine(Environment env) throws Exception {
        String backend = env.getValue("STORAGE_BACKEND");
        if(backend == null) backend = "file";
        switch (backend){
            case "file":
                this.fileBackend(env);
                break;
        }
    }

    private void fileBackend(Environment env) throws Exception {
        String path = env.getValue("STORAGE_FILE_PATH");
        if(path == null)
            path = "/tmp/fileStorage";
        this.backend = new FileBackend(path);


    }

    public void store(String key,  byte[] data){
        backend.store(key, data);
    }

    public byte[] retrieve(String key) {
        return backend.retrieve(key);
    }


}
