package com.qdocs.smartschool.students;

import static android.widget.Toast.makeText;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.qdocs.smartschool.BaseActivity;
import com.qdocs.smartschools.R;
import com.qdocs.smartschool.utils.Constants;
import com.qdocs.smartschool.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

public class CreateTaskActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener{

    String startweek;

    Button submitBtn;
    EditText dateTV;
    private boolean isDateSelected = false;
    public Map<String, String> createTaskParams = new Hashtable<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_create_task, null, false);
        mDrawerLayout.addView(contentView, 0);
        titleTV.setText(getApplicationContext().getString(R.string.createTask));

        dateTV = findViewById(R.id.addTask_dialog_dateTV);

        final EditText titleET = findViewById(R.id.addTask_dialog_titleET);
        startweek = Utility.getSharedPreferences(getApplicationContext(), "startWeek");
        submitBtn = findViewById(R.id.addTask_dialog_submitBtn);


        final Calendar c = Calendar.getInstance();
        int startYear = c.get(Calendar.YEAR);
        int starthMonth = c.get(Calendar.MONTH);
        int startDay = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, startYear, starthMonth, startDay);

        if (startweek.equals("Monday")) {
            datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);
        } else if (startweek.equals("Tuesday")) {
            datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.TUESDAY);
        } else if (startweek.equals("Wednesday")) {
            datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.WEDNESDAY);
        } else if (startweek.equals("Thursday")) {
            datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.THURSDAY);
        } else if (startweek.equals("Friday")) {
            datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.FRIDAY);
        } else if (startweek.equals("Saturday")) {
            datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.SATURDAY);
        } else if (startweek.equals("Sunday")) {
            datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.SUNDAY);
        }

        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog.show();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isDateSelected) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.selectDateError), Toast.LENGTH_LONG).show();
                } else if (titleET.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.selectTitleError), Toast.LENGTH_LONG).show();
                } else {
                    if (Utility.isConnectingToInternet(getApplicationContext())) {
                        createTaskParams.put("user_id", Utility.getSharedPreferences(getApplicationContext(), "userId"));
                        createTaskParams.put("event_title", titleET.getText().toString());
                        createTaskParams.put("task_id", "");
                        JSONObject obj = new JSONObject(createTaskParams);
                        Log.e("params ", obj.toString());
                        createTaskApi(obj.toString());
                    } else {
                        makeText(getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        //DECORATE
        //DECORATE
    }

    private void createTaskApi(String bodyParams) {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;

        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + Constants.createTaskUrl;
        Log.e("URL", url);

        Log.d("TAG", requestBody + "createTaskApi: " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONObject object = new JSONObject(result);

                        String status = object.getString("status");
                        if (object.getString("msg").equals("success")) {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.submit_success), Toast.LENGTH_LONG).show();
                        }

                        //Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_LONG).show();

                        if (status.equals("1")) {
                            finish();
                            startActivity(getIntent());
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
                Toast.makeText(CreateTaskActivity.this, R.string.apiErrorMsg, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(CreateTaskActivity.this);//Creating a Request Queue
        requestQueue.add(stringRequest);//Adding request to the queue
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "-" + (++monthOfYear) + "-" + dayOfMonth;
        createTaskParams.put("date", date);
        System.out.println("Date==" + Utility.parseDate("yyyy-MM-dd", defaultDateFormat, date));
        dateTV.setText(Utility.parseDate("yyyy-MM-dd", defaultDateFormat, date));
        isDateSelected = true;

    }
}