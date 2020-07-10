package mtech.com.visitortesting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class LoginActivity extends Activity {

    String socName = null, pass = null, IMEINo = null;
    ArrayAdapter<String> society_adapter;
    Spinner sName;
    EditText password;
    SharedPreferences sp,sp_device;
    SharedPreferences.Editor editor, editor_device;
    ProgressDialog pd = null;
    String android_id=null;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (android.os.Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        password = (EditText) findViewById(R.id.newpass);
        sName =  (Spinner) findViewById(R.id.societyName);
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        cd = new ConnectionDetector(LoginActivity.this);
        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
        editor = sp.edit();

        new GetLongLat("E").execute();
        new GetLongLat("android_id").execute();
    }
    public void login(View v){
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent == true) {
            socName= sName.getSelectedItem().toString();
            pass= password.getText().toString();
            new GetLongLat("C").execute();
        }else{
            Toast.makeText(this, "Internet Connection Error", Toast.LENGTH_SHORT).show();
        }

    }
    public class GetLongLat extends AsyncTask<String, String, String>{
        Connection conn;
        ResultSet rs = null, rs1 = null;
        ProgressDialog progressDialog;
        DBConnection dbConnection = new DBConnection();
        String ret = "", flag = "";

        public GetLongLat(String flag)
        {
            this.flag = flag;
        }

        @Override
        protected void onPreExecute(){
//            pd = ProgressDialog.show(LoginActivity.this, "","Please wait ...", true);

//            pd.setCancelable(true);
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String res) {
            progressDialog.dismiss();
//            pd.dismiss();
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
                    ret = "Internet Connection Error";
                } else {
                    if (flag.equalsIgnoreCase("E")) {

                        String query = "Select DISTINCT SocietyName from OwnerDetails";
                        Statement stmt = conn.createStatement();
                        rs = stmt.executeQuery(query);
                        ret = "1";
                    }
                    else if (flag.equalsIgnoreCase("android_id")) {

                        String query = "SELECT DeviceId from TrackAndroidDeviceInfo where DeviceId='" + android_id + "'";
                        Statement stmt = conn.createStatement();
                        rs = stmt.executeQuery(query);
                        ret = "1";
                    }
                    else if (flag.equalsIgnoreCase("C")) {
                        String query = "SELECT TOP 1 * from TrackAndroidDeviceInfo where SocietyName='"+socName+"' AND Password = '"+pass+"' ";
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
//                    new GetLongLat("android_id").execute();
                } catch (Exception e){
                    e.toString();
                }
            }
            else if (flag.equalsIgnoreCase("android_id")) {
                if(rs.next()) {

                }else{

                    //intent registration activity
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            else if (flag.equalsIgnoreCase("C")){
                if(rs.next()) {
                    String socieName = null;
                    socieName= sName.getSelectedItem().toString();
                    editor.putBoolean("sf", true);
                    editor.putString("societyname",socieName);
                    editor.commit();
                    Intent ii= new Intent(LoginActivity.this,BackgroundService.class);
                    startService(ii);
                    Intent intent = new Intent(LoginActivity.this, MobileActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(this, "Invalid Password !", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

//-------------------------------------------------------------------------------------------

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
