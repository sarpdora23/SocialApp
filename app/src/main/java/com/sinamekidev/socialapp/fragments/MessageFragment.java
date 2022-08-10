package com.sinamekidev.socialapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sinamekidev.socialapp.adapters.MFViewAdapter;
import com.sinamekidev.socialapp.databinding.MessageFragmentBinding;
import com.sinamekidev.socialapp.models.MessageParent;
import com.sinamekidev.socialapp.models.User;

import java.util.ArrayList;

public class MessageFragment extends Fragment {
    private MessageFragmentBinding binding;
    private ArrayList<MessageParent> messageParents = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private MFViewAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MessageFragmentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        binding.messageFragmentRw.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MFViewAdapter(messageParents);
        binding.messageFragmentRw.setAdapter(adapter);
        super.onStart();
        firebaseFirestore.collection("Message Rooms").whereArrayContains("user_list",mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null){
                    messageParents.clear();
                    for(DocumentChange documentChange: value.getDocumentChanges()){
                        MessageParent messageParent = documentChange.getDocument().toObject(MessageParent.class);
                        messageParents.add(messageParent);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
