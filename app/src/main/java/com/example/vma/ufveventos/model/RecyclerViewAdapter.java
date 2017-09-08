package com.example.vma.ufveventos.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vma.ufveventos.R;
import com.example.vma.ufveventos.controller.OnItemClickListener;

import java.util.List;

/**
 * Created by vma on 08/09/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> {
    private List<Evento> eventos;
    private Context mContext;

    public RecyclerViewAdapter(Context context, List<Evento> eventos) {
        this.eventos = eventos;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.evento_row, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        final Evento evento = eventos.get(i);
        //Seta imagem do evento

        //Seta denominação do evento
        customViewHolder.denominacao.setText(evento.getDenominacao());
        //Seta data do evento
        customViewHolder.data.setText(evento.getDataInicio());
        //Seta horário de início e fim do evento
        customViewHolder.horario.setText("Horário: "+evento.getHoraInicio()+" - "+evento.getHoraFim());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(evento);
            }
        };

        customViewHolder.imagem.setOnClickListener(listener);
        customViewHolder.data.setOnClickListener(listener);
        customViewHolder.horario.setOnClickListener(listener);
        customViewHolder.denominacao.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return (null != eventos ? eventos.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imagem;
        protected TextView denominacao,horario,data;

        public CustomViewHolder(View view) {
            super(view);
            this.imagem = (ImageView) view.findViewById(R.id.imagemEventoRow);
            this.denominacao = (TextView) view.findViewById(R.id.denominacaoEventoRow);
            this.horario = (TextView) view.findViewById(R.id.horarioEventoRow);
            this.data = (TextView) view.findViewById(R.id.dataEventoRow);
        }
    }
    private OnItemClickListener onItemClickListener;
    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}