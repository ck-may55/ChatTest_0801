package com.example.chie.notifitest0429;

import java.util.HashMap;

/**
 * Created by chie on 2017/06/11.
 */

public class User {
    public long createdAt;
    public long endedAt;
    public HashMap<String,String>  token;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)

    }

    public User(long createdAt, long endedAt, HashMap mapToken) {
        this.createdAt = createdAt;
        this.endedAt = endedAt;
        this.token = mapToken;
    }

}
