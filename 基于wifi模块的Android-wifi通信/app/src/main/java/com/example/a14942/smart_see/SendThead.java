package com.example.a14942.smart_see;

/**
 * Created by 14942 on 2018/3/26.
 */

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

class SendThread implements Runnable {

    private String ip;
    private String port;
    BufferedReader in;
    PrintWriter out;
    Handler mainHandler;
    Socket s;
    private String receiveMsg;

    ArrayList<String> list = new ArrayList<String>();

    public SendThread(String ip,String port, Handler mainHandler) {
        this.ip = ip;
        this.port=port;
        this.mainHandler = mainHandler;
    }

    /**
     * 套接字的打开
     */
    void open(){
        try {
            s = new Socket(ip, Integer.parseInt(port));
            //in收单片机发的数据
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    s.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 套接字的关闭
     */
    void close(){
        try {
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        //创建套接字
        open();

        //BufferedReader
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(200);
                        close();
                        open();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    if (!s.isClosed()) {
                        if (s.isConnected()) {
                            if (!s.isInputShutdown()) {
                                try {
                                    Log.i("mr", "等待接收信息");

                                    char[] chars = new char[1024];
                                    int len = 0;
                                    while((len = in.read(chars)) != -1){
                                        System.out.println("收到的消息：  "+new String(chars, 0, len));
                                        receiveMsg = new String(chars, 0, len);

                                        Message msg=mainHandler.obtainMessage();
                                        msg.what=0x00;
                                        msg.obj=receiveMsg;
                                        mainHandler.sendMessage(msg);
                                    }

                                } catch (IOException e) {
                                    Log.i("mr", e.getMessage());
                                    try {
                                        s.shutdownInput();
                                        s.shutdownOutput();
                                        s.close();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                }
            }

        });
        thread.start();

        while (true) {

            //连接中
            if (!s.isClosed()&&s.isConnected()&&!s.isInputShutdown()) {

                // 如果消息集合有东西，并且发送线程在工作。
                if (list.size() > 0 && !s.isOutputShutdown()) {
                    out.println(list.get(0));
                    list.remove(0);
                }

                Message msg=mainHandler.obtainMessage();
                msg.what=0x01;
                mainHandler.sendMessage(msg);
            } else {
                //连接中断了
                Log.i("mr", "连接断开了");
                Message msg=mainHandler.obtainMessage();
                msg.what=0x02;
                mainHandler.sendMessage(msg);
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                try {
                    out.close();
                    in.close();
                    s.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }

    }

    public void send(String msg) {
        System.out.println("msg的值为：  " + msg);
        list.add(msg);
    }

}

