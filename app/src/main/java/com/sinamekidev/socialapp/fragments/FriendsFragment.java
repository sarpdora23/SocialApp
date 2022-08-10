package com.sinamekidev.socialapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.sinamekidev.socialapp.adapters.FriendViewAdapter;
import com.sinamekidev.socialapp.adapters.RequestViewAdapter;
import com.sinamekidev.socialapp.databinding.FriendsFragmentBinding;
import com.sinamekidev.socialapp.models.User;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayList<String> req_user_list;
    private ArrayList<String> friends_list;
    private FriendsFragmentBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FriendsFragmentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null){
                    User user = value.toObject(User.class);
                    friends_list = user.getFriends_list();
                    req_user_list = user.getFriend_request_list();
                    binding.friendsRw.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.waitingReqRw.setLayoutManager(new LinearLayoutManager(getContext()));
                    FriendViewAdapter friendViewAdapter = new FriendViewAdapter(friends_list);
                    RequestViewAdapter adapter = new RequestViewAdapter(req_user_list,user);
                    binding.friendsRw.setAdapter(friendViewAdapter);
                    binding.waitingReqRw.setAdapter(adapter);
                }
                else {

                }
            }
        });
    }
}
