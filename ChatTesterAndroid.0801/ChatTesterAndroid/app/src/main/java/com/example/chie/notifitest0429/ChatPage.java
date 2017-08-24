package com.example.chie.notifitest0429;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.chie.notifitest0429.MessageListAdapter;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatPage extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final int DELAY = 5000;

    private String userId;
    private OnFragmentInteractionListener mListener;

    //private ListView chatView;
    private Button sendButton;
    private EditText messageText;

    private FirebaseDatabase database;

    //DBに登録する内容
    private String message;
    private String mPhotoUrl = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg";
    private String mToUserId = "susmedroot";

    //2017/08/22追加
    private long macTime;
    //private boolean firstInit;

    //
    private MessageListAdapter adapter = null;
    private boolean isend = false;

    private List<ChatData>messages = new ArrayList<ChatData>();

    // 2017/08/03追加　担当者からのメッセージであるかどうかを保持
    boolean fromRoot = false;

    //2017/08/09追加　メッセージのデータタイプ列挙型
    public enum MESSAGE_TYPE {TEXT, IMAGE}
    //

    //2017/08/24追加　onResume()に渡すためのListViewを保持
    private ListView chatView2;
    //

    public ChatPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatPage.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatPage newInstance(String param1, String param2) {
        ChatPage fragment = new ChatPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        this.userId = getArguments().getString("USER_ID").toLowerCase();
        Log.d("ChatPage", "onCreate " + this.userId);

        //
        //firstInit = true;
        //

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_chat_page, container, false);

        Log.d("ChatPage", "onCreateView 1");

        // ListViewを使ったチャットページの表示
        View view = inflater.inflate(R.layout.fragment_chat_page, container, false);
        final ListView chatView = (ListView)view.findViewById(R.id.list_chat);

        //2017/08/24追加　onResume()に渡すためchatViewの内容を保持
        chatView2 =chatView;
        //

        sendButton = (Button) view.findViewById(R.id.send_button);
        messageText = (EditText) view.findViewById(R.id.message_edit);

        // 初期化
        initChatView(chatView);
        //int itemCount = chatView.getCount();
        //chatView.setSelection(itemCount - 1);
        //Log.d("ChatPage", "setSelection after init");

        //2017/08/22追加
        chatView.post(new Runnable() {
            @Override
            public void run() {
                int itemCount = chatView.getCount();
                //Log.d("ChatPage", "setSelection1");
                //chatView.setSelection(itemCount - 1);
            }
        });
        //

        messageText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                Log.d("ChatViewFragment", "setTextareaButton");
                // EditTextのフォーカスが外れた場合
                if (hasFocus == false) {
                    // ソフトキーボードを非表示にする
                    Context context = getActivity().getApplicationContext();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        //送信ボタンでチャットDBとListViewに追加
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = messageText.getText().toString();
                //2017/08/22追加
                macTime = (long) com.example.chie.notifitest0429.DateUtils.getTimeNumberFromDate(new Date());
                sendToDB(userId,message,macTime);
                //
                messageText.getEditableText().clear();
                initChatView(chatView);
                //chatView.requestFocus();
            }
        });

        // 戻るボタンのセット
        setBackButton();

        // DELAYミリ秒毎に実行する

        final Handler _handler = new Handler();
        _handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //
                initChatView(chatView);
                Log.d("ChatPage", "postDelayed");
                if (isend == false) {
                    _handler.postDelayed(this, DELAY);
                }
            }
        }, DELAY);

        return view;
    }


    /**
     * ListView内の初期化。
     */
    private void initChatView(final ListView chatView) {
        database = FirebaseDatabase.getInstance();
        final DatabaseReference refChat = database.getReference("messages");
        //ユーザと担当者とのやり取りをListViewに表示

        refChat.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //Log.d("ChatPage", "getDataFromRemote ChatData!!! ");
                        List<ChatData>newMessages = new ArrayList<ChatData>();
                        for (DataSnapshot msg : dataSnapshot.getChildren()) {
                            String suid = (String)(msg.child("userID").getValue());
                            //Log.d("ChatPage", "suid is " + suid + " : " + userId);
                            if (suid == null) {
                                continue;
                            }
                            //ユーザから送信したメッセージ
                            if (suid.equals(userId)) {
                                ChatData chatMsg = new ChatData();
                                chatMsg.text = (String)(msg.child("text").getValue());
                                chatMsg.userID = (String)(msg.child("userID").getValue());

                                //2017/08/03追加
                                fromRoot = false;

                                newMessages.add(chatMsg);
                            }
                            //担当者から受信したメッセージ
                            else if (suid.equals("susmedroot") && msg.child("toUserid").getValue().equals(userId)) {
                                ChatData chatMsg = new ChatData();
                                //chatMsg.text = (String) (msg.child("text").getValue());
                                chatMsg.userID = (String)(msg.child("userID").getValue());

                                //2017/08/09変更
                                /*msgTypeに子を持つ新しいデータの場合*/
                                if(msg.hasChild("msgType")) {
                                    chatMsg.msgType = (long)(msg.child("msgType").getValue());
                                    //msgTypeがtext(0)のとき
                                    if(/*MESSAGE_TYPE.TEXT.equals(chatMsg.msgType)*/ chatMsg.msgType==0)
                                        chatMsg.text = (String)(msg.child("text").getValue());

                                    //msgTypeがimage(1)のとき
//                                    else if(/*MESSAGE_TYPE.IMAGE.equals(chatMsg.msgType)*/chatMsg.msgType==1) {
//                                        chatMsg.imageUrl = (String) (msg.child("imageUrl").getValue());
//                                        Log.d("ChatPage", "imageUrl: " + chatMsg.imageUrl);
//                                    }
                                    //
                                    //2017/08/22修正
                                    //msgTypeが１かつ、画像URLがgs://始まりかをチェック
                                    else if (/*MESSAGE_TYPE.IMAGE.equals(chatMsg.msgType)*/chatMsg.msgType == 1
                                            && msg.child("imageUrl").getValue().toString().startsWith("gs://")) {
                                        chatMsg.imageUrl = (String) (msg.child("imageUrl").getValue());
                                        //Log.d("ChatPage", "imageUrl: " + chatMsg.imageUrl);
                                    }
                                    else {
                                        Log.d("ChatPage", "Url started with https!!");
                                        //Log.d("ChatPage", "imageUrl: " + chatMsg.imageUrl);
                                        return;
                                    }
                                    //

                                }
                                /*以前のデータの場合*/
                                else {
                                    chatMsg.text = (String)(msg.child("text").getValue());
                                }
                                //

                                //2017/08/03追加
                                fromRoot = true;

                                newMessages.add(chatMsg);
                            }
                        }

                        //Log.d("ChatPage", "messages size: " + newMessages.size());

                        if (adapter == null) {
                            adapter = new MessageListAdapter(getActivity(), R.layout.list_chat, newMessages);
                            chatView.setAdapter(adapter);
                            Log.d("ChatPage", "setSelection if adapter is null");
                            chatView.setSelection(chatView.getCount() - 1);
                        }
                        else {
                            //Log.d("ChatPage", "update adapter");
                            int diff = newMessages.size() - messages.size();
                            int pos = newMessages.size() - diff;

                            //2017/08/03追加
                            //ListViewの更新をデータが上書きされた時のみに行う
                            if(diff>0){
                            //
                                for (int i = 0; i < diff; i++) {
                                    //Log.d("ChatPage", "add adapter " + newMessages.get(pos + i));
                                    adapter.add(newMessages.get(pos + i));
                                }
                                chatView.setAdapter(adapter);

                                //アプリがフォアグラウンド中にメッセージが更新
                                Log.d("ChatPage", "setSelection if diff>0 ");
                                chatView.setSelection(chatView.getCount()-1);

                                adapter.notifyDataSetChanged();

                                //2017/08/03追加　
                                //担当者からのメッセージであれば通知音を再生（ユーザ端末のデフォルトのもの）
                                if(fromRoot) {
                                    Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
                                    Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), defaultRingtoneUri);
                                    if (ringtone != null) ringtone.play();
                                }
                                //Log.d("ChatPage","setSelection onDatachange");
                                //chatView.setSelection(chatView.getCount()-1);
                                //chatView.setSelection(chatView.getFirstVisiblePosition());
                                //
                            }
                        }
                        messages = newMessages;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("FirebaseManager", "getData:onCancelled", databaseError.toException());
                    }

                }//
        );
        /*
        if(firstInit) {
            //Log.d("ChatPage", "setSelection for firstInit");
              //chatView.setSelection(chatView.getCount()-1);
            firstInit = false;
        }
        */

    }

    /**
     * FirebaseDBのmessages下にメッセージの内容を登録
     */
    private void sendToDB(String userId, String message, long time){
        database = FirebaseDatabase.getInstance();

        final DatabaseReference refChat = database.getReference("messages");
        final ChatData chatData = new ChatData();
        chatData.photoUrl = mPhotoUrl;
        chatData.text = message;
        chatData.toUserid = mToUserId;
        chatData.userID = userId.toLowerCase();

        //2017/08/08追加
        //ユーザが送信したメッセージのmsgTypeはすべてTEXT(0)
        chatData.msgType = MESSAGE_TYPE.TEXT.ordinal();
        chatData.imageUrl = null;
        //

        //2017/08/22追加
        //ユーザが送信した時刻を付与
        chatData.time = time;
        //
        refChat.push().setValue(chatData);
    }

    /**
     * ListViewに送信メッセージを表示
     */
/*
    //ListAdapterで対応できているためコメントアウト
    private void addToList(String message){

    }
*/
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onBack();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        isend = true;
    }

    //2017/08/24追加　バックグラウンドから復帰時に最下行までスクロールする
    @Override
    public void onResume() {
        super.onResume();
        chatView2.setSelection(chatView2.getCount()-1);

    }
    //

    private void setBackButton() {
        android.widget.Button left_button = (Button) getActivity().findViewById(R.id.left_back_button);

        left_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("ChatPage", "onClick back button");

                if (mListener != null) {
                    if (isend == false)
                        isend = true;
                    //
                    mListener.onBack();
                }
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onBack();
        void onFragmentInteraction(Uri uri);
    }
}
