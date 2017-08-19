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

    //2017/08/08追加
    public String imageUrl;
    public long msgType; /*0:text, 1:image*/
    //public String msgType;
    //


    public ChatData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)

    }

    public ChatData(String photoUrl, String text, String toUserid, String userId, String imageUrl,
                    long msgType/*String msgType*/) {
        this.photoUrl = photoUrl;
        this.text = text;
        this.toUserid = toUserid;
        this.userID = userId;

        //2017/08/08追加
        this.imageUrl = imageUrl;
        this.msgType = msgType;
        //
    }


}
