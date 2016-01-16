package com.old_box.fileStore;


import java.util.Map;

public class Environment {
    protected Map<String, String> env;

    public Environment(){
        this.env = System.getenv();
    }

    public String getValue(String key){
        return env.get(key);
    }


}
