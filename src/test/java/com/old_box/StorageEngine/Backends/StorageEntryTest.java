package com.old_box.StorageEngine.Backends;

import org.junit.Test;

import static org.junit.Assert.*;

public class StorageEntryTest {


    @Test
    public void testConstructor(){
        String key = "key";
        byte[] data = "Hello World".getBytes();
        StorageEntry entry = new StorageEntry(key, data);

        assertTrue("The data should have been equal.",
                entry.getData() == data);
        assertTrue("The key should have been the same.",
                entry.getKey().equals(key));

    }

    @Test
    public void testAddingHeader(){
        String key = "header";
        String headerValue = "Hello World!";

        StorageEntry entry = new StorageEntry("foo", "bar".getBytes());
        entry.addHeader(key, headerValue);

        assertTrue("The value for the key should be the same as the value passed.",
                entry.getHeader(key).equals(headerValue));

    }

}
