package com.example.a14942.smart_see;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private String a;
    private String b;
    /*接收发送定义的常量*/
    private SendThread sendthread;
    String receive_Msg;
    String l;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;

    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;
    /*****************************/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
         a = intent.getStringExtra("acc");
         b = intent.getStringExtra("pwd");
        /***************连接*****************/
        sendthread = new SendThread(a, b, mHandler);
        Thread1();
        new Thread().start();
        /**********************************/

        editText1 = (EditText) findViewById(R.id.edit1);
        editText2 = (EditText) findViewById(R.id.edit2);
        editText3 = (EditText) findViewById(R.id.edit3);
        editText4 = (EditText) findViewById(R.id.edit4);

        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String a1 = editText1.getText().toString();
                sendthread.send(a1);
            }

        });

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String a2 = editText2.getText().toString();
                sendthread.send(a2);
            }

        });

        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String a3 = editText3.getText().toString();
                sendthread.send(a3);
            }

        });

        button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String a4 = editText4.getText().toString();
                sendthread.send(a4);
            }

        });

    }

    private class FragmentAdapter extends FragmentPagerAdapter {
        List<Fragment> fragmentList = new ArrayList<Fragment>();

        public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }


    /*接收线程*******************************************************************************/
    /**
     * 开启socket连接线程
     */
    void Thread1(){
//        sendthread = new SendThread(mIp, mPort, mHandler);
        new Thread(sendthread).start();//创建一个新线程
    }

    Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            TextView text0 = (TextView)findViewById(R.id.ttv2);
            super.handleMessage(msg);
            if (msg.what == 0x00) {

                Log.i("mr_收到的数据： ", msg.obj.toString());
                receive_Msg = msg.obj.toString();
                l = receive_Msg;
                text0.setText(l);
            }
        }
    };
}
