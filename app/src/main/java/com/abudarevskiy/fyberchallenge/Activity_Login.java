package com.abudarevskiy.fyberchallenge;

import com.abudarevskiy.fyberchallenge.model.FyberRequest;
import com.abudarevskiy.fyberchallenge.model.FyberResponse;
import volley.AppController;
import volley.Config_URL;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class Activity_Login extends Activity {
    // LogCat tag
    private static final String TAG = Activity_Main.class.getSimpleName();
    private Button btnLogin;
    private EditText appId;
    private EditText apiKey;
    private EditText pub0;
    private EditText uid;

    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appId = (EditText) findViewById(R.id.appid);
        apiKey = (EditText) findViewById(R.id.apikey);
        pub0 = (EditText) findViewById(R.id.pub0);
        uid = (EditText) findViewById(R.id.uid);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String appId = Activity_Login.this.appId.getText().toString();
                String apiKey = Activity_Login.this.apiKey.getText().toString();
                String pub0 = Activity_Login.this.pub0.getText().toString();
                String uid = Activity_Login.this.uid.getText().toString();

                // Check for empty data in the form
                if (appId.trim().length() > 0 && apiKey.trim().length() > 0) {
                    // login user
                    getOffers(appId, apiKey, pub0, uid);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the appId and apiKey!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });
    }

    /**
     * function to verify login details in mysql db
     */
    private void getOffers(final String apiKey, final String appId, final String pub0, final String uid) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("getting offers in ...");
        showDialog();

        final FyberRequest fyberRequest = new FyberRequest();

        String UUID = Config_URL.getUUID(this).toUpperCase();
        String params = fyberRequest.makeRequestParamsString(UUID, apiKey, appId, pub0, uid);
        String url = Config_URL.getUrl(params);
        final Map headers = new HashMap();
        Log.d(TAG, url);
        StringRequest strReq = new StringRequest(Method.GET,
                url
                , new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    FyberResponse fyberResponse = new FyberResponse(response, headers);
                    boolean responseIsValid = fyberResponse.checkReponseMatchWithRequest(fyberRequest);
                    if (responseIsValid) {

                        fyberResponse.getOffers();

                        Intent intent = new Intent(Activity_Login.this,
                                Activity_Main.class);


                        intent.putExtra("response", fyberResponse);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = null;
                        try {
                            errorMsg = fyberResponse.getResponse().getString("message");
                        } catch (JSONException e) {
                            errorMsg = "Unknown reason";
                        }
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (FyberResponse.SponsorPayBadResponseException e) {
                    Toast.makeText(getApplicationContext(),
                            e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (FyberResponse.SponsorPayNoOffersException e1) {
                    Toast.makeText(getApplicationContext(),
                            "Oops! There is no offers to show ;(", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Request Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {


            @Override
            protected Response parseNetworkResponse(NetworkResponse response){
                headers.putAll(response.headers);
                Log.d(TAG + "#HEADERS", headers.toString());
                return super.parseNetworkResponse(response);
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);



    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}