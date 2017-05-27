package com.example.meima.clienteauto;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
//import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created by meima on 12/01/2017.
 */

public class EsperaDatagramSocket extends Activity {

    private TextView segundo;
    private boolean recepcion;
    private String response;
    int puerto;
    String address;
    float metrosFila;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

///////////// Take from the other acticity the data it gave you ////////////
        Bundle resp = getIntent().getExtras();
        puerto = Integer.parseInt(resp.getString("port"));
        address = resp.getString("ipAddress");
        metrosFila = Float.parseFloat(resp.getString("metrosFila"));
        int color = resp.getInt("color");

        ///////////////// Pon el layout de vista de la aplicación
        setContentView(R.layout.espera);
        RelativeLayout milayout = (RelativeLayout) findViewById(R.id.layoutEspera);
        if (color == R.id.colorAzul) {
            milayout.setBackgroundResource(R.drawable.degradado_azul);
        }
        if (color == R.id.colorAmarillo) {
            milayout.setBackgroundResource(R.drawable.degradado_amarillo);
        }
        if (color == R.id.colorNaranja) {
            milayout.setBackgroundResource(R.drawable.degradado_naranja);
        }
        segundo = (TextView) findViewById(R.id.tiempo);


        EsperaDatagramSocket.MyClientTask myClientTask = new EsperaDatagramSocket.MyClientTask(address, puerto, metrosFila);


        myClientTask.execute();


        myClientTask.onPostExecute(response);


        /*Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();*/

    }


    public class MyClientTask extends AsyncTask<Void, Void, String> {

        String dstAddress;
        DatagramSocket datagramSocket = null;
        int dstPort;
        float metrosfila;
        //String response = "";
        String preguntar = "T?";

        MyClientTask(String addr, int port, float metros) {
            dstAddress = addr;
            dstPort = port;
            metrosfila = metros;

        }
        //InetSocketAddress socketAddress = new InetSocketAddress(dstAddress,dstPort);


        protected String doInBackground(Void... voids) {
           // Log.d("doInbackground", "al menos entro");

            try {
                datagramSocket = new DatagramSocket(dstPort);
                byte[] envio = new byte[2];

                envio = preguntar.getBytes();
                DatagramPacket packetenvio = new DatagramPacket(envio, envio.length, InetAddress.getByName(dstAddress), dstPort);
                datagramSocket.connect(InetAddress.getByName(dstAddress), dstPort);
                datagramSocket.send(packetenvio);
                recepcion = true;

            } catch (SocketException e) {
              //  Log.d("doInbackground", "socket envio mal hecho");

            } catch (IOException e) {
                //Log.d("doInbackground", "socket envio mal hecho");
            }

            try {

              //  Log.d("envio", "Entro en el bucle!!!");
                byte[] recibir = new byte[6];
                DatagramPacket packetrecibo = new DatagramPacket(recibir, recibir.length);

                datagramSocket.receive(packetrecibo);
                response = new String(packetrecibo.getData(), 0, packetrecibo.getLength());

                recepcion = false;


            } catch (SocketException e) {
                Log.d("doInbackground", "socket recepción mal hecho");


            } catch (IOException e) {
             //   Log.d("doInbackground", "socket recepción mal hecho");

            } finally {
                if (datagramSocket.isConnected()) {
                    datagramSocket.disconnect();
                    datagramSocket.close();
                }
            }

            return response;

        }


        protected void onPostExecute(String result) {

           // Log.d("onPostExecute","cambiamos lo que hemos recibido");

            segundo.setText(result);

            super.onPostExecute(result);
        }

    }



}

