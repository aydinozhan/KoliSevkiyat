package com.example.kolisevkiyat;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class SonIslemAdapter extends RecyclerView.Adapter<SonIslemAdapter.SonIslemViewHolder> {
    ArrayList<SonIslem> sonIslemler;

    public SonIslemAdapter(ArrayList<SonIslem> sonIslemler) {
        this.sonIslemler=sonIslemler;
    }

    @NonNull
    @Override
    public SonIslemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View tekSatirSonIslem = inflater.inflate(R.layout.tek_satir_sonislem,parent,false);
        return new SonIslemViewHolder(tekSatirSonIslem);
    }

    @Override
    public void onBindViewHolder(@NonNull SonIslemViewHolder holder, int position) {
        holder.BelgeNo.setText(sonIslemler.get(position).BelgeNo);
        holder.EvrakNo.setText(sonIslemler.get(position).EvrakNo);
        holder.CariAdi.setText(sonIslemler.get(position).CariAdi);
        SonIslem secilenSonIslem = sonIslemler.get(position);
        holder.setData(secilenSonIslem,position);
    }

    @Override
    public int getItemCount() {
        return sonIslemler.size();
    }

    class SonIslemViewHolder extends RecyclerView.ViewHolder{
        TextView BelgeNo,EvrakNo,CariAdi;
        public SonIslemViewHolder(@NonNull View itemView) {
            super(itemView);
            BelgeNo=itemView.findViewById(R.id.rv_tv_belgeNo);
            EvrakNo=itemView.findViewById(R.id.rv_tv_evrakNo);
            CariAdi=itemView.findViewById(R.id.rv_tv_cariAdi);
        }
        public void setData(SonIslem sonIslem,int position){
            Dto dto = new Dto(sonIslem.BelgeNo,sonIslem.EvrakNo,sonIslem.RecNo);
           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(v.getContext(), BarkodEkrani.class);
                   intent.putExtra("Dto", dto);
                   v.getContext().startActivity(intent);
               }
           });

        }
    }
}
