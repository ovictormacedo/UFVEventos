package com.labd2m.vma.ufveventos.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.labd2m.vma.ufveventos.R;
import com.labd2m.vma.ufveventos.controller.OnEventoTelaInicialClickListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
        char primeiraLetra = evento.getDenominacao().toLowerCase().charAt(0);
        switch (primeiraLetra){
            case 'a':
                customViewHolder.imagem.setImageResource(R.drawable.a);
                break;
            case 'b':
                customViewHolder.imagem.setImageResource(R.drawable.b);
                break;
            case 'c':
                customViewHolder.imagem.setImageResource(R.drawable.c);
                break;
            case 'd':
                customViewHolder.imagem.setImageResource(R.drawable.d);
                break;
            case 'e':
                customViewHolder.imagem.setImageResource(R.drawable.e);
                break;
            case 'f':
                customViewHolder.imagem.setImageResource(R.drawable.f);
                break;
            case 'g':
                customViewHolder.imagem.setImageResource(R.drawable.g);
                break;
            case 'h':
                customViewHolder.imagem.setImageResource(R.drawable.h);
                break;
            case 'i':
                customViewHolder.imagem.setImageResource(R.drawable.i);
                break;
            case 'j':
                customViewHolder.imagem.setImageResource(R.drawable.j);
                break;
            case 'k':
                customViewHolder.imagem.setImageResource(R.drawable.k);
                break;
            case 'l':
                customViewHolder.imagem.setImageResource(R.drawable.l);
                break;
            case 'm':
                customViewHolder.imagem.setImageResource(R.drawable.m);
                break;
            case 'n':
                customViewHolder.imagem.setImageResource(R.drawable.n);
                break;
            case 'o':
                customViewHolder.imagem.setImageResource(R.drawable.o);
                break;
            case 'p':
                customViewHolder.imagem.setImageResource(R.drawable.p);
                break;
            case 'q':
                customViewHolder.imagem.setImageResource(R.drawable.q);
                break;
            case 'r':
                customViewHolder.imagem.setImageResource(R.drawable.r);
                break;
            case 's':
                customViewHolder.imagem.setImageResource(R.drawable.s);
                break;
            case 't':
                customViewHolder.imagem.setImageResource(R.drawable.t);
                break;
            case 'u':
                customViewHolder.imagem.setImageResource(R.drawable.u);
                break;
            case 'v':
                customViewHolder.imagem.setImageResource(R.drawable.v);
                break;
            case 'w':
                customViewHolder.imagem.setImageResource(R.drawable.w);
                break;
            case 'x':
                customViewHolder.imagem.setImageResource(R.drawable.x);
                break;
            case 'y':
                customViewHolder.imagem.setImageResource(R.drawable.y);
                break;
            case 'z':
                customViewHolder.imagem.setImageResource(R.drawable.b);
                break;
            default:
                customViewHolder.imagem.setImageResource(R.drawable.outros);
                break;
        }


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
        protected CircleImageView imagem;
        protected TextView denominacao,horario,data;

        public CustomViewHolder(View view) {
            super(view);
            this.imagem = (CircleImageView) view.findViewById(R.id.imagemEventoRow);
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