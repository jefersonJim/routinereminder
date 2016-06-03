package br.com.jry.routinereminder.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.jry.routinereminder.R;
import br.com.jry.routinereminder.adapter.AlarmeAdapter;
import br.com.jry.routinereminder.dao.AlarmeDao;
import br.com.jry.routinereminder.entity.Alarme;

public class LocaisVisitadosActivity extends AppCompatActivity {

    private AlarmeAdapter adapter;
    private List<Alarme> alarmes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Routine Reminder - Locais visitados");
        AlarmeDao alarmeDao = new AlarmeDao(getApplicationContext());
        this.alarmes = alarmeDao.getListAlarmesVisitados();

        setContentView(R.layout.activity_locais_visitados);

        ListView listView = (ListView) findViewById(R.id.lvLocais);

        this.adapter = new AlarmeAdapter(this.alarmes, getApplicationContext());
        TextView empty = (TextView) findViewById(R.id.tvEmpty);

        listView.setEmptyView(empty);
        listView.setAdapter(adapter);

        registerForContextMenu(listView);

        ActionBar actionBar =getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu,v,menuInfo);
        menu.setHeaderTitle("Opções");
        menu.setHeaderIcon(R.drawable.mr_ic_settings_light);
        getMenuInflater().inflate(R.menu.menu_lvalarme, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ListView lv = (ListView) findViewById(R.id.lvLocais);
        Alarme alarme = (Alarme) lv.getItemAtPosition(acmi.position);
        AlarmeDao dao = new AlarmeDao(getApplicationContext());

        switch (item.getItemId()){
            case R.id.action_delete :
                dao.delete(alarme.getId());
                Toast toast = Toast.makeText(getApplicationContext(), "Local deletado com successo!", Toast.LENGTH_LONG);
                toast.show();
                this.alarmes.remove(alarme);
                this.adapter.notifyDataSetChanged();
                return  true;
            case R.id.action_editar:
                Intent intent = new Intent(getApplicationContext(), AlarmeActivity.class);
                intent.putExtra("alarme", alarme);
                startActivity(intent);
            default:
                return super.onContextItemSelected(item);
        }

    }
}
