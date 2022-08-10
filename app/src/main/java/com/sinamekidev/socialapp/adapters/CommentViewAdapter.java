package com.sinamekidev.socialapp.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sinamekidev.socialapp.ProfileDetail;
import com.sinamekidev.socialapp.R;
import com.sinamekidev.socialapp.databinding.CommentLayoutBinding;
import com.sinamekidev.socialapp.models.Comment;
import com.sinamekidev.socialapp.models.Post;
import com.sinamekidev.socialapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentViewAdapter extends RecyclerView.Adapter<CommentViewAdapter.MyViewHolder> {
    private ArrayList<Comment> comments;
    private FirebaseFirestore firebaseFirestore;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(CommentLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        firebaseFirestore = FirebaseFirestore.getInstance();
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.binding.commentText.setText(comment.getComment());
        firebaseFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentChange documentChange: task.getResult().getDocumentChanges()){
                        if(documentChange.getDocument().getId().equals(comment.getPerson_uid())){
                            User user = documentChange.getDocument().toObject(User.class);
                            holder.binding.commentAuthorName.setText(user.getUserInfo().getUsername());
                            if(user.getUserInfo().getProfile_url().equals("default")){
                                holder.binding.commentAuthorImage.setImageResource(R.drawable.user);
                            }
                            else{
                                Picasso.get().load(user.getUserInfo().getProfile_url()).into(holder.binding.commentAuthorImage);
                            }
                        }
                    }
                }
            }
        });
        holder.binding.commentAuthorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goProfileDetail(view,comment);
            }
        });
        holder.binding.commentAuthorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goProfileDetail(view,comment);
            }
        });
    }
    private void goProfileDetail(View view, Comment comment){
        Intent intent = new Intent(view.getContext(), ProfileDetail.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("profile_uid",comment.getPerson_uid());
        view.getContext().startActivity(intent);
    }
    public CommentViewAdapter(ArrayList<Comment> comments){
        this.comments = comments;

    }
    @Override
    public int getItemCount() {
        return this.comments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private CommentLayoutBinding binding;
        public MyViewHolder(@NonNull CommentLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
