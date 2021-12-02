package com.example.eventify;

import static com.example.eventify.R.*;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Fragment2 extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    MyAdapter myAdapter;
    Button soetbt,sombt,solbt,allbt;
    String postsSelection = "ALL";
    FloatingActionButton floatingActionButton;
    ArrayList<Item> items;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_2,container,false);

        recyclerView =(RecyclerView) view.findViewById(id.rv1);
        databaseReference = FirebaseDatabase.getInstance("https://eventify-f35eb-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("posts");

        floatingActionButton = view.findViewById(id.add_posts);

        soetbt = view.findViewById(id.soet_button);
        sombt = view.findViewById(id.som_button);
        solbt = view.findViewById(id.sol_button);
        allbt = view.findViewById(id.all_button);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        items = new ArrayList<>();
        myAdapter = new MyAdapter(getActivity(),items);
        recyclerView.setAdapter(myAdapter);
        loadDataIntoRecyclerView();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    anim.bottom_right_top_left,  // enter
                    anim.fade_out,  // exit
                    anim.fade_in,   // popEnter
                    anim.slide_out  // popExit
            ).replace(id.frame_layout,new Fragment3()).commit();
            }
        });

        allbt.setBackgroundResource(drawable.button_style3);
        solbt.setBackgroundResource(drawable.button_style4);
        sombt.setBackgroundResource(drawable.button_style4);
        soetbt.setBackgroundResource(drawable.button_style4);

        soetbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postsSelection = "SOET";
                loadDataIntoRecyclerView();
                myAdapter.notifyDataSetChanged();
                soetbt.setBackgroundResource(drawable.button_style3);
                solbt.setBackgroundResource(drawable.button_style4);
                sombt.setBackgroundResource(drawable.button_style4);
                allbt.setBackgroundResource(drawable.button_style4);
            }
        });

        solbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postsSelection = "SOL";
                loadDataIntoRecyclerView();
                myAdapter.notifyDataSetChanged();
                solbt.setBackgroundResource(drawable.button_style3);
                soetbt.setBackgroundResource(drawable.button_style4);
                sombt.setBackgroundResource(drawable.button_style4);
                allbt.setBackgroundResource(drawable.button_style4);
            }
        });

        sombt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postsSelection = "SOM";
                loadDataIntoRecyclerView();
                myAdapter.notifyDataSetChanged();
                sombt.setBackgroundResource(drawable.button_style3);
                solbt.setBackgroundResource(drawable.button_style4);
                soetbt.setBackgroundResource(drawable.button_style4);
                allbt.setBackgroundResource(drawable.button_style4);
            }
        });

        allbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                postsSelection = "ALL";
                loadDataIntoRecyclerView();
                myAdapter.notifyDataSetChanged();
                allbt.setBackgroundResource(drawable.button_style3);
                solbt.setBackgroundResource(drawable.button_style4);
                sombt.setBackgroundResource(drawable.button_style4);
                soetbt.setBackgroundResource(drawable.button_style4);
            }
        });
        return view;
    }
    void loadDataIntoRecyclerView(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Item item = dataSnapshot.getValue(Item.class);
                    ArrayList<String> tempItem = item.getCategory();
                    if(postsSelection != "ALL"){
                        if(tempItem.contains(postsSelection)) {
                            items.add(item);
                        }
                    }
                    else{
                        items.add(item);
                    }
                }
                Collections.reverse(items);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
