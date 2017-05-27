package com.example.meima.clienteauto;

/**
 * Created by meima on 11/01/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


/**
 * Created by meima on 19/07/2016.
 */
public class EsperaSensonet2 extends Activity {

    private TextView segundo;
    private boolean recepcion;
    private String response= "0013";
    int puerto;
    String address;
    String result;
    float metrosFila;
    DatagramSocket datagramSocket;
    MyClientTask myClientTask;
    /*Timer timer = new Timer();

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            boolean a = myClientTask.getStatus().equals(AsyncTask.Status.FINISHED);
            if (a) {
                myClientTask.cancel(true);
                myClientTask = new MyClientTask(address, puerto, metrosFila);
                myClientTask.execute();

            }
        }
    };*/
    public android.os.Handler handler = new android.os.Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            boolean a = myClientTask.getStatus().equals(AsyncTask.Status.FINISHED);
            if (a) {
                myClientTask.cancel(true);
                myClientTask = new MyClientTask(address, puerto, metrosFila);
                myClientTask.execute();

                handler.postDelayed(this,5000);

            }

        }
    };




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


        myClientTask = new MyClientTask(address, puerto, metrosFila);


        myClientTask.execute();


        handler.postDelayed(runnable,5000);

        //timer.scheduleAtFixedRate(timerTask, 5000, 5000);




        /*Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();*/

    }


    public class MyClientTask extends AsyncTask<Void, Void, String> {

        String dstAddress;
        //DatagramSocket datagramSocket = null;
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
                datagramSocket.setSoTimeout(800);
                int soto = datagramSocket.getSoTimeout();
                byte[] envio = new byte[2];

                envio = preguntar.getBytes();
                DatagramPacket packetenvio = new DatagramPacket(envio, envio.length, InetAddress.getByName(dstAddress), dstPort);
                datagramSocket.connect(InetAddress.getByName(dstAddress), dstPort);
                datagramSocket.send(packetenvio);
                recepcion = true;

            } catch (SocketException e) {
                //  Log.d("doInbackground", "socket envio mal hecho");
                if (datagramSocket.isConnected()) {
                    datagramSocket.disconnect();
                    datagramSocket.close();
                }
                return "T=0000";

            } catch (IOException e) {
                //Log.d("doInbackground", "socket envio mal hecho");
                if (datagramSocket.isConnected()) {
                    datagramSocket.disconnect();
                    datagramSocket.close();
                }
                return "T=0000";
            }

            try {

                //  Log.d("envio", "Entro en el bucle!!!");
                byte[] recibir = new byte[6];
                datagramSocket.setSoTimeout(800);
                int soto = datagramSocket.getSoTimeout();
                DatagramPacket packetrecibo = new DatagramPacket(recibir, recibir.length);

                datagramSocket.receive(packetrecibo);
                result = new String(packetrecibo.getData(), 0, packetrecibo.getLength());


            } catch (SocketTimeoutException e){
                if (datagramSocket.isConnected()) {
                    datagramSocket.disconnect();
                    datagramSocket.close();
                }
                return "T=0000";

            }catch (SocketException e) {
                //Log.d("doInbackground", "socket recepción mal hecho");
                if (datagramSocket.isConnected()) {
                    datagramSocket.disconnect();
                    datagramSocket.close();
                }
                return "T=0000";


            } catch (IOException e) {
                //   Log.d("doInbackground", "socket recepción mal hecho");
                if (datagramSocket.isConnected()) {
                    datagramSocket.disconnect();
                    datagramSocket.close();
                }
                return "T=0000";

            } finally {
                if (datagramSocket.isConnected()) {
                    datagramSocket.disconnect();
                    datagramSocket.close();
                }
            }

            return result;

        }


        protected void onPostExecute(String result) {

            // Log.d("onPostExecute","cambiamos lo que hemos recibido");
            //segundo.setText(result);

            boolean a = result.equals("T=0000");
            String str;

            if (a) {

                str = response.substring(2);
                final int[] localtime = seconds2min(Integer.parseInt(str) * metrosfila * 1.563);
                if (localtime[1] > 9) {
                    segundo.setText("" + localtime[0] + ":" + localtime[1]);
                } else {
                    segundo.setText("" + localtime[0] + ":" + 0 + localtime[1]);
                }

            }else{

                str = result.substring(2);
                int temps = Integer.parseInt(str);

                final int[] localtime = seconds2min(temps * metrosfila * 1.563);//aqui hay que añadir los metros a los que se encuentra puesta la pantalla

                if (localtime[1] > 9) {
                    segundo.setText("" + localtime[0] + ":" + localtime[1]);
                } else {
                    segundo.setText("" + localtime[0] + ":" + 0 + localtime[1]);
                }
                response = result;
            }
            super.onPostExecute(result);
        }

    }

    /*protected void onDestroy(){
        super.onDestroy();
        if (datagramSocket.isConnected()) {
            datagramSocket.disconnect();
            datagramSocket.close();
            handler.removeCallbacks(runnable);
        }


    }*/

    public void onBackPressed() {
        super.onBackPressed();
        if (datagramSocket.isConnected()) {
            datagramSocket.disconnect();
            datagramSocket.close();
            handler.removeCallbacks(runnable);
            this.finish();
        }
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

