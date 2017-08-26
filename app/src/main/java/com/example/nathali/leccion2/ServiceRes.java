package com.example.nathali.leccion2;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by nathali on 24/08/17.
 */

public class ServiceRes extends IntentService {



    public ServiceRes() {
        super("ServiceRes");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                try {
                    System.out.println("Servicio iniciado");
                    new requestREST().execute().get();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        }, 0, 10000);



    }

    private class requestREST extends AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            String devuelve = "";
            ArrayList<String> listaString = new ArrayList<String>();

            URL url = null;

            try {
                //URL Correcto
                url = new URL("https://jsonplaceholder.typicode.com/posts");

                //URL incorrecto
                //url = new URL("http://paseoporlagranja.com/yi");


            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }

            HttpURLConnection connection = null;
            int respuesta;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0*(Linux;Android 1.5;es-ES)Ejemplo HTTP");

                respuesta = connection.getResponseCode();

                if (respuesta == 200) {

                    try {

                        connection = (HttpURLConnection) url.openConnection();
                        InputStream in = connection.getInputStream();
                        InputStreamReader reader = new InputStreamReader(in);

                        int data = reader.read();
                        String articleInfo = "";

                        while (data != -1) {
                            char current = (char) data;
                            articleInfo += current;
                            data = reader.read();
                        }


                        JSONObject respuestaJSON=null;

                        JSONArray jsonArray = new JSONArray(articleInfo);

                        for (int i = 0; i < 5; i++) {


                            respuestaJSON = jsonArray.getJSONObject(i);
                            Integer iduser = respuestaJSON.getInt("userId");
                            Integer id = respuestaJSON.getInt("id");
                            String title = respuestaJSON.getString("title");
                            String body = respuestaJSON.getString("body");

                            int m=i+1;
                            String iduser2 = String.valueOf(iduser);
                            String id2 = String.valueOf(id);
                            devuelve +="Resultado: "+m+".-IdUser: " + iduser2 + ";Id: " + id2 +";Title: " + title+";Body: " + body+"\n";

                            //System.out.println(devuelve);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    listaString.add(devuelve);
                    listaString.add("Rest Exitoso");

                }else{

                    listaString.add("URL incorrecto");
                    listaString.add("Rest Fallido");

                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            return listaString;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {

            sendBroadcast(result.get(0),result.get(1));
            result.remove(1);
            result.remove(0);

        }
    }

    private void sendBroadcast(String resultado1, String resultado2){
        Intent intent = new Intent ("message");
        intent.putExtra("internetText", resultado1);
        intent.putExtra("restnotification", resultado2);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
