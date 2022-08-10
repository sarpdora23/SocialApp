package com.sinamekidev.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sinamekidev.socialapp.adapters.PostsViewAdapters;
import com.sinamekidev.socialapp.databinding.ActivityProfileDetailBinding;
import com.sinamekidev.socialapp.models.Post;
import com.sinamekidev.socialapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileDetail extends AppCompatActivity {
    private ActivityProfileDetailBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private User profile_user;
    private ArrayList<Post> post_list = new ArrayList<>();
    private PostsViewAdapters adapter;
    private FirebaseAuth mAuth;
    private String user_uid;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().hide();
        user_uid = getIntent().getExtras().getString("profile_uid");
        firebaseFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentChange documentChange: task.getResult().getDocumentChanges()){
                        if(documentChange.getDocument().getId().equals(mAuth.getCurrentUser().getUid())){
                            user = documentChange.getDocument().toObject(User.class);
                            firebaseFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        for(DocumentChange documentChange: task.getResult().getDocumentChanges()){
                                            if(user_uid.equals(documentChange.getDocument().getId())){
                                                profile_user = documentChange.getDocument().toObject(User.class);
                                                init();
                                            }
                                        }
                                    }
                                    else{
                                        Snackbar.make(binding.profileDetailLayout,task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
                else{
                    Snackbar.make(binding.profileDetailLayout,task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void init(){
        binding.profileDetailName.setText(profile_user.getUserInfo().getUsername());
        adapter = new PostsViewAdapters(post_list,mAuth);
        if(profile_user.getUserInfo().getProfile_url().equals("default")){
            binding.profileDetailImage.setImageResource(R.drawable.user);
        }
        else{
            Picasso.get().load(profile_user.getUserInfo().getProfile_url()).into(binding.profileDetailImage);
        }
        if(mAuth.getCurrentUser().getUid().equals(user_uid)){
            binding.profileDetailFriends.setVisibility(View.INVISIBLE);
        }
        else{
            if(user.getFriends_list().contains(user_uid)){
                binding.profileDetailFriends.setImageResource(R.drawable.chat);
            }
            else{
                if(profile_user.getFriend_request_list().contains(mAuth.getCurrentUser().getUid())){
                    binding.profileDetailFriends.setImageResource(R.drawable.user_waiting);
                }
                else{
                    binding.profileDetailFriends.setImageResource(R.drawable.add_friend);
                }
            }
        }
        binding.profileDetailFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.getFriends_list().contains(user_uid)){
                    Intent intent = new Intent(ProfileDetail.this,MainActivity.class);
                    intent.putExtra("detail_message",user_uid);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }
                else{
                    if(!profile_user.getFriend_request_list().contains(mAuth.getCurrentUser().getUid())){
                        profile_user.getFriend_request_list().add(mAuth.getCurrentUser().getUid());
                        firebaseFirestore.collection("Users").document(user_uid).update("friend_request_list",profile_user.getFriend_request_list()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    binding.profileDetailFriends.setImageResource(R.drawable.user_waiting);
                                }
                                else{
                                    Snackbar.make(binding.profileDetailLayout,task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
        firebaseFirestore.collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentChange documentChange: task.getResult().getDocumentChanges()){
                        if(profile_user.getUser_postList().contains(documentChange.getDocument().getId())){
                            Post post = documentChange.getDocument().toObject(Post.class);
                            post_list.add(post);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        binding.profileDetailRecyclerview.setLayoutManager(new LinearLayoutManager(ProfileDetail.this));
        binding.profileDetailRecyclerview.setAdapter(adapter);
        binding.profileDetailBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileDetail.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }
        });
    }
}