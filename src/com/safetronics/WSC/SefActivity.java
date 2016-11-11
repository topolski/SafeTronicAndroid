package com.safetronics.WSC;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.safetronics.WSC.RestClient.RequestMethod;
import com.safetronics.WSC.RestClient.RestClient;
import org.json.JSONObject;

/**
 * Created by Tope on 8/14/2014.
 */
public class SefActivity extends Activity {
    private String sid;
    private String naziv;
    private String slika;
    private String opis;
    private String cena;
    private String kategorijaNaziv;
    private String boja;
    private String brava;
    private String ubrava;
    private String zabravljivanje;
    private String tip;
    private String sv;
    private String ss;
    private String sd;
    private String uv;
    private String us;
    private String ud;
    private String police;
    private String zapremina;
    private String tezina;
    private String created;
    private String updated;
    private TextView tnaziv;
    private TextView topis;
    private TextView tcena;
    private TextView tkategorija;
    private TextView tboja;
    private TextView tbrava;
    private TextView tubrava;
    private TextView tzabravljivanje;
    private TextView ttip;
    private TextView tsv;
    private TextView tss;
    private TextView tsd;
    private TextView tuv;
    private TextView tus;
    private TextView tud;
    private TextView tpolice;
    private TextView tzapremina;
    private TextView ttezina;
    private TextView tcreated;
    private TextView tupdated;
    private ImageView islika;
    private Button edit;
    private View.OnClickListener droidTapListenerEdit;
    private Button delete;
    private View.OnClickListener droidTapListenerDelete;
    private ProgressDialog pDialog;
    private RestClient client;
    private String poruka;
    private Boolean ok = false;

    private static String url = "http://rwstope.esy.es/api/sefovi/";
    private static final String TAG_NAZIV = "naziv";
    private static final String TAG_OPIS = "opis";
    private static final String TAG_CENA = "cena";
    private static final String TAG_BOJA = "boja";
    private static final String TAG_BRAVA = "brava";
    private static final String TAG_UBRAVA = "ubrava";
    private static final String TAG_ZABRAVLJIVANJE = "zabravljivanje";
    private static final String TAG_TIP = "tip";
    private static final String TAG_SV = "sv";
    private static final String TAG_SS = "ss";
    private static final String TAG_SD = "sd";
    private static final String TAG_UV = "uv";
    private static final String TAG_US = "us";
    private static final String TAG_UD = "ud";
    private static final String TAG_POLICE = "police";
    private static final String TAG_ZAPREMINA = "zapremina";
    private static final String TAG_TEZINA = "tezina";
    private static final String TAG_SLIKA = "slika";
    private static final String TAG_NAZIV_AKCIJE = "nazivAkcije";
    private static final String TAG_CREATED_AT = "created_at";
    private static final String TAG_UPDATED_AT = "updated_at";
    private static final String TAG_ERROR = "error";
    private static final String TAG_KATEGORIJA = "kategorija";
    private static final String TAG_ID = "id";
    private static final String TAG_SEFOVI = "sefovi";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sef);

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent
        sid = in.getStringExtra(TAG_ID);

        initializeApp();
        new GetSef().execute();
    }

    private void initializeApp() {
        client = new RestClient(url + sid);
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        final String token  = globalVariable.getToken();
        final String header = globalVariable.getHeader();
        client.addHeader(header, token);
        tnaziv = (TextView) findViewById(R.id.lblSNaziv);
        tkategorija = (TextView) findViewById(R.id.lblSKategorija);
        topis = (TextView) findViewById(R.id.lblSOpis);
        tcena = (TextView) findViewById(R.id.lblSCena);
        tboja = (TextView) findViewById(R.id.lblSBoja);
        tbrava = (TextView) findViewById(R.id.lblSBrava);
        tubrava = (TextView) findViewById(R.id.lblSuBrava);
        tzabravljivanje = (TextView) findViewById(R.id.lblSZabravljivanje);
        ttip = (TextView) findViewById(R.id.lblSTip);
        tsv = (TextView) findViewById(R.id.lblSsv);
        tss = (TextView) findViewById(R.id.lblSss);
        tsd = (TextView) findViewById(R.id.lblSsd);
        tuv = (TextView) findViewById(R.id.lblSuv);
        tus = (TextView) findViewById(R.id.lblSus);
        tud = (TextView) findViewById(R.id.lblSud);
        tpolice = (TextView) findViewById(R.id.lblSPolice);
        tzapremina = (TextView) findViewById(R.id.lblSZapremina);
        ttezina = (TextView) findViewById(R.id.lblSTezina);
        tcreated = (TextView) findViewById(R.id.lblSCreated);
        tupdated = (TextView) findViewById(R.id.lblSUpdated);
        islika = (ImageView) findViewById(R.id.imageViewSef);
        edit = (Button) findViewById(R.id.sEdit);
        delete = (Button) findViewById(R.id.sDelete);

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
        String message = getString(R.string.labelMessageBoxBodyZaSef);
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
        Intent i = new Intent(this, SefEditNewActivity.class);
        i.putExtra(TAG_ID, sid);
        startActivity(i);
        finish();
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetSef extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SefActivity.this);
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
                        JSONObject s = jObj.getJSONObject(TAG_SEFOVI);
                        naziv = "Naziv sefa: " + s.getString(TAG_NAZIV);
                        JSONObject kategorija = s.getJSONObject(TAG_KATEGORIJA);
                        kategorijaNaziv = "Kategorija: " + kategorija.getString(TAG_NAZIV);
                        cena = "Cena: " + s.getString(TAG_CENA) + " €";
                        opis = "Opis: " + s.getString(TAG_OPIS);
                        boja = "Boja: " + s.getString(TAG_BOJA);
                        brava = "Brava: " + s.getString(TAG_BRAVA);
                        ubrava = "Unutrašnja brava: " + s.getString(TAG_UBRAVA);
                        zabravljivanje = "Zabravljivanje: " + s.getString(TAG_ZABRAVLJIVANJE);
                        tezina = "Težina: " + s.getString(TAG_TEZINA) + " kg";
                        zapremina = "Zapremina: " + s.getString(TAG_ZAPREMINA) + " mm3";
                        sv = "Spoljašnja visina: " + s.getString(TAG_SV) + " mm";
                        ss = "Spoljašnja širina: " + s.getString(TAG_SS) + " mm";
                        sd = "Spoljašnja dubina: " + s.getString(TAG_SD) + " mm";
                        uv = "Unutrašnja visina: " + s.getString(TAG_UV) + " mm";
                        us = "Unutrašnja širina: " + s.getString(TAG_US) + " mm";
                        ud = "Unutrašnja dubina: " + s.getString(TAG_UD) + " mm";
                        police = "Police: " + s.getString(TAG_POLICE);
                        tip = "Tip: " + s.getString(TAG_TIP);
                        slika = s.getString(TAG_SLIKA);
                        created = "Kreiran: " + s.getString(TAG_CREATED_AT);
                        updated = "Izmenjen: " + s.getString(TAG_UPDATED_AT);
                    }
                }
            }catch (Exception e) {
                Log.e("Service", "Greška: "+e.toString());
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
            tnaziv.setText(naziv);
            topis.setText(opis);
            tcena.setText(cena);
            tbrava.setText(brava);
            tubrava.setText(ubrava);
            ttip.setText(tip);
            ttezina.setText(tezina);
            tzapremina.setText(zapremina);
            tzabravljivanje.setText(zabravljivanje);
            tboja.setText(boja);
            tsv.setText(sv);
            tss.setText(ss);
            tsd.setText(sd);
            tuv.setText(uv);
            tus.setText(us);
            tud.setText(ud);
            tcreated.setText(created);
            tupdated.setText(updated);
            tkategorija.setText(kategorijaNaziv);
            tpolice.setText(police);
            byte[] decodedString = Base64.decode(slika, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            islika.setImageBitmap(decodedByte);
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
            pDialog = new ProgressDialog(SefActivity.this);
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
            Intent i = new Intent(this, SefoviActivity.class);
            startActivity(i);
            finish();
        }else{
            Toast.makeText(this, poruka, Toast.LENGTH_LONG).show();
        }
    }
}
