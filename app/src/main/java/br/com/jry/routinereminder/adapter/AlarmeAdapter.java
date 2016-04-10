package br.com.jry.routinereminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.jry.routinereminder.R;
import br.com.jry.routinereminder.entity.Alarme;

/**
 * Created by Jeferson on 08/04/2016.
 */
public class AlarmeAdapter extends BaseAdapter {

    private List<Alarme> alarmes;
    private Context context;

    public AlarmeAdapter(List<Alarme> alarmes, Context context){
        this.context = context;
        this.alarmes = alarmes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        convertView = inflater.inflate(R.layout.list_alarme, null);

        TextView titulo = (TextView) convertView.findViewById(R.id.tvTitulo);
        TextView mensagem = (TextView) convertView.findViewById(R.id.tvMsg);

        Alarme alarme = this.alarmes.get(position);

        titulo.setText(alarme.getDescricao());
        mensagem.setText(alarme.getMensagem());

        return convertView;
    }

    @Override
    public int getCount() {
        return this.alarmes.size();
    }

    @Override
    public Object getItem(int position) {
        return alarmes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return alarmes.get(position).getId();
    }
}
