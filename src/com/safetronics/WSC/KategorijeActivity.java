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
public class KategorijeActivity extends ListActivity {

    private ProgressDialog pDialog;
    private static String url = "http://rwstope.esy.es/api/kategorije";
    protected JSONArray kategorije = null;
    private ArrayList<HashMap<String, String>> kategorijaList;

    private static final String TAG_KATEGORIJE = "kategorije";
    private static final String TAG_ID = "id";
    private static final String TAG_NAZIV = "naziv";
    private static final String TAG_ERROR = "error";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.kategorije);

        kategorijaList = new ArrayList<HashMap<String, String>>();

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String kid = ((TextView) view.findViewById(R.id.kategorijaID)).getText().toString();
                // Starting single kategorija activity
                Intent i = new Intent(getApplicationContext(), KategorijaActivity.class);
                i.putExtra(TAG_ID, kid);
                startActivity(i);
                finish();
            }
        });

        new GetKategorije().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetKategorije extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(KategorijeActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            final String token  = globalVariable.getToken();
            final String header = globalVariable.getHeader();
            RestClient client = new RestClient(url);
            client.addHeader(header, token);
            try {
                client.execute(RequestMethod.GET);
                if(client.getResponseCode() == 200) {
                    JSONObject jObj = new JSONObject(client.getResponse());
                    if (!jObj.getBoolean(TAG_ERROR)) {
                        kategorije = jObj.getJSONArray(TAG_KATEGORIJE);
                        for (int i = 0; i < kategorije.length(); i++) {
                            JSONObject k = kategorije.getJSONObject(i);

                            String id = k.getString(TAG_ID);
                            String naziv = k.getString(TAG_NAZIV);

                            // tmp hashmap for single kategorija
                            HashMap<String, String> kategorija = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            kategorija.put(TAG_ID, id);
                            kategorija.put(TAG_NAZIV, naziv);

                            // adding kategorija to kategorija list
                            kategorijaList.add(kategorija);
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
                    KategorijeActivity.this, kategorijaList,
                    R.layout.kategorija_list_item, new String[]{TAG_NAZIV, TAG_ID},
                    new int[]{R.id.title,
                    R.id.kategorijaID});

            setListAdapter(adapter);
        }
    }
}


