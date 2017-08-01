package com.example.chie.notifitest0429;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.R.attr.fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignInPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignInPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInPage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "flagment_SignInPage";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Authentication機能を使うのに必要
    private FirebaseAuth mAuth;
    //ログイン状態を追うためのリスナー
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static String uid;

    //ユーザが入力したIDとパスワードの末尾に連結する文字列
    private String domain = "@yawnchat.webapp";
    private String passcat = "00";

    private EditText editId;
    private EditText editPass;
    private String userId;
    private String email /*= "hoge@yawnchat.webapp"*/;
    private String password /*= "fugafuga"*/;
    private Button buttonSignIn;

    private OnFragmentInteractionListener mListener;

    public SignInPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignInPage.
     */
    // TODO: Rename and change types and number of parameters
    public static SignInPage newInstance(String param1, String param2) {
        SignInPage fragment = new SignInPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in_page, container, false);
        Log.d(TAG, "view inflated");
        editId = (EditText) view.findViewById(R.id.edit_text_id);
        userId = editId.getText().toString();
        editPass = (EditText) view.findViewById(R.id.edit_text_pass);
        buttonSignIn = (Button) view.findViewById(R.id.button_login);
        Log.d(TAG, "view setting");

        // 認証が終わった時（変更された時）に呼ばれるクロージャを設定
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            // 認証が変更された時に呼ばれる関数
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "AuthListener Changed");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // 認証が完了
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    // サインイン済みであれば初期画面へ遷移
                    if (user.getUid() != null) {
                        uid = user.getUid().toString();
                        Constants.UID = uid;
                        if (mListener != null) {
                            // 認証されたユーザ情報からeメールアドレスを取得して、頭の８文字をStartPageに渡す
                            String authUid = user.getEmail().substring(0, 8);
                            Log.d(TAG, "onCreateView authUid: " + authUid);
                            mListener.openStartPage(authUid);
                        }
                    }
                } else {
                    //サインアウト時
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick_login_button");
                userId = editId.getText().toString();
                email = userId.toLowerCase().concat(domain);
                password = editPass.getText().toString().concat(passcat);
                Log.d(TAG, "sign in with : " + email);
                Log.d(TAG, "sign in with : " + password);
                signIn(email,password);
            }
        });

        return view;
    }

    // Firebaseへのサインインを担う部分
    private void signIn(String email, final String password){

        Context context = getActivity().getApplicationContext();
        final SharedPreferences sp = context.getSharedPreferences("FCMessages", Context.MODE_PRIVATE);

        final String user = email.substring(0, 8);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            sp.edit().putString("PWD", password).commit();
                            sp.edit().putString("UID", user).commit();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            //Toast.makeText(this, "IDかパスワードが間違っています", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        Log.d(TAG,"onStart()");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
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
        Log.d(TAG, "onDetach()");
        mListener = null;
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
    // Activityとやりとりするためのインターフェース
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void openStartPage(String userId);
    }
}
