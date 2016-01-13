package com.old_box.StorageEngine.Backends;

import com.old_box.StorageEngine.StorageBackend;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Sometimes you want more persistanse than just memory, this is for that time.
 */
public class FileBackend implements StorageBackend {

    String storagePath;


    /**
     * Constructor makes sure we can access the specified storage location.
     * @param storagePath
     * @throws Exception
     */
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

    /**
     * Writes two files, one with the data, and one with the metadata.
     * @param key
     * @param value
     * @param contentType
     */
    @Override
    public void store(String key, byte[] value, String contentType) {
        String path = this.convertPath(key);
        Path file = Paths.get(this.storagePath + "/" + path);
        Path ctFile = Paths.get(this.storagePath + "/" + "ContentType" + path);
        try {
            Files.write(file, value);
            Files.write(ctFile, contentType.getBytes(Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the data and the metadata from the two files on disk.
     * @param key
     * @return
     */
    @Override
    public StorageEntry retrieve(String key) {
        String path = this.convertPath(key);
        Path file = Paths.get(this.storagePath + "/" + path);
        Path ctPath = Paths.get(this.storagePath + "/" + "ContentType" + path);
        try {
            byte[] data = Files.readAllBytes(file);
            byte[] contentTypeAsBytes = Files.readAllBytes(ctPath);
            String contentType = new String(contentTypeAsBytes, Charset.defaultCharset());
            return new StorageEntry(key, data, contentType);
        } catch (IOException e) {
            System.out.println("File with key: '"+ key +"' not found.");
        }
        return null;
    }

    /**
     * Helper method for making friendlier filenames than just the keys with their "/" characters.
     * @param path
     * @return
     */
    private String convertPath(String path){
        return path.replace("/", "___");
    }
}
