package com.safetronics.WSC;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import com.safetronics.WSC.FormGenerator.FormActivity;
import com.safetronics.WSC.RestClient.RequestMethod;
import com.safetronics.WSC.RestClient.RestClient;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by Tope on 8/19/2014.
 */
public class SefEditNewActivity extends FormActivity {

    private String sid;
    private GlobalClass globalVariable;
    private String token;
    private String header;
    private String encoded = null;
    private String mimeType = null;
    private String poruka;
    private Bitmap bitmap;
    private ProgressDialog pDialog;
    private JSONObject obj;
    private RestClient client;
    private String forma;

    public static final int OPTION_SAVE = 0;
    public static final int OPTION_UPLOAD = 1;
    public static final int OPTION_CANCEL = 2;

    private static String url = "http://rwstope.esy.es/api";
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
    private static final String TAG_ERROR = "error";
    private static final String TAG_KATEGORIJA = "kategorija";
    private static final String TAG_ID = "id";
    private static final String TAG_ID_KATEGORIJA = "idKategorija";
    private static final String TAG_MIMETYPE = "mimeType";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent in = getIntent();
        sid = in.getStringExtra(TAG_ID);
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
        menu.add( 0, OPTION_UPLOAD, 0, "Upload" );
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
                new SaveSef().execute();
                break;

            case OPTION_UPLOAD:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
                break;

            case OPTION_CANCEL:
                break;
        }

        return super.onMenuItemSelected( id, item );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            String imagepath = getPath(selectedImageUri);
            bitmap= BitmapFactory.decodeFile(imagepath);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            mimeType = "image/jpeg";
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class SaveSef extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SefEditNewActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            RestClient client;
            if(sid != null) {
                client = new RestClient(url + "/sefovi/" + sid);
            }else {
                client = new RestClient(url + "/sefovi");
            }
            client.addHeader(header, token);
            try {
                client.addParam(TAG_MIMETYPE, mimeType);
                client.addParam(TAG_SLIKA, encoded);
                client.addParam(TAG_NAZIV, obj.getString(TAG_NAZIV));
                client.addParam(TAG_OPIS, obj.getString(TAG_OPIS));
                client.addParam(TAG_CENA, obj.getString(TAG_CENA));
                client.addParam(TAG_ID_KATEGORIJA, obj.getString(TAG_KATEGORIJA));
                client.addParam(TAG_BOJA, obj.getString(TAG_BOJA));
                client.addParam(TAG_BRAVA, obj.getString(TAG_BRAVA));
                client.addParam(TAG_UBRAVA, obj.getString("unutrašnja_brava"));
                client.addParam(TAG_ZABRAVLJIVANJE, obj.getString(TAG_ZABRAVLJIVANJE));
                client.addParam(TAG_TIP, obj.getString(TAG_TIP));
                client.addParam(TAG_SV, obj.getString("spoljašnja_visina"));
                client.addParam(TAG_SS, obj.getString("spoljašnja_širina"));
                client.addParam(TAG_SD, obj.getString("spoljašnja_dubina"));
                client.addParam(TAG_UV, obj.getString("unutrašnja_visina"));
                client.addParam(TAG_US, obj.getString("unutrašnja_širina"));
                client.addParam(TAG_UD, obj.getString("unutrašnja_dubina"));
                client.addParam(TAG_POLICE, obj.getString(TAG_POLICE));
                client.addParam(TAG_TEZINA, obj.getString("težina"));
                client.addParam(TAG_ZAPREMINA, obj.getString("zapremina"));
            }catch (Exception e){
                Log.e("Greška: ", e.toString());
            }
            try {
                if(sid != null){
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
                    poruka = "Kod nije 200";
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
             * Updating parsed JSON data into ListView
             * */
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
            pDialog = new ProgressDialog(SefEditNewActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if(sid != null){
                client = new RestClient(url + "/sefovi/" + sid + "/edit");
            }else{
                client = new RestClient(url + "/sefovi/create");
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
                    Log.e("Greška: ", "Kod nije 200!");
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
