package com.example.chie.notifitest0429;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import static com.example.chie.notifitest0429.MainActivity.flag;
import static com.example.chie.notifitest0429.MainActivity.uid;

import com.example.chie.notifitest0429.Constants;

/**
 * Created by chie on 2017/04/29.
 */

public class MyFirebaseInstanceIdservice extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIdservice";
    public  static String token;
    private long createdAt;
    private long endedAt;

    private final int DELAY = 10;

    //ユーザIDの指定
    private static String userId = "AB0012-6";
    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(getApplicationContext());
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onTokenRefresh() {
        //アプリ初起動時にFCMトークンを生成
        token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "RefreshedToken = " + token);
        createdAt = new Date().getTime() /1000L;
        endedAt = 0;

        Log.d(TAG, "onTokenReflesh ");

        // 新しいトークンを発行したことをSharedPreferencesにセットする
        Context context = getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences("FCMessages", Context.MODE_PRIVATE);
        sp.edit().putBoolean("UpdatedToken", true).commit();

        //
        // TODO
        // activity_mainのflag_viewの表示を変化させる
        // トークンメッセージを受け取った事をLocalBroadcasterを使って発信する
        //
        final Handler _handler = new Handler(Looper.getMainLooper());
        _handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //
                Log.d(TAG, "postDelayed sendBroadcast");
                Intent intent = new Intent("UpdatedToken");
                broadcaster.sendBroadcast(intent);
            }
        }, DELAY);


        //トークンの値をサーバーへ送信
        //submit();
    }


/**
    private void submit() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
		Log.d(TAG, "getInstance");



		//ListとMapを用いたDBへの書き込み
        final DatabaseReference refUser = database.getReference("usersTokens");

        // 新しいレコードの作成
        final User userToken = new User();
        userToken.createdAt = createdAt;
        userToken.endedAt = 0;
        // 新しいトークンのレコードmapを作成（トークンとUIDの組み合わせ）
        // Firebaseコンソールの操作でトークンをキーにした方がコピーペーストし易いために変更しました
        HashMap<String, String>mapToken = new HashMap<String,String>();
        mapToken.put(token, Constants.UID);
        userToken.token = mapToken;

        // /usersTokens/userId以下を受け取るクロージャを設定
        refUser.child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {

                    // データを受信した時に実行される関数
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {			

                        /**
                        Log.d(TAG, "onDataChange userId : " + dataSnapshot.child(userId));

                        if (dataSnapshot.child(userId) == null) {
                            Log.d(TAG, "onDataChange user data is null");
                            return;
                        }
                         */

                        // DBにセットするMap
 //                       Map<String, Object> map = new HashMap<String, Object>();

                        /**
                        Log.d(TAG, "getChildren: " + dataSnapshot.getChildren());
                        Log.d(TAG, "getKey : " + dataSnapshot.getKey());
                        Log.d(TAG, "getRef : " + dataSnapshot.getRef());
                        */
/*
                        // UserId以下に設定されている全レコードチェックする
                       int i = 0;
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            Log.d(TAG, "in for");
                            // endedAtが0だった場合は、現在の時間をセット
                            User user = child.getValue(User.class);
                            if (user.endedAt == 0) {
                                user.endedAt = createdAt;
                            }
                            // i（添字）とUserクラスのオブジェクトの組み合わせmapを追加
                            map.put(i + "", user);
                            i++;
                        }
                        // 最後に新しいトークンのレコードを追加する
                        map.put(i + "", userToken);

                        Log.d(TAG, "onDataChange " + userToken.token);

                        // 作成したmapをDBにセットする
                        refUser.child(userId).setValue(map);
                        Log.d(TAG, "onDataChange setValue " + map);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }*/
}
