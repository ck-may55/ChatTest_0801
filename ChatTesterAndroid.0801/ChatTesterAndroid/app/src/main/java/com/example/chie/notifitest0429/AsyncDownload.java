package com.example.chie.notifitest0429;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by chie on 2017/08/09.
 */

//AsyncTaskを継承した、非同期処理のためのクラス
public class AsyncDownload extends AsyncTask<Uri.Builder, Integer, Bitmap> {

    private ImageView imageView;

    public AsyncDownload(ImageView imageView) {
        this.imageView = imageView;
    }

    //非同期で行う処理
    @Override
    protected Bitmap doInBackground(Uri.Builder... builder) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        Bitmap bitmap = null;

        try {
            URL url = new URL(builder[0].toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            inputStream = connection.getInputStream();

            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(connection != null){
                connection.disconnect();
            }
            try {
                if(inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    //非同期処理の終了後に呼び出される
    @Override
    protected void onPostExecute(Bitmap result) {

        this.imageView.setImageBitmap(result);
    }

}
