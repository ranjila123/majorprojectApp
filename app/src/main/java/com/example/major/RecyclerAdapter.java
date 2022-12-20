package com.example.major;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

     Context context;
    private ArrayList<Uri> uriArrayList;
    CountOfImagesWhenRemoved countOfImagesWhenRemoved;


    public RecyclerAdapter(ArrayList<Uri> uriArrayList, Context context, CountOfImagesWhenRemoved countOfImagesWhenRemoved) {
        this.uriArrayList = uriArrayList;
        this.context = context;
        this.countOfImagesWhenRemoved = countOfImagesWhenRemoved;
    }

    //create new view(inflate custom_single_image ie convert xml to java) view-holder type object is returned
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_single_image,parent,false);
        return new ViewHolder(view,countOfImagesWhenRemoved);
    }

    //Replace content of view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.imageView.setImageURI(uriArrayList.get(position));

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uriArrayList.remove(uriArrayList.get(position));
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,getItemCount());
                countOfImagesWhenRemoved.clicked(uriArrayList.size());

            }
        });

    }

    @Override
    public int getItemCount() {

        return uriArrayList.size();
    }

    ImageView imageView;
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView,delete;
        CountOfImagesWhenRemoved countOfImagesWhenRemoved;
        public ViewHolder(@NonNull View itemView,CountOfImagesWhenRemoved countOfImagesWhenRemoved) {
            super(itemView);
            this.countOfImagesWhenRemoved = countOfImagesWhenRemoved;
            imageView = itemView.findViewById(R.id.imageR);
            delete = itemView.findViewById(R.id.delete);
        }
    }
    public interface CountOfImagesWhenRemoved{
        void clicked(int getSize);
    }
}
