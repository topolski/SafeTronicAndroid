package com.safetronics.WSC;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import com.safetronics.WSC.FormGenerator.FormActivity;
import com.safetronics.WSC.RestClient.RequestMethod;
import com.safetronics.WSC.RestClient.RestClient;
import org.json.JSONObject;

/**
 * Created by Tope on 8/19/2014.
 */
public class KategorijaEditNewActivity extends FormActivity {

    private String kid;
    private GlobalClass globalVariable;
    private String token;
    private String header;
    private RestClient client;
    private JSONObject obj;
    private ProgressDialog pDialog;
    private String poruka;
    private String forma;

    public static final int OPTION_SAVE = 0;
    public static final int OPTION_CANCEL = 1;

    private static String url = "http://rwstope.esy.es/api";
    private static final String TAG_ID = "id";
    private static final String TAG_NAZIV_AKCIJE = "nazivAkcije";
    private static final String TAG_ERROR = "error";
    private static final String TAG_NAZIV_KATEGORIJE = "nazivKategorije";
    private static final String TAG_NAZIV_KATEGORIJE_JSON = "naziv_kategorije";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent in = getIntent();
        kid = in.getStringExtra(TAG_ID);
        globalVariable = (GlobalClass) getApplicationContext();
        token  = globalVariable.getToken();
        header = globalVariable.getHeader();

        initializeApp();
    }

    private void initializeApp() {
        new GetForma().execute();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        menu.add( 0, OPTION_SAVE, 0, "Save" );
        menu.add( 0, OPTION_CANCEL, 0, "Cancel" );
        return true;
    }

    @Override
    public boolean onMenuItemSelected( int id, MenuItem item )
    {

        switch( item.getItemId() )
        {
            case OPTION_SAVE:
                obj = save();
                new SaveKategorija().execute();
                break;

            case OPTION_CANCEL:
                break;
        }

        return super.onMenuItemSelected( id, item );
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class SaveKategorija extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(KategorijaEditNewActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            RestClient client;
            if(kid != null) {
                client = new RestClient(url + "/kategorije/" + kid);
            }else {
                client = new RestClient(url + "/kategorije");
            }
            client.addHeader(header, token);
            try {
               client.addParam(TAG_NAZIV_KATEGORIJE, obj.getString(TAG_NAZIV_KATEGORIJE_JSON));
            }catch (Exception e){
                Log.e("Greška: ", e.toString());
            }
            try {
                if(kid != null){
                    client.execute(RequestMethod.PUT);
                }else{
                    client.execute(RequestMethod.POST);
                }
                if(client.getResponseCode() == 200) {
                    JSONObject jObj = new JSONObject(client.getResponse());
                    if (!jObj.getBoolean(TAG_ERROR)) {
                        poruka = jObj.getString(TAG_NAZIV_AKCIJE);
                    }else{
                        poruka = jObj.getString(TAG_NAZIV_AKCIJE);
                    }
                }else{
                    poruka = "Greška, kod: " + client.getResponseCode();
                }
            }catch (Exception e) {
                Log.e("Service", "Greška: " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            touchSaveButton();
        }
    }

    private void touchSaveButton() {
        Toast.makeText(this, poruka, Toast.LENGTH_LONG).show();
    }

    private class GetForma extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(KategorijaEditNewActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if(kid != null){
                client = new RestClient(url + "/kategorije/" + kid + "/edit");
            }else{
                client = new RestClient(url + "/kategorije/create");
            }
            client.addHeader(header, token);
            try {
                client.execute(RequestMethod.GET);
                if(client.getResponseCode() == 200){
                    JSONObject jObj = new JSONObject(client.getResponse());
                    if(jObj.getBoolean(TAG_ERROR)){
                        Log.e("Greška: ", jObj.getString(TAG_NAZIV_AKCIJE));
                    }else{
                        forma = jObj.getString("formaAndroid");
                    }
                }else{
                    Log.e("Greška: ", "Kod: " + client.getResponseCode());
                }
            }catch (Exception e) {
                Log.e("Greška: ",  e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            generateForm(forma);
        }
    }
}
