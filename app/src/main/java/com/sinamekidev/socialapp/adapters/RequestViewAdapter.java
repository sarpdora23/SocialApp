package com.sinamekidev.socialapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sinamekidev.socialapp.R;
import com.sinamekidev.socialapp.databinding.WaitingRequestLayoutBinding;
import com.sinamekidev.socialapp.models.MessageChild;
import com.sinamekidev.socialapp.models.MessageParent;
import com.sinamekidev.socialapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RequestViewAdapter extends RecyclerView.Adapter<RequestViewAdapter.MyViewHolder> {
    private ArrayList<String> request_list;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private User currentUser;
    private User user;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(WaitingRequestLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       String user_uid = request_list.get(position);
       firebaseFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentChange documentChange: task.getResult().getDocumentChanges()){
                        if (documentChange.getDocument().getId().equals(user_uid)){
                            user = documentChange.getDocument().toObject(User.class);
                            holder.binding.waitReqName.setText(user.getUserInfo().getUsername());
                            if(user.getUserInfo().getProfile_url().equals("default")){
                                holder.binding.circleImageView.setImageResource(R.drawable.user);
                            }
                            else{
                                Picasso.get().load(user.getUserInfo().getProfile_url()).into(holder.binding.circleImageView);
                            }
                        }
                    }
                }
                else{
                    Snackbar.make(holder.itemView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                }
           }
       });
       holder.binding.acceptButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               currentUser.getFriend_request_list().remove(user_uid);
               currentUser.getFriends_list().add(user_uid);
               user.getFriends_list().add(mAuth.getCurrentUser().getUid());
               createMessageRoom(user_uid,view);
           }
       });
       holder.binding.declineButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               currentUser.getFriend_request_list().remove(user_uid);
               updateUsers(user_uid);
           }
       });
    }
    private void updateUsers(String user_uid){
        firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).update("friends_list",currentUser.getFriends_list());
        firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).update("friend_request_list",currentUser.getFriend_request_list());
        firebaseFirestore.collection("Users").document(user_uid).update("friends_list",user.getFriends_list()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    notifyDataSetChanged();
                }
            }
        });
    }
    private void createMessageRoom(String new_friend,View view){
        String message_uid = UUID.randomUUID().toString();
        MessageParent messageParent = new MessageParent(mAuth.getCurrentUser().getUid(),new_friend);
        firebaseFirestore.collection("Message Rooms").document(message_uid).set(messageParent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Snackbar.make(view,"Succesfully added",Snackbar.LENGTH_SHORT).show();
                    updateUsers(new_friend);
                    HashMap<String, Object> postData = new HashMap<>();
                    postData.put("date", FieldValue.serverTimestamp());
                    firebaseFirestore.collection("Message Rooms").document(message_uid).update("date",postData);
                }
            }
        });
    }
    public RequestViewAdapter(ArrayList<String> request_list,User currentUser){
        this.request_list = request_list;
        this.currentUser = currentUser;
    }
    @Override
    public int getItemCount() {
        return request_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private WaitingRequestLayoutBinding binding;
        public MyViewHolder(@NonNull WaitingRequestLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
