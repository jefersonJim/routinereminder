package br.com.jry.routinereminder.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.jry.routinereminder.R;
import br.com.jry.routinereminder.adapter.AlarmeAdapter;
import br.com.jry.routinereminder.dao.AlarmeDao;
import br.com.jry.routinereminder.entity.Alarme;
import br.com.jry.routinereminder.service.AlarmeService;

public class MainActivity extends AppCompatActivity {
    private AlarmeAdapter adapter;
    private List<Alarme> alarmes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        boolean isOn = manager.isProviderEnabled( LocationManager.GPS_PROVIDER);
        if(!isOn){
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        AlarmeDao alarmeDao = new AlarmeDao(getApplicationContext());
        this.alarmes = alarmeDao.getListAlarmesAtivos();

        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.listAlarmeView);

        this.adapter = new AlarmeAdapter(this.alarmes, getApplicationContext());
        TextView empty = (TextView) findViewById(R.id.tvEmpty);

        listView.setEmptyView(empty);
        listView.setAdapter(adapter);

        registerForContextMenu(listView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AlarmeActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = new Intent(getApplicationContext(), AlarmeService.class);
        getApplicationContext().startService(intent);
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
        ListView lv = (ListView) findViewById(R.id.listAlarmeView);
        Alarme alarme = (Alarme) lv.getItemAtPosition(acmi.position);
        AlarmeDao dao = new AlarmeDao(getApplicationContext());
        switch (item.getItemId()){
            case R.id.action_delete :
                dao.delete(alarme.getId());
                Toast toast = Toast.makeText(getApplicationContext(), "Alarme deletado com successo!", Toast.LENGTH_LONG);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_locais){
            Intent intent = new Intent(getApplicationContext(), LocaisVisitadosActivity.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.action_sobre){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
