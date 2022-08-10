package com.sinamekidev.socialapp.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sinamekidev.socialapp.MessageActivity;
import com.sinamekidev.socialapp.R;
import com.sinamekidev.socialapp.databinding.FriendLayoutBinding;
import com.sinamekidev.socialapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendViewAdapter extends RecyclerView.Adapter<FriendViewAdapter.MyViewHolder> {
    private ArrayList<String> friends_list;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(FriendLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String user_uid = friends_list.get(position);
        firebaseFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentChange documentChange: task.getResult().getDocumentChanges()){
                        if(user_uid.equals(documentChange.getDocument().getId())){
                            User user = documentChange.getDocument().toObject(User.class);
                            holder.binding.friendName.setText(user.getUserInfo().getUsername());
                            if(user.getUserInfo().getProfile_url().equals("default")){
                                holder.binding.friendImage.setImageResource(R.drawable.user);
                            }
                            else{
                                Picasso.get().load(user.getUserInfo().getProfile_url()).into(holder.binding.friendImage);
                            }
                        }
                    }
                }
            }
        });
        holder.binding.chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), MessageActivity.class);
                intent.putExtra("friend_uid",user_uid);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }
    public FriendViewAdapter(ArrayList<String> friends_list){
        this.friends_list = friends_list;
    }
    @Override
    public int getItemCount() {
        return friends_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private FriendLayoutBinding binding;
        public MyViewHolder(@NonNull FriendLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
