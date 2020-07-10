package mtech.com.visitortesting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SearchVisitors extends Activity {
    ListView list;
    TableRow row;
    Spinner day;
    ArrayAdapter<String> day_adapter;
    String day1[] = {"Today-आज", "Yesterday-कल", "Day Before Yesterday-परसो"};
    LinearLayout vehicleLayout, nameLayout, flatLayout, mobileLayout;
    RadioButton vehicle, visitor, flat, mobile;
    RadioGroup rgroup;
    String daytype = null, dayDate = null, radioSelection = "Vehicle No.", vehNo = null, visName = null, mob = null, vn = null;
    EditText one, two, three, four, vName, mobileno;
    String OwnerDetails = null;
    EditText FlatNo;
    TableRow insiderow;
    ArrayList<String> rpoints;
    ArrayList<String> od;
    ArrayList<String> vehicleNo;
    ArrayList<String> EnterDate;
    ArrayList<String> purpose;
    ArrayList<String> Active;

    MyListAdapter adapter = null;
    Handler refreshHandler;
    Runnable runnable;
    String act = null, CreationDate = null;
    ViewGroup header;
    LayoutInflater inflater;
    String societyname=null;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_visitors);
        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
        editor = sp.edit();
        societyname= sp.getString("societyname",null);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
//        startService(new Intent(this, IncorrectService.class));

        list = (ListView) findViewById(R.id.list);

        rpoints = new ArrayList<String>();
        od = new ArrayList<String>();
        vehicleNo = new ArrayList<String>();
        EnterDate = new ArrayList<String>();
        purpose = new ArrayList<String>();
        Active = new ArrayList<String>();

        // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        list,
                        new SwipeDismissListViewTouchListener.OnDismissCallback(){
                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions){

                                for(int position : reverseSortedPositions){
//                                    adapter.remove(adapter.getItem(position));
                                    vn = adapter.getItem(position-1);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
                                    String Date = dateFormat.format(new Date());
                                    String Time = dateFormat1.format(new Date());
                                    CreationDate = Date + " " + Time;
//                                    Toast.makeText(SearchVisitors.this, ""+vn, Toast.LENGTH_SHORT).show();
                                    new GetLongLat("A").execute();

                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
        list.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        list.setOnScrollListener(touchListener.makeScrollListener());

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
//                if(position == 0) {
//                    //code specific to first list item
//                    Toast.makeText(getApplicationContext(),"Place Your First Option Code",Toast.LENGTH_SHORT).show();
//                }
//                else if(position == 1) {
//                    //code specific to 2nd list item
//                    Toast.makeText(getApplicationContext(),"Place Your Second Option Code",Toast.LENGTH_SHORT).show();
//                }
//
//                else if(position == 2) {
//
//                    Toast.makeText(getApplicationContext(),"Place Your Third Option Code",Toast.LENGTH_SHORT).show();
//                }
//                else if(position == 3) {
//
//                    Toast.makeText(getApplicationContext(),"Place Your Forth Option Code",Toast.LENGTH_SHORT).show();
//                }
//                else if(position == 4) {
//
//                    Toast.makeText(getApplicationContext(),"Place Your Fifth Option Code",Toast.LENGTH_SHORT).show();
//                }

            }
        });

//        tableLayout.removeAllViews();
//        new GetLongLat("E").execute();
        one = (EditText) findViewById(R.id.one);
        two = (EditText) findViewById(R.id.two);
        three = (EditText) findViewById(R.id.three);
        four = (EditText) findViewById(R.id.four);
        vName = (EditText) findViewById(R.id.vName);
        mobileno = (EditText) findViewById(R.id.Mobile);
        FlatNo = (EditText) findViewById(R.id.flatNumber);
        vehicleLayout = (LinearLayout) findViewById(R.id.vehicleLayout);
        nameLayout = (LinearLayout) findViewById(R.id.NameLayout);
        flatLayout = (LinearLayout) findViewById(R.id.FlatLayout);
        mobileLayout = (LinearLayout) findViewById(R.id.MobileLayout);
        vehicle = (RadioButton) findViewById(R.id.vehicleNo);
        visitor = (RadioButton) findViewById(R.id.visitorName);
        flat = (RadioButton) findViewById(R.id.flatNo);
        mobile = (RadioButton) findViewById(R.id.MobileNumber);
        rgroup = (RadioGroup) findViewById(R.id.rgroup);
        rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId){
                if (group.getCheckedRadioButtonId() != -1) {
                    int id = group.getCheckedRadioButtonId();
                    View radioButton = group.findViewById(id);
                    int radioId = rgroup.indexOfChild(radioButton);
                    RadioButton btn = (RadioButton) group.getChildAt(radioId);
                    radioSelection = (String) btn.getText();
                    if (radioSelection.equalsIgnoreCase("Vehicle No.")) {
                        vehicleLayout.setVisibility(View.VISIBLE);
                        nameLayout.setVisibility(View.GONE);
                        flatLayout.setVisibility(View.GONE);
                        mobileLayout.setVisibility(View.GONE);
                    } else if (radioSelection.equalsIgnoreCase("Visitor Name")) {
                        vehicleLayout.setVisibility(View.GONE);
                        nameLayout.setVisibility(View.VISIBLE);
                        flatLayout.setVisibility(View.GONE);
                        mobileLayout.setVisibility(View.GONE);
                    } else if (radioSelection.equalsIgnoreCase("Flat No.")) {
                        vehicleLayout.setVisibility(View.GONE);
                        nameLayout.setVisibility(View.GONE);
                        flatLayout.setVisibility(View.VISIBLE);
                        mobileLayout.setVisibility(View.GONE);
                    } else if (radioSelection.equalsIgnoreCase("Mobile No.")) {
                        vehicleLayout.setVisibility(View.GONE);
                        nameLayout.setVisibility(View.GONE);
                        flatLayout.setVisibility(View.GONE);
                        mobileLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        //------------------------------------------------------------------------------------------

//        insiderow.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });
        //------------------------------------------------------------------------------------------
        day = (Spinner) findViewById(R.id.day);
        day_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, day1);
        day.setAdapter(day_adapter);

//        row = new TableRow(getApplicationContext());
//        row.setBackgroundColor(Color.parseColor("#c0c0c0"));
//        row.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//
//        String[] headerText = { "VisitorName", "Flat No./Purpose", "Vehicle No. ", "InTime"};
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


        //Spinner onclick================================
        day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (day != null && day.getSelectedItem() != null) {
                    daytype = (String) day.getSelectedItem();
                    //  Toast.makeText(SearchVisitors.this, ""+daytype, Toast.LENGTH_SHORT).show();
                    if (daytype.equals("Today-आज")) {
                        dayDate = null;

//                        tableLayout.removeAllViews();
                        dayDate = "getDate()";

//                       tview();
                        if (header != null) {
                            header.removeAllViews();
                        }
                        if (adapter != null) {
                            list.setAdapter(null);
                            adapter.clear();
                            rpoints.clear();
                            od.clear();
                            vehicleNo.clear();
                            EnterDate.clear();
                            purpose.clear();
                            Active.clear();

                        }

                        new GetLongLat("E").execute();
                    } else if (daytype.equals("Yesterday-कल")) {
                        dayDate = null;
                        dayDate = "getDate()-1";
//                       tableLayout.removeAllViews();
//                       tview();
                        if (header != null) {
                            header.removeAllViews();
                        }
                        if (adapter != null) {
                            list.setAdapter(null);
                            adapter.clear();
                            rpoints.clear();
                            od.clear();
                            vehicleNo.clear();
                            EnterDate.clear();
                            purpose.clear();
                            Active.clear();
                        }
                        new GetLongLat("E").execute();
                    } else if (daytype.equals("Day Before Yesterday-परसो")) {
                        dayDate = null;
                        dayDate = "getDate()-2";
//                       tableLayout.removeAllViews();
//                       tview();
                        if (header != null) {
                            header.removeAllViews();
                        }
                        if (adapter != null) {
                            list.setAdapter(null);
                            adapter.clear();
                            rpoints.clear();
                            od.clear();
                            vehicleNo.clear();
                            EnterDate.clear();
                            purpose.clear();
                            Active.clear();
                        }
                        new GetLongLat("E").execute();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView){

            }
        });

        //Spinner onclick===============================
        one.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (one.getText().toString().length() == 2)     //size as per your requirement
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

//    private void tview(){
//        row = new TableRow(getApplicationContext());
//        row.setBackgroundColor(Color.parseColor("#c0c0c0"));
//        row.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        String[] headerText = { "VisitorName", "Flat No./Purpose", "Vehicle No. ", "InTime"};
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
//    }

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
            progressDialog = new ProgressDialog(SearchVisitors.this);
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
//                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
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
                        String query = "SELECT VisitorName,OwnerDetails,VehicleNumber,EnteredAt,Purpose,Active FROM VisitorDetails where convert(VARCHAR, EnteredAt,105) = convert(VARCHAR," + dayDate + ",105)    and SocietyName='" +
                                ""+societyname+"' ORDER BY SrNo DESC ";
                        Statement stmt = conn.createStatement();
                        rs = stmt.executeQuery(query);
                        ret = "1";
                    } else if (flag.equalsIgnoreCase("vehicle")) {

                        String query = "SELECT VisitorName,OwnerDetails,VehicleNumber,EnteredAt,Purpose,Active FROM VisitorDetails where convert(VARCHAR, EnteredAt,105) = convert(VARCHAR," + dayDate + ",105) AND " +
                                "VehicleNumber LIKE '%" + vehNo + "%'  and SocietyName='" +
                                ""+societyname+"' ORDER BY SrNo DESC";
                        Statement stmt = conn.createStatement();
                        rs = stmt.executeQuery(query);
                        ret = "1";
                    } else if (flag.equalsIgnoreCase("vname")) {

                        String query = "SELECT VisitorName,OwnerDetails,VehicleNumber,EnteredAt,Purpose,Active FROM VisitorDetails where convert(VARCHAR, EnteredAt,105) = convert(VARCHAR," + dayDate + ",105) AND " +
                                "VisitorName LIKE '%" + visName + "%'   and SocietyName='" +
                                ""+societyname+"' ORDER BY SrNo DESC";
                        Statement stmt = conn.createStatement();
                        rs = stmt.executeQuery(query);
                        ret = "1";
                    } else if (flag.equalsIgnoreCase("mobile")){

                        String query = "SELECT VisitorName,OwnerDetails,VehicleNumber,EnteredAt,Purpose,Active FROM VisitorDetails where convert(VARCHAR, EnteredAt,105) = convert(VARCHAR," + dayDate + ",105) AND " +
                                "MobileNo LIKE '%" + mob + "%'   and SocietyName='" +
                                ""+societyname+"' ORDER BY SrNo DESC ";
                        Statement stmt = conn.createStatement();
                        rs = stmt.executeQuery(query);
                        ret = "1";

                    } else if (flag.equalsIgnoreCase("FlatNo")){

                        String query = "SELECT VisitorName,OwnerDetails,VehicleNumber,EnteredAt,Purpose,Active FROM VisitorDetails where convert(VARCHAR, EnteredAt,105) = convert(VARCHAR," + dayDate + ",105) AND " +
                                "OwnerDetails LIKE '%" + OwnerDetails + "%'   and SocietyName='" +
                                ""+societyname+"' ORDER BY DESC";
                        Statement stmt = conn.createStatement();
                        rs = stmt.executeQuery(query);
                        ret = "1";
                    } else if (flag.equalsIgnoreCase("A")) {

                        String sql = "update VisitorDetails set Active=?,LeaveAt=? where VisitorName=? AND convert(VARCHAR, EnteredAt,105) = convert(VARCHAR," + dayDate + ",105) AND Active=? "+
                        " and SocietyName=?";

                        PreparedStatement preparedStatement = conn.prepareStatement(sql);

                        preparedStatement.setString(1, "1");
                        preparedStatement.setTimestamp(2, java.sql.Timestamp.valueOf(CreationDate));
                        preparedStatement.setString(3, vn);
                        preparedStatement.setString(4, "0");
                        preparedStatement.setString(5, societyname);

                        int i = preparedStatement.executeUpdate();
                        ret = "" + i;
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
//                    String d = String.valueOf(rs.getDate("EnteredAt"))+" at "+String.valueOf(rs.getTime("EnteredAt"));
//                    EnterDate.add(d);
                    String e = rs.getString("Purpose");
                    purpose.add(e);
                    String act1 = rs.getString("Active");
                    Active.add(act1);
                }
                Log.e("date:::",EnterDate.toString());
                adapter = new MyListAdapter(SearchVisitors.this, rpoints, od, vehicleNo, EnterDate, purpose, Active);
                inflater = getLayoutInflater();
                header = (ViewGroup) inflater.inflate(R.layout.sv_listheader, list, false);
                list.addHeaderView(header);
                list.setAdapter(adapter);

//                runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e(TAG,"service running");
//                        new GetLongLat("E").execute();
//                        refreshHandler.postDelayed(this, 30 * 1000);
//                    }
//                };
//                refreshHandler.postDelayed(runnable, 30 * 1000);


            } else if (flag.equalsIgnoreCase("vehicle")) {

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
                    String e = rs.getString("Purpose");
                    purpose.add(e);
                    String act1 = rs.getString("Active");
                    Active.add(act1);
                }
                adapter = new MyListAdapter(SearchVisitors.this, rpoints, od, vehicleNo, EnterDate, purpose, Active);
                list.setAdapter(adapter);
            } else if (flag.equalsIgnoreCase("vname")) {

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
//                Toast.makeText(this, ""+ rpoints.get(0)+rpoints.get(1), Toast.LENGTH_SHORT).show();
                adapter = new MyListAdapter(SearchVisitors.this, rpoints, od, vehicleNo, EnterDate, purpose, Active);
                list.setAdapter(adapter);
            } else if (flag.equalsIgnoreCase("mobile")) {


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
                adapter = new MyListAdapter(SearchVisitors.this, rpoints, od, vehicleNo, EnterDate, purpose, Active);
                list.setAdapter(adapter);
            } else if (flag.equalsIgnoreCase("FlatNo")) {

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
                adapter = new MyListAdapter(SearchVisitors.this, rpoints, od, vehicleNo, EnterDate, purpose, Active);
                list.setAdapter(adapter);
            } else if (flag.equalsIgnoreCase("A")) {
                Toast.makeText(this, "Leave Entry Marked !", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void searchby(View v) {
        String on = null, tw = null, thre = null, fou = null;
        //    Toast.makeText(this, ""+dayDate, Toast.LENGTH_SHORT).show();
        //    Toast.makeText(this, ""+radioSelection.toString(), Toast.LENGTH_SHORT).show();
        if (radioSelection.equalsIgnoreCase("Vehicle No.")) {

            on = one.getText().toString();
            tw = two.getText().toString();
            thre = three.getText().toString();
            fou = four.getText().toString();
            if (!on.equals("")) {
                if (!tw.equals("")) {
                    if (!thre.equals("")) {
                        if (!fou.equals("")) {
                            vehNo = on + " " + tw + " " + thre + " " + fou;
                            if (adapter != null) {
                                adapter.clear();
                                list.setAdapter(null);
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

        } else if (radioSelection.equalsIgnoreCase("Visitor Name")) {


            visName = vName.getText().toString();
            if (!visName.equals("")) {
                if (adapter != null) {
                    adapter.clear();
                    list.setAdapter(null);
                }
                new GetLongLat("vname").execute();
            } else {
                Toast.makeText(this, "Invalid Visitor Name", Toast.LENGTH_SHORT).show();
            }
        } else if (radioSelection.equalsIgnoreCase("Flat No.")) {

            OwnerDetails = FlatNo.getText().toString();
//            Toast.makeText(this, "Invalid Flat No", Toast.LENGTH_SHORT).show();

            if (!OwnerDetails.equals("")) {
                if (adapter != null) {
                    adapter.clear();
                    list.setAdapter(null);
                }
                new GetLongLat("FlatNo").execute();
            } else {
                Toast.makeText(this, "Invalid Flat No", Toast.LENGTH_SHORT).show();
            }
        } else if (radioSelection.equalsIgnoreCase("Mobile No.")) {
            mob = mobileno.getText().toString();

            if (!mob.equals("")) {
                if (adapter != null) {
                    adapter.clear();
                    list.setAdapter(null);
                }
                new GetLongLat("mobile").execute();
            } else {
                Toast.makeText(this, "Invalid Mobile No", Toast.LENGTH_SHORT).show();
            }
        } else if (radioSelection.equalsIgnoreCase("Flat No.")) {

        }

    }
    //=============================================================================================


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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