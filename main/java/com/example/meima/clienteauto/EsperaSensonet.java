package com.example.meima.clienteauto;

/**
 * Created by meima on 17/12/2016.
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
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;




/**
 * Created by meima on 19/07/2016.
 */
public class EsperaSensonet extends Activity {

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
        boolean entrarenvio = false;
        String response = "";

        MyClientTask(String addr, int port, float metros) {
            dstAddress = addr;
            dstPort = port;
            metrosfila = metros;

        }

        @Override
        protected Void doInBackground(Void... arg0) {

                int i = 0;
                int tiempo = 0;
                String preguntar = "T?";
                Socket socket2=null;
                PrintStream printStream=null;

                while (entrarenvio == false) {


                    try {
                        entrarenvio = true;
                        socket2 = new Socket(dstAddress, dstPort);

                        Log.d("doInbackground","hemos entrado en el socket");
                        OutputStream outputStream = socket2.getOutputStream();
                        //DataInputStream inputStream = new DataInputStream(socket2.getInputStream());
                        printStream = new PrintStream(outputStream);
                        printStream.print(preguntar);
                        printStream.close();
                        ByteArrayOutputStream byteArrayOutputStream =
                                new ByteArrayOutputStream(1024);
                        byte[] buffer = new byte[1024];

                        int bytesRead;
                        InputStream inputStream = socket2.getInputStream();
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            byteArrayOutputStream.write(buffer, 0, bytesRead);

                            Log.d("read","hemos leido el socket");
                            response = byteArrayOutputStream.toString();
                            //response = new String(buffer);
                        }


                    /*EsperaSensonet.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Context context = getApplicationContext();
                            Toast toast = Toast.makeText(context, "entro", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                    });*/
                    }catch (UnknownHostException e){
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Context context = getApplicationContext();
                        Toast toast = Toast.makeText(context, "Algo ha ido mal!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        entrarenvio = true;

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Context context = getApplicationContext();
                        Toast toast = Toast.makeText(context, "Algo ha ido mal!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        entrarenvio = true;
                        //message += "Something wrong! " + e.toString() + "\n";
                    }finally {

                        Log.d("finally","hemos salido del while del socket");
                        if (socket2.isConnected()) {
                            try {
                                printStream.close();
                                socket2.close();



                            }catch (IOException e){

                            }

                        }
                    }


                }
         return null;
        }
        protected void onPostExecute(Void result) {

            Log.d("onPostExecute","cambiamos lo que hemos recibido");

            segundo.setText(response);

            super.onPostExecute(result);
        }

    }




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
