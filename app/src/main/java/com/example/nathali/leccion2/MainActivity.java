package com.example.nathali.leccion2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SensorEventListener{

    TextView resultnuevo;
    NotificationCompat.Builder notification1;
    private static final int uniqueID=45612;

    private Sensor nmSensor;
    private SensorManager SM;
    private String result2;


    private Integer x,y,z;

    private Handler handler = new Handler();
    private AccelerometerThread aHandlerThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notification1 = new NotificationCompat.Builder(this);
        notification1.setAutoCancel(true);

        resultnuevo=(TextView) findViewById(R.id.textresult);

        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        nmSensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SM.registerListener(this,nmSensor,SensorManager.SENSOR_DELAY_NORMAL);

        aHandlerThread = new AccelerometerThread("aHandlerThread");

        aHandlerThread.start();
        aHandlerThread.prepareHandler();

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    public void onSensorChanged(SensorEvent event){

             x = (int) event.values[0];
             y = (int) event.values[1];
             z = (int) event.values[2];

             HiloSensor();

    }

    protected void HiloSensor(){

        final Runnable myRunnable = new Runnable() {

            @Override
            public void run() {

                try {
                    //Cada 10 segundos
                    Thread.sleep(10000);
                    //Cada 2 minutos
                    //Thread.sleep(120000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        result2 = "X: "+x+";Y: "+y+";Z: "+z;
                        System.out.println(result2);

                        Toast.makeText(getApplicationContext(),result2, Toast.LENGTH_LONG).show();

                    }
                });

            }
        };

        aHandlerThread.postTask(myRunnable);
    }


    public void LlamarRes(View v){


       if(estaConectado()){
           Intent intent= new Intent(MainActivity.this,ServiceRes.class);
           startService(intent);


       }else{
            Toast.makeText(MainActivity.this, "Requiere Conexión a Internet", Toast.LENGTH_LONG).show();
        }

    }

    private BroadcastReceiver BReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {

            String receivemessage1= intent.getStringExtra("internetText");
            String receivemessage2= intent.getStringExtra("restnotification");

            resultnuevo.setText(receivemessage1);
            crearNotificacion(receivemessage2);

        }
    };

    public void crearNotificacion(String receivemessage){

        notification1.setSmallIcon(R.drawable.robotina);
        notification1.setTicker("Robotina");
        notification1.setWhen(System.currentTimeMillis());
        notification1.setContentTitle("Robotina Resultado");
        notification1.setContentText(receivemessage);

        Intent intent  = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notification1.setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID,notification1.build());

    }
    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(BReceiver, new IntentFilter("message"));
    }

    protected void onPause (){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(BReceiver);
        SM.unregisterListener(this);
    }

    protected void onDestroy() {
        super.onDestroy();

    }

    protected Boolean estaConectado(){

        if(conectadoWifi()){
            return true;
        }else if(conectadoRedMovil()){
            return true;
        }else{
            return false;
        }
    }


    protected Boolean conectadoWifi(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected Boolean conectadoRedMovil(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
}
