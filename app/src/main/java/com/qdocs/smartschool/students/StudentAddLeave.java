package com.qdocs.smartschool.students;

import static android.widget.Toast.makeText;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.qdocs.smartschools.R;
import com.qdocs.smartschool.utils.Constants;
import com.qdocs.smartschool.utils.Utility;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@SuppressLint("SimpleDateFormat")
public class StudentAddLeave extends AppCompatActivity {
    public ImageView backBtn;
    public String defaultDateFormat, currency, startweek;
    Context mContext = this;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String> headers = new HashMap<String, String>();
    protected FrameLayout mDrawerLayout, actionBar;
    String applyDate = "";
    String fromDate = "";
    String filePath;
    ProgressDialog progress;
    String toDate = "";
    private boolean isFromDateSelected = false;
    private boolean istoDateSelected = false;
    Bitmap bitmap;
    Button submit;
    private static final int CAMERA_REQUEST = 100;
    String url;
    private static final int PICK_IMAGE_REQUEST = 1;
    RequestBody file_body;
    EditText reason;
    ImageView imageView;
    TextView textView;
    Uri imageuri;

    TextView titleTV;
    private static final String TAG = "StudentAddLeave";
    TextView toDateTV, apply_dateTV, fromDateTV, buttonSelectImage;
    public static Boolean camera = false;
    public static Boolean gallery = false;
    boolean isKitKat = false;
    String[] mimeTypes =
            {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                    "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                    "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                    "text/plain",
                    "application/pdf",
                    "application/zip", "image/*"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_leave);
        backBtn = findViewById(R.id.actionBar_backBtn);
        actionBar = findViewById(R.id.actionBarSecondary);
        titleTV = findViewById(R.id.actionBar_title);

        defaultDateFormat = Utility.getSharedPreferences(getApplicationContext(), "dateFormat");
        currency = Utility.getSharedPreferences(getApplicationContext(), Constants.currency);
        startweek = Utility.getSharedPreferences(getApplicationContext(), "startWeek");

        decorate();
        Utility.setLocale(getApplicationContext(), Utility.getSharedPreferences(getApplicationContext(), Constants.langCode));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        titleTV.setText(getApplicationContext().getString(R.string.applyleave));
        apply_dateTV = findViewById(R.id.addLeave_dialog_apply_dateTV);
        fromDateTV = findViewById(R.id.addLeave_dialog_fromdateTV);
        toDateTV = findViewById(R.id.addLeave_dialog_todateTV);
        reason = findViewById(R.id.reason);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textview);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        submit = findViewById(R.id.addLeave_dialog_submitBtn);

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                showFileChooser();

             /*   if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(StudentAddLeave.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(StudentAddLeave.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE))) {

                    } else {
                        ActivityCompat.requestPermissions(StudentAddLeave.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                    }
                } else {

                }*/

            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateAndTime = sdf.format(new Date());
        apply_dateTV.setText(currentDateAndTime);

        String mStringDate = currentDateAndTime;
        String oldFormat = "dd-MM-yyyy";
        String newFormat = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        Date myDate = null;
        try {
            myDate = dateFormat.parse(mStringDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
        assert myDate != null;
        applyDate = timeFormat.format(myDate);

        submit.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (apply_dateTV.getText().equals("")) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.applyDateError), Toast.LENGTH_LONG).show();
                    } else if (!isFromDateSelected) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.fromDateError), Toast.LENGTH_LONG).show();
                    } else if (!istoDateSelected) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.toDateError), Toast.LENGTH_LONG).show();
                    } else {
                        if (Utility.isConnectingToInternet(getApplicationContext())) {
                            uploadBitmap();
                        } else {
                            makeText(getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        fromDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentDate = Calendar.getInstance();
                int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);
                int mMonth = mCurrentDate.get(Calendar.MONTH);
                int mYear = mCurrentDate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(StudentAddLeave.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {
                        //month = month + 1;
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedYear, selectedMonth, selectedDay);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        fromDate = sdf.format(newDate.getTime());
                        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
                        fromDateTV.setText(sdfDate.format(newDate.getTime()));
                        isFromDateSelected = true;
                    }
                }, mYear, mMonth, mDay);
                switch (startweek) {
                    case "Monday":
                        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);
                        break;
                    case "Tuesday":
                        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.TUESDAY);
                        break;
                    case "Wednesday":
                        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.WEDNESDAY);
                        break;
                    case "Thursday":
                        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.THURSDAY);
                        break;
                    case "Friday":
                        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.FRIDAY);
                        break;
                    case "Saturday":
                        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.SATURDAY);
                        break;
                    case "Sunday":
                        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.SUNDAY);
                        break;
                }
                datePickerDialog.show();
            }
        });

        toDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCurrentDate = Calendar.getInstance();
                int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);
                int mMonth = mCurrentDate.get(Calendar.MONTH);
                int mYear = mCurrentDate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(StudentAddLeave.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        //month = month + 1;
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        toDate = sdf.format(newDate.getTime());
                        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
                        toDateTV.setText(sdfDate.format(newDate.getTime()));
                        istoDateSelected = true;
                    }
                }, mYear, mMonth, mDay);
                switch (startweek) {
                    case "Monday":
                        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);
                        break;
                    case "Tuesday":
                        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.TUESDAY);
                        break;
                    case "Wednesday":
                        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.WEDNESDAY);
                        break;
                    case "Thursday":
                        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.THURSDAY);
                        break;
                    case "Friday":
                        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.FRIDAY);
                        break;
                    case "Saturday":
                        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.SATURDAY);
                        break;
                    case "Sunday":
                        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.SUNDAY);
                        break;
                }
                datePickerDialog.show();
            }
        });
    }

    private void decorate() {
        actionBar.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
    }

    private void showFileChooser() {
        Log.d(TAG, "showFileChooser: ");
        final Dialog dialog = new Dialog(StudentAddLeave.this);
        dialog.setContentView(R.layout.choose_file);
        dialog.setCanceledOnTouchOutside(false);
        RelativeLayout headerLay = (RelativeLayout) dialog.findViewById(R.id.addTask_dialog_header);
        final LinearLayout takePhoto = (LinearLayout) dialog.findViewById(R.id.takephoto);
        final LinearLayout chooseGallery = (LinearLayout) dialog.findViewById(R.id.gallery);
        ImageView closeBtn = (ImageView) dialog.findViewById(R.id.addTask_dialog_crossIcon);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraPic();
                camera = true;
                gallery = false;
                dialog.dismiss();
            }
        });
        chooseGallery.setOnClickListener(new View.OnClickListener() {
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

    void cameraPic() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    /* private void  {
         ContentValues contentValues = new ContentValues();
         contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic");
         contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
         imageuri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
         Intent camerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
         camerIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
         startActivityForResult(camerIntent, CAMERA_REQUEST);
     }*/

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
                intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
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
                intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
            }
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @TargetApi(19)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                Uri selectedImageUri = data.getData();
                try {
                    textView.setText("File Selected");
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmap);
                    filePath = saveBitmap(bitmap);
                    Log.d(TAG, "onActivityResulvfbt: " + filePath);
                    File f = new File(filePath);
                    String mimeType = URLConnection.guessContentTypeFromName(f.getName());
                    file_body = RequestBody.create(MediaType.parse(mimeType), f);
                    System.out.println("file_bodypathasd" + file_body);
                    progress.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == CAMERA_REQUEST) {
                Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                if (bitmap != null) {
                    progress = new ProgressDialog(StudentAddLeave.this);
                    progress.setTitle("uploading");
                    progress.setMessage("Please Wait....");
                    progress.show();
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmap);
                    filePath = saveBitmap(bitmap);
                    Log.d(TAG, "onActivityResulvfbt: " + filePath);
                    File f = new File(filePath);
                    String mimeType = URLConnection.guessContentTypeFromName(f.getName());
                    file_body = RequestBody.create(MediaType.parse(mimeType), f);
                    System.out.println("file_bodypathasd" + file_body);
                    progress.dismiss();
                }
            }
        }

        /*if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            boolean isImageFromGoogleDrive = false;

            Uri uri = data.getData();

            if (isKitKat && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
                if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        filePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else {
                        Pattern DIR_SEPORATOR = Pattern.compile("/");
                        Set<String> rv = new HashSet<>();
                        String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
                        String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
                        String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
                        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
                            if (TextUtils.isEmpty(rawExternalStorage)) {
                                rv.add("/storage/sdcard0");
                            } else {
                                rv.add(rawExternalStorage);
                            }
                        } else {
                            String rawUserId;
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                rawUserId = "";
                            } else {
                                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                                String[] folders = DIR_SEPORATOR.split(path);
                                String lastFolder = folders[folders.length - 1];
                                boolean isDigit = false;
                                try {
                                    Integer.valueOf(lastFolder);
                                    isDigit = true;
                                } catch (NumberFormatException ignored) {
                                }
                                rawUserId = isDigit ? lastFolder : "";
                            }
                            if (TextUtils.isEmpty(rawUserId)) {
                                rv.add(rawEmulatedStorageTarget);
                            } else {
                                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
                            }
                        }
                        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                            String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                            Collections.addAll(rv, rawSecondaryStorages);
                        }
                        String[] temp = rv.toArray(new String[rv.size()]);
                        for (int i = 0; i < temp.length; i++) {
                            File tempf = new File(temp[i] + "/" + split[1]);
                            if (tempf.exists()) {
                                filePath = temp[i] + "/" + split[1];
                            }
                        }
                    }
                } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    String id = DocumentsContract.getDocumentId(uri);
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    Cursor cursor = null;
                    String column = "_data";
                    String[] projection = {column};
                    try {
                        cursor = getApplicationContext().getContentResolver().query(contentUri, projection, null, null,
                                null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int column_index = cursor.getColumnIndexOrThrow(column);
                            filePath = cursor.getString(column_index);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    String selection = "_id=?";
                    String[] selectionArgs = new String[]{split[1]};

                    Cursor cursor = null;
                    String column = "_data";
                    String[] projection = {column};

                    try {
                        cursor = getApplicationContext().getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int column_index = cursor.getColumnIndexOrThrow(column);
                            filePath = cursor.getString(column_index);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                } else if ("com.google.android.apps.docs.storage".equals(uri.getAuthority())) {
                    isImageFromGoogleDrive = true;
                }
            }
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                Cursor cursor = null;
                String column = "_data";
                String[] projection = {column};

                try {
                    cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int column_index = cursor.getColumnIndexOrThrow(column);
                        filePath = cursor.getString(column_index);
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                filePath = uri.getPath();
            }

            try {
                Log.d(TAG, "Real Path 1 : " + filePath);
                System.out.println("Real Path 1" + filePath);
                textView.setText("File Selected");
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                System.out.println("bitmap image==" + bitmap);
                String file_name = filePath.substring(filePath.lastIndexOf("/") + 1);
                String[] filenameArray = file_name.split("\\.");
                String extension = filenameArray[filenameArray.length - 1];
                System.out.println("extension" + extension);
                if (extension.equals("jpg") || extension.equals("png") || extension.equals("jpeg")) {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selected_file));
                }
                File f = new File(filePath);
                String mimeType = URLConnection.guessContentTypeFromName(f.getName());
                file_body = RequestBody.create(MediaType.parse(mimeType), f);
                System.out.println("file_bodypathasd" + file_body);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else*/

    }

  /*  public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            boolean isImageFromGoogleDrive = false;

            Uri uri = data.getData();

            if (isKitKat && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
                if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        filePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else {
                        Pattern DIR_SEPORATOR = Pattern.compile("/");
                        Set<String> rv = new HashSet<>();
                        String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
                        String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
                        String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
                        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
                            if (TextUtils.isEmpty(rawExternalStorage)) {
                                rv.add("/storage/sdcard0");
                            } else {
                                rv.add(rawExternalStorage);
                            }
                        } else {
                            String rawUserId;
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                rawUserId = "";
                            } else {
                                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                                String[] folders = DIR_SEPORATOR.split(path);
                                String lastFolder = folders[folders.length - 1];
                                boolean isDigit = false;
                                try {
                                    Integer.valueOf(lastFolder);
                                    isDigit = true;
                                } catch (NumberFormatException ignored) {
                                }
                                rawUserId = isDigit ? lastFolder : "";
                            }
                            if (TextUtils.isEmpty(rawUserId)) {
                                rv.add(rawEmulatedStorageTarget);
                            } else {
                                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
                            }
                        }
                        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                            String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                            Collections.addAll(rv, rawSecondaryStorages);
                        }
                        String[] temp = rv.toArray(new String[rv.size()]);
                        for (int i = 0; i < temp.length; i++) {
                            File tempf = new File(temp[i] + "/" + split[1]);
                            if (tempf.exists()) {
                                filePath = temp[i] + "/" + split[1];
                            }
                        }
                    }
                } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    String id = DocumentsContract.getDocumentId(uri);
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    Cursor cursor = null;
                    String column = "_data";
                    String[] projection = {column};
                    try {
                        cursor = getApplicationContext().getContentResolver().query(contentUri, projection, null, null,
                                null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int column_index = cursor.getColumnIndexOrThrow(column);
                            filePath = cursor.getString(column_index);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    String selection = "_id=?";
                    String[] selectionArgs = new String[]{split[1]};

                    Cursor cursor = null;
                    String column = "_data";
                    String[] projection = {column};

                    try {
                        cursor = getApplicationContext().getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int column_index = cursor.getColumnIndexOrThrow(column);
                            filePath = cursor.getString(column_index);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                } else if ("com.google.android.apps.docs.storage".equals(uri.getAuthority())) {
                    isImageFromGoogleDrive = true;
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                Cursor cursor = null;
                String column = "_data";
                String[] projection = {column};

                try {
                    cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int column_index = cursor.getColumnIndexOrThrow(column);
                        filePath = cursor.getString(column_index);
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                filePath = uri.getPath();
            }

            try {
                Log.d(TAG, "Real Path 1 : " + filePath);
                System.out.println("Real Path 1" + filePath);
                textView.setText("File Selected");
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                System.out.println("bitmap image==" + bitmap);
                String file_name = filePath.substring(filePath.lastIndexOf("/") + 1);
                String[] filenameArray = file_name.split("\\.");
                String extension = filenameArray[filenameArray.length - 1];
                System.out.println("extension" + extension);
                if (extension.equals("jpg") || extension.equals("png") || extension.equals("jpeg")) {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selected_file));
                }
                File f = new File(filePath);
                String mimeType = URLConnection.guessContentTypeFromName(f.getName());
                file_body = RequestBody.create(MediaType.parse(mimeType), f);
                System.out.println("file_bodypathasd" + file_body);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST) {
            Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            if (bitmap != null) {
                progress = new ProgressDialog(StudentAddLeave.this);
                progress.setTitle("uploading");
                progress.setMessage("Please Wait....");
                progress.show();
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
                // Uri imageuri = getImageUri(getApplicationContext(), bitmap);
                Log.d(TAG, "onActivityResulvfbt: "+imageuri);
                filePath = saveBitmap(bitmap);
                Log.d(TAG, "onActivityResulvfbt: "+filePath);
                File f = new File(filePath);
                String mimeType = URLConnection.guessContentTypeFromName(f.getName());
                file_body = RequestBody.create(MediaType.parse(mimeType), f);
                System.out.println("file_bodypathasd" + file_body);
                progress.dismiss();
            }
        }
    }*/

    public static String saveBitmap(Bitmap bitmap) {
        String filePath = null;
        File file = null;
        try {
            File directory = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "MyApp");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            file = new File(directory, "image_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            filePath = file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    private void uploadBitmap() throws IOException {
        url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + Constants.addLeave;
        OkHttpClient client = new OkHttpClient();
        Log.d(TAG, "uploadBitmap: " + url);

        if (filePath != null || file_body != null) {
            {
                String file_name = filePath.substring(filePath.lastIndexOf("/") + 1);
                Log.d(TAG, filePath + "uploadBitmap: " + file_body);

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", file_name, file_body)
                        .addFormDataPart("to_date", toDate)
                        .addFormDataPart("apply_date", applyDate)
                        .addFormDataPart("from_date", fromDate)
                        .addFormDataPart("reason", reason.getText().toString())
                        .addFormDataPart("student_id", Utility.getSharedPreferences(getApplicationContext(), Constants.studentId))
                        .addFormDataPart("session_id", Utility.getSharedPreferences(getApplicationContext(), Constants.sessionId))
                        .build();
                Log.d(TAG, "uploadBitmap: " + toDate);
                Log.d(TAG, "uploadBitmap: " + reason.getText().toString());
                Log.d(TAG, "uploadBitmap: " + fromDate);
                Log.d(TAG, "uploadBitmaprr: " + applyDate);
                Request request = new Request.Builder()
                        .url(url)
                        .header("Client-Service", Constants.clientService)
                        .header("Auth-Key", Constants.authKey)
                        .header("User-ID", Utility.getSharedPreferences(getApplicationContext(), Constants.userId))
                        .header("Authorization", Utility.getSharedPreferences(getApplicationContext(), "accessToken"))
                        .post(requestBody)
                        .build();
                Log.d(TAG, "uploadBitmapr: " + requestBody);
                Log.d(TAG, "uploadBitmapb: " + request);
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                makeText(mContext, R.string.apiErrorMsg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        ResponseBody body = response.body();
                        if (body != null) {
                            try {
                                assert response.body() != null;
                                String jsonData = response.body().string();
                                Log.d(TAG, "onResponsef: " + jsonData);
                                try {
                                    final JSONObject Object = new JSONObject(jsonData);
                                    String Array = Object.getString("status");
                                    Log.d(TAG, "onResponsef: " + jsonData);
                                    if (Array.equals("1")) {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(mContext, getApplicationContext().getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });

                                    } else {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                try {
                                                    final JSONObject Object = new JSONObject(jsonData);
                                                    String error = Object.getString("error");
                                                    Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
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
            }
        }
    }

  /*  private void uploadBitmap() throws IOException{
        url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + ;
        OkHttpClient client=new OkHttpClient();
        Log.i("url=", url);
        Log.d(TAG, "uploadBitmap: "+url);
        if(filePath!=null || file_body!=null)  {
            String file_name=filePath.substring(filePath.lastIndexOf("/")+1);
            System.out.println("file_name== "+file_name);

            RequestBody requestBody=new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file",file_name,file_body)
                    .addFormDataPart("to_date", toDate)
                    .addFormDataPart("apply_date",applydate)
                    .addFormDataPart("from_date",fromdate)
                    .addFormDataPart("reason",reason.getText().toString())
                    .addFormDataPart("student_id", Utility.getSharedPreferences(getApplicationContext(), Constants.studentId))
                    .addFormDataPart("session_id", Utility.getSharedPreferences(getApplicationContext(), Constants.sessionId))
                    .build();
            Log.d(TAG, "uploadBitmap: "+toDate);
            Log.d(TAG, "uploadBitmap: "+applydate);
            Log.d(TAG, "uploadBitmap: "+reason.getText().toString());

            Request request=new Request.Builder()
                    .url(url)
                    .header("Client-Service", Constants.clientService)
                    .header("Auth-Key", Constants.authKey)
                    .header("User-ID",Utility.getSharedPreferences(getApplicationContext(), Constants.userId))
                    .header("Authorization",Utility.getSharedPreferences(getApplicationContext(), "accessToken"))
                    .post(requestBody)
                    .build();
            Log.d(TAG, url+"uploadBitmap: "+requestBody);
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) { }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody body = response.body();
                    if(body != null) {
                        try {
                            String jsonData = response.body().string();
                            Log.d(TAG, filePath+"uploadBitmap: "+file_body);
                            Log.d(TAG, "uploadBitmap: "+jsonData);
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
        }


    }*/

}



