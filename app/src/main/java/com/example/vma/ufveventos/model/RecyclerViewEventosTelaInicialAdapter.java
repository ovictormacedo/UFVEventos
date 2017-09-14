package com.example.vma.ufveventos.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vma.ufveventos.R;
import com.example.vma.ufveventos.controller.OnEventoTelaInicialClickListener;

import java.util.List;

/**
 * Created by vma on 08/09/2017.
 */

public class RecyclerViewEventosTelaInicialAdapter extends RecyclerView.Adapter<RecyclerViewEventosTelaInicialAdapter.CustomViewHolder> {
    private List<Evento> eventos;
    private Context mContext;

    public RecyclerViewEventosTelaInicialAdapter(Context context, List<Evento> eventos) {
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
        String data = evento.getDataInicio().substring(8,10)+"/"+evento.getDataInicio().substring(5,7);
        customViewHolder.data.setText(data);
        //Seta horário de início e fim do evento
        customViewHolder.horario.setText("Horário: "+evento.getHoraInicio().substring(0,5)+" - "+evento.getHoraFim().substring(0,5));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEventoTelaInicialClickListener.onItemClick(evento);
            }
        };

        customViewHolder.itemView.setOnClickListener(listener);
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

    private OnEventoTelaInicialClickListener onEventoTelaInicialClickListener;

    public OnEventoTelaInicialClickListener getOnEventoTelaInicialClickListener() {
        return onEventoTelaInicialClickListener;
    }

    public void setOnEventoTelaInicialClickListener(OnEventoTelaInicialClickListener onEventoTelaInicialClickListener) {
        this.onEventoTelaInicialClickListener = onEventoTelaInicialClickListener;
    }
}