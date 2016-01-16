package com.old_box.StorageEngine.Backends;

import com.old_box.StorageEngine.StorageBackend;
import com.sun.net.httpserver.Headers;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;


/**
 * Sometimes you want more persistanse than just memory, this is for that time.
 */
public class FileBackend implements StorageBackend {
    public static final String DEFAULT_PATH = "/tmp/fileStorage";
    public static final String TTL_PREFIX = "TTL";
    public static final String CONTENT_TYPE_PREFIX = "ContentType";

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
        new Thread(new TTLChecker(storagePath)).start();
    }

    /**
     * Writes two files, one with the data, and one with the metadata.
     * @param key
     * @param value
     * @param headers
     */
    @Override
    public void store(String key, byte[] value, Headers headers) {
        String path = this.convertPath(key);
        Path file = Paths.get(this.storagePath + "/" + path);
        Path ctFile = Paths.get(this.storagePath + "/" + CONTENT_TYPE_PREFIX + path);

        // Figure out the TTL
        String ttlHeader = headers.getFirst(TTL_HEADER_NAME);
        Path ttlFile = Paths.get(this.storagePath + "/" + TTL_PREFIX + path);
        Date now = new Date();

        long endOfTtl = 0;
        if (ttlHeader != null) {
            long ttl = Long.parseLong(ttlHeader) * 1000; //Turn the provided seconds to milliseconds
            endOfTtl = now.getTime() + ttl;
        }

        try {
            Files.write(file, value);
            Files.write(ctFile, headers.getFirst(CONTENT_TYPE_HEADER_NAME).getBytes(Charset.defaultCharset()));
            Files.write(ttlFile, String.valueOf(endOfTtl).getBytes(Charset.defaultCharset()));
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
        Path ctPath = Paths.get(this.storagePath + "/" + CONTENT_TYPE_PREFIX + path);
        try {
            byte[] data = Files.readAllBytes(file);
            byte[] contentTypeAsBytes = Files.readAllBytes(ctPath);
            String contentType = new String(contentTypeAsBytes, Charset.defaultCharset());
            StorageEntry entry = new StorageEntry(key, data);
            entry.addHeader(StorageBackend.CONTENT_TYPE_HEADER_NAME, contentType);
            return entry;
        } catch (IOException e) {
            System.out.println("File with key: '"+ key +"' not found. Was there a TTL set?");
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


    /**
     * Simple class that checks TTL files to see if records have expired.
     *
     * This class implements Runnable so it should be run in its own thread.
     */
    private class TTLChecker implements Runnable {
        String storagePath;

        TTLChecker(String path){
            this.storagePath = path;
        }

        @Override
        public void run() {
            File storageDir = new File(storagePath);
            while (true){

                File [] ttlFiles = storageDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.startsWith(FileBackend.TTL_PREFIX);
                    }
                });

                for (File file : ttlFiles){
                    Path filePath = Paths.get(file.getAbsolutePath());

                    try {
                        byte[] data = Files.readAllBytes(filePath);
                        String ttlData = new String(data, Charset.defaultCharset());
                        Date now = new Date();
                        long ttlTime = Long.parseLong(ttlData);
                        if(ttlTime == 0) continue;
                        Date ttlDate = new Date(ttlTime);
                        if (now.after(ttlDate)){
                            //Delete all files relating to this entry.

                            // Get the key by deleting the known factors from the path.
                            int prefixLength = (this.storagePath + "/" + "TTL").length();
                            // Remove the path and TTL prefix to get the actual key.
                            String key = file.getAbsolutePath().substring(prefixLength);

                            String[] prefixes = new String[]{
                                    "", //No prefix
                                    FileBackend.TTL_PREFIX,
                                    FileBackend.CONTENT_TYPE_PREFIX
                            };

                            for (String prefix : prefixes){
                                File f = new File(this.storagePath + "/" + prefix + key);
                                f.delete();
                            }

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Sleeping failed, oh well...");
                    e.printStackTrace();
                }
            }
        }
    }
}
