package br.com.jry.routinereminder.activity;
import br.com.jry.routinereminder.R;
import br.com.jry.routinereminder.dao.AlarmeDao;
import br.com.jry.routinereminder.entity.Alarme;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class AlarmeActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarme);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarme, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_saveAlarm :
                EditText titulo = (EditText) findViewById(R.id.etTituloAlarme);
                EditText msg = (EditText) findViewById(R.id.etMsgAlarme);

                if(msg.getText().toString().trim().equals("") && titulo.getText().toString().trim().equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Favor preecher Todos os campos", Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    AlarmeDao alarmeDao = new AlarmeDao(getApplicationContext());
                    Alarme alarme = new Alarme();
                    alarme.setDescricao(titulo.getText().toString());
                    alarme.setMensagem(msg.getText().toString());
                    alarmeDao.insert(alarme);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    this.finish();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
