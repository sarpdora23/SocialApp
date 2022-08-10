package com.sinamekidev.socialapp;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sinamekidev.socialapp.databinding.ActivityMainBinding;
import com.sinamekidev.socialapp.fragments.FriendsFragment;
import com.sinamekidev.socialapp.fragments.HomeFragment;
import com.sinamekidev.socialapp.fragments.MessageFragment;
import com.sinamekidev.socialapp.fragments.SettingsFragment;
import com.sinamekidev.socialapp.models.User;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        changeFragment("home",new HomeFragment());
        binding.menuNavigation.setVisibility(View.INVISIBLE);
        init();

    }
    private void initMenu(){
        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.menuNavigation.setVisibility(View.VISIBLE);
            }
        });
        binding.mainFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.menuNavigation.getVisibility() == View.VISIBLE){
                    binding.menuNavigation.setVisibility(View.INVISIBLE);
                }
            }
        });
        binding.menuUsername.setText(user.getUserInfo().getUsername());
        if(user.getUserInfo().getProfile_url().equals("default")){
            binding.menuProfileImage.setImageResource(R.drawable.user);
        }
        else{
            Picasso.get().load(user.getUserInfo().getProfile_url()).into(binding.menuProfileImage);
        }
        binding.menuHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment("home",new HomeFragment());
                binding.menuNavigation.setVisibility(View.INVISIBLE);
            }
        });
        binding.menuProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ProfileDetail.class);
                intent.putExtra("profile_uid",mAuth.getCurrentUser().getUid());
                finish();
                startActivity(intent);
            }
        });
        binding.menuSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment("settings",new SettingsFragment());
                binding.menuNavigation.setVisibility(View.INVISIBLE);
            }
        });
        binding.menuMesages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment("message",new MessageFragment());
                binding.menuNavigation.setVisibility(View.INVISIBLE);
            }
        });
        binding.menuFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment("friends",new FriendsFragment());
            }
        });
        binding.menuSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }
        });
    }
    private void init(){
        binding.home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment("home",new HomeFragment());
            }
        });
        binding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment("settings",new SettingsFragment());
            }
        });
        binding.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment("message",new MessageFragment());
            }
        });
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null){
                    user = value.toObject(User.class);
                    if(user.getUserInfo().getProfile_url().equals("default")){
                        binding.imageProfile.setImageResource(R.drawable.user);
                    }
                    else{
                        Picasso.get().load(user.getUserInfo().getProfile_url()).into(binding.imageProfile);
                    }
                    initMenu();
                }
                else{
                    Snackbar.make(binding.mainLayout,error.getMessage(),Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }
    private void changeFragment(String fragment_name,Fragment fragment){
       binding.home.setImageTintList(ColorStateList.valueOf(Color.parseColor("#B3B0B0")));
       binding.settings.setImageTintList(ColorStateList.valueOf(Color.parseColor("#B3B0B0")));
       binding.message.setImageTintList(ColorStateList.valueOf(Color.parseColor("#B3B0B0")));
       if(fragment_name.equals("home")){
           binding.home.setImageTintList(ColorStateList.valueOf(Color.parseColor("#1ED5B9")));
       }
       else if(fragment_name.equals("settings")){
            binding.settings.setImageTintList(ColorStateList.valueOf(Color.parseColor("#1ED5B9")));
       }
       else if(fragment_name.equals("message")){
           binding.message.setImageTintList(ColorStateList.valueOf(Color.parseColor("#1ED5B9")));
       }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(binding.mainFrame.getId(),fragment);
        fragmentTransaction.commit();
        binding.menuNavigation.setVisibility(View.INVISIBLE);
    }
}