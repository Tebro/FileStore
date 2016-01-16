package com.old_box.StorageEngine.Backends;


import com.old_box.StorageEngine.StorageBackend;
import com.sun.net.httpserver.Headers;
import org.junit.Test;

import static org.junit.Assert.assertNull;


public class FileBackendTest {

    @Test
    public void testStoringAndWaitingOutTTLBeforeRetreiving() throws Exception {
        StorageBackend backend = new FileBackend(FileBackend.DEFAULT_PATH);
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
        StorageBackend backend = new FileBackend(FileBackend.DEFAULT_PATH);
        StorageEntry entry = backend.retrieve("ShouldNotExist");
        assertNull("The retrieved entry should be null.", entry);
    }

}
