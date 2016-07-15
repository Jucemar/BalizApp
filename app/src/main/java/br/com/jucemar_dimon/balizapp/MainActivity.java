package br.com.jucemar_dimon.balizapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static int ENABLE_BLUETOOTH = 1;
    public static int SELECT_PAIRED_DEVICE = 2;
    public static int SELECT_DISCOVERED_DEVICE = 3;
    public static int FININSH_BALIZA = 5;
    public static int INIT_BALIZA = 4;

    private static ImageButton btnIniciarBaliza;

    static TextView statusMessage;
    static TextView textSpace;
    ConnectionThread connect;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnIniciarBaliza= (ImageButton) findViewById(R.id.button_iniciar_baliza);
        btnIniciarBaliza.setEnabled(false);
        btnIniciarBaliza.setBackgroundColor(Color.parseColor("#FFFF5252"));
        btnIniciarBaliza.setOnClickListener(this);
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            mostrarMensagens("Que pena! Hardware Bluetooth não está funcionando :(");
        } else {
            mostrarMensagens("Ótimo! Hardware Bluetooth está funcionando :)");
            if (!btAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
                mostrarMensagens("Solicitando ativação do Bluetooth...");
            } else {
                mostrarMensagens("Bluetooth ativado");
            }
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(requestCode == ENABLE_BLUETOOTH) {
            if(resultCode == RESULT_OK) {
                mostrarMensagens("Bluetooth ativado");
            }
            else {
                mostrarMensagens("Bluetooth desativado");
            }
        }
        else if(requestCode == SELECT_PAIRED_DEVICE || requestCode == SELECT_DISCOVERED_DEVICE) {
            if(resultCode == RESULT_OK) {
                mostrarMensagens("Você selecionou " + data.getStringExtra("btDevName") + "\n"
                        + data.getStringExtra("btDevAddress"));

                connect = new ConnectionThread(data.getStringExtra("btDevAddress"));
                connect.start();

            }
            else {
                mostrarMensagens("Nenhum dispositivo selecionado :(");
            }
        }else if(requestCode == INIT_BALIZA){
            if(resultCode==RESULT_OK){
                connect.write("d".getBytes());
            }
        }


    }

    public void searchPairedDevices(View view) {

        Intent searchPairedDevicesIntent = new Intent(this, PairedDevices.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_PAIRED_DEVICE);
    }

    public void discoverDevices(View view) {

        Intent searchPairedDevicesIntent = new Intent(this, DiscoveredDevices.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_DISCOVERED_DEVICE);
    }

    public void iniciar() {
        connect.write("i".getBytes());
    }

    public static Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString = new String(data);

            if (dataString.equals("---N")) {

                //mostrarMensagens("Ocorreu um erro durante a conexão D:");
            } else if (dataString.equals("---S")) {
                btnIniciarBaliza.setEnabled(true);
                btnIniciarBaliza.setBackgroundColor(Color.parseColor("#4caf50"));
                //statusMessage.setText("Conectado :D");
            } else {

                //textSpace.setText(new String(data));
            }
        }
    };

    private void mostrarMensagens(String msg) {


        Snackbar.make(findViewById(R.id.layoutPrincipal),msg,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        if(btnIniciarBaliza.isEnabled()){
            iniciar();
            Intent baliza = new Intent(this, EstacionarActivity.class);

            startActivityForResult(baliza, INIT_BALIZA);
        }
    }
}