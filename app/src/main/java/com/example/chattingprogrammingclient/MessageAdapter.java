package com.example.chattingprogrammingclient;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private ArrayList<Data> list;

    public MessageAdapter(ArrayList<Data> list) {
        this.list = list;
    }



    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.message_list_item, parent, false);
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        holder.userID.setText(list.get(position).userId);
        holder.chatMsg.setText(list.get(position).msg);
        // holder.time.setText(list.get(position).time);
    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() :0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userID;
        TextView chatMsg;
        TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userID = itemView.findViewById(R.id.userId);
            chatMsg = itemView.findViewById(R.id.chatMessage);
            time = itemView.findViewById(R.id.time);
        }
    }

}
