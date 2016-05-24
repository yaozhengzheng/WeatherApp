package com.yao.feicui.weatherapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    static ImageView iv;
    static MainActivity ma;
    static Handler handler = new Handler() {
        //此方法在主线程中调用，可以用来刷新ui
        public void handleMessage(android.os.Message msg) {
            //处理消息时，需要知道到底是成功的消息，还是失败的消息
            switch (msg.what) {
                case 1:
                    //把位图对象显示至Imageview
                    iv.setImageBitmap((Bitmap) msg.obj);
                    break;

                case 0:
                    Toast.makeText(ma, "请求失败", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.iv_show);
        ma = this;
    }

    public void click(View v) {
        Thread t = new Thread() {
            @Override
            public void run() {
                //下载图片
                //1.确定网址
                String path = "http://img3.yxlady.com/uploads/intro/20120524/1628190.jpg";
                try {
                    //2.把网址封装成URL对象
                    URL url = new URL(path);
                    //3.获取客户端和服务器的链接对象，但是此时还没有建立连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //4.对连接对象进行初始化
                    //设置请求方法，注意大写
                    conn.setRequestMethod("GET");
                    //设置连接超时
                    conn.setConnectTimeout(5000);
                    //设置读取超时
                    conn.setReadTimeout(5000);
                    //5.发送请求，与服务器建立链接
                    conn.connect();
                    //如果响应码为200，说明请求成功
                    if (conn.getResponseCode() == 200) {
                        //获取服务器响应头中的流，流里的数据就是客户端请求的数据
                        InputStream is = conn.getInputStream();
                        //读取出流里的数据，并构造成位图对象
                        Bitmap bm = BitmapFactory.decodeStream(is);
                        Message msg = new Message();
                        //消息对象可以携带数据
                        msg.obj = bm;
                        msg.what = 1;
                        //把消息发送至主线程的消息队列
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.run();
            }
        };
        t.start();
    }
}
