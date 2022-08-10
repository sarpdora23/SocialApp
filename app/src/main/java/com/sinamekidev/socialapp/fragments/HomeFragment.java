package com.sinamekidev.socialapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sinamekidev.socialapp.CreatePost;
import com.sinamekidev.socialapp.adapters.PostsViewAdapters;
import com.sinamekidev.socialapp.databinding.HomeFragmentBinding;
import com.sinamekidev.socialapp.models.Post;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private HomeFragmentBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private PostsViewAdapters postsViewAdapters;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(inflater,container,false);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreatePost.class);
                startActivity(intent);
            }
        });
        ArrayList<Post> post_list = new ArrayList<>();
        postsViewAdapters = new PostsViewAdapters(post_list,mAuth);
        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null){
                    post_list.clear();
                    for(DocumentChange documentChange: value.getDocumentChanges()){
                        Post post = documentChange.getDocument().toObject(Post.class);
                        System.out.println(post.getText());
                        post_list.add(post);
                        postsViewAdapters.notifyDataSetChanged();
                    }

                }
            }
        });
        binding.homeFragmentRw.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.homeFragmentRw.setAdapter(postsViewAdapters);
    }
}
