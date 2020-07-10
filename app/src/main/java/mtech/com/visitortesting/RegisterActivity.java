package mtech.com.visitortesting;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RegisterActivity extends ActionBarActivity {
    String generatedKey = null;
    EditText pass,cpass , mobileNumber;
    EditText K1, K2, K3, K4, V1, V2, V3, V4;
    TextView Msg;
    Button SignIn;
    String password,confirmpass,socName,macAddress, android_id, mobile, encodedKey, vendorKey;
    int ipAddress;
    SharedPreferences sp,sp_device;
    SharedPreferences.Editor editor, editor_device;
    ProgressDialog progressDialog;
    ArrayAdapter<String> society_adapter;
    Spinner sName;
    String KeyToEmail=null;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (android.os.Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        sName =  (Spinner) findViewById(R.id.societyName);
        pass = (EditText) findViewById(R.id.newpass);
        cpass = (EditText) findViewById(R.id.confirmpass);
        mobileNumber = (EditText) findViewById(R.id.mobile);
        cd = new ConnectionDetector(RegisterActivity.this);
        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
        editor = sp.edit();
        sp_device = getSharedPreferences("sp_device", Context.MODE_PRIVATE);
        editor_device = sp_device.edit();
        new GetLongLat("E").execute();
        new GetLongLat("Email").execute();

//        email.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!isValidEmail(email.getText().toString())) {
//                    email.setError("Invalid Email");
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

    }

    //===============================================================================================
    public void register(View v){
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent == true) {
            try {

//            keyActivities();
                password = pass.getText().toString();
                confirmpass = cpass.getText().toString();
                mobile = mobileNumber.getText().toString();
                socName = sName.getSelectedItem().toString();
                int sn = sName.getSelectedItemPosition();
                if (sn != 0) {
                    if (password.length() >= 6) {
                        if (password.equalsIgnoreCase(confirmpass)) {
                            if (mobile.toString().length() == 10) {
                                getDeviceInfo();
//                                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                            } else {
                                mobileNumber.setError("Mobile Number must be 10 digit");
                                Toast.makeText(this, "Mobile Number must be 10 digit !", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Confirm Password did not match !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Password must contain atleast 6 characters !", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Please Select Society", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Internet Connection Error", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
//        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //============== Getting android parameter & generate encrypted key ====================
    private void getDeviceInfo() {
        try {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            macAddress = wInfo.getMacAddress();
            ipAddress = wInfo.getIpAddress();
            android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

            //=====
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String Date = dateFormat.format(new Date());
            String passwordToHash = macAddress + Date + android_id;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10; i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedKey = sb.toString().toUpperCase();
//            K1.setText(generatedKey.substring(0, 5));
//            K2.setText(generatedKey.substring(5, 10));
//            K3.setText(generatedKey.substring(10, 15));
//            K4.setText(generatedKey.substring(15, 20));
            encodedKey = Base64.encodeToString(generatedKey.substring(0, 20).getBytes(), Base64.DEFAULT).substring(0, 20).toUpperCase();
            Log.d("encodedKey Key1 : ", encodedKey);
            //======

            //=====
            new DevicesInfo().execute();

            //========
            try {
                GMailSender sender = new GMailSender("vmsvstrack@gmail.com", "vstrack12345");
                sender.sendMail( "VANKAN- New Vendor Key",
                        "Generated Key:  "  +generatedKey+"" +
                                " \n Vendor Key : "+encodedKey,
                        "vmsvstrack@gmail.com",
                        KeyToEmail);
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }
            //================

//            Toast.makeText(this, "" + macAddress, Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "" + ipAddress, Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "" + android_id, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //======================= Device Info ======================================================
    public class DevicesInfo extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog;
        Connection conn;
        DBConnection dbConnection = new DBConnection();
        String ret = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
        String Date = dateFormat.format(new Date());
        String Time = dateFormat1.format(new Date());
        String TimeStamp = Date + " " + Time;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage("please wait....");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String r) {
            progressDialog.dismiss();
            if (r.equalsIgnoreCase("1")) {
                Toast.makeText(getApplicationContext(), "Device Registered Successfully!", Toast.LENGTH_SHORT).show();
                keyActivities();
            } else {
                Toast.makeText(getApplicationContext(), r, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                conn = dbConnection.connectionclass(); //Connect to database
                if (conn == null) {
                    ret = "Error in connection with SQL server";
//                    ret = "Internet Connection Error";
                } else {

                    PreparedStatement stmt = conn.prepareStatement("insert into " + "TrackAndroidDeviceInfo" + "(SocietyName,Password,Mobile,DeviceMac,DeviceIp,DeviceId,CreationDate,GeneratedKey,VendorKey)" + " values(?,?,?,?,?,?,?,?,?)");
                    stmt.setString(1, socName);
                    stmt.setString(2, password);
                    stmt.setString(3, mobile);
                    stmt.setString(4, macAddress);
                    stmt.setString(5, String.valueOf(ipAddress));
                    stmt.setString(6, android_id);
                    stmt.setTimestamp(7, java.sql.Timestamp.valueOf(TimeStamp));
                    stmt.setString(8, generatedKey);
                    stmt.setString(9, encodedKey);
                    int i = stmt.executeUpdate();
                    ret = "" + i;
                }
            } catch (Exception ex) {
                ret = ex.getMessage();
            }
            return ret;
        }
    }

    //=========================================================================================
    private void keyActivities() {
        setContentView(R.layout.key_generation);
        K1 = (EditText) findViewById(R.id.k1);
        K2 = (EditText) findViewById(R.id.k2);
        K3 = (EditText) findViewById(R.id.k3);
        K4 = (EditText) findViewById(R.id.k4);
        V1 = (EditText) findViewById(R.id.v1);
        V2 = (EditText) findViewById(R.id.v2);
        V3 = (EditText) findViewById(R.id.v3);
        V4 = (EditText) findViewById(R.id.v4);
        Msg = (TextView) findViewById(R.id.msg);
        SignIn = (Button) findViewById(R.id.signin);

        try {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String Date = dateFormat.format(new Date());
//            String passwordToHash = macAddress + Date + android_id;
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            md.update(passwordToHash.getBytes());
//            byte[] bytes = md.digest();
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < 10; i++) {
//                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
//            }
//            String generatedKey = sb.toString().toUpperCase();
             K1.setText(generatedKey.substring(0, 5));
             K2.setText(generatedKey.substring(5, 10));
             K3.setText(generatedKey.substring(10, 15));
             K4.setText(generatedKey.substring(15, 20));
            encodedKey = Base64.encodeToString(generatedKey.substring(0, 20).getBytes(), Base64.DEFAULT).substring(0, 20).toUpperCase();
            Log.d("encodedKey Key : ", encodedKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SignIn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                try {
                    new KeyMatching().execute();
                   /* vendorKey = V1.getText().toString().toUpperCase() + V2.getText().toString().toUpperCase() + V3.getText().toString().toUpperCase() + V4.getText().toString().toUpperCase();
                    Log.d("vendorKey Key : ", vendorKey);
                    if (encodedKey.equalsIgnoreCase(vendorKey)) {
                        Toast.makeText(LoginActivity.this, "Key Authentication Successful !", Toast.LENGTH_SHORT).show();
                        editor.putBoolean("sf", true);
                        editor.commit();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Msg.setText("Sorry your key not match, Try again");
                        Toast.makeText(LoginActivity.this, "Sorry your key not match, Try again", Toast.LENGTH_SHORT).show();
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void matchKey() {
        try {
            vendorKey = V1.getText().toString().toUpperCase() + V2.getText().toString().toUpperCase() + V3.getText().toString().toUpperCase() + V4.getText().toString().toUpperCase();
            Log.d("vendorKey Key : ", vendorKey);
            Log.d("Encoded Key : ", encodedKey);
            if (encodedKey.equalsIgnoreCase(vendorKey)) {
                Toast.makeText(RegisterActivity.this, "Key Authentication Successful !", Toast.LENGTH_SHORT).show();
//                startService(new Intent(this, BackgroundService.class));
//                editor.putBoolean("sf", true);
//                editor.commit();
                String socieName=null;
                socieName= sName.getSelectedItem().toString();
                editor.putBoolean("sf", true);
                editor.putString("societyname",socieName);
                editor.commit();
                Intent ii= new Intent(RegisterActivity.this,BackgroundService.class);
                startService(ii);
                Intent intent = new Intent(RegisterActivity.this, MobileActivity.class);
                startActivity(intent);
                finish();
            } else {
                Msg.setText("Sorry your key not match, Try again");
                Toast.makeText(RegisterActivity.this, "Sorry your key not match, Try again", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class KeyMatching extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            matchKey();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
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
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String res) {
            progressDialog.dismiss();
            if (res.equalsIgnoreCase("1")) {

                showName(rs, rs1, flag);
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
                    ret = "Error in connection with SQL server";
                } else {
                    if (flag.equalsIgnoreCase("E")) {

                        String query = "Select DISTINCT SocietyName from OwnerDetails";
                        Statement stmt = conn.createStatement();
                        rs = stmt.executeQuery(query);
                        ret = "1";
                    }
                   else if (flag.equalsIgnoreCase("Email")) {

                        String query = "Select Top 1 EmailId from VMS_KeySendToEmail";
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

    private void showName(ResultSet rs, ResultSet rs1, String flag) {
        try {

            if (flag.equalsIgnoreCase("E")) {
                try {
                    ArrayList<String> purpose = new ArrayList<String>();
                    purpose.add("----Select Society----");
                    while (rs.next()){
                        purpose.add(rs.getString("SocietyName"));
                    }
                    society_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, purpose);
                    sName.setAdapter(society_adapter);
                } catch (Exception e){
                    e.toString();
                }
            }
           else if (flag.equalsIgnoreCase("Email")) {
                try {

                    while (rs.next()){
                        KeyToEmail = rs.getString("EmailId");
                    }

                } catch (Exception e){
                    e.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

}

