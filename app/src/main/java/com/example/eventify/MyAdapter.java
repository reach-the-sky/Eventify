package com.example.eventify;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Item> items; // posts and Items

    public MyAdapter(Context context, ArrayList<Item> Items) {
        this.context = context;
        this.items = Items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Item item = items.get(position);
        holder.title.setText(item.getTitle());
        String descriptionSplitString = item.getDescription();
        String[] descriptionSplit = descriptionSplitString.split("\n");
        int len = descriptionSplit.length;
        if(len > 2){
            String descriptionTemp = "";
            for(int i = 0;i< len-2;i++){
                descriptionTemp += descriptionSplit[i] + "\n";
            }
            if(descriptionTemp.length() < 100){
                holder.description.setText(descriptionTemp + "\n" + descriptionSplit[len - 2] + "\n" + descriptionSplit[len - 1]);
            }
            else{
                holder.description.setText(descriptionTemp.substring(0,100) + "....\n" + descriptionSplit[len - 2] + "\n" + descriptionSplit[len - 1]);
            }
        }
        else{
            if(descriptionSplitString.length() < 100){
                holder.description.setText(descriptionSplitString);
            }
            else{
                holder.description.setText(descriptionSplitString.substring(0,100) + "....");
            }
        }

        holder.setItemClickListner(new ItemClickListner(){
            @Override
            public void OnClick(View V, int position) {
                Intent intent = new Intent(V.getContext(),PostClickActivity.class);
                intent.putExtra("uuid",item.uuid);
                intent.putExtra("title",item.getTitle());
                intent.putExtra("description",item.getDescription());
                intent.putExtra("categoryList",item.getCategory());
                intent.putExtra("position",String.valueOf(items.size() - position - 1));
                V.getContext().startActivity(intent);

//                Toast.makeText(V.getContext(),item.getUuid(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title,description;
        ItemClickListner itemClickListner;

        public void setItemClickListner(ItemClickListner itemClickListner) {
            this.itemClickListner = itemClickListner;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            description = itemView.findViewById(R.id.item_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(itemView.getContext(),getAdapterPosition(),Toast.LENGTH_LONG).show();
//                    Log.d("onclick: ","button is registered");
                    itemClickListner.OnClick(view,getAdapterPosition());

                }
            });
        }

    }
}
