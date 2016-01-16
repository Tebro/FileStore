package com.old_box.FileStore;

import com.old_box.fileStore.Environment;

import java.util.HashMap;


/**
 * A utility class for testing.
 *
 * To allow test code to create a fake environment setup.
 *
 */
public class ProgrammableEnvironment extends Environment {


    public ProgrammableEnvironment() {
        this.env = new HashMap<>();
    }


    /**
     * Set a Key-Value pair
     *
     * @param key The key
     * @param value The value that the key maps to
     */
    public void setValue(String key, String value){
        this.env.put(key, value);
    }

}
