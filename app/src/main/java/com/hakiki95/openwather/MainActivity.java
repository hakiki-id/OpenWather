package com.hakiki95.openwather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;

public class MainActivity extends AppCompatActivity {

    EditText kotaEdit ;
    Button cariTmbl;
    TextView cuaca, suhu, tekanan, lembab, degAngin,kecAngin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        kotaEdit = (EditText) findViewById(R.id.kotaEdit);
        cariTmbl = (Button) findViewById(R.id.cariTmbl);
        cuaca = (TextView) findViewById(R.id.cuacaText);
        suhu = (TextView) findViewById(R.id.suhu);
        tekanan = (TextView) findViewById(R.id.tekanan);
        lembab = (TextView) findViewById(R.id.lembab);
        degAngin = (TextView) findViewById(R.id.degAngin);
        kecAngin = (TextView) findViewById(R.id.kecAngin);

        cariTmbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String input = kotaEdit.getText().toString()+",ID"+ "&appid=5060dcc0a8a3624a74d30c1182c14983";
                JSONParser task = new JSONParser();
                task.execute(input);
            }
        });
    }

    private  String getData (String inp) {
        String data1=null;
        final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
        InputStream is ;
        URL u ;
        HttpURLConnection uc ;

        try {
            uc = (HttpURLConnection) (new URL(BASE_URL + inp)).openConnection();
            uc.setRequestMethod("GET");

            uc.setReadTimeout(10000  /*millisecon */);
            uc.setConnectTimeout(15000 /*millisecond*/);

            uc.setDoInput(true);
            uc.setDoOutput(true);


            uc.connect();

            StringBuffer buffer  = new StringBuffer();
            is=uc.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line = null;
            while ((line=br.readLine()) != null) {

                buffer.append(line + "\r\n");

                is.close();;
                uc.disconnect();
                data1 =buffer.toString();
            }



        } catch (MalformedURLException ex){
            ex.printStackTrace();}
        catch (IOException e)
        {
            e.printStackTrace();
        }


        return data1;
    }

    private class  JSONParser extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            String data = getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);

            try {
                JSONArray cuacaArray = (new JSONObject(data)).getJSONArray("weather");
                cuaca.setText(cuacaArray.getJSONObject(0).getString("main") + " ( " +
                cuacaArray.getJSONObject(0).getString("description") + " ) :"  );

                JSONObject mainobjek = (new JSONObject(data)).getJSONObject("main");
                suhu.setText("" + Math.round(mainobjek.getDouble("temp") - 273.15)+ "C");
                tekanan.setText("" + mainobjek.getDouble("pressure") + "hPa");
                lembab.setText(""+mainobjek.getInt("humidity") + "%");

                JSONObject windObject = (new JSONObject(data)).getJSONObject("wind");

                kecAngin.setText(""+windObject.getDouble("speed") + "m/s");
                degAngin.setText(""+windObject.getDouble("deg") + "deg");



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
