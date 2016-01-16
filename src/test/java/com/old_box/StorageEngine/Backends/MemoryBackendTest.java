package com.old_box.StorageEngine.Backends;


import com.old_box.StorageEngine.StorageBackend;
import com.sun.net.httpserver.Headers;
import org.junit.Test;
import static org.junit.Assert.*;

public class MemoryBackendTest {

    @Test
    public void testStoringAndRetrievingDataWithoutTTL(){

        StorageBackend backend = new MemoryBackend();
        String key = "key";
        byte[] data = "Hello World".getBytes();
        Headers headers = new Headers();
        headers.add(StorageBackend.CONTENT_TYPE_HEADER_NAME, "text/plain");
        backend.store(key, data, headers);
        StorageEntry entry = backend.retrieve(key);

        assertTrue("The key should match", key.equals(entry.getKey()));
        assertTrue("The data should match", data == entry.getData());
        assertTrue("The header should match",
                headers.getFirst("Content-Type").equals(entry.getHeader("Content-Type")));
    }


    @Test
    public void testStoringAndRetrievingDataWithTTL() {
        StorageBackend backend = new MemoryBackend();
        String key = "key";
        byte[] data = "Hello World".getBytes();
        Headers headers = new Headers();
        headers.add(StorageBackend.CONTENT_TYPE_HEADER_NAME, "text/plain");
        headers.add(StorageBackend.TTL_HEADER_NAME, "5");
        backend.store(key, data, headers);

        StorageEntry entry = backend.retrieve(key);
        assertTrue("The key should match", key.equals(entry.getKey()));
        assertTrue("The data should match", data == entry.getData());
        assertTrue("The Content-Type header should match",
                headers.getFirst(StorageBackend.CONTENT_TYPE_HEADER_NAME).equals(entry.getHeader(StorageBackend.CONTENT_TYPE_HEADER_NAME)));



    }

    @Test
    public void testStoringAndWaitingOutTTLBeforeRetreiving(){
        StorageBackend backend = new MemoryBackend();
        String key = "key";
        byte[] data = "Hello World".getBytes();
        Headers headers = new Headers();
        headers.add(StorageBackend.CONTENT_TYPE_HEADER_NAME, "text/plain");
        headers.add(StorageBackend.TTL_HEADER_NAME, "1");
        backend.store(key, data, headers);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StorageEntry shouldBeNull = backend.retrieve(key);

        assertNull("The object retrieved after the TTL has expired should be null.", shouldBeNull);
    }

    @Test
    public void testRetrievingNonExistentKey() throws Exception {
        StorageBackend backend = new MemoryBackend();
        StorageEntry entry = backend.retrieve("ShouldNotExist");
        assertNull("The retrieved entry should be null.", entry);
    }

}
