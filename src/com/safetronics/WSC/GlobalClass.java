package com.safetronics.WSC;

import android.app.Application;
/**
 * Created by Tope on 8/16/2014.
 */
public class GlobalClass extends  Application {

    private String token;
    private String header = "X-Auth-Token";

    public String getToken() {

        return token;
    }

    public void setToken(String aToken) {

        token = aToken;

    }

    public String getHeader(){
        return header;
    }
}