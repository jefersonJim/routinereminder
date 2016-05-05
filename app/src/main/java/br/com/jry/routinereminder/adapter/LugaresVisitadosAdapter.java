package br.com.jry.routinereminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.jry.routinereminder.R;
import br.com.jry.routinereminder.entity.LugaresVisitados;

/**
 * Created by betoe on 04/05/2016.
 */
public class LugaresVisitadosAdapter extends BaseAdapter{

    private List<LugaresVisitados> lugaresVisitados;
    private Context context;

    public LugaresVisitadosAdapter(List<LugaresVisitados> lugaresVisitados, Context context){
        this.context = context;
        this.lugaresVisitados = lugaresVisitados;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        convertView = inflater.inflate(R.layout.list_lugares_visitados, null);

        TextView titulo = (TextView) convertView.findViewById(R.id.lugarVisitadoTitulo);
        TextView mensagem = (TextView) convertView.findViewById(R.id.lugarVisitadoDescricao);

        LugaresVisitados lugaresVisitados = this.lugaresVisitados.get(position);

        titulo.setText(lugaresVisitados.getTitulo());
        mensagem.setText(lugaresVisitados.setDescricao());

        return convertView;
    }
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
