package com.example.artbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.artbook.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class ArtAdaptor extends RecyclerView.Adapter<ArtAdaptor.ArtHolder> {

    ArrayList<Art> artArrayList;

    public ArtAdaptor(ArrayList<Art> artArrayList){
        this.artArrayList=artArrayList;

    }

    @NonNull
    @Override
    public ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new  ArtHolder(recyclerRowBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull ArtAdaptor.ArtHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.text1.setText(artArrayList.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(holder.itemView.getContext(),ArtBook2.class);
                intent.putExtra("info","old");
                intent.putExtra("artId",artArrayList.get(position).id);
                holder.itemView.getContext().startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return artArrayList.size();
    }

    public class ArtHolder extends  RecyclerView.ViewHolder {
        private RecyclerRowBinding binding;

        public ArtHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
