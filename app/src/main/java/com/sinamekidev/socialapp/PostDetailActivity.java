package com.sinamekidev.socialapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
import com.sinamekidev.socialapp.adapters.CommentViewAdapter;
import com.sinamekidev.socialapp.databinding.ActivityPostDetailBinding;
import com.sinamekidev.socialapp.models.Comment;
import com.sinamekidev.socialapp.models.Post;
import com.sinamekidev.socialapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class PostDetailActivity extends AppCompatActivity {
    private ActivityPostDetailBinding binding;
    private Post post;
    private User postAuthor;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private CommentViewAdapter commentViewAdapter;
    private ArrayList<Comment> commentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().hide();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        binding.postCommentBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostDetailActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }
        });
        firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null){
                    String post_uid = getIntent().getExtras().getString("post_uid");
                    for(DocumentChange documentChange: value.getDocumentChanges()){
                        if(post_uid.equals(documentChange.getDocument().getId())){
                            post = documentChange.getDocument().toObject(Post.class);
                            Collections.reverse(post.getCommentList());
                            commentViewAdapter = new CommentViewAdapter(post.getCommentList());
                            binding.postCommentRecyclerview.setLayoutManager(new LinearLayoutManager(PostDetailActivity.this));
                            binding.postCommentRecyclerview.setAdapter(commentViewAdapter);
                            initLayoutComponents();
                        }
                    }
                }
                else{
                    Snackbar.make(binding.postCommentLayout,error.getMessage(),Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
    private void initLayoutComponents(){
        binding.commentPostAuthorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostDetailActivity.this,ProfileDetail.class);
                intent.putExtra("profile_uid",post.getAuthor());
                startActivity(intent);
            }
        });
        binding.commentPostsText.setText(post.getText());
        binding.commentPostLikeCount.setText(String.valueOf(post.getLikeList().size()));
        firebaseFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentChange documentChange :task.getResult().getDocumentChanges()){
                        if (documentChange.getDocument().getId().equals(post.getAuthor())){
                            postAuthor = documentChange.getDocument().toObject(User.class);
                            binding.commentPostAuthorName.setText(postAuthor.getUserInfo().getUsername());
                            if(postAuthor.getUserInfo().getProfile_url().equals("default")){
                                binding.commentPostAuthorImage.setImageResource(R.drawable.user);
                            }
                            else{
                                Picasso.get().load(postAuthor.getUserInfo().getProfile_url()).into(binding.commentPostAuthorImage);
                            }
                        }
                    }
                }
                else{
                    Toast.makeText(PostDetailActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
        if(post.getLikeList().contains(mAuth.getUid())){
            binding.commentPostLikeButton.setColorFilter(R.color.like_button_2);
        }
        else{
            binding.commentPostLikeButton.setColorFilter(Color.WHITE);
        }
        binding.commentPostLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(post.getLikeList().contains(mAuth.getUid())){
                    post.getLikeList().remove(mAuth.getUid());
                    binding.commentPostLikeButton.setColorFilter(Color.WHITE);
                }
                else{
                    post.getLikeList().add(mAuth.getUid());
                    binding.commentPostLikeButton.setColorFilter(R.color.like_button_2);
                }
                firebaseFirestore.collection("Posts").document(post.getPost_uid()).update("likeList",post.getLikeList()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            Snackbar.make(binding.postCommentLayout,task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        binding.shareComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment_str = binding.commentEditText.getText().toString();
                if (comment_str.isEmpty()){
                    Snackbar.make(binding.postCommentLayout,"Comment field can not be empty",Snackbar.LENGTH_SHORT).show();
                }
                else{
                    Comment comment = new Comment(mAuth.getUid(),comment_str);
                    post.getCommentList().add(comment);
                    firebaseFirestore.collection("Posts").document(post.getPost_uid()).update("commentList",post.getCommentList())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                commentViewAdapter.notifyDataSetChanged();
                                binding.commentEditText.setText("");
                            }
                            else{
                                Snackbar.make(binding.postCommentLayout,task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        firebaseFirestore.collection("Posts").document(post.getPost_uid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null){
                    post = value.toObject(Post.class);
                    commentViewAdapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(PostDetailActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}