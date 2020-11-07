package mtech.com.visitortesting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import mtech.com.visitortesting.resident.ResidentAccess;
import mtech.com.visitortesting.resident.ResidentRegistration;

public class MobileActivity extends Activity {
    EditText phone;
    Button addVisitor, searchVisitor, searchVehicles;
    String phoneNo = null;
    byte[] vbyteimage = null, idbyteimage = null;
    Bitmap vpic = null, idpic = null;
    String name = null;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    BackgroundService bs = new BackgroundService();
    String android_id = null, societyName=null;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    Button resRegister,resAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile);
//        boolean b = sp.getBoolean("sf",false);
//        if(!b)
//        startService(new Intent(this, BackgroundService.class));

        if (android.os.Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        phone = (EditText) findViewById(R.id.phonenumber);
        resAccess= (Button) findViewById(R.id.resident_access);
        resRegister= (Button) findViewById(R.id.resident_register);
        resRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(MobileActivity.this, ResidentRegistration.class);
                startActivity(i);
            }
        });
        resAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(MobileActivity.this, ResidentAccess.class);
                startActivity(i);
            }
        });

        cd = new ConnectionDetector(MobileActivity.this);
        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
        editor = sp.edit();

        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        //next step
        //------------------ Key Generation Call ----------------------------
        if (!sp.getBoolean("sf", false)){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            startService(new Intent(this, BackgroundService.class));
            societyName= sp.getString("societyname",null);
        }
        //------------------------------------------------------------------
//        new GetLongLat("android_id").execute();
    }
    public void addVisitor(View v){
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent == true){
            phoneNo = phone.getText().toString();
            if (!phoneNo.equals("")) {
                if (phoneNo.length() == 10) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new GetLongLat("E").execute();
                        }
                    }, 25);
                } else {
                    Toast.makeText(this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Internet Connection Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchVisitor(View v){
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent == true){
            Intent i = new Intent(MobileActivity.this, SearchVisitors.class);
            startActivity(i);
        }else{
            Toast.makeText(this, "Internet Connection Error", Toast.LENGTH_SHORT).show();
        }
    }
    public void incorrectVisitor(View v){
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent == true) {
            Intent i = new Intent(MobileActivity.this, IncorrectVisitors.class);
            startActivity(i);
        }else{
            Toast.makeText(this, "Internet Connection Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchVehicles(View v){
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent == true) {
        Intent i = new Intent(MobileActivity.this, SearchVehicles.class);
        startActivity(i);
        }else{
            Toast.makeText(this, "Internet Connection Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void predefinedVisitor(View v){
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent == true) {
        Intent i = new Intent(MobileActivity.this, PredefinedVisitors.class);
        startActivity(i);
    }else{
        Toast.makeText(this, "Internet Connection Error", Toast.LENGTH_SHORT).show();
    }
    }

    public void logout(View v){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MobileActivity.this);
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface arg0, int arg1){

                    }
                });

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int which){
                onDestroy();

                SharedPreferences sharedPreferences = getSharedPreferences("sp", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                sp.unregisterOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener(){
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s){
                        SharedPreferences.Editor editor1 = sharedPreferences.edit();
                        editor1.clear();
                        editor1.commit();
                    }
                });
                Intent serviceIntent = new Intent(MobileActivity.this, BackgroundService.class);
                serviceIntent.addCategory("some_unique_string");
                stopService(serviceIntent);
                startService(serviceIntent);
//                Intent logout = new Intent(MobileActivity.this, RegisterActivity.class);
//                startActivity(logout);
//                finish();
                Intent intent = new Intent(MobileActivity.this, LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finishAffinity();

//                Log.d(TAG, sharedPreferences.getString("password", ""));
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void showName(ResultSet rs, String flag){
        boolean flag1 = false;
        String activee=null;
        try {
            if (flag.equalsIgnoreCase("E")){
                while (rs.next()) {
//                    name= rs.getString("VisitorName");
//                    Toast.makeText(this, ""+name, Toast.LENGTH_SHORT).show();
//                  vbyteimage = rs.getBytes("VisitorPhoto");
//                  idbyteimage = rs.getBytes("VisitorIdPhoto");
                    flag1 = true;
                   activee = rs.getString("Active");
                }
                if(activee.equalsIgnoreCase("2")){
                    android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle("Block List Alert");
                    alertDialogBuilder.setMessage("Continue?");
                    alertDialogBuilder.setNegativeButton("No",
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface arg0, int arg1){

//                        onDestroy();
//                        finish();
                                }
                            });

                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isInternetPresent = cd.isConnectingToInternet();
                            if (isInternetPresent == true){
                                new GetLongLat("E").execute();
//                    onDestroy();

//                    SharedPreferences sharedPreferences =getSharedPreferences("sp", Context.MODE_PRIVATE);
//
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.clear();
//                    editor.commit();
//
//                    Intent logout = new Intent(getApplication(), LoginActivity.class);
//                    startActivity(logout);
//                    finish();
//                Log.d(TAG, sharedPreferences.getString("password", ""));
                            }else{

                                Toast.makeText(MobileActivity.this, "Internet Connection Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

            } else if (flag.equalsIgnoreCase("android_id")) {
                if (rs.next()) {
                    //next step
                    //------------------ Key Generation Call ----------------------------
                    if (!sp.getBoolean("sf", false)) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        startService(new Intent(this, BackgroundService.class));
                    }
                    //------------------------------------------------------------------

                } else {
                    //intent registration activity
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        if (flag1) {
//                 vpic = BitmapFactory.decodeByteArray(vbyteimage, 0, vbyteimage.length);
//                 dpic = BitmapFactory.decodeByteArray(vbyteimage, 0, idbyteimage.length);
            //  image.setImageBitmap(bmp);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent i = new Intent(MobileActivity.this, AddVisitorTwo.class);
//                    i.putExtra("fullname",name);
//                    i.putExtra("visitorphoto", vpic);
//                    i.putExtra("idphoto",idpic);
                    i.putExtra("stat", "Y");
                    i.putExtra("mobile", phoneNo);
                    startActivity(i);
                    finish();
                }
            }, 25);

        } else {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(MobileActivity.this, AddVisitorOne.class);
                    i.putExtra("phone", phoneNo);
                    startActivity(i);
                    finish();
                }
            }, 25);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

//-------------------------------------------------------------------------------------------

    @Override
    public void onPause() {
        super.onPause();
    }

    public class GetLongLat extends AsyncTask<String, String, String> {
        Connection conn;
        ResultSet rs = null, rs1 = null;
        ProgressDialog progressDialog;
        DBConnection dbConnection = new DBConnection();
        String ret = "", flag = "";

        public GetLongLat(String flag) {
            this.flag = flag;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MobileActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String res) {
            progressDialog.dismiss();
            if (res.equalsIgnoreCase("1")) {

                showName(rs, flag);
                //Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
//                msg.setTextColor(Color.RED);
//                msg.setText("Internet Connection Error ");
//                btn_Status.setVisibility(onCreatePanelView(-1).INVISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                conn = dbConnection.connectionclass(); //Connect to database
                if (conn == null) {
                    ret = "Internet Connection Error";
                } else {
                    if (flag.equalsIgnoreCase("E")) {

                        String query = "SELECT VisitorFirstName from VisitorDetails where MobileNo='" + phoneNo + "' AND SocietyName= '"+societyName+"'  ORDER BY SrNo DESC ";
                        Statement stmt = conn.createStatement();
                        rs = stmt.executeQuery(query);
                        ret = "1";
                    } else if (flag.equalsIgnoreCase("android_id")) {

                        String query = "SELECT DeviceId from TrackAndroidDeviceInfo where DeviceId='" + android_id + "'";
                        Statement stmt = conn.createStatement();
                        rs = stmt.executeQuery(query);
                        ret = "1";
                    }
                }
            } catch (Exception ex) {
                ret = ex.getMessage();
            }
            return ret;
        }
    }
}
