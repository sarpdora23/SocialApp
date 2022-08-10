package com.sinamekidev.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sinamekidev.socialapp.databinding.ActivityCreatePostBinding;
import com.sinamekidev.socialapp.models.Post;
import com.sinamekidev.socialapp.models.User;

import java.util.HashMap;
import java.util.UUID;

public class CreatePost extends AppCompatActivity {
    private ActivityCreatePostBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        init();
    }
    private void init(){
        binding.createPostBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreatePost.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }
        });
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        binding.createPostShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePost();
            }
        });
        firebaseFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentChange documentChange: task.getResult().getDocumentChanges()){
                        if(documentChange.getDocument().getId().equals(mAuth.getCurrentUser().getUid())){
                            user = documentChange.getDocument().toObject(User.class);
                        }
                    }
                }
            }
        });
    }
    private void sharePost(){
        String post_text = binding.createPostText.getText().toString();
        if(post_text.isEmpty()){
            Snackbar.make(binding.createPostLayout,"Text can not be empty",Snackbar.LENGTH_SHORT).show();
        }
        else{
            String post_uuid = UUID.randomUUID().toString();
            Post post = new Post(mAuth.getCurrentUser().getUid(),post_text,post_uuid);
            firebaseFirestore.collection("Posts").document(post_uuid).set(post)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                HashMap<String, Object> postData = new HashMap<>();
                                postData.put("date", FieldValue.serverTimestamp());
                                firebaseFirestore.collection("Posts").document(post_uuid).update(postData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            user.getUser_postList().add(post.getPost_uid());
                                            firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).update("user_postList",user.getUser_postList()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Intent intent = new Intent(CreatePost.this,MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        finish();
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                        }
                                       else{
                                            Snackbar.make(binding.createPostLayout,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }
                            else{
                                Snackbar.make(binding.createPostLayout,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}