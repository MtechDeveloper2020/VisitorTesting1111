package mtech.com.visitortesting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class IncorrectVisitors extends Activity {
    TableLayout tableLayout;
    TableRow row;
    Spinner day;
    ArrayAdapter<String> day_adapter;
    String day1[] = {"Today-आज", "Yesterday-कल", "Day Before Yesterday-परसो"};
    String daytype = null, dayDate = null, radioSelection = "Vehicle No.", vehNo = null, visName = null, mob = null;
    ArrayList<String> rpoints;
    ArrayList<String> od;
    ArrayList<String> vehicleNo;
    ArrayList<String> EnterDate;
    ArrayList<String> purpose;
    ListView list;
    ViewGroup header;
    LayoutInflater inflater;
    String societyname=null;
    SharedPreferences sp;
    SharedPreferences.Editor editor;MyListAdapter adapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incorrect_visitors);
        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
        editor = sp.edit();
        societyname= sp.getString("societyname",null);
        if (android.os.Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        list = (ListView) findViewById(R.id.list);

        rpoints = new ArrayList<String>();
        od = new ArrayList<String>();
        vehicleNo = new ArrayList<String>();
        EnterDate = new ArrayList<String>();
        purpose = new ArrayList<String>();

        day = (Spinner) findViewById(R.id.day);
        day_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, day1);
        day.setAdapter(day_adapter);


        //Spinner onclick================================
        day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (day != null && day.getSelectedItem() != null) {
                    daytype = (String) day.getSelectedItem();
                    //  Toast.makeText(SearchVisitors.this, ""+daytype, Toast.LENGTH_SHORT).show();
                    if (daytype.equals("Today-आज")) {
                        dayDate = null;
                        dayDate = "getDate()";
                        if (header != null) {
                            header.removeAllViews();
                        }
                        if (adapter != null) {
                            adapter.clear();
                            rpoints.clear();
                            od.clear();
                            vehicleNo.clear();
                            EnterDate.clear();
                            purpose.clear();

                        }
                        new GetLongLat("E").execute();
                    } else if (daytype.equals("Yesterday-कल")) {
                        dayDate = null;
                        dayDate = "getDate()-1";
                        if (header != null) {
                            header.removeAllViews();
                        }
                        if (adapter != null) {
                            adapter.clear();
                            rpoints.clear();
                            od.clear();
                            vehicleNo.clear();
                            EnterDate.clear();
                            purpose.clear();
                        }
                        new GetLongLat("E").execute();
                    } else if (daytype.equals("Day Before Yesterday-परसो")) {
                        dayDate = null;
                        if (header != null) {
                            header.removeAllViews();
                        }
                        if (adapter != null) {
                            adapter.clear();
                            rpoints.clear();
                            od.clear();
                            vehicleNo.clear();
                            EnterDate.clear();
                            purpose.clear();
                        }
                        dayDate = "getDate()-2";
                        new GetLongLat("E").execute();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Spinner onclick===============================
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
            progressDialog = new ProgressDialog(IncorrectVisitors.this);
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

                        String query = "SELECT VisitorName,OwnerDetails,VehicleNumber,EnteredAt,Purpose FROM VisitorDetails where convert(VARCHAR, EnteredAt,105) = convert(VARCHAR," + dayDate + ",105)  " +
                                "AND Active=2    and SocietyName='" +
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

            if (flag.equalsIgnoreCase("E")) {
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
                }

//                if(adapter != null) {
                adapter = new MyListAdapter(IncorrectVisitors.this, rpoints, od, vehicleNo, EnterDate, purpose, purpose);
                inflater = getLayoutInflater();
                header = (ViewGroup) inflater.inflate(R.layout.iv_listheader, list, false);
                list.addHeaderView(header);
                list.setAdapter(adapter);
//                }
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
    public void onPause(){
        super.onPause();
    }
}
