package com.code.felipe.appwebservices;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements Response.Listener<JSONObject>, Response.ErrorListener {

    private CardView btnLogin;
    private EditText usuario, password;
    private boolean respuesta, fin;

    RequestQueue requestQueue;

    ProgressDialog progress;

    JsonObjectRequest getRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);


        btnLogin = (CardView) findViewById(R.id.btninicio);

        usuario = (EditText) findViewById(R.id.user);

        password = (EditText) findViewById(R.id.pass);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                autenticacion(usuario.getText().toString(), password.getText().toString());

            }
        });


    }


    public void autenticacion(String user, String pass) {

        progress=new ProgressDialog(this);
        progress.setMessage("Cargando..");
        progress.show();

        String URL = "http://172.16.18.128:8085/AppRest/webresources/generic/login?user=" + user + "&pass=" + pass + "";

        getRequest = new JsonObjectRequest(Request.Method.GET, URL, null, this, this);

        requestQueue.add(getRequest);

        /*JsonObjectRequest getRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {



                        try {
                            if(response.getBoolean("respuesta")==true){

                               // Log.e("exito ",response.getString("message"));
                               // Toast toast1 = Toast.makeText(getApplicationContext(),"Acceso exitoso", Toast.LENGTH_SHORT);
                               // toast1.show();

                                respuesta=response.getBoolean("respuesta");
                                //Log.e("Respuesta es : ",""+respuesta);


                            }else{
                              //  Log.e("fallo al logear ","rr");
                                //Toast toast1 = Toast.makeText(getApplicationContext(),"credenciales Invalidas", Toast.LENGTH_SHORT);
                                respuesta=response.getBoolean("respuesta");
                              //  Log.e("Respuesta es : ",""+respuesta);


                            }

                            // Log.e("Responde rest  ",response.toString());

                            //Log.e("Respuesta final : ",""+respuesta);
                          //  respuesta=fin;
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("Responde rest error ",error.toString());
                        Toast toast1 = Toast.makeText(getApplicationContext(),"Responde rest error "+error.toString(), Toast.LENGTH_SHORT);
                        toast1.show();

                    }
                }


        );*/

        //requestQueue.getCache().clear();
        // requestQueue.add(getRequest);

        // Log.e("Respuesta final2 : ",""+respuesta);
        // Log.e("Respuesta es : ","bb");
        // return respuesta;

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progress.hide();
        Toast toast1 = Toast.makeText(getApplicationContext(), "Responde rest error " + error.toString(), Toast.LENGTH_SHORT);
        toast1.show();
    }

    @Override
    public void onResponse(JSONObject response) {

        try {
            if (response.getBoolean("respuesta") == true) {


                Toast toast1 = Toast.makeText(getApplicationContext(), "Acceso exitoso", Toast.LENGTH_SHORT);
                toast1.show();
                progress.hide();

                Intent page1 = new Intent(this, Main2Activity.class);
                startActivity(page1);


            } else {
                Toast toast1 = Toast.makeText(getApplicationContext(), "credenciales Invalidas", Toast.LENGTH_SHORT);
                toast1.show();
                progress.hide();

            }
        } catch (JSONException e) {
            progress.hide();
            e.printStackTrace();
        }

    }


}
