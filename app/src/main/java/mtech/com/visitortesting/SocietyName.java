package mtech.com.visitortesting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class SocietyName extends Activity {
    EditText phone;
    Button login, searchVisitor, searchVehicles;
    String phoneNo = null;
    ArrayAdapter<String> society_adapter;
    Spinner sName;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_societyname);
        sName =  (Spinner) findViewById(R.id.societyName);
        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
        editor = sp.edit();
        new GetLongLat("E").execute();

    }
        public void login(View view){
            int sn = sName.getSelectedItemPosition();
            if(sn != 0){
                String socieName=null;
                socieName= sName.getSelectedItem().toString();
                editor.putBoolean("sf", true);
                editor.putString("societyname",socieName);
                editor.commit();
                Intent ii= new Intent(SocietyName.this,BackgroundService.class);
                startService(ii);
                Intent intent = new Intent(SocietyName.this, MobileActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(this, "Please Select Society", Toast.LENGTH_SHORT).show();
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
                    progressDialog = new ProgressDialog(SocietyName.this);
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
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }

}