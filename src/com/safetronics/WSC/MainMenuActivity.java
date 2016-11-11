package com.safetronics.WSC;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tope on 8/11/2014.
 */
public class MainMenuActivity extends Activity{

    private JSONObject jObj;

    private static final String TAG_NAZIV = "naziv";
    private static final String TAG_MENI = "meni";
    private static final String TAG_DODAJ_KATEGORIJU_SEFOVA = "Dodaj kategoriju sefova";
    private static final String TAG_ADMINISTRIRAJ_KATEGORIJE = "Administriraj kategorije";
    private static final String TAG_DODAJ_SEF = "Dodaj sef";
    private static final String TAG_ADMINISTRIRAJ_SEFOVE = "Administriraj sefove";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_menu);

        try {
            jObj = new JSONObject(getIntent().getStringExtra(TAG_MENI));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        initializeApp();
    }

    private void initializeApp() {
        LinearLayout linear = (LinearLayout)findViewById(R.id.mainMenu);
        JSONArray niz = null;
        try {
            niz = jObj.getJSONArray(TAG_MENI);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i<niz.length(); i++){
            JSONObject json = null;
            try {
                json = niz.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
            final Button btn = new Button(this);
            btn.setId(i);
            final int id_ = btn.getId();
            try {
                btn.setText(json.getString(TAG_NAZIV));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            linear.addView(btn, params);
            Button btn1 = ((Button) findViewById(id_));
            btn1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    dodajActivity(id_);
                }
            });
        }
    }

    private void dodajActivity(int id) {
        Button btn = ((Button) findViewById(id));
        if(btn.getText().toString().equalsIgnoreCase(TAG_DODAJ_KATEGORIJU_SEFOVA)){
            Intent i = new Intent(this, KategorijaEditNewActivity.class);
            startActivity(i);
        }else if(btn.getText().toString().equalsIgnoreCase(TAG_ADMINISTRIRAJ_KATEGORIJE)){
            Intent i = new Intent(this, KategorijeActivity.class);
            startActivity(i);
        }else if(btn.getText().toString().equalsIgnoreCase(TAG_DODAJ_SEF)){
            Intent i = new Intent(this, SefEditNewActivity.class);
            startActivity(i);
        }else if(btn.getText().toString().equalsIgnoreCase(TAG_ADMINISTRIRAJ_SEFOVE)){
            Intent i = new Intent(this, SefoviActivity.class);
            startActivity(i);
        }
    }
}
