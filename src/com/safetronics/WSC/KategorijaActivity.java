package com.safetronics.WSC;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.safetronics.WSC.RestClient.RequestMethod;
import com.safetronics.WSC.RestClient.RestClient;
import org.json.JSONObject;

/**
 * Created by Tope on 8/11/2014.
 */
public class KategorijaActivity extends Activity{

    private String kid;
    private String naziv;
    private String created;
    private String updated;
    private TextView tnaziv;
    private TextView tcreated;
    private TextView tupdated;
    private Button edit;
    private View.OnClickListener droidTapListenerEdit;
    private Button delete;
    private View.OnClickListener droidTapListenerDelete;
    private ProgressDialog pDialog;
    private RestClient client;
    private String poruka;
    private Boolean ok = false;

    private static String url = "http://rwstope.esy.es/api/kategorije/";
    private static final String TAG_NAZIV = "naziv";
    private static final String TAG_NAZIV_AKCIJE = "nazivAkcije";
    private static final String TAG_CREATED_AT = "created_at";
    private static final String TAG_UPDATED_AT = "updated_at";
    private static final String TAG_ERROR = "error";
    private static final String TAG_KATEGORIJE = "kategorije";
    private static final String TAG_ID = "id";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.kategorija);

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        kid = in.getStringExtra(TAG_ID);

        initializeApp();
        new GetKategorija().execute();
    }

    private void initializeApp() {
        client = new RestClient(url+kid);
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        final String token  = globalVariable.getToken();
        final String header = globalVariable.getHeader();
        client.addHeader(header, token);
        tnaziv = (TextView) findViewById(R.id.lblNaziv);
        tcreated = (TextView) findViewById(R.id.lblCreated);
        tupdated = (TextView) findViewById(R.id.lblUpdated);
        edit = (Button) findViewById(R.id.kEdit);
        delete = (Button) findViewById(R.id.kDelete);

        droidTapListenerEdit = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                touchEditButton();
            }
        };
        edit.setOnClickListener(droidTapListenerEdit);

        droidTapListenerDelete = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                touchDeleteButton();
            }
        };
        delete.setOnClickListener(droidTapListenerDelete);
    }

    private void touchDeleteButton() {
        String message = getString(R.string.labelMessageBoxBody);
        String title = getString(R.string.labelMessageBoxTitle);
        String yes = getString(R.string.labelYes);
        String no = getString(R.string.labelNo);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setTitle(title)
                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        youClickedYes();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void youClickedYes() {
        new Delete().execute();
    }

    private void touchEditButton() {
        Intent i = new Intent(this, KategorijaEditNewActivity.class);
        i.putExtra(TAG_ID, kid);
        startActivity(i);
        finish();
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetKategorija extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(KategorijaActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                client.execute(RequestMethod.GET);
                if(client.getResponseCode() == 200) {
                    JSONObject jObj = new JSONObject(client.getResponse());
                    if (!jObj.getBoolean(TAG_ERROR)) {
                        JSONObject k = jObj.getJSONObject(TAG_KATEGORIJE);
                        naziv = k.getString(TAG_NAZIV);
                        created = k.getString(TAG_CREATED_AT);
                        updated = k.getString(TAG_UPDATED_AT);
                    }
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
            /**
             * Updating parsed JSON data into TextViews
             * */
            tnaziv.setText("Naziv kategorije: " + naziv);
            tcreated.setText("Kreirana: " + created);
            tupdated.setText("Izmenjena: " + updated);
        }
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class Delete extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(KategorijaActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                client.execute(RequestMethod.DELETE);
                if(client.getResponseCode() == 200) {
                    JSONObject jObj = new JSONObject(client.getResponse());
                    if (!jObj.getBoolean(TAG_ERROR)) {
                        ok = true;
                        poruka = jObj.getString(TAG_NAZIV_AKCIJE);
                    }else{
                        poruka = jObj.getString(TAG_NAZIV_AKCIJE);
                    }
                }else{
                    poruka = "Greška. Kod: " + client.getResponseCode();
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
            /**
             * Updating parsed JSON data into ListView
             * */
            rezultat();
        }
    }

    private void rezultat() {
        if(ok){
            Toast.makeText(this, poruka, Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, KategorijeActivity.class);
            startActivity(i);
            finish();
        }else{
            Toast.makeText(this, poruka, Toast.LENGTH_LONG).show();
        }

    }
}
