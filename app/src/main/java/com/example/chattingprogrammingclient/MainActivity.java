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
import java.text.BreakIterator;
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
    // TextView chatView;
    EditText message;
    String sendmsg;
    String read;
    ImageButton backButton;
    RecyclerView recyclerView;
    TextView chatMessage;


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

        chatMessage = (TextView) findViewById(R.id.chatMessage);

        // chatView는 수정예정
//        chatView = (TextView) findViewById(R.id.chatView);

        adapter = new MessageAdapter(list);
        recyclerView = findViewById(R.id.chatRv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        new Thread() {
            public void run() {
                // 수신 스레드
                try {
                    InetAddress serverAddr = InetAddress.getByName(IpAddress);
                    socket = new Socket(serverAddr, port);
                    sendWriter = new PrintWriter(socket.getOutputStream());
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    System.out.println("확인!!!!!!!!!!!!" + input);
                    while(true){
//                        Log.d(TAG, "확인");
                        read = input.readLine();
                        String array[] = read.split(" ");
                        Log.d(TAG, "확인하고싶다..."+array[0]);
                        Log.d(TAG, "확인하고싶다..."+array[1]);
                        System.out.println("TTTTTTTT"+read);
                        if(read!=null){
                            mHandler.post(new msgUpdate(read));
                            // adapter.notifyItemChanged(adapter.getItemCount() - 1);
                            recyclerView.setAdapter(adapter);
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

        // 송신 스레드
        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmsg = message.getText().toString();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
//                            hour = now.getHour();
//                            min = now.getMinute();
//                            if (hour < 12 ) {
//                                time = "오전 "+ hour + "시 " + min +"분";
//                            } else {
//                                hour -= 12;
//                                time = "오후 "+ hour + "시 " + min +"분";
//                            }
//
                            list.add(new Data(UserID, sendmsg, time));
                            // adapter.notifyItemChanged(adapter.getItemCount() - 1);
                            recyclerView.setAdapter(adapter);
                            sendWriter.println(UserID +" "+ sendmsg);
//                            sendWriter.println(list.get(list.size()-1));
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
            hour = now.getHour();
            min = now.getMinute();
            if (hour < 12 ) {
                time = "오전 "+ hour + "시 " + min +"분";
            } else {
                hour -= 12;
                time = "오후 "+ hour + "시 " + min +"분";
            }
            String array[] = msg.split(" ");
            list.add(new Data(array[0], array[1], time));
            // chatMessage.setText(chatMessage.getText().toString()+msg+"\n");
        }
    }
}