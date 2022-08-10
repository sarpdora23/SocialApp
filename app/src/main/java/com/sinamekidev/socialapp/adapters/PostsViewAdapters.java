package com.sinamekidev.socialapp.adapters;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sinamekidev.socialapp.PostDetailActivity;
import com.sinamekidev.socialapp.ProfileDetail;
import com.sinamekidev.socialapp.R;
import com.sinamekidev.socialapp.databinding.PostLayoutBinding;
import com.sinamekidev.socialapp.models.Post;
import com.sinamekidev.socialapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostsViewAdapters extends RecyclerView.Adapter<PostsViewAdapters.MyViewHolder> {
    private ArrayList<Post> postList;
    private User author;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(PostLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        firebaseFirestore = FirebaseFirestore.getInstance();
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.binding.postsText.setText(post.getText());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PostDetailActivity.class);
                intent.putExtra("post_uid",post.getPost_uid());
                view.getContext().startActivity(intent);
            }
        });
        if(post.getLikeList().contains(mAuth.getUid())){
            holder.binding.postLikeButton.setColorFilter(R.color.like_button_2);
        }
        else{
            holder.binding.postLikeButton.setColorFilter(Color.WHITE);
        }
        firebaseFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                        author = queryDocumentSnapshot.toObject(User.class);
                        if(post.getAuthor().equals(queryDocumentSnapshot.getId())){
                            author = queryDocumentSnapshot.toObject(User.class);
                            holder.binding.postsAuthorName.setText(author.getUserInfo().getUsername());

                            if(author.getUserInfo().getProfile_url().equals("default")){
                                holder.binding.postsAuthorImage.setImageResource(R.drawable.user);
                            }
                            else{
                                Picasso.get().load(author.getUserInfo().getProfile_url()).into(holder.binding.postsAuthorImage);
                            }
                            return;
                        }
                    }
                }
                else{

                }
            }
        });
        holder.binding.postLikeCount.setText(String.valueOf(post.getLikeList().size()));
        holder.binding.postCommentCount.setText(String.valueOf(post.getCommentList().size()));
        holder.binding.postLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("DEBUG");
                if(post.getLikeList().contains(mAuth.getUid())){
                    post.getLikeList().remove(mAuth.getUid());
                    holder.binding.postLikeButton.setColorFilter(Color.WHITE);
                }
                else{
                    post.getLikeList().add(mAuth.getUid());
                    holder.binding.postLikeButton.setColorFilter(R.color.like_button_2);
                }
                firebaseFirestore.collection("Posts").document(post.getPost_uid()).update("likeList",post.getLikeList()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            notifyDataSetChanged();
                        }
                        else{
                            Snackbar.make(holder.binding.postlayoutLayout,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        holder.binding.postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PostDetailActivity.class);
                intent.putExtra("post_uid",post.getPost_uid());
                view.getContext().startActivity(intent);
            }
        });
        holder.binding.postsAuthorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goProfileDetail(view,post);
            }
        });
        holder.binding.postsAuthorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goProfileDetail(view,post);
            }
        });
    }
    private void goProfileDetail(View view,Post post){
        Intent intent = new Intent(view.getContext(), ProfileDetail.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("profile_uid",post.getAuthor());
        view.getContext().startActivity(intent);
    }
    public PostsViewAdapters(ArrayList<Post> postList,FirebaseAuth mAuth){
        this.postList = postList;
        this.mAuth = mAuth;
    }
    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private PostLayoutBinding binding;
        public MyViewHolder(@NonNull PostLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
