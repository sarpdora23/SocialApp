package com.sinamekidev.socialapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinamekidev.socialapp.databinding.MessageLayoutBinding;
import com.sinamekidev.socialapp.models.MessageChild;

import java.util.ArrayList;

public class MessageViewAdapter extends RecyclerView.Adapter<MessageViewAdapter.MyViewHolder> {
    private ArrayList<MessageChild> messageChildren;
    private String current_uid;
    private String friend_uid;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(MessageLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MessageChild messageChild = messageChildren.get(position);
        if(messageChild.getAuthor().equals(current_uid)){
            holder.binding.friendMessage.setVisibility(View.INVISIBLE);
            holder.binding.currentMessage.setText(messageChild.getMessage());
        }
        else if(messageChild.getAuthor().equals(friend_uid)){
            holder.binding.currentMessage.setVisibility(View.INVISIBLE);
            holder.binding.friendMessage.setText(messageChild.getMessage());
        }
    }

    public MessageViewAdapter(ArrayList<MessageChild> messageChildren,String current_uid,String friend_uid){
        this.messageChildren = messageChildren;
        this.current_uid = current_uid;
        this.friend_uid = friend_uid;
    }
    @Override
    public int getItemCount() {
        return messageChildren.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private MessageLayoutBinding binding;
        public MyViewHolder(@NonNull MessageLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
