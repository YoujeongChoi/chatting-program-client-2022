package com.example.chattingprogrammingclient;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.ArrayList;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {


    private Handler mHandler;
    Socket socket;
    PrintWriter sendWriter;
    String IpAddress;
    // private String IpAddress = "10.101.3.35";
    private int port = 8885;

    TextView textView;
    String UserID;
    ImageButton chatbutton;
    TextView chatView;
    EditText message;
    String sendmsg;
    String read;
    ImageButton backButton;
    Long sendTime;




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
        Intent intent = getIntent();

        mHandler = new Handler();
        textView = (TextView) findViewById(R.id.textView);
        chatView = (TextView) findViewById(R.id.chatView);
        message = (EditText) findViewById(R.id.message);
        UserID = intent.getStringExtra("username");
        IpAddress = intent.getStringExtra("ip");
        textView.setText(UserID);
        chatbutton = (ImageButton) findViewById(R.id.chatbutton);

        // back
        backButton = (ImageButton) findViewById(R.id.backButton);

        new Thread() {
            public void run() {
                try {

                    InetAddress serverAddr = InetAddress.getByName(IpAddress);
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EnterActivity.class);
                IpAddress = null;
                UserID = null;
                startActivity(intent);
                finish();
            }
        });


        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmsg = message.getText().toString();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sendTime = System.currentTimeMillis();
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