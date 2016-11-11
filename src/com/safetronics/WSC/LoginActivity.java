package com.safetronics.WSC;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.safetronics.WSC.RestClient.RequestMethod;
import com.safetronics.WSC.RestClient.RestClient;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends Activity {

    private EditText username;
    private EditText pass;
    private Button btnLogin;
    private View.OnClickListener droidTapListener;
    private TextView results;
    private JSONObject jObj;
    private ProgressDialog pDialog;
    private String poruka;
    private String token;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);

        initializeApp();
    }

    private void initializeApp() {
        username = (EditText) findViewById(R.id.editTextUsername);
        pass = (EditText) findViewById(R.id.editTextPassword);
        btnLogin = (Button) findViewById(R.id.buttonLogin);
        results = (TextView) findViewById(R.id.textViewResults);

        droidTapListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                touchLoginButton();
            }
        };
        btnLogin.setOnClickListener(droidTapListener);
    }

    private void touchLoginButton() {
        new Login().execute();
    }

    private void openMainMenu() {
        Intent i = new Intent(this, MainMenuActivity.class);
        i.putExtra("meni", jObj.toString());
        startActivity(i);
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class Login extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            RestClient client = new RestClient("http://rwstope.esy.es/api/login");
            client.addParam("email", username.getText().toString());
            client.addParam("password", pass.getText().toString());
            try {
                client.execute(RequestMethod.POST);
                if(client.getResponseCode() == 200){
                    jObj = new JSONObject(client.getResponse());
                    if(jObj.getBoolean("error")){
                        poruka = jObj.getString("nazivAkcije");
                    }else{
                        poruka = "ok";
                        token = jObj.getString("token");
                    }
                }else{
                    poruka = "Greška, kod: " + client.getResponseCode();
                }
            }catch (Exception e) {
                poruka = "Greška: " + e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            rezultat();
        }
    }

    private void rezultat() {
        if(poruka.toString() == "ok"){
            // Calling Application class (see application tag in AndroidManifest.xml)
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            //Set token in global/application context
            globalVariable.setToken(token);
            openMainMenu();
            //results.setText(poruka);
        }else{
            results.setText(poruka);
        }
    }
}
