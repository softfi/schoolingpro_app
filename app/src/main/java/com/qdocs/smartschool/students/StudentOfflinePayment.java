package com.qdocs.smartschool.students;

import static android.widget.Toast.makeText;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.qdocs.smartschool.BaseActivity;
import com.qdocs.smartschool.R;
import com.qdocs.smartschool.utils.Constants;
import com.qdocs.smartschool.utils.Utility;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class StudentOfflinePayment extends BaseActivity implements DatePickerDialog.OnDateSetListener  {
    EditText dateofPayment,paymentMode,paymentFrom,reference,amount;
    CardView card_view_outer;
    String defaultDateFormat,startweek,feesTypeId,feesId,feesSessionId,paymenttype,transfeesIdList;
    String paymentdate = "";
    Context mContext = this;
    private static final String TAG = "StudentOfflinePayment";
    private boolean istoDateSelected = false;
    public static Boolean camera = false;
    public static Boolean gallery = false;
    boolean isKitKat = false;
    Button submit;
    Bitmap selectedImageString = null;
    String filePath;
    File f;
    RequestBody file_body;
    ImageView imageView;
    TextView textView;
    ProgressDialog progress;
    String extension="",name="";
    EditText amountTextInputLayout;
    String[] mimeTypes =
            {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                    "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                    "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                    "text/plain",
                    "application/pdf",
                    "application/zip","image/*"};
    Bitmap bitmap;
    Button buttonUploadImage;
    public TextView buttonSelectImage;
    WebView instruction;
    private static final int CAMERA_REQUEST = 1888;
    String url;
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_student_offline_payment, null, false);
        mDrawerLayout.addView(contentView, 0);
        titleTV.setText(getApplicationContext().getString(R.string.offline_payment));
        feesId = getIntent().getStringExtra("feesId");
        feesTypeId = getIntent().getStringExtra("feesTypeId");
        feesSessionId = getIntent().getStringExtra("feesSessionId");
        paymenttype = getIntent().getStringExtra("paymenttype");
        transfeesIdList = getIntent().getStringExtra("transfeesIdList");
       // amountTextInputLayout = findViewById(R.id.amountTextInputLayout);
      //  amountTextInputLayout.setHint(getApplicationContext().getString(R.string.amount)+" ("+Utility.getSharedPreferences(getApplicationContext(), Constants.currency)+")");
        card_view_outer = findViewById(R.id.card_view_outer);
        card_view_outer.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
        buttonUploadImage =  findViewById(R.id.buttonUploadImage);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        instruction = findViewById(R.id.instruction);
        instruction.getSettings().setJavaScriptEnabled(true);
        instruction.getSettings().setBuiltInZoomControls(true);
        instruction.getSettings().setLoadWithOverviewMode(true);
        instruction.getSettings().setUseWideViewPort(true);
        instruction.getSettings().setDefaultFontSize(40);
        instruction.getSettings().setTextZoom(100);
        submit = findViewById(R.id.addLeave_dialog_submitBtn);
        dateofPayment=findViewById(R.id.dateofPayment);
        paymentMode=findViewById(R.id.paymentMode);
        paymentFrom=findViewById(R.id.paymentFrom);
        reference=findViewById(R.id.reference);
        amount=findViewById(R.id.amount);
        imageView =  findViewById(R.id.imageView);
        textView =  findViewById(R.id.textview);
        defaultDateFormat = Utility.getSharedPreferences(getApplicationContext(), "dateFormat");
        startweek = Utility.getSharedPreferences(getApplicationContext(), "startWeek");
        final Calendar c = Calendar.getInstance();
        int startYear = c.get(Calendar.YEAR);
        int starthMonth = c.get(Calendar.MONTH);
        int startDay = c.get(Calendar.DAY_OF_MONTH);

        if(Utility.isConnectingToInternet(StudentOfflinePayment.this)){
            StudentOfflineInstruction();
        }else{
            makeText(getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
        }
        final DatePickerDialog datePickerDialog = new DatePickerDialog(StudentOfflinePayment.this, StudentOfflinePayment.this, startYear, starthMonth, startDay);
        if(startweek.equals("Monday")){
            datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);
        }else if(startweek.equals("Tuesday")){
            datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.TUESDAY);
        }else if(startweek.equals("Wednesday")){
            datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.WEDNESDAY);
        }else if(startweek.equals("Thursday")){
            datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.THURSDAY);
        }else if(startweek.equals("Friday")){
            datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.FRIDAY);
        }else if(startweek.equals("Saturday")){
            datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.SATURDAY);
        }else if(startweek.equals("Sunday")){
            datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.SUNDAY);
        }
        dateofPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT <= 12) {
                    if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                        if ((ActivityCompat.shouldShowRequestPermissionRationale(StudentOfflinePayment.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(StudentOfflinePayment.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE))) {

                        } else {
                            ActivityCompat.requestPermissions(StudentOfflinePayment.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_PERMISSIONS);
                        }
                    } else {
                        Log.e("Else", "Else");
                        showFileChooser();
                    }
                }else{
                    Log.e("Else", "Else");
                    ActivityCompat.requestPermissions(StudentOfflinePayment.this, permissions(), 1);
                    showFileChooser();
                }

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                   /* makeText(mContext, Utility.changeAmounttousd(amount.getText().toString(), Utility.getSharedPreferences(getApplicationContext(), Constants.currency),
                            Utility.getSharedPreferences(getApplicationContext(), Constants.currency_price)), Toast.LENGTH_SHORT).show();*/
                    if(!istoDateSelected) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.paymentdateError), Toast.LENGTH_LONG).show();
                    }else if(paymentMode.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.paymentmodeError), Toast.LENGTH_LONG).show();
                    }else if(paymentFrom.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.paymentfromError), Toast.LENGTH_LONG).show();
                    }else if(amount.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.paymentamountError), Toast.LENGTH_LONG).show();
                    }else {
                        if(Utility.isConnectingToInternet(getApplicationContext())){
                            uploadBitmap();
                        }else{
                            makeText(getApplicationContext(),R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void showFileChooser() {
        final Dialog dialog = new Dialog(StudentOfflinePayment.this);
        dialog.setContentView(R.layout.choose_file);
        dialog.setCanceledOnTouchOutside(false);
        RelativeLayout headerLay = (RelativeLayout) dialog.findViewById(R.id.addTask_dialog_header);
        final LinearLayout takephoto = (LinearLayout) dialog.findViewById(R.id.takephoto);
        final LinearLayout choosegallery = (LinearLayout) dialog.findViewById(R.id.gallery);
        ImageView closeBtn = (ImageView) dialog.findViewById(R.id.addTask_dialog_crossIcon);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camerapic();
                camera = true;
                gallery = false;
                dialog.dismiss();
            }
        });
        choosegallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opengallery();
                gallery = true;
                camera = false;
                dialog.dismiss();
            }
        });

        headerLay.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
        dialog.show();
    }

    void camerapic() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
    private void opengallery() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                if (mimeTypes.length > 0) {
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                }
            } else {
                String mimeTypesStr = "";
                for (String mimeType : mimeTypes) {
                    mimeTypesStr += mimeType + "|";
                }
                intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
            }
            isKitKat = true;
            startActivityForResult(Intent.createChooser(intent, "Select file"), PICK_IMAGE_REQUEST);
        } else {
            isKitKat = false;

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                if (mimeTypes.length > 0) {
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                }
            } else {
                String mimeTypesStr = "";
                for (String mimeType : mimeTypes) {
                    mimeTypesStr += mimeType + "|";
                }
                intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
            }
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
    }
    @TargetApi(19)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            System.out.println("uri==" + uri);

            String path = new File(uri.getPath()).getAbsolutePath();
            System.out.println("path==" + path);

            if (path != null) {
                uri = data.getData();

                String filenames;
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                if (cursor == null) filenames = uri.getPath();
                else {
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    filenames = cursor.getString(idx);
                    cursor.close();
                }

                name = filenames.substring(0, filenames.lastIndexOf("."));
                System.out.println("name==" + name);
                extension = filenames.substring(filenames.lastIndexOf(".") + 1);
                System.out.println("extension==" + extension);
            } else {
                makeText(this, "Please select file", Toast.LENGTH_SHORT).show();
            }

            try {
                selectedImageString = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            textView.setText(getApplicationContext().getString(R.string.fileselected));

            filePath = getgalleryRealPathFromURI(StudentOfflinePayment.this, uri);
            if (extension.equals("jpg") || extension.equals("png") || extension.equals("jpeg")) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(selectedImageString);
            } else if (extension.equals("PDF") || extension.equals("pdf") || extension.equals("doc") || extension.equals("docx") || extension.equals("txt")) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selected_file));
            }
            f = new File(filePath);
            System.out.println("file==" + filePath);
            String mimeType = URLConnection.guessContentTypeFromName(f.getName());
            file_body = RequestBody.create(MediaType.parse(mimeType), f);
            System.out.println("file_bodypathasd" + file_body);
            System.out.println("bitmap image==" + selectedImageString);
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            if (bitmap != null) {
                progress = new ProgressDialog(StudentOfflinePayment.this);
                progress.setTitle("uploading");
                progress.setMessage("Please Wait....");
                progress.show();
                imageView.setVisibility(View.VISIBLE);
                textView.setText(getApplicationContext().getString(R.string.fileselected));
                imageView.setImageBitmap(bitmap);
                Uri tempUri = getImageUri(getApplicationContext(), bitmap);
                filePath = getRealPathFromURI(tempUri);
                System.out.println("pathasd" + filePath);
                File f = new File(filePath);
                String mimeType = URLConnection.guessContentTypeFromName(f.getName());
                file_body = RequestBody.create(MediaType.parse(mimeType), f);
                System.out.println("file_bodypathasd" + file_body);
                progress.dismiss();
            }
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public String getgalleryRealPathFromURI(Context context, Uri contentUri) {

        OutputStream out;
        File file = new File(getFilename(context));
        System.out.println("filenamepath==="+file);

        try {
            if (file.createNewFile()) {
                InputStream iStream = context != null ? context.getContentResolver().openInputStream(contentUri) : context.getContentResolver().openInputStream(contentUri);
                byte[] inputData = getBytes(iStream);
                out = new FileOutputStream(file);
                out.write(inputData);
                out.close();
                return file.getAbsolutePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }
    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
    private String getFilename(Context context) {
        File mediaStorageDir = new File(context.getExternalFilesDir(""), "Soers_Images");
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        String mImageName=name+"."+extension;
        System.out.println("mImageName=="+mImageName);
        System.out.println("Image=="+mediaStorageDir.getAbsolutePath() + "/" + mImageName);
        return mediaStorageDir.getAbsolutePath() + "/" + mImageName;
    }

    public static String[] storage_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
    }
    private void uploadBitmap() throws IOException {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
        url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + Constants.addofflinepaymentUrl;
        OkHttpClient client=new OkHttpClient();
        Log.i("url=", url);

        if(filePath==null || file_body==null){

            RequestBody requestBody=new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file","")
                    .addFormDataPart("student_session_id",feesSessionId)
                    .addFormDataPart("fee_groups_feetype_id",feesTypeId)
                    .addFormDataPart("student_fees_master_id",feesId)
                    .addFormDataPart("payment_date", paymentdate)
                    .addFormDataPart("amount",  Utility.changeAmountToUSD(amount.getText().toString(), Utility.getSharedPreferences(getApplicationContext(), Constants.currency),
                                    Utility.getSharedPreferences(getApplicationContext(), Constants.currency_price)))
                    .addFormDataPart("reference", reference.getText().toString())
                    .addFormDataPart("bank_account_transferred",paymentFrom.getText().toString())
                    .addFormDataPart("payment_type", paymenttype)
                    .addFormDataPart("student_transport_fee_id", transfeesIdList)
                    .build();

            Request request=new Request.Builder()
                    .url(url)
                    .header("Client-Service", Constants.clientService)
                    .header("Auth-Key", Constants.authKey)
                    .header("User-ID",Utility.getSharedPreferences(getApplicationContext(), "userId"))
                    .header("Authorization",Utility.getSharedPreferences(getApplicationContext(), "accessToken"))
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) { }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody body = response.body();
                    pd.dismiss();
                    if(body != null) {
                        try {
                            String jsonData = response.body().string();
                            try {
                                final JSONObject Jobject = new JSONObject(jsonData);
                                String Jarray = Jobject.getString("status");
                                if(Jarray.equals("1")){
                                    runOnUiThread(new Runnable(){
                                        public void run() {
                                            Toast.makeText(mContext, getApplicationContext().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                }else{
                                    runOnUiThread(new Runnable(){
                                        public void run() {
                                            try {
                                                JSONObject error = Jobject.getJSONObject("error");
                                                Toast.makeText(mContext, error.getString("reason"), Toast.LENGTH_SHORT).show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            });

        }else{
            String file_name=filePath.substring(filePath.lastIndexOf("/")+1);
            System.out.println("file_name== "+file_name);

            RequestBody requestBody=new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file",file_name,file_body)
                    .addFormDataPart("student_session_id",feesSessionId)
                    .addFormDataPart("fee_groups_feetype_id",feesTypeId)
                    .addFormDataPart("student_fees_master_id",feesId)
                    .addFormDataPart("payment_date", paymentdate)
                    .addFormDataPart("amount", Utility.changeAmountToUSD(amount.getText().toString(), Utility.getSharedPreferences(getApplicationContext(), Constants.currency),
                            Utility.getSharedPreferences(getApplicationContext(), Constants.currency_price)))
                    .addFormDataPart("reference", reference.getText().toString())
                    .addFormDataPart("bank_account_transferred",paymentFrom.getText().toString())
                    .addFormDataPart("payment_type", paymenttype)
                    .addFormDataPart("student_transport_fee_id", transfeesIdList)
                    .build();

            Request request=new Request.Builder()
                    .url(url)
                    .header("Client-Service", Constants.clientService)
                    .header("Auth-Key", Constants.authKey)
                    .header("User-ID",Utility.getSharedPreferences(getApplicationContext(), "userId"))
                    .header("Authorization",Utility.getSharedPreferences(getApplicationContext(), "accessToken"))
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) { }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody body = response.body();
                    pd.dismiss();
                    if(body != null) {
                        try {
                            String jsonData = response.body().string();
                            try {
                                final JSONObject Jobject = new JSONObject(jsonData);
                                String Jarray = Jobject.getString("status");

                                if(Jarray.equals("1")){
                                    runOnUiThread(new Runnable(){
                                        public void run() {
                                            Toast.makeText(mContext, getApplicationContext().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });

                                }else{
                                    runOnUiThread(new Runnable(){
                                        public void run() {
                                            try {
                                                JSONObject error = Jobject.getJSONObject("error");
                                                Toast.makeText(mContext, error.getString("reason"), Toast.LENGTH_SHORT).show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();}
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            });
        }

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        paymentdate = year+"-"+(++monthOfYear)+"-"+dayOfMonth;
        //createTaskParams.put("date", date);
        System.out.println("Date=="+Utility.parseDate("yyyy-MM-dd", defaultDateFormat,paymentdate));
        dateofPayment.setText(Utility.parseDate("yyyy-MM-dd", defaultDateFormat,paymentdate));
        istoDateSelected = true;
    }

    private void StudentOfflineInstruction() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+ Constants.getOfflineBankPaymentInstructionUrl;
        System.out.println("url=="+url);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(result);
                    String offline_bank_payment_instruction = object.getString("offline_bank_payment_instruction");
                    Utility.setSharedPreference(getApplicationContext(),"offline_bank_payment_instruction",offline_bank_payment_instruction);
                    System.out.println("offline_bank_payment_instruction="+offline_bank_payment_instruction);
                    instruction.loadDataWithBaseURL(null,offline_bank_payment_instruction,"text/html; charset=utf-8", "utf-8", null);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.e("Volley Error", volleyError.toString());
                Toast.makeText(StudentOfflinePayment.this, R.string.apiErrorMsg, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(StudentOfflinePayment.this);//Creating a Request Queue
        requestQueue.add(stringRequest); //Adding request to the queue
    }
}