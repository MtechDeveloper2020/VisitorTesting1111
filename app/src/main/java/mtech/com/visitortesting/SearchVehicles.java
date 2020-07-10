package mtech.com.visitortesting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SearchVehicles extends Activity {
    TableLayout tableLayout;
    TableRow row;
    String daytype = null, dayDate = null, radioSelection = "Vehicle No.", vehNo = null, visName = null, mob = null;
    EditText one, two, three, four, vName, mobileno;
    String societyname=null;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    ListView list;
    VehicleListAdapter adapter = null;
    ArrayList<String> rpoints;
    ArrayList<String> od;
    ArrayList<String> vehicleNo;
    ArrayList<String> EnterDate;
    ArrayList<String> purpose;
    ViewGroup header;
    LayoutInflater inflater;
    ArrayList<String> Active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_vehicles);

        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
        editor = sp.edit();
        societyname= sp.getString("societyname",null);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        rpoints = new ArrayList<String>();
        od = new ArrayList<String>();
        vehicleNo = new ArrayList<String>();
        EnterDate = new ArrayList<String>();
        purpose = new ArrayList<String>();
        Active = new ArrayList<String>();
        list = (ListView) findViewById(R.id.list);
//        tableLayout = (TableLayout) findViewById(R.id.tablelayout);
//        tableLayout.removeAllViews();
//        new GetLongLat("E").execute();
        one = (EditText) findViewById(R.id.one);
        two = (EditText) findViewById(R.id.two);
        three = (EditText) findViewById(R.id.three);
        four = (EditText) findViewById(R.id.four);

        //-------------------------------------------------------------------------------------------


//        row = new TableRow(getApplicationContext());
//        row.setBackgroundColor(Color.parseColor("#c0c0c0"));
//        row.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        String[] headerText = {"Flat No./Purpose", "Vehicle No. ", "InTime"};
//        for (String c : headerText) {
//            TextView tv = new TextView(this);
//            tv.setLayoutParams(new TableRow.LayoutParams(150,
//                    140));
//            tv.setGravity(Gravity.LEFT);
//            tv.setTextSize(18);
//            tv.setPadding(5, 5, 5, 5);
//
//            tv.setText(c);
//            row.addView(tv);
//
//        }
//        tableLayout.addView(row);


        //Spinner onclick===============================
        one.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (one.getText().toString().length() == 2)    //size as per your requirement
                {
                    two.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });
        //=======================================
        two.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (two.getText().toString().length() == 2)     //size as per your requirement
                {
                    three.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });
        //================================================
        three.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (three.getText().toString().length() == 2)     //size as per your requirement
                {
                    four.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });
        //=================================================


    }


    private void tview() {
        row = new TableRow(getApplicationContext());
        row.setBackgroundColor(Color.parseColor("#c0c0c0"));
        row.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        String[] headerText = {"Flat No./Purpose", "Vehicle No. ", "Type/Time"};
        for (String c : headerText) {
            TextView tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(150,
                    140));
            tv.setGravity(Gravity.LEFT);
            tv.setTextSize(18);
            tv.setPadding(5, 5, 5, 5);

            tv.setText(c);
            row.addView(tv);

        }
        tableLayout.addView(row);
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
            progressDialog = new ProgressDialog(SearchVehicles.this);
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
                    if (flag.equalsIgnoreCase("vehicle")) {

                        String query = "SELECT * FROM VisitorDetails where convert(VARCHAR, EnteredAt,105) = convert(VARCHAR,getDate(),105) AND " +
                                "VehicleNumber LIKE '%" + vehNo + "%'   and SocietyName='" +
                                ""+societyname+"' ";
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
            if (flag.equalsIgnoreCase("vehicle")) {
                int i = 1;
                while (rs.next()) {
                    String a = rs.getString("VisitorName");
                    rpoints.add(a);
                    String b = rs.getString("OwnerDetails");
                    od.add(b);
                    String c = rs.getString("VehicleNumber");
                    vehicleNo.add(c);
                    String datePattern = "dd MMM yyyy";
                    String timePattern = "HH:mm a";
                    Date today;
                    String dateOutput;
                    SimpleDateFormat simpleDateFormat, sp;
                    simpleDateFormat = new SimpleDateFormat(datePattern);
                    Time t;
                    String timeOutput;
                    sp = new SimpleDateFormat(timePattern);
                    t = rs.getTime("EnteredAt");
                    timeOutput = sp.format(t);
                    today = rs.getDate("EnteredAt");
                    dateOutput = simpleDateFormat.format(today);
//                        System.out.println(datePattern + " - " + dateOutput);
                    EnterDate.add(dateOutput + " at " + timeOutput);
//
                    String e = rs.getString("Purpose");
                    purpose.add(e);
                    String act1 = rs.getString("Active");
                    Active.add(act1);
                }
                adapter = new VehicleListAdapter(SearchVehicles.this, rpoints, od, vehicleNo, EnterDate, purpose, Active);
                inflater = getLayoutInflater();
                header = (ViewGroup) inflater.inflate(R.layout.vehicle_listheader, list, false);
                list.addHeaderView(header);

                list.setAdapter(adapter);

            }

        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void searchby(View v){
        String on = null, tw = null, thre = null, fou = null;
        //    Toast.makeText(this, ""+dayDate, Toast.LENGTH_SHORT).show();
        //    Toast.makeText(this, ""+radioSelection.toString(), Toast.LENGTH_SHORT).show();

        on = one.getText().toString();
        tw = two.getText().toString();
        thre = three.getText().toString();
        fou = four.getText().toString();
        if (!on.equals("")){
            if (!tw.equals("")){
                if (!thre.equals("")){
                    if (!fou.equals("")){
                        vehNo = on + " " + tw + " " + thre + " " + fou;
                        if (header != null){
                            header.removeAllViews();
                        }
                        if (adapter != null){
                            adapter.clear();
                            rpoints.clear();
                            od.clear();
                            vehicleNo.clear();
                            EnterDate.clear();
                            purpose.clear();
                            Active.clear();
                        }
                        new GetLongLat("vehicle").execute();
                        //  Toast.makeText(this, ""+vehNo, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(this, "Invalid Vehicle No.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Invalid Vehicle No.", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Invalid Vehicle No.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Invalid Vehicle No.", Toast.LENGTH_SHORT).show();
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