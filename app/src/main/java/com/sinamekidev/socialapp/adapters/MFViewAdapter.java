package com.sinamekidev.socialapp.adapters;

import static android.os.Build.VERSION_CODES.R;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sinamekidev.socialapp.MessageActivity;
import com.sinamekidev.socialapp.R;
import com.sinamekidev.socialapp.databinding.LastMessageLayoutBinding;
import com.sinamekidev.socialapp.models.MessageParent;
import com.sinamekidev.socialapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MFViewAdapter extends RecyclerView.Adapter<MFViewAdapter.LastViewHolder> {
    private ArrayList<MessageParent> messageParents;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String friend_uid;
    @NonNull
    @Override
    public LastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LastViewHolder lastViewHolder = new LastViewHolder(LastMessageLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        return lastViewHolder;
    }
    public MFViewAdapter(ArrayList<MessageParent> messageParents){
        this.messageParents = messageParents;
    }
    @Override
    public void onBindViewHolder(@NonNull LastViewHolder holder, int position) {
        MessageParent messageParent = messageParents.get(position);
        for(String user_uid: messageParent.getUser_list()){
            if (!user_uid.equals(mAuth.getCurrentUser().getUid())){
                firebaseFirestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()) {
                            if(documentChange.getDocument().getId().equals(user_uid)){
                                User user = documentChange.getDocument().toObject(User.class);
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(messageParent.getUser_list().get(0).equals(mAuth.getCurrentUser().getUid())){
                                            friend_uid = messageParent.getUser_list().get(1);
                                        }
                                        else{
                                            friend_uid = messageParent.getUser_list().get(0);
                                        }
                                        Intent intent = new Intent(view.getContext(), MessageActivity.class);
                                        intent.putExtra("friend_uid",friend_uid);
                                        view.getContext().startActivity(intent);
                                    }
                                });
                                holder.binding.messageFragmentFriendName.setText(user.getUserInfo().getUsername());
                                if(user.getUserInfo().getProfile_url().equals("default")){
                                    holder.binding.messageFragmentFriendImage.setImageResource(com.sinamekidev.socialapp.R.drawable.user);
                                }
                                else{
                                    Picasso.get().load(user.getUserInfo().getProfile_url()).into(holder.binding.messageFragmentFriendImage);
                                }
                                if(messageParent.getMessage_List().size() == 0){
                                    holder.binding.messageFragmentLastMessage.setText("");
                                }
                                else{
                                    holder.binding.messageFragmentLastMessage.setText(messageParent.getMessage_List().get(messageParent.getMessage_List().size() -1).getMessage());
                                }
                            }
                        }
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return messageParents.size();
    }

    public class LastViewHolder extends RecyclerView.ViewHolder{
        private LastMessageLayoutBinding binding;
        public LastViewHolder(@NonNull LastMessageLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
