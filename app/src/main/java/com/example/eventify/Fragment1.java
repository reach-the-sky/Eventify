package com.example.eventify;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.w3c.dom.Text;

public class Fragment1 extends Fragment {

    FirebaseAuth auth;

    ImageView imageView;
    TextView button,tvname,tvbio,tvemail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_1,container,false);

        Button logoutButton = view.findViewById(R.id.profile_logout_button);
        Button editButton = view.findViewById(R.id.profile_edit_button);
        imageView = view.findViewById(R.id.iv_cp);
        button = view.findViewById(R.id.profile_button);
        tvname = view.findViewById(R.id.profile_name);
        tvbio = view.findViewById(R.id.profile_bio);
//        tvprofession = view.findViewById(R.id.profile_profession);
        tvemail = view.findViewById(R.id.profile_email);
//        tvwebsite = view.findViewById(R.id.profile_website);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),CreateProfile.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth = FirebaseAuth.getInstance();
                auth.signOut();
                Toast.makeText(getActivity(),"Logged Out!!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("user").document(user.getUid());
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String nameResult = task.getResult().getString("name");
                            String bioResult = task.getResult().getString("bio");
                            String websiteResult = task.getResult().getString("web");
                            String emailResult = task.getResult().getString("email");
                            String profResult = task.getResult().getString("prof");
                            String imageUrlResult = task.getResult().getString("url");

                            Picasso.get().load(imageUrlResult).into(imageView);
                            tvname.setText(nameResult);
                            tvbio.setText(bioResult);
                            tvemail.setText(emailResult);
//                            tvprofession.setText(profResult);
//                            tvwebsite.setText(websiteResult);
                        }
                        else{
//                            Intent intent = new Intent(getActivity(),CreateProfile.class);
//                            startActivity(intent);
                            Toast.makeText(getActivity(),"Data not found, Create Profile",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
