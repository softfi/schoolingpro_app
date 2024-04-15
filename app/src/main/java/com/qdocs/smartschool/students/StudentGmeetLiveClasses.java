package com.qdocs.smartschool.students;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.qdocs.smartschool.BaseActivity;
import com.qdocs.smartschool.R;
import com.qdocs.smartschool.adapters.StudentGmeetLiveClassesAdapter;
import com.qdocs.smartschool.utils.Constants;
import com.qdocs.smartschool.utils.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import static android.widget.Toast.makeText;

public class StudentGmeetLiveClasses extends BaseActivity {
    RecyclerView recyclerView;
    LinearLayout nodata_layout,data_layout;
    SwipeRefreshLayout pullToRefresh;
    ArrayList<String> titleList = new ArrayList<String>();
    ArrayList<String> dateList = new ArrayList<String>();
    ArrayList<String> classList = new ArrayList<String>();
    ArrayList<String> idList = new ArrayList<String>();
    ArrayList<String> join_url = new ArrayList<String>();
    ArrayList<String> statuslist = new ArrayList<String>();
    ArrayList<String> staff_namelist = new ArrayList<String>();
    ArrayList<String> descriptionlist = new ArrayList<String>();
    ArrayList<String> staff_rolelist = new ArrayList<String>();
    ArrayList<String> durationlist = new ArrayList<String>();
    StudentGmeetLiveClassesAdapter adapter;
    public String defaultDatetimeFormat, currency;
    CardView card_view_outer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_student_gmeet_live_classes, null, false);
        mDrawerLayout.addView(contentView, 0);
        defaultDatetimeFormat = Utility.getSharedPreferences(getApplicationContext(), "datetimeFormat");
        currency = Utility.getSharedPreferences(getApplicationContext(), Constants.currency);
        titleTV.setText(getApplicationContext().getString(R.string.livegmeetclasses));
        card_view_outer = findViewById(R.id.card_view_outer);
        card_view_outer.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
        recyclerView=findViewById(R.id.studentLiveclass_listview);
        nodata_layout = (LinearLayout) findViewById(R.id.nodata_layout);
        data_layout = (LinearLayout) findViewById(R.id.data_layout);
        loaddata();
        if(Utility.isConnectingToInternet(StudentGmeetLiveClasses.this)){
            gmeetsettings();
        }else{
            makeText(getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
        }
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                loaddata();
            }
        });

        adapter = new StudentGmeetLiveClassesAdapter(StudentGmeetLiveClasses.this,titleList,dateList,classList,idList,join_url,statuslist,staff_namelist,descriptionlist,durationlist,staff_rolelist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }

    public  void  loaddata(){
        if (Utility.isConnectingToInternet(getApplicationContext())) {
            params.put("student_id", Utility.getSharedPreferences(getApplicationContext(), Constants.studentId));
            JSONObject obj=new JSONObject(params);
            Log.e("params ", obj.toString());
            getDataFromApi(obj.toString());
        } else {
            makeText(getApplicationContext(),R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
        }

    }

    private void getDataFromApi (String bodyParams) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;
        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+Constants.gmeetclassesUrl;
        Log.e("URL", url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                pullToRefresh.setRefreshing(false);
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONObject obj = new JSONObject(result);
                        JSONArray dataArray = obj.getJSONArray("live_classes");
                        titleList.clear();
                        dateList.clear();
                        classList.clear();
                        idList.clear();
                        staff_namelist.clear();
                        join_url.clear();
                        staff_namelist.clear();
                        descriptionlist.clear();
                        durationlist.clear();
                        staff_rolelist.clear();
                        statuslist.clear();



                        if(dataArray.length() != 0) {
                            data_layout.setVisibility(View.VISIBLE);
                            nodata_layout.setVisibility(View.GONE);
                            for(int i = 0; i < dataArray.length(); i++) {
                                titleList.add(dataArray.getJSONObject(i).getString("title"));
                                idList.add(dataArray.getJSONObject(i).getString("id"));
                                dateList.add(Utility.parseDate("yyyy-MM-dd HH:mm:ss", defaultDatetimeFormat,dataArray.getJSONObject(i).getString("date")));
                                join_url.add(dataArray.getJSONObject(i).getString("url"));
                                statuslist.add(dataArray.getJSONObject(i).getString("status"));
                                staff_rolelist.add(dataArray.getJSONObject(i).getString("staff_role"));
                                descriptionlist.add(dataArray.getJSONObject(i).getString("description"));
                                durationlist.add(dataArray.getJSONObject(i).getString("duration"));
                                staff_namelist.add(dataArray.getJSONObject(i).getString("staff_name")+" "+dataArray.getJSONObject(i).getString("staff_surname")+"("+dataArray.getJSONObject(i).getString("staff_role")+": "+dataArray.getJSONObject(i).getString("staff_id")+")");
                                classList .add(dataArray.getJSONObject(i).getString("class")+" ("+dataArray.getJSONObject(i).getString("section")+")");
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            pullToRefresh.setVisibility(View.GONE);
                            data_layout.setVisibility(View.GONE);
                            nodata_layout.setVisibility(View.VISIBLE);
                         // Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.noData), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    pd.dismiss();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.e("Volley Error", volleyError.toString());
                Toast.makeText(StudentGmeetLiveClasses.this, R.string.apiErrorMsg, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                headers.put("User-ID", Utility.getSharedPreferences(getApplicationContext(), "userId"));
                headers.put("Authorization", Utility.getSharedPreferences(getApplicationContext(), "accessToken"));
                Log.e("Headers", headers.toString());
                return headers;
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(StudentGmeetLiveClasses.this); //Creating a Request Queue
        requestQueue.add(stringRequest);  //Adding request to the queue
    }

    private void gmeetsettings() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+ Constants.getgmeetsettingsUrl;
        System.out.println("url=="+url);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(result);
                    JSONObject live_classesobj = object.getJSONObject("live_classes");
                    String parent_live_class = live_classesobj.getString("parent_live_class");
                    Utility.setSharedPreference(getApplicationContext(),Constants.parent_live_class,parent_live_class);
                    System.out.println("parent_live_class="+parent_live_class);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.e("Volley Error", volleyError.toString());
                Toast.makeText(StudentGmeetLiveClasses.this, R.string.apiErrorMsg, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(StudentGmeetLiveClasses.this);//Creating a Request Queue
        requestQueue.add(stringRequest); //Adding request to the queue
    }
}
