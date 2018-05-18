package com.labd2m.vma.ufveventos.controller;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.labd2m.vma.ufveventos.R;
import com.labd2m.vma.ufveventos.model.Categoria;

import java.util.List;

/**
 * Created by vma on 11/05/2018.
 */

public class AdapterCategoria extends BaseAdapter{

    private List<Categoria> categorias;
    private List<Categoria> prefencias;
    private Activity act;
    int cont = 0;
    int max;

    public AdapterCategoria(List<Categoria> categorias, List<Categoria> preferencias, Activity act) {
        try {
            this.categorias = categorias;
            this.prefencias = preferencias;
            this.act = act;
            max = categorias.size();

        }catch (Exception e){
            Log.e("POKEDEX", "ERRO: " + e.getMessage());
        }
    }

    @Override
    public int getCount() {
        return categorias.size();
    }

    @Override
    public Object getItem(int position) {
        return categorias.get(position);
    }

    @Override
    public long getItemId(int position) {
        return categorias.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            Log.i("List", "getView " + categorias.size());
            View view = act.getLayoutInflater().inflate(R.layout.categoria_row, parent, false);

            if(cont < max) {

                Categoria categoria = categorias.get(position);

                TextView nomeCatRow = (TextView)
                        view.findViewById(R.id.nomeCategoriaRow);
                TextView idCatRow = (TextView)
                        view.findViewById(R.id.idCategoriaRow);
                CheckBox checkBox = (CheckBox)
                        view.findViewById(R.id.checkBoxCategoriaRow);

                Log.i("List", "n: " + categoria.getNome());
                Log.i("List", "i: " + categoria.getId());

                nomeCatRow.setText(categoria.getNome());
                idCatRow.setText(categoria.getId() + "");

                Log.i("List", " 3 ");

                for (Categoria p : prefencias) {
                    Log.i("List", "getView 2");
                    if (categoria.getId() == p.getId())
                        checkBox.setChecked(true);
                }

                cont++;
            }

            return view;
        }catch (Exception e){
            Log.e("POKEDEX", "ERRO 1: " + e.getMessage());
            return null;
        }
    }
}

/*package teste.lucasvegi.pokemongooffline.View;

        import android.app.Activity;
        import android.util.Log;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

        import java.util.List;

        import teste.lucasvegi.pokemongooffline.Model.ControladoraFachadaSingleton;
        import teste.lucasvegi.pokemongooffline.Model.Pokemon;
        import teste.lucasvegi.pokemongooffline.R;

public class AdapterPokedex extends BaseAdapter {

    private List<Pokemon> pokemons;
    private Activity act;
    //List<Bitmap> bitmapCache;

    public AdapterPokedex(List<Pokemon> pokemons, Activity act) {
        try {
            this.pokemons = pokemons;
            this.act = act;
            //this.bitmapCache = new ArrayList<Bitmap>();

            //carregarBitmapsNoCache();
        }catch (Exception e){
            Log.e("POKEDEX", "ERRO: " + e.getMessage());
        }
    }

    @Override
    public int getCount() {
        return pokemons.size();
    }

    @Override
    public Object getItem(int position) {
        return pokemons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return pokemons.get(position).getNumero();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            View view = act.getLayoutInflater().inflate(R.layout.lista_pokedex_personalizada, parent, false);

            Pokemon pkmn = pokemons.get(position);

            Log.i("POKEDEX", "Montando lista pokedex para " + pkmn.getNome());

            TextView nomePokemon = (TextView)
                    view.findViewById(R.id.txtNomePokemonPokedex);
            TextView numeroPokemon = (TextView)
                    view.findViewById(R.id.txtNumeroPokemonPokedex);
            ImageView imagem = (ImageView)
                    view.findViewById(R.id.imagemPokemonPokedex);

            //Decide se vai ter informações do pokemon ou não
            if(ControladoraFachadaSingleton.getInstance().getUsuario().getQuantidadeCapturas(pkmn) > 0) {
                nomePokemon.setText(pkmn.getNome());

                //ajusta o visual do número acrescendo zeros ao lado
                if(pkmn.getNumero() < 10)
                    numeroPokemon.setText("#00"+pkmn.getNumero());
                else if(pkmn.getNumero() < 100)
                    numeroPokemon.setText("#0"+pkmn.getNumero());
                else
                    numeroPokemon.setText("#" + pkmn.getNumero());

                imagem.setImageResource(pkmn.getIcone());
            }else {
                nomePokemon.setText("???");

                //ajusta o visual do número acrescendo zeros ao lado
                if(pkmn.getNumero() < 10)
                    numeroPokemon.setText("#00"+pkmn.getNumero());
                else if(pkmn.getNumero() < 100)
                    numeroPokemon.setText("#0"+pkmn.getNumero());
                else
                    numeroPokemon.setText("#"+pkmn.getNumero());

                imagem.setImageResource(R.drawable.help);
            }

            return view;
        }catch (Exception e){
            Log.e("POKEDEX", "ERRO: " + e.getMessage());
            return null;
        }
    }

}*/
