package com.example.chattingprogrammingclient;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    ArrayList<Data> list = new ArrayList();
    // LocalTime now = LocalTime.now();
    MessageAdapter adapter;
    RecyclerView recyclerView;


    private Handler mHandler;
    Socket socket;
    PrintWriter sendWriter;
    String IpAddress;
    // private String IpAddress = "10.101.3.35";
    private int port = 8887;

    String read;


    TextView main_top_userId;
    String UserID;
    ImageButton chatbutton;
    EditText message;
    String sendmsg;
    ImageButton backButton;
    TextView chatMessage;

//    int hour = 0 ;
//    int min = 0;
//    String time;

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


        list = new ArrayList<>();

        UserID = intent.getStringExtra("username");
        IpAddress = intent.getStringExtra("ip");

        backButton = (ImageButton) findViewById(R.id.backButton);
        chatbutton = (ImageButton) findViewById(R.id.chatbutton);

        main_top_userId = (TextView) findViewById(R.id.main_top_userId);
        main_top_userId.setText(UserID);
        message = (EditText) findViewById(R.id.getMessageText);

        chatMessage = (TextView) findViewById(R.id.chatMessage);

        adapter = new MessageAdapter(list);
        recyclerView = findViewById(R.id.chatRv);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        new Thread() {
            public void run() {
                // 수신 스레드
                try {
                    InetAddress serverAddr = InetAddress.getByName(IpAddress);
                    socket = new Socket(serverAddr, port);
                    sendWriter = new PrintWriter(socket.getOutputStream());
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while(true){
//                        Log.d(TAG, "확인");
                        read = input.readLine();
                        String array[] = read.split(" ");
                        Log.d(TAG, "확인용 - id: "+array[0]);
                        Log.d(TAG, "확인용 - 메세지: "+array[1]);
                        System.out.println("<Server> from Client: "+read);
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
                            sendWriter.println(UserID +" "+ sendmsg);
                            list.add(new Data(UserID, sendmsg));
                            // recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
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

    public class msgUpdate implements Runnable{
        private String msg;
        public msgUpdate(String str) {this.msg=str;}

        @Override
        public void run() {
//            hour = now.getHour();
//            min = now.getMinute();
//            if (hour < 12 ) {
//                time = "오전 "+ hour + "시 " + min +"분";
//            } else {
//                hour -= 12;
//                time = "오후 "+ hour + "시 " + min +"분";
//            }
            String array[] = msg.split(" ");
            list.add(new Data(array[0], array[1]));
            //
            // recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            // chatMessage.setText(chatMessage.getText().toString()+msg+"\n");
        }
    }


}