package com.old_box.StorageEngine;


import com.old_box.StorageEngine.Backends.FileBackend;
import com.old_box.StorageEngine.Backends.MemoryBackend;
import com.old_box.StorageEngine.Backends.StorageEntry;
import com.old_box.fileStore.Environment;
import com.sun.net.httpserver.Headers;


/***
 * CLass for handling storage operations.
 */
public class StorageEngine {

    StorageBackend backend;

    public StorageEngine(Environment env) throws Exception {
        // Check the ENV variable for non-default value.
        String backend = env.getValue("STORAGE_BACKEND");
        // If nothing has been configured use the default one.
        if(backend == null) backend = "memory";

        switch (backend){
            case "file":
                this.fileBackend(env);
                break;
            case "memory":
                this.memoryBackend(env);
                break;
        }
    }

    /***
     * Helper for creating an instance of the FileBackend to use
     * @param env
     * @throws Exception
     */
    private void fileBackend(Environment env) throws Exception {
        String path = env.getValue("STORAGE_FILE_PATH");
        if(path == null)
            path = FileBackend.DEFAULT_PATH;
        this.backend = new FileBackend(path);
        System.out.println("Storing data in: " + path);
    }

    /***
     * Helper for creating an instance of the MemoryBackend.
     *
     * @param env
     */
    private void memoryBackend(Environment env){
        this.backend = new MemoryBackend();
        System.out.println("Storing data in memory");
    }


    /***
     * Want to store data through this class? Use this method.
     * @param key
     * @param data
     * @param headers
     */
    public void store(String key,  byte[] data, Headers headers){
        backend.store(key, data, headers);
    }

    /***
     * Want to retreive data stored through this class? Use this method.
     * @param key
     * @return
     */
    public StorageEntry retrieve(String key) {
        return backend.retrieve(key);
    }


}
