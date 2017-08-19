package com.example.chie.notifitest0429;

import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.util.Log;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


import com.bumptech.glide.Glide;
import com.example.chie.notifitest0429.ChatData;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by satoruishii on 2017/07/25.
 */

public class MessageListAdapter extends ArrayAdapter {
    private static final String TAG = "MessageListAdapter";

    private int mResource;
    private List<ChatData> mItems;
    private LayoutInflater mInflater;

    //2017/08/19追加
    private final int DELAY = 5000;
    private boolean isDownloadSuccess = false;
    //

    /**
     * コンストラクタ
     * @param context コンテキスト
     * @param resource リソースID
     * @param items リストビューの要素
     */
    public MessageListAdapter(Context context, int resource, List<ChatData> items) {
        super(context, resource, items);

        Log.d("MessageListAdapter", "construct!");

        mResource = resource;
        mItems = items;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView avator;
        TextView msgView;
        LinearLayout listItem;
        FrameLayout msgLayout;
        ImageView carrot_admin;
        ImageView carrot_user;

        //2017/08/08追加
        ImageView imgView;
        //

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder = null;
        //2017/08/19追加
        isDownloadSuccess=false;
        //

        //Log.d("MessageListAdapter", "getView " + convertView);
        view = mInflater.inflate(mResource, null);
        holder = new ViewHolder();
        holder.msgLayout = (FrameLayout) view.findViewById(R.id.message_layout);
        holder.msgView = (TextView) view.findViewById(R.id.message);
        holder.avator = (ImageView) view.findViewById(R.id.avator);
        holder.listItem = (LinearLayout)view.findViewById(R.id.list_item);
        holder.carrot_user = (ImageView)view.findViewById(R.id.carrot_user);
        holder.carrot_admin = (ImageView)view.findViewById(R.id.carrot_admin);

        //2017/08/08追加
        holder.imgView = (ImageView)view.findViewById(R.id.image_view);
        //

        view.setTag(holder);

        // リストビューに表示する要素を取得
        ChatData item = mItems.get(position);

        //担当者からのメッセージの表示
        if (item.userID.equals("susmedroot")) {
            holder.avator.setVisibility(View.VISIBLE);
            holder.avator.setImageResource(R.drawable.avatar_chat_natural);
            holder.carrot_admin.setVisibility(View.VISIBLE);
            holder.carrot_admin.setImageResource(R.drawable.carrot_admin);
            holder.carrot_user.setVisibility(View.GONE);

            LayoutParams lp = holder.msgLayout.getLayoutParams();
            MarginLayoutParams mlp = (MarginLayoutParams)lp;
            mlp.setMargins(mlp.leftMargin, 10, mlp.rightMargin, 10);

            //2017/08/09変更　msgTypeの判定をlong値に
            Log.d("Adapter", "msgType: " + item.msgType);

            /*
            if(item.msgType==null){
                holder.msgView.setVisibility(View.VISIBLE);
                holder.msgView.setText(item.text);
                holder.imgView.setVisibility(View.GONE);
            }*/
            if(ChatPage.MESSAGE_TYPE.TEXT.equals(item.msgType))
            {
                holder.msgView.setVisibility(View.VISIBLE);
                holder.msgView.setText(item.text);
                holder.imgView.setVisibility(View.GONE);
            }
            else if (/*ChatPage.MESSAGE_TYPE.IMAGE.equals(item.msgType)*/item.msgType==1) {
                holder.imgView.setVisibility(View.VISIBLE);
                holder.msgView.setVisibility(View.GONE);

                //2017/08/09追加
                //非同期でダウンロードを行う
/*
                Uri uri = Uri.parse(item.imageUrl);
                Uri.Builder builder = uri.buildUpon();
                AsyncDownload asyncDownload = new AsyncDownload(holder.imgView);
                asyncDownload.execute(builder);
*/

                //2017/08/17追加
                //GoogleColudStrageから画像をダウンロード＆表示
/*
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference imgRef = null;
                if(item.imageUrl.startsWith("gs://")){
                    imgRef = storage.getReferenceFromUrl(item.imageUrl);
                    Log.d(TAG, "imageRef:" + imgRef);
                    Glide.with(this.getContext())
                            .using(new FirebaseImageLoader())
                            .load(imgRef).into(holder.imgView);
                }


                else if(item.imageUrl.startsWith("https://")){
                    //imageUrlの中身がhttpから始まる場合
                    //非同期処理ダウンロードを呼び出す
                    Uri uri = Uri.parse(item.imageUrl);
                    Uri.Builder builder = uri.buildUpon();
                    AsyncDownload asyncDownload = new AsyncDownload(holder.imgView);
                    asyncDownload.execute(builder);

                }
*/

                //holder.imgView.setImageBitmap(getBitmapFromURL(item.imageUrl));
                /*
                    try {
                        Log.d("Aadpter", "url: " + item.imageUrl);
                        url = new URL(item.imageUrl);
                        // url = new URL("https://firebasestorage.googleapis.com/v0/b/yawnchat-919a4.appspot.com/o/OK9SqgiJd4elD3PP645JKnZVSRo2%2F-KqzMaVDFe1NaoURPr6U%2FLenna.png?alt=media&token=b1f2c4ad-3961-4ac5-a8c0-addb24e6c05b");
                        Log.d("Adapter", "url: " + url);
                        inputStream = url.openStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        holder.imgView.setImageBitmap(bitmap);
                        inputStream.close();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        holder.imgView.setImageResource(R.drawable.ic_yawn);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                */

                //2017/08/19追加
                //ダウンロード完了するまで繰り返す
                while (!isDownloadSuccess){
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference imgRef = null;
                    if(item.imageUrl.startsWith("gs://")){
                        imgRef = storage.getReferenceFromUrl(item.imageUrl);
                        Log.d(TAG, "imageRef:" + imgRef);
                        Glide.with(this.getContext())
                                .using(new FirebaseImageLoader())
                                .load(imgRef).into(holder.imgView);
                        isDownloadSuccess=true;
                }
                else if(item.imageUrl.startsWith("https://")){
                    Log.d(TAG, "image:" + item.imageUrl);
                    /*
                    Uri uri = Uri.parse(item.imageUrl);
                    Uri.Builder builder = uri.buildUpon();
                    AsyncDownload asyncDownload = new AsyncDownload(holder.imgView);
                    asyncDownload.execute(builder);
                    asyncDownload.setOnCallBack(new AsyncDownload.CallBackTask(){
                        @Override
                        public void CallBack(Bitmap result){
                            super.CallBack(result);
                            //非同期処理の実行終了を受け取るコールバック
                            //continue;
                        }
                    });
                    */

                    //
                    downloadImageByAsync(item.imageUrl, holder.imgView);
                    //DELAYミリ秒待ってから実行
                    try {
                        Thread.sleep(DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                    //

                }
            }
            }
            else {
                holder.msgView.setVisibility(View.VISIBLE);
                holder.msgView.setText(item.text);
                holder.imgView.setVisibility(View.GONE);
            }

        }
        //ユーザ送信メッセージの表示
        else {
            holder.listItem.setGravity(Gravity.RIGHT);
            LayoutParams lp = holder.msgLayout.getLayoutParams();

            if (holder.msgView.getText().length() > 70) {
                lp.width = 700;
            }
            MarginLayoutParams mlp = (MarginLayoutParams)lp;
            mlp.setMargins(mlp.leftMargin+150, 5, mlp.rightMargin-160, 5);

            //マージンを設定
            holder.msgLayout.setLayoutParams(mlp);
            holder.carrot_admin.setVisibility(View.GONE);

            //ユーザ側の吹き出し表示
            holder.carrot_user.setVisibility(View.VISIBLE);
            holder.carrot_user.setImageResource(R.drawable.carrot_user);

            //holder.msgLayout.setVisibility(View.INVISIBLE);

            //2017/08/08追加
            //メッセージ本文の表示
            holder.msgView.setText(item.text);
            //
        }

        //holder.msgView.setText(item.text);

        final LinearLayout litem = holder.listItem;

        //
        holder.listItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                litem.requestFocus();
                Log.d("MessageListAdapter", "setOnTouchListener");
                return false;
            }
        });

        return view;
    }
    void downloadImageByAsync(String imageUrl, ImageView imageView){
        //if(imageUrl.startsWith("gs"))
        Uri uri = Uri.parse(imageUrl);
        Uri.Builder builder = uri.buildUpon();
        AsyncDownload asyncDownload = new AsyncDownload(imageView);
        asyncDownload.execute(builder);
    }
}
