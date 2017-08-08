package com.example.chie.notifitest0429;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


import com.example.chie.notifitest0429.ChatData;

/**
 * Created by satoruishii on 2017/07/25.
 */

public class MessageListAdapter extends ArrayAdapter {
    private int mResource;
    private List<ChatData> mItems;
    private LayoutInflater mInflater;

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

            //2017/08/08追加
            Log.d("Adapter", "msgType: " + item.msgType);

            URL url;
            InputStream inputStream;
            if(item.msgType == null){
                holder.msgView.setVisibility(View.VISIBLE);
                holder.msgView.setText(item.text);
                holder.imgView.setVisibility(View.GONE);
            }
            else if(item.msgType.equals("text"))
            {
                holder.msgView.setVisibility(View.VISIBLE);
                holder.msgView.setText(item.text);
                holder.imgView.setVisibility(View.GONE);
            }
            else if (item.msgType.equals("image")) {
                    holder.imgView.setVisibility(View.VISIBLE);
                    holder.msgView.setVisibility(View.GONE);
                    try {
                        url = new URL(item.imageUrl);
                        inputStream = url.openStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        holder.imgView.setImageBitmap(bitmap);
                        inputStream.close();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            //



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

}
