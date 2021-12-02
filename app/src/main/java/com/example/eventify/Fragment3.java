package com.example.eventify;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.StringValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class Fragment3 extends Fragment {
    FirebaseDatabase database;
    Calendar date = null;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference;
    EditText title_et,description_et,date_et;
    Button send_button;
    FloatingActionButton floatingActionButton;
    Button category_tv;
    boolean[] selectCategory;
    ArrayList<String> categoryList = new ArrayList<>();
    String[] categoryArray = {"SOET","SOM","SOL"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_3, container, false);

        title_et = view.findViewById(R.id.send_post_title);
        description_et = view.findViewById(R.id.send_post_description);
        category_tv = view.findViewById(R.id.send_post_category);
        send_button = view.findViewById(R.id.send_button);
        date_et = view.findViewById(R.id.Birthday);

        floatingActionButton = view.findViewById(R.id.close);

        database = FirebaseDatabase.getInstance("https://eventify-f35eb-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = database.getReference("count");

        final long[] count = new long[1];

        selectCategory = new boolean[categoryArray.length];

        category_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Organizations");
                builder.setCancelable(false);
                builder.setMultiChoiceItems(categoryArray, selectCategory, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if(b){
                            categoryList.add(categoryArray[i]);
                            Collections.sort(categoryList);
                        }else{
                            categoryList.remove(categoryArray[i]);
                        }
                        Log.d("Category list",String.valueOf(categoryList));
                    }
                });
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for(int j = 0; j < categoryList.size();j++){
                            stringBuilder.append(categoryList.get(j));
                            if(j != categoryList.size() - 1){
                                stringBuilder.append(", ");
                            }
                        }
                        category_tv.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
//                Log.d("Count",String.valueOf(count[0]));
                builder.show();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity obj = (MainActivity) getActivity();
                obj.buttonClick(1);
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count[0] = snapshot.getValue(long.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title_et.getText().toString().equals("") || description_et.getText().toString().equals("")){
                    Toast.makeText(getActivity(),"fill all the fields",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(categoryList.size() == 0){
                    Toast.makeText(getActivity(),"fill the categories",Toast.LENGTH_SHORT).show();
                    return;
                }
                reference.setValue(count[0] + 1);
                Item newItem = new Item();
                String newItemTitle = title_et.getText().toString();
                String newItemDescription = description_et.getText().toString();
                newItem.setTitle( newItemTitle.substring(0,1).toUpperCase(Locale.ROOT) + newItemTitle.substring(1));
                if(date != null){
                    String dateAndTime = "Date: " + date.get(Calendar.DATE) + "/" + date.get(Calendar.MONTH) + "/" + date.get(Calendar.YEAR)
                            + "\nTime: " + date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE);
                    newItem.setDescription(newItemDescription.substring(0,1).toUpperCase(Locale.ROOT) + newItemDescription.substring(1) + "\n" + dateAndTime);
                }
                else{
                    newItem.setDescription(newItemDescription.substring(0,1).toUpperCase(Locale.ROOT) + newItemDescription.substring(1));
                }

                newItem.setCategory(categoryList);
                newItem.setUuid(auth.getUid());

                database.getReference("posts/" + String.valueOf(count[0])).setValue(newItem);
                Toast.makeText(getActivity(),"Done!!" , Toast.LENGTH_SHORT).show();

                MainActivity obj = (MainActivity) getActivity();
                obj.buttonClick(1);
            }
        });



        date_et.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDateTimePicker();
                date_et.setText("Date: " + date.get(Calendar.DATE) + "/" + date.get(Calendar.MONTH) + "/" + date.get(Calendar.YEAR)
                + "    Time: " + date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE));
            }
        });

        return view;
    }
    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
//                        Toast.makeText(getActivity(), "The choosen one " + date.getTime(),Toast.LENGTH_SHORT).show();

                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

}
