package br.com.jry.routinereminder.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import android.widget.Toast;

import java.util.List;

import br.com.jry.routinereminder.R;
import br.com.jry.routinereminder.adapter.AlarmeAdapter;
import br.com.jry.routinereminder.dao.AlarmeDao;
import br.com.jry.routinereminder.entity.Alarme;

public class MainActivity extends AppCompatActivity {
    private AlarmeAdapter adapter;
    private List<Alarme> alarmes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AlarmeDao alarmeDao = new AlarmeDao(getApplicationContext());
        this.alarmes = alarmeDao.getListAlarmes();

        if(this.alarmes.size() > 0){
            setContentView(R.layout.activity_main);

            ListView listView = (ListView) findViewById(R.id.listAlarmeView);
            this.adapter = new AlarmeAdapter(this.alarmes, getApplicationContext());
            listView.setAdapter(adapter);
            registerForContextMenu(listView);
        }else{
            setContentView(R.layout.empty_layout);
        }
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
            default:
                return super.onContextItemSelected(item);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
