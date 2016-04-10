package br.com.jry.routinereminder.activity;
import br.com.jry.routinereminder.R;
import br.com.jry.routinereminder.dao.AlarmeDao;
import br.com.jry.routinereminder.entity.Alarme;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
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
import com.google.android.gms.maps.SupportMapFragment;

public class AlarmeActivity extends  AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Integer id = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarme);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Alarme alarme = (Alarme) getIntent().getSerializableExtra("alarme");
        if(alarme!= null){
            this.id = alarme.getId();
            EditText titulo = (EditText) findViewById(R.id.etTituloAlarme);
            EditText msg = (EditText) findViewById(R.id.etMsgAlarme);

            titulo.setText(alarme.getDescricao());
            msg.setText(alarme.getMensagem());

        }
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

                if(msg.getText().toString().trim().equals("") || titulo.getText().toString().trim().equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Favor preecher Todos os campos!", Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    AlarmeDao alarmeDao = new AlarmeDao(getApplicationContext());
                    Alarme alarme = new Alarme();
                    alarme.setId(this.id);
                    alarme.setDescricao(titulo.getText().toString());
                    alarme.setMensagem(msg.getText().toString());
                    alarmeDao.insertOrUpdate(alarme);
                    Toast toast = Toast.makeText(getApplicationContext(), "Salvo com sucesso!", Toast.LENGTH_LONG);
                    toast.show();
                    this.onBackPressed();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

    }
}
