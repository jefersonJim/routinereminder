package br.com.jry.routinereminder.service;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.jry.routinereminder.R;
import br.com.jry.routinereminder.dao.AlarmeDao;
import br.com.jry.routinereminder.entity.Alarme;

public class AlarmeService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    List<Alarme> alarmes = new ArrayList<>();
    LatLng myLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    boolean exec = true; // variavel de controle
    private final long[] vibrate = new long[]{150,300,150,600, 150, 300,600};
    AlarmeDao dao = new AlarmeDao(this);

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (exec) {
            exec = false;
            mGoogleApiClient.connect();
        }
        alarmes = dao.getListAlarmesAtivos();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
        createLocationRequest();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


    @Override
    public void onLocationChanged(Location location) {
            for (int i = 0; i < alarmes.size(); i++){
                Alarme alarme = alarmes.get(i);
                LatLng latlng = new LatLng(alarme.getLatitude(), alarme.getLongitude());
                LatLng atual = new LatLng(location.getLatitude(), location.getLongitude());
                if(getDistance(latlng, atual) < alarme.getDistancia()){
                    Log.i(alarme.getDescricao(), alarme.getMensagem());
                    sendNotification(alarme);
                    if(!alarme.getContatos().equals("")){
                        sendSMS(alarme);
                    }
                    dao.desativarAlarmeVisitado(alarme);
                    alarmes.remove(alarme);
                }
            }
            if(alarmes.size() == 0){
                stopSelf();
            }
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public double getDistance(LatLng LatLng1, LatLng LatLng2) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(LatLng1.latitude);
        locationA.setLongitude(LatLng1.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(LatLng2.latitude);
        locationB.setLongitude(LatLng2.longitude);
        distance = locationA.distanceTo(locationB);
        return distance;
    }

    public void sendNotification(Alarme alarme){

        Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_marker)
                        .setContentTitle(alarme.getDescricao())
                        .setContentText(alarme.getMensagem())
                        .setVibrate(vibrate)
                        .setSound(som);
        NotificationManager mNotificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(alarme.getId(),mBuilder.build());
    }

    public void sendSMS(Alarme alarme){
        PendingIntent sendPI = PendingIntent.getBroadcast(this, 0 , new Intent("SMS enviado"), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS entregue"), 0);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getBaseContext(), getResultCode(),Toast.LENGTH_LONG).show();
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter("SMS Enviado"));

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(alarme.getContatos(), null, alarme.getMensagem(), sendPI, deliveredPI);

        unregisterReceiver(broadcastReceiver);
    }
}
