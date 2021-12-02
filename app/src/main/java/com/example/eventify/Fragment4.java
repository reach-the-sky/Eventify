package com.example.eventify;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class Fragment4 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_4,container,false);

        ImageView posts = view.findViewById(R.id.home_jump);
        ImageView newPosts = view.findViewById(R.id.home_new_post);
        ImageView location = view.findViewById(R.id.bmu_location);
        ImageView phone = view.findViewById(R.id.bmu_phone);
        TextView textViewHeading = view.findViewById(R.id.textViewHeading);
        getUserName(textViewHeading);

        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity obj = (MainActivity) getActivity();
                obj.buttonClick(1);
//            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new Fragment2()).commit();
            }
        });

        newPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity obj = (MainActivity) getActivity();
                obj.buttonClick(1);
//                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new Fragment3()).commit();
            }
        });


        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "tel:/+9118001036888";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 28.2476758,76.8114382);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });

        return view;
    }
    public void getUserName(TextView textViewHeading){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("user").document(user.getUid());
        final String[] nameResult = new String[1];
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            nameResult[0] = "Hi, "+ task.getResult().getString("name") + "!";
                            textViewHeading.setText(nameResult[0]);
                        }
                        else{
                            nameResult[0] =  "Hi, User!";
                            textViewHeading.setText(nameResult[0]);
                        }
                    }
                });

    }
}
