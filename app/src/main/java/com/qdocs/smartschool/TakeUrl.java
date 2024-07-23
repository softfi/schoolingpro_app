package com.qdocs.smartschool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.qdocs.smartschools.R;
import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.qdocs.smartschool.utils.Constants;
import com.qdocs.smartschool.utils.MyApp;
import com.qdocs.smartschool.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.widget.Toast.makeText;

import androidx.core.content.ContextCompat;

public class TakeUrl extends Activity {
    EditText urlET;
    LinearLayout submitBtn;
    Locale myLocale;
    String langCode = "";

    public Map<String, String> headers = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.WHITE);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.BLACK);

        }
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.take_url_activity);

        urlET = findViewById(R.id.et_domain_takeUrl);

        String url = "https://schoolingpro.in/admin" + Constants.baseSUrl;

        urlET.setText(url);

        submitBtn = findViewById(R.id.btn_submit_takeUrl);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  String baseUrl = "https://schoolingpro.in/admin/api/";

                String appDomain = urlET.getText().toString();
                Log.d("TAG", "onClick: " + appDomain);
                if (appDomain.isEmpty()) {

                    makeText(TakeUrl.this, "Please Enter URL", Toast.LENGTH_SHORT).show();

                } else {
                    if (Utility.isConnectingToInternet(TakeUrl.this)) {

                      //  getDataFhromApi ();
                    } else {
                        makeText(getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                    }
                    Utility.setSharedPreference(getApplicationContext(), Constants.appDomain, appDomain);
                }

            }
        });
    }

    public void setLocale(String localeName) {

        if (localeName.isEmpty() || localeName.equals("null")) {
            localeName = "en";
            Log.e("localName status", "empty");
        }
        Locale myLocale = new Locale(localeName);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Log.e("Status", "Locale updated!");
        Utility.setSharedPreferenceBoolean(getApplicationContext(), Constants.isLocaleSet, true);
        Utility.setSharedPreference(getApplicationContext(), Constants.currentLocale, localeName);
    }



    private void getDataFhromApi () {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String url = "https://schoolingpro.in/admin/app";
        Log.d("url", "getDataFromApi: "+ url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                Log.d("Result", "getDataFromApii: "+result.toString());
                if (result != null) {
                    pd.dismiss();
                    try {
                        JSONObject object = new JSONObject(result);
                        String success = "200"; //object.getString("status");
                        if (success.equals("200")) {
                            Log.d("TAG", "onResponse: "+success);

                            Utility.setSharedPreferenceBoolean(getApplicationContext(), "isUrlTaken", true);
                            Utility.setSharedPreference(MyApp.getContext(), Constants.apiUrl, object.getString("url"));
                            Utility.setSharedPreference(MyApp.getContext(), Constants.imagesUrl, object.getString("site_url"));
                            String app_ver= object.getString("app_ver");
                            Utility.setSharedPreference(getApplicationContext(), Constants.app_ver, app_ver);
                            String appLogo = object.getString("site_url") + "uploads/school_content/logo/app_logo/" + object.getString("app_logo");
                            Utility.setSharedPreference(MyApp.getContext(), Constants.appLogo, appLogo );

                            String secColour = object.getString("app_secondary_color_code");
                            String primaryColour = object.getString("app_primary_color_code");

                            if(secColour.length() == 7 && primaryColour.length() == 7) {
                                Utility.setSharedPreference(getApplicationContext(), Constants.secondaryColour, secColour);
                                Utility.setSharedPreference(getApplicationContext(), Constants.primaryColour, primaryColour);
                            }else {
                                Utility.setSharedPreference(getApplicationContext(), Constants.secondaryColour, Constants.defaultSecondaryColour);
                                Utility.setSharedPreference(getApplicationContext(), Constants.primaryColour, Constants.defaultPrimaryColour);
                            }
                            Log.e("apiUrl Utility", Utility.getSharedPreferences(getApplicationContext(), "apiUrl"));
                            langCode = object.getString("lang_code");
                            Utility.setSharedPreference(getApplicationContext(), Constants.langCode,langCode);

                            if(!langCode.isEmpty()) {
                                setLocale(langCode);
                            }

                            Intent asd = new Intent(getApplicationContext(), Login.class);
                            startActivity(asd);
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid Domain.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        langCode = "";
                        e.printStackTrace();
                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Invalid Domain.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                System.out.println("not responding");
                try {
                    int  statusCode = error.networkResponse.statusCode;
                    NetworkResponse response = error.networkResponse;
                    Log.e("Volley Error", statusCode+" "+response.data.toString());
                    if(error instanceof ClientError) {
                        Toast.makeText(getApplicationContext(), R.string.apiErrorMsg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.apiErrorMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException npe) {
                    Toast.makeText(getApplicationContext(), R.string.apiErrorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(TakeUrl.this); //Creating a Request Queue
        requestQueue.add(stringRequest);          //Adding request to the queue
    }

    private void isMaintenanceMode() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        String url = Constants.mainUrl + Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + Constants.getMaintenanceModeStatusUrl;
        System.out.println("url==" + url);

        Log.d("TAG", "countenances: " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {

                Log.d("Response From Api", "baseUrlApiary: " + result);

                try {
                    JSONObject object = new JSONObject(result);
                    String maintenance_mode = object.getString("maintenance_mode");
                    System.out.println("maintenance_mode=" + maintenance_mode);
                    if (maintenance_mode.equals("0")) {
                        Utility.setSharedPreferenceBoolean(getApplicationContext(), "maintenance_mode", false);
                        pd.dismiss();
                        Intent asd = new Intent(getApplicationContext(), Login.class);
                        startActivity(asd);
                        finish();
                    } else {
                        Utility.setSharedPreferenceBoolean(getApplicationContext(), "maintenance_mode", true);
                        pd.dismiss();
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TakeUrl.this);
                        builder.setCancelable(false);
                        builder.setMessage(R.string.maintainMessage);
                        builder.setTitle("");
                        android.app.AlertDialog alert = builder.create();
                        alert.show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.e("Volley Error", volleyError.toString());
                Toast.makeText(TakeUrl.this, R.string.apiErrorMsg, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                Log.d("Headers","jdhghgh"+ headers);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(TakeUrl.this);//Creating a Request Queue
        requestQueue.add(stringRequest); //Adding request to the queue

    }

   /* private void getDataFromApi(String domain) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        if (!domain.contains("/")) {
            domain += "/";
        }

        final String url = domain;
        Log.d("Verification Url", url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                Log.d("Result", "fghnjh"+result);

                if (result != null) {
                    pd.dismiss();

                    isMaintenanceMode();            //here's........................

                    */
    /* try {
                         JSONObject object = new JSONObject(result);

                         String success = "200"; //object.getString("status");
                         if (success.equals("200")) {
                             //Log.d("TAG", "fhgjuoio: "+"Success");
                             Utility.setSharedPreferenceBoolean(getApplicationContext(), "isUrlTaken", true);
                             Utility.setSharedPreference(MyApp.getContext(), Constants.baseUrl, object.getString("url"));
                             Utility.setSharedPreference(MyApp.getContext(), Constants.siteUrl, object.getString("site_url"));
                             String app_ver= object.getString("app_ver");
                             Utility.setSharedPreference(getApplicationContext(), Constants.app_ver, app_ver);
                             String appLogo = object.getString("site_url") + "uploads/school_content/logo/app_logo/" + object.getString("app_logo");
                             Utility.setSharedPreference(MyApp.getContext(), Constants.appLogo, appLogo );

                             String secColour = object.getString("app_secondary_color_code");
                             String primaryColour = object.getString("app_primary_color_code");

                            String sec0Colour = "#E7F1EE";
                             String pri0maryColour = "#424242";

                             if(secColour.length() == 7 && primaryColour.length() == 7) {
                                 Utility.setSharedPreference(getApplicationContext(), Constants.secondaryColour, secColour);
                                 Utility.setSharedPreference(getApplicationContext(), Constants.primaryColour, primaryColour);
                             }else {
                                 Utility.setSharedPreference(getApplicationContext(), Constants.secondaryColour, Constants.defaultSecondaryColour);
                                 Utility.setSharedPreference(getApplicationContext(), Constants.primaryColour, Constants.defaultPrimaryColour);
                             }
                             Log.e("apiUrl Utility", Utility.getSharedPreferences(getApplicationContext(), "apiUrl"));
                             langCode = object.getString("lang_code");
                             Utility.setSharedPreference(getApplicationContext(), Constants.langCode,langCode);

                             if(!langCode.isEmpty()) {
                                 setLocale(langCode);
                             }

                          //   ismaintenancemode();


                         } else {
                             Toast.makeText(getApplicationContext(), "Invalid Domain.", Toast.LENGTH_SHORT).show();
                         }
                     } catch (JSONException e) {
                             langCode = "";
                         e.printStackTrace();
                     }*/
    /*

                } else {
                    pd.dismiss();

                    Toast.makeText(getApplicationContext(), "Invalid Domain.", Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onErrorResponse: " + error);
                pd.dismiss();
                System.out.println("not responding");
                try {
                    int statusCode = error.networkResponse.statusCode;
                    NetworkResponse response = error.networkResponse;
                    Log.e("Volley Error", statusCode + " " + Arrays.toString(response.data));
                    if (error instanceof ClientError) {
                        Toast.makeText(getApplicationContext(), R.string.apiErrorMsg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.apiErrorMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException npe) {
                    Toast.makeText(getApplicationContext(), R.string.apiErrorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(TakeUrl.this); //Creating a Request Queue
        requestQueue.add(stringRequest);//Adding request to the queue
    }*/
   /* private void baseUrlApi () {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+ Constants.baseUrl;
        Log.d("TAG", "baseUrlApi: "+url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                try {
                    Log.d("TAG", "baseUrlApietry: "+result);
                    if (result != null) {
                        pd.dismiss();
                        Log.e("Result", result);
                        JSONObject obj = new JSONObject(result);
                        urlET.setText((CharSequence) obj);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.e("Volley Error", volleyError.toString());
                Toast.makeText(TakeUrl.this, R.string.apiErrorMsg, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                Log.e("Headers", headers.toString());
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(TakeUrl.this);//Creating a Request Queue
        requestQueue.add(stringRequest); //Adding request to the queue

    }*/

}
