package com.labd2m.vma.ufveventos.controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.labd2m.vma.ufveventos.R;
import com.labd2m.vma.ufveventos.model.Categoria;

import java.util.List;

/**
 * Created by vma on 08/09/2017.
 */

public class RecyclerViewCategoriasAdapter extends RecyclerView.Adapter<RecyclerViewCategoriasAdapter.CustomViewHolder> {
    private List<Categoria> categorias;
    private Context mContext;

    public RecyclerViewCategoriasAdapter(Context context, List<Categoria> categorias) {
        this.categorias = categorias;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.categoria_row, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        final Categoria categoria = categorias.get(i);

        //Seta nome da categoria
        customViewHolder.nome.setText(categoria.getNome());
        customViewHolder.idCategoria.setText(String.valueOf(categoria.getId()));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoriaClickListener.onItemClick(categoria);
            }
        };

        customViewHolder.itemView.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return (null != categorias ? categorias.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView nome;
        protected TextView idCategoria;

        public CustomViewHolder(View view) {
            super(view);
            this.nome = (TextView) view.findViewById(R.id.nomeCategoriaRow);
            this.idCategoria = (TextView) view.findViewById(R.id.idCategoriaRow);
        }
    }
    private OnCategoriaClickListener onCategoriaClickListener;

    public OnCategoriaClickListener getOnCategoriaClickListener() {
        return onCategoriaClickListener;
    }

    public void setCategoriaClickListener(OnCategoriaClickListener onCategoriaClickListener) {
        this.onCategoriaClickListener = onCategoriaClickListener;
    }
}