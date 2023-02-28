package com.yigit.artbookjava;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yigit.artbookjava.databinding.RecylerLayoutBinding;

import java.util.ArrayList;

public class ArtAdaptor extends RecyclerView.Adapter <ArtAdaptor.ArtHolder> {

    private ArrayList<Art> arts;
    public ArtAdaptor( ArrayList<Art> arts){
        this.arts = arts;

    }

    @NonNull
    @Override
    public ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecylerLayoutBinding recylerLayoutBinding = RecylerLayoutBinding.inflate(LayoutInflater.from(parent.getContext()) , parent, false);
        return new ArtHolder(recylerLayoutBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull ArtHolder holder, int position) {
    holder.binding.recylerViewText.setText(arts.get(position).name);
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(holder.itemView.getContext(), ArtActivity.class);
            intent.putExtra("info","old");
            intent.putExtra("artId" , arts.get(position).id );
            holder.itemView.getContext().startActivity(intent);
        }
    });
    }

    @Override
    public int getItemCount() {
        return arts.size();
    }


    public class ArtHolder extends  RecyclerView.ViewHolder {
     RecylerLayoutBinding binding;

        public ArtHolder(@NonNull RecylerLayoutBinding recylerLayoutBinding) {
            super(recylerLayoutBinding.getRoot());
            this.binding = recylerLayoutBinding;
        }
    }

}

