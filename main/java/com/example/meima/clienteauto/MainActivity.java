package com.example.meima.clienteauto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.net.ServerSocket;

public class MainActivity extends Activity {

    TextView textResponse;
    EditText editTextAddress, editTextPort,editTextMetrosFila;
    Button buttonConnect, buttonClear;
    int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextAddress = (EditText) findViewById(R.id.address);
        editTextPort = (EditText) findViewById(R.id.port);
        editTextMetrosFila = (EditText)findViewById(R.id.metrosfila);
    }

    public void aEspera(View vista) {

        Intent i = new Intent(this, EsperaSensonet2.class);
        i.putExtra("port", editTextPort.getText().toString());
        i.putExtra("ipAddress", editTextAddress.getText().toString());
        i.putExtra("metrosFila", editTextMetrosFila.getText().toString());
        i.putExtra("color",((RadioGroup)findViewById(R.id.colores)).getCheckedRadioButtonId());

        startActivity(i);
        //finish();

    }


}