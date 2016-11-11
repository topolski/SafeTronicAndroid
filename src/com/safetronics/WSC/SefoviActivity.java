package com.safetronics.WSC;

import android.app.ListActivity;
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
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tope on 8/11/2014.
 */
public class SefoviActivity extends ListActivity {

    private ProgressDialog pDialog;
    private static String url = "http://rwstope.esy.es/api/sefovi";
    protected JSONArray sefovi = null;
    private ArrayList<HashMap<String, String>> sefoviList;

    private static final String TAG_SEFOVI = "sefovi";
    private static final String TAG_ID = "id";
    private static final String TAG_NAZIV = "naziv";
    private static final String TAG_ERROR = "error";
    private static final String TAG_KATEGORIJA = "kategorija";
    private static final String TAG_CENA = "cena";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sefovi);

        sefoviList = new ArrayList<HashMap<String, String>>();

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sid = ((TextView) view.findViewById(R.id.textViewSefID)).getText().toString();
                // Starting single sef activity
                Intent i = new Intent(getApplicationContext(), SefActivity.class);
                i.putExtra(TAG_ID, sid);
                startActivity(i);
                finish();
            }
        });

        new GetSefovi().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetSefovi extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SefoviActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            RestClient client = new RestClient(url);
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            final String token  = globalVariable.getToken();
            final String header = globalVariable.getHeader();
            client.addHeader(header, token);
            try {
                client.execute(RequestMethod.GET);
                if(client.getResponseCode() == 200) {
                    JSONObject jObj = new JSONObject(client.getResponse());
                    if (!jObj.getBoolean(TAG_ERROR)) {
                        sefovi = jObj.getJSONArray(TAG_SEFOVI);
                        for (int i = 0; i < sefovi.length(); i++) {
                            JSONObject s = sefovi.getJSONObject(i);

                            String id = s.getString(TAG_ID);
                            String naziv = s.getString(TAG_NAZIV);
                            JSONObject kategorija = s.getJSONObject(TAG_KATEGORIJA);
                            String kategorijaNaziv = "Kategorija: " + kategorija.getString(TAG_NAZIV);
                            String cena = "Cena: " + s.getString(TAG_CENA) + " €";

                            // tmp hashmap for single sef
                            HashMap<String, String> sef = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            sef.put(TAG_ID, id);
                            sef.put(TAG_NAZIV, naziv);
                            sef.put(TAG_KATEGORIJA, kategorijaNaziv);
                            sef.put(TAG_CENA, cena);

                            // adding sef to sefovi list
                            sefoviList.add(sef);
                        }
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
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    SefoviActivity.this, sefoviList,
                    R.layout.sef_list_item, new String[]{TAG_NAZIV, TAG_KATEGORIJA, TAG_CENA, TAG_ID},
                    new int[]{R.id.textViewSefTitle, R.id.textViewSefKategorija, R.id.textViewSefCena,
                            R.id.textViewSefID});

            setListAdapter(adapter);
        }
    }
}
