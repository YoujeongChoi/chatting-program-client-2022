package com.example.chattingprogrammingclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    Intent intent = getIntent();
    private Handler mHandler;
    Socket socket;
    PrintWriter sendWriter;
    private String ip = intent.getStringExtra("ip");
    private int port = 8880;

    TextView textView;
    String UserID;
    Button chatbutton;
    TextView chatView;
    EditText message;
    String sendmsg;
    String read;


    @Override
    protected void onStop() {
        super.onStop();
        try {
            sendWriter.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        textView = (TextView) findViewById(R.id.textView);
        chatView = (TextView) findViewById(R.id.chatView);
        message = (EditText) findViewById(R.id.message);
        UserID = intent.getStringExtra("username");
        textView.setText(UserID);
        chatbutton = (Button) findViewById(R.id.chatbutton);


        new Thread() {
            public void run() {
                try {
                    InetAddress serverAddr = InetAddress.getByName(ip);
                    socket = new Socket(serverAddr, port);
                    sendWriter = new PrintWriter(socket.getOutputStream());
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while(true){
                        read = input.readLine();

                        System.out.println("TTTTTTTT"+read);
                        if(read!=null){
                            mHandler.post(new msgUpdate(read));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } }}.start();

        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmsg = message.getText().toString();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sendWriter.println(UserID +">"+ sendmsg);
                            sendWriter.flush();
                            message.setText("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    class msgUpdate implements Runnable{
        private String msg;
        public msgUpdate(String str) {this.msg=str;}

        @Override
        public void run() {
            chatView.setText(chatView.getText().toString()+msg+"\n");
        }
    }
}