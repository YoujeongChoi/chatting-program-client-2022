package com.example.chattingprogrammingclient;

import static android.content.ContentValues.TAG;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.time.LocalTime;

public class MainActivity extends AppCompatActivity {

    ArrayList<Data> list = new ArrayList();
    LocalTime now = LocalTime.now();
    MessageAdapter adapter;

    private Handler mHandler;
    Socket socket;
    PrintWriter sendWriter;
    String IpAddress;
    // private String IpAddress = "10.101.3.35";
    private int port = 8885;

    TextView main_top_userId;
    String UserID;
    ImageButton chatbutton;
    TextView chatView;
    EditText message;
    String sendmsg;
    String read;
    ImageButton backButton;


    int hour = 0 ;
    int min = 0;
    String time;

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

        list = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();

        mHandler = new Handler();

        UserID = intent.getStringExtra("username");
        IpAddress = intent.getStringExtra("ip");

        backButton = (ImageButton) findViewById(R.id.backButton);
        chatbutton = (ImageButton) findViewById(R.id.chatbutton);

        main_top_userId = (TextView) findViewById(R.id.main_top_userId);
        main_top_userId.setText(UserID);
        message = (EditText) findViewById(R.id.getMessageText);

        // chatView는 수정예정
        chatView = (TextView) findViewById(R.id.chatView);

        adapter = new MessageAdapter(list);
        RecyclerView recyclerView = findViewById(R.id.chatRv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Log.d(TAG, "순서확인용44444444");
        new Thread() {
            public void run() {
                Log.d(TAG, "순서확인용2222222");
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
                list = null;
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
                            Log.d(TAG, "순서확인용3333333");

                            hour = now.getHour();
                            min = now.getMinute();
                            time = hour + "시 " + min +"분";
                            list.add(new Data(UserID, sendmsg, time));
                            Log.d(TAG, "확인시작");
                            Log.d(TAG, "확인 list 사이즈 "+ list.size());
                            Log.d(TAG, "확인 -userid "+ UserID);
                            Log.d(TAG, "확인 -sendmsg "+ sendmsg);
                            Log.d(TAG, "확인 -hour "+ hour);
                            Log.d(TAG, "확인 list 사이즈 "+ list.get(0).msg);
                            adapter.notifyItemChanged(adapter.getItemCount() - 1, "click");
                            recyclerView.setAdapter(adapter);
                            // sendWriter.println(UserID +">"+ sendmsg);
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