package com.old_box.StorageEngine.Backends;

import com.old_box.StorageEngine.StorageBackend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackend implements StorageBackend {

    String storagePath;

    public FileBackend(String storagePath) throws Exception {
        File storageDir = new File(storagePath);
        if(!storageDir.exists()){
            boolean ok = storageDir.mkdir();
            if(!ok){
                throw new Exception("No permissions");
            }
        }
        this.storagePath = storagePath;
    }

    @Override
    public void store(String key, byte[] value) {
        String path = this.convertPath(key);
        Path file = Paths.get(this.storagePath + "/" + path);
        try {
            Files.write(file, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] retrieve(String key) {
        String path = this.convertPath(key);
        Path file = Paths.get(this.storagePath + "/" + path);
        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            System.out.println("File with key: '"+ key +"' not found.");
        }
        return new byte[0];
    }

    private String convertPath(String path){
        return path.replace("/", "___");
    }
}
