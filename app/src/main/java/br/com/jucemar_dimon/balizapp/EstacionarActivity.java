package br.com.jucemar_dimon.balizapp;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.Calendar;

public class EstacionarActivity extends AppCompatActivity implements View.OnClickListener {

    private static ImageView iconeDistanciaDianteira;
    private static ImageView iconeDistanciaTraseira;
    private static TextView dadosSensorTraseiro;
    private static TextView dadosSensorDianteiro;
    private float valorDistanciaTraseira;
    private float valorDistanciaDianteira;
    private float millisAnterior;
    private FloatingActionButton fabDesligarSom;
    private FloatingActionButton fabDesligarLeds;

    private static float distanciaBaixoRisco;
    private static float distanciaAltoRisco;
    private static float distanciaMedioRisco;

    private boolean ledLigado=true;
    private boolean somLigado=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        distanciaBaixoRisco = (float) 100.00;
        distanciaAltoRisco = (float) 30.00;
        distanciaMedioRisco = (float) 50.00;

        setContentView(R.layout.activity_estacionar);

        fabDesligarLeds = (FloatingActionButton) findViewById(R.id.fab_desligar_leds);
        fabDesligarSom = (FloatingActionButton) findViewById(R.id.fab_desligar_som);

        fabDesligarLeds.setOnClickListener(this);
        fabDesligarSom.setOnClickListener(this);

        iconeDistanciaDianteira = (ImageView) findViewById(R.id.indicador_dianteiro);
        iconeDistanciaTraseira = (ImageView) findViewById(R.id.indicador_traseiro);


        iconeDistanciaTraseira.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flicker));
        iconeDistanciaDianteira.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flicker));

        dadosSensorDianteiro = (TextView) findViewById(R.id.dados_sensor_dianteiro);
        dadosSensorTraseiro = (TextView) findViewById(R.id.dados_sensor_traseiro);


    }

    @Override
    public void onBackPressed() {

        setResult(RESULT_OK, null);
        finish();
        super.onBackPressed();
    }

    public static Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            String dataString = bundle.getString("data");
            String tempDistanciaDianteira = "";
            String tempDistanciaTraseiro = "";
            int i = 0;
            int pos = -1;
            int tamanho = dataString.length();
            while (pos == -1 && i < tamanho) {
                if (dataString.charAt(i) == '-') {
                    pos = i;
                    tempDistanciaDianteira = dataString.substring(0, i);
                    tempDistanciaTraseiro = dataString.substring(i + 1, dataString.length() - 1);
                }
                i++;
            }
            dadosSensorDianteiro.setText(tempDistanciaDianteira);
            dadosSensorTraseiro.setText(tempDistanciaTraseiro);

            controlarCores();
        }

    };

    private static void controlarCores() {
        float valorDistanciaTraseira = Float.parseFloat((String) dadosSensorTraseiro.getText());
        float valorDistanciaDianteira = Float.parseFloat((String) dadosSensorDianteiro.getText());

        if (valorDistanciaTraseira <= distanciaAltoRisco) {
            iconeDistanciaTraseira.setImageResource(R.drawable.traseiro_vermelho);
            iconeDistanciaTraseira.refreshDrawableState();


        } else if (valorDistanciaTraseira <= distanciaMedioRisco) {
            iconeDistanciaTraseira.setImageResource(R.drawable.traseiro_amarelo);
            iconeDistanciaTraseira.refreshDrawableState();
        } else {

            iconeDistanciaTraseira.setImageResource(R.drawable.traseiro_verde);
            iconeDistanciaTraseira.refreshDrawableState();

        }


        if (valorDistanciaDianteira <= distanciaAltoRisco) {
            iconeDistanciaDianteira.setImageResource(R.drawable.dianteiro_vermelho);
            iconeDistanciaDianteira.refreshDrawableState();


        } else if (valorDistanciaDianteira <= distanciaMedioRisco) {
            iconeDistanciaDianteira.setImageResource(R.drawable.dianteiro_amarelo);
            iconeDistanciaDianteira.refreshDrawableState();
        } else {

            iconeDistanciaDianteira.setImageResource(R.drawable.dianteiro_verde);
            iconeDistanciaDianteira.refreshDrawableState();

        }
    }

    private void toConnectionActivity(String data) {

        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("data", data);
        message.setData(bundle);
        ConnectionThread.handler.sendMessage(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_desligar_leds:
                ledLigado=!ledLigado;
                if(ledLigado){
                    fabDesligarLeds.setImageResource(R.drawable.ic_led_on_white_24dp);
                }else{
                    fabDesligarLeds.setImageResource(R.drawable.ic_led_outline_white_24dp);
                }
                toConnectionActivity("l");
                break;
            case R.id.fab_desligar_som:
                somLigado=!somLigado;
                if(somLigado){
                    fabDesligarSom.setImageResource(R.drawable.ic_volume_high_white_24dp);
                }else{
                    fabDesligarSom.setImageResource(R.drawable.ic_volume_low_white_24dp);
                }
                toConnectionActivity("s");
                break;
        }

    }
}
