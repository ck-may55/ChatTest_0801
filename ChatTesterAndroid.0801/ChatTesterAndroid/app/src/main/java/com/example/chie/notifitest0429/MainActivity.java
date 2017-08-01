package com.example.chie.notifitest0429;

import android.app.Activity;
import android.app.FragmentTransaction;
//

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
//

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends Activity implements SignInPage.OnFragmentInteractionListener,
        StartPage.OnFragmentInteractionListener,
        ChatPage.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";

    private boolean updatedToken = false;
    private boolean messageReceived = false;

    private final String UPDATED_TOKEN = "UpdatedToken";
    private final String RECEIVED_MESSAGE = "ReceivedMessage";

    //ログイン成功時に取得したUIDを保存
    public static String uid;

    //Authentication機能を使うのに必要
    private FirebaseAuth mAuth;

    //ログイン状態を追うためのリスナー
    private FirebaseAuth.AuthStateListener mAuthListener;

    private TextView textFlag;
    private TextView textToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        boolean fromTray = false;

        //
        IntentFilter filter = new IntentFilter(RECEIVED_MESSAGE);
        filter.addAction(UPDATED_TOKEN);

        // MyFirebaseMessagingServiceからのLocalBroadcasterを受け取るための登録
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver), filter);


        Log.d(TAG, "onCreate");

        // 起動直後にサインインfragmentに遷移
        openSignInPage();

        // 起動がアイコンからか、システムトレイ経由かどうかをチェック
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
                // システムトレイ経由だったらfromTrayをtrueにセットする
                fromTray = true;
            }
            Log.d(TAG, "getExtras() is not null !!");
        }
        // 初期画面をオープン
        //openStartPage(fromTray);
    }

    private void openSignInPage() {
        Log.d(TAG, "openSignInPage");
        setContentView(R.layout.activity_main);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        SignInPage signInPage = new SignInPage();
        transaction.add(R.id.container, signInPage);
        transaction.commit();
    }


    /**
     *　　初期画面のFragmentを開く
     */
    public void openStartPage(String userId) {

        Log.d("MainActivity", "openStart 1");
        // 初期画面のFragmentを開くトランザクション
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // 初期画面Fragmentの作成とセット
        StartPage startPage = new StartPage();
        transaction.replace(R.id.container, startPage);
        // トランザクションのコミット
        transaction.commit();

        // 起動がシステムトレイ（通知）経由かどうかを初期画面Fragmentに伝える
        Bundle bundle = new Bundle();
        // SignInPageでのユーザIDも伝える
        bundle.putString("USER_ID", userId);
        startPage.setArguments(bundle);

        Log.d("MainActivity", "openStart 2");
    }

    /**
     *　　チャット画面のFragmentを開く
     */
    public void openChat(String userId) {

        //setContentView(R.layout.activity_main);

        Log.d("MainActivity", "openChat 1");

        //　チャットページFragmentへの切り替え
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        ChatPage chatPage = new ChatPage();
        transaction.replace(R.id.container, chatPage);
        // 初期画面に戻るためのスタックをセットする
        transaction.addToBackStack(null);
        transaction.commit();

        Log.d("MainActivity", "openChat 2");

        Bundle bundle = new Bundle();
        // SignInPageでのユーザIDも伝える
        bundle.putString("USER_ID", userId);
        chatPage.setArguments(bundle);

        //  画面トップのツールバーの表示を変更
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText("サポート・チャット");
        Button backButton = (Button) findViewById(R.id.left_back_button);
        backButton.setVisibility(View.VISIBLE);
        Button nextButton = (Button) findViewById(R.id.right_next_button);
        nextButton.setVisibility(View.GONE);

        Log.d("MainActivity", "openChat 3");
    }

    // MyFirebaeMessagingServiceからの通知の受信クロージャのセット
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        // LocalBroadcasterを受信した時に実行される関数
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "MessageReceiver onReceived " + intent.getAction());

            //
            if (UPDATED_TOKEN.equals(intent.getAction())) {
                Log.d(TAG, "onReceive UPDATED_TOKEN!");
                setUpdatedToken(true);
            }
            if (RECEIVED_MESSAGE.equals(intent.getAction())) {
                setMessageReceived(true);
            }
        }
    };

    /**
    // MyFirebaeMessagingServiceからの通知の受信クロージャのセット
    private BroadcastReceiver mUpdatedToken = new BroadcastReceiver() {

        // LocalBroadcasterを受信した時に実行される関数
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "UpdatedToken onReceived");
            setUpdatedToken(true);
            // チャット画面を開くボタンを表示する
        }
    };
        */

    //
    public void setMessageReceived(boolean onoff) {
        this.messageReceived = onoff;
    }
    public boolean getMessageReceived() {
        return this.messageReceived;
    }
    //
    public void setUpdatedToken(boolean onoff) {
        this.updatedToken = onoff;
    }
    public boolean getUpdatedToken() {
        return this.updatedToken;
    }


    @Override
    public void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);
        Log.d(TAG,"onStart()");
        /**
        // MyFirebaseMessagingServiceからのLocalBroadcasterを受け取るための登録
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("ReceivedMessage")
        );

        LocalBroadcastManager.getInstance(this).registerReceiver((mUpdatedToken),
                new IntentFilter("UpdatedToken")
        );
         */
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        // LocalBroadcasterのリリース
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        // Updated Token LocalBroadcasterのリリース
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mUpdatedToken);
    }

    @Override
    public void onFragmentInteraction(Uri uri){

    }

    // チャット画面から初期画面に戻る処理
    public void onBack() {

        // スタックを一つ戻して（pop）初期画面に戻る
        getFragmentManager().popBackStack();

        // ツールバーの設定
        Button backButton = (Button) findViewById(R.id.left_back_button);
        backButton.setVisibility(View.GONE);
        Button nextButton = (Button) findViewById(R.id.right_next_button);
        nextButton.setVisibility(View.VISIBLE);
    }
}