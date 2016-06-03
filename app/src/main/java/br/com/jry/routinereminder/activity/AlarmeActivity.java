package br.com.jry.routinereminder.activity;
import br.com.jry.routinereminder.R;
import br.com.jry.routinereminder.dao.AlarmeDao;
import br.com.jry.routinereminder.entity.Alarme;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import java.nio.channels.FileLock;
import java.util.jar.Manifest;

public class AlarmeActivity extends  AppCompatActivity  {
    private Integer id = null;
    private String endereco;
    private Integer distancia;
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarme);

        setTitle("Routine Reminder - Cadastro");

        Alarme alarme = (Alarme) getIntent().getSerializableExtra("alarme");
        if(alarme!= null){
            setTitle("Routine Reminder - Editar");
            this.id = alarme.getId();
            EditText titulo = (EditText) findViewById(R.id.etTituloAlarme);
            EditText msg = (EditText) findViewById(R.id.etMsgAlarme);
            EditText contato = (EditText) findViewById(R.id.etContatos);

            titulo.setText(alarme.getDescricao());
            msg.setText(alarme.getMensagem());
            contato.setText(alarme.getContatos());

            endereco = alarme.getEndereco();
            latitude = alarme.getLatitude();
            longitude = alarme.getLongitude();
            distancia = alarme.getDistancia();
            setInfoLocal();
        }

        ActionBar actionBar =getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
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
                EditText contato = (EditText) findViewById(R.id.etContatos);

                if(msg.getText().toString().trim().equals("") || titulo.getText().toString().trim().equals("")
                        && latitude == null && longitude == null){
                    Toast toast = Toast.makeText(getApplicationContext(), "Favor preecher Todos os campos!", Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    AlarmeDao alarmeDao = new AlarmeDao(getApplicationContext());
                    Alarme alarme = new Alarme();
                    alarme.setId(this.id);
                    alarme.setDescricao(titulo.getText().toString());
                    alarme.setMensagem(msg.getText().toString());
                    alarme.setContatos(contato.getText().toString());
                    alarme.setLatitude(latitude);
                    alarme.setLongitude(longitude);
                    alarme.setDistancia(distancia);
                    alarme.setEndereco(endereco);
                    alarmeDao.insertOrUpdate(alarme);
                    Toast toast = Toast.makeText(getApplicationContext(), "Salvo com sucesso!", Toast.LENGTH_LONG);
                    toast.show();
                    this.onBackPressed();
                }

                return true;
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){

                endereco = (String) data.getSerializableExtra("endereco");
                latitude = (Double) data.getSerializableExtra("lat");
                longitude = (Double) data.getSerializableExtra("lng");
                distancia = (Integer) data.getSerializableExtra("distancia");

                setInfoLocal();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(latitude != null){
            setInfoLocal();
        }
    }

    public void setLocal(View view){

        Intent intent = new Intent(this,  MapsActivity.class);
        if(id != null || endereco != null) {
            intent.putExtra("endereco", endereco);
            intent.putExtra("lat", latitude);
            intent.putExtra("lng", longitude);
            intent.putExtra("distancia", distancia);
        }

        startActivityForResult(intent, 1);
    }

    public void setInfoLocal(){
        ((TextView) findViewById(R.id.tvEndereco)).setText(endereco);
        ((TextView) findViewById(R.id.tvLat)).setText(latitude.toString());
        ((TextView) findViewById(R.id.tvLng)).setText(longitude.toString());
        ((TextView) findViewById(R.id.tvDistancia)).setText(distancia.toString()+"m");
    }

}
