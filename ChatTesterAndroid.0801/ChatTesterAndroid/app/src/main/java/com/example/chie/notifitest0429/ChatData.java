package com.example.chie.notifitest0429;

import java.util.HashMap;

/**
 * Created by chie on 2017/07/22.
 */

public class ChatData {
    public String photoUrl;
    public String text;
    public String toUserid;
    public String userID;

    public ChatData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)

    }

    public ChatData(String photoUrl, String text, String toUserid, String userId) {
        this.photoUrl = photoUrl;
        this.text = text;
        this.toUserid = toUserid;
        this.userID = userId;
    }
}
