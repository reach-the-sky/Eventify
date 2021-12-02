package com.example.eventify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.LocusId;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(onNav);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new Fragment4()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNav = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selected = null;
            switch (item.getItemId()){
                case R.id.ask_bottom:
                    selected = new Fragment2();
                    break;
                case R.id.home_bottom:
                    selected = new Fragment4();
                    break;
                case R.id.profile_bottom:
                    selected = new Fragment1();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,selected).commit();


            return true;
        }
    };

    public void logout(View view) {

        auth.signOut();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void buttonClick(int button_id){
        if(button_id == 0){
            bottomNavigationView.setSelectedItemId(R.id.home_bottom);
        }
        else if(button_id == 1){
            bottomNavigationView.setSelectedItemId(R.id.ask_bottom);
        }
        else if(button_id == 3) {
            bottomNavigationView.setSelectedItemId(R.id.profile_bottom);
        }
    }
}