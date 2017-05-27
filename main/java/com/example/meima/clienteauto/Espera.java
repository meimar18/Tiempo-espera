package com.example.meima.clienteauto;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Created by meima on 19/07/2016.
 */
public class Espera extends Activity {

    ServerSocket serverSocket;
    TextView segundo;
    //double [] tiempo = new double[15];
    //int posicion;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


///////////// Take from the other acticity the data it gave you ////////////
        Bundle resp = getIntent().getExtras();
        int puerto = Integer.parseInt(resp.getString("port"));
        String address = resp.getString("ipAddress");
        float metrosFila = Float.parseFloat(resp.getString("metrosFila"));
        int color = resp.getInt("color");

        ///////////////// Pon el layout de vista de la aplicación
        setContentView(R.layout.espera);
        RelativeLayout milayout = (RelativeLayout) findViewById(R.id.layoutEspera);
        if(color==R.id.colorAzul) {
            milayout.setBackgroundResource(R.drawable.degradado_azul);
        }
        if(color==R.id.colorAmarillo) {
            milayout.setBackgroundResource(R.drawable.degradado_amarillo);
        }
        if (color==R.id.colorNaranja){
            milayout.setBackgroundResource(R.drawable.degradado_naranja);
        }

        segundo = (TextView) findViewById(R.id.tiempo);


        MyClientTask myClientTask = new MyClientTask(address, puerto, metrosFila);

        myClientTask.execute();

        /*Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();*/

    }


    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        float metrosfila;
        String response = "";

        MyClientTask(String addr, int port, float metros) {
            dstAddress = addr;
            dstPort = port;
            metrosfila =metros;

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;

            try {
                int i = 0;
                int tiempo = 0;
                String preguntar = new String("T?");

                while (true) {
                    socket = new Socket(dstAddress, dstPort);
                    Espera.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            segundo.clearComposingText();
                        }
                    });
                    ByteArrayOutputStream byteArrayOutputStream =
                            new ByteArrayOutputStream(1024);
                    byte[] buffer = new byte[1024];

                    int bytesRead;
                    InputStream inputStream = socket.getInputStream();
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                        response = byteArrayOutputStream.toString("UTF-8");
                    }

                    final String finalRespuesta = response ;

                    ////////// Aquí extraigo del String el tiempo medio! pero falta por extraer la T primera

                    tiempo =Integer.parseInt(finalRespuesta);
                    if (tiempo!= 0) {
                        final int[] localtime = seconds2min(tiempo * metrosfila * 1.563);//aqui hay que añadir los metros a los que se encuentra puesta la pantalla

                        Espera.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (localtime[1] > 9) {
                                    segundo.setText("" + localtime[0] + ":" + localtime[1]);
                                } else {
                                    segundo.setText("" + localtime[0] + ":" + 0 + localtime[1]);

                                }
                            }
                        });
                    }
                    socket.close();
                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } /*finally {
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }*/

            return null;
        }




       /* @Override
        protected void onPostExecute(Void result) {

            TextView segundo = (TextView) findViewById(R.id.tiempo);
            segundo.setText(response);
            super.onPostExecute(result);
        }*/

    }
    /*
        private class SocketServerThread extends Thread {

            static final int SocketServerPORT = 9998;


            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(SocketServerPORT);

                    double [] tiempo = new double[15];
                    int i = 0;
                    while (true) {
                        Socket socket = serverSocket.accept();
                        ByteArrayOutputStream byteArrayOutputStream =
                                new ByteArrayOutputStream(1024);
                        byte[] buffer = new byte[1024];
                        String respuesta = null;
                        int bytesRead;

                        InputStream inputStream = socket.getInputStream();


                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                            respuesta = byteArrayOutputStream.toString("UTF-8");
                        }
                        final String finalRespuesta = respuesta;

                        tiempo[i]=Double.parseDouble(finalRespuesta);
                        i++;
                        if (i>tiempo.length -1){

                            i = 0;
                        }
                        final double media = tiempoaverage(tiempo);


                        final int finalI = i;
                        Espera.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                //segundo.setText(""+media+ finalI);
                            }
                        });


                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


        }
    */
    private int tiempoaverage(int [] temp) {

        int suma = 0;
        int dimension = temp.length;
        int tiempomedio =0;

        for (int i = 0; i < temp.length; i++) {
            if (temp[i] == 0) {
                dimension = dimension - 1;
            } else {
                suma = suma + temp[i];
            }



        }

        tiempomedio = (int)suma / dimension;
        return tiempomedio;
    }
    public int[] seconds2min(double segundos){

        int segundosentero = (int) (segundos%60);

        int minutos = (int)segundos/60;

        int [] tiempomin = new int [2];

        tiempomin[0] = minutos;

        tiempomin[1] = segundosentero;

        return tiempomin;
    }
}