package com.sinamekidev.socialapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sinamekidev.socialapp.adapters.MessageViewAdapter;
import com.sinamekidev.socialapp.databinding.ActivityMessageBinding;
import com.sinamekidev.socialapp.models.MessageChild;
import com.sinamekidev.socialapp.models.MessageParent;
import com.sinamekidev.socialapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {
    private ActivityMessageBinding binding;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String room_uid;
    private User current_user;
    private User friend_user;
    private String friend_uid;
    private MessageViewAdapter adapter;
    private MessageParent messageParent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        friend_uid = getIntent().getExtras().getString("friend_uid");
        init1();
        binding.messageBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }
        });
        binding.messageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.messageEditText.getText().toString();
                if(!message.isEmpty()){
                    MessageChild messageChild = new MessageChild(mAuth.getCurrentUser().getUid(),message);
                    messageParent.getMessage_List().add(messageChild);
                    firebaseFirestore.collection("Message Rooms").document(room_uid).update("message_List",messageParent.getMessage_List());
                    binding.messageEditText.setText("");
                    adapter.notifyDataSetChanged();
                    HashMap<String, Object> postData = new HashMap<>();
                    postData.put("date", FieldValue.serverTimestamp());
                    firebaseFirestore.collection("Message Rooms").document(room_uid).update("date",postData);
                }
            }
        });
    }
    private void init1(){
        firebaseFirestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                    if(documentChange.getDocument().getId().equals(mAuth.getCurrentUser().getUid())){
                        current_user = documentChange.getDocument().toObject(User.class);
                    }
                    else if(documentChange.getDocument().getId().equals(friend_uid)){
                        friend_user = documentChange.getDocument().toObject(User.class);
                    }
                }
                init2();
            }
        });
    }
    private void init2(){

        if(friend_uid != null){
            firebaseFirestore.collection("Message Rooms").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                        messageParent = documentChange.getDocument().toObject(MessageParent.class);
                        if(messageParent.getUser_list().contains(friend_uid) && messageParent.getUser_list().contains(mAuth.getCurrentUser().getUid())){
                            room_uid = documentChange.getDocument().getId();
                            ArrayList<MessageChild> messageList = messageParent.getMessage_List();
                            adapter = new MessageViewAdapter(messageList,mAuth.getCurrentUser().getUid(),friend_uid);
                            binding.messageRw.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                            binding.messageRw.setAdapter(adapter);
                            init3();
                        }
                    }
                }
            });
        }
    }
    private void init3(){
        binding.messageFriendName.setText(friend_user.getUserInfo().getUsername());
        if(friend_user.getUserInfo().equals("default")){
            binding.messageFriendImage.setImageResource(R.drawable.user);
        }
        else{
            Picasso.get().load(friend_user.getUserInfo().getProfile_url()).into(binding.messageFriendImage);
        }
        firebaseFirestore.collection("Message Rooms").document(room_uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null){
                    messageParent = value.toObject(MessageParent.class);
                    ArrayList<MessageChild> messageList = messageParent.getMessage_List();
                    adapter = new MessageViewAdapter(messageList,mAuth.getCurrentUser().getUid(),friend_uid);
                    binding.messageRw.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                    binding.messageRw.setAdapter(adapter);

                }
                else{
                    Toast.makeText(MessageActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}