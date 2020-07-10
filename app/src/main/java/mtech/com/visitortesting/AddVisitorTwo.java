package mtech.com.visitortesting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddVisitorTwo extends Activity {
    private static final int CAMERA_REQUEST = 1890;
    private static final int CAMERA_REQUEST1 = 1891;
    TextView name, mobileNo;
    ImageView vPhoto, iPhoto;
    Bitmap visphoto, idsphoto;
    String[] languages = {"Android ", "java", "IOS", "SQL", "JDBC", "Web services", "php"};
    AutoCompleteTextView text;
    EditText totalVisitors, Vone, Vtwo, Vthree, Vfour, VFname, VLname, from;
    ArrayAdapter<String> purpose_adapter;
    SearchableAdapter flat_adapter;
    Spinner VPurpose;
    String CreationDate, Vname, OwnerDetails, OwnerBuildingName = null, vehicleNo = null, FromPlace, purpose, phone, mob,flatno=null;
    String fullname = null, stat = "N";
    byte[] vbyteimage = null, idbyteimage = null, vp = null, ip = null;
    String fname = null;
    String lname = null, ffname, llname, place = null, totVisitors = "1";
    String societyname = null;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_visitor_two);
        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
        editor = sp.edit();
        societyname = sp.getString("societyname", null);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        new GetLongLat("E").execute();
        new GetLongLat("D").execute();

        name = (TextView) findViewById(R.id.name);
        mobileNo = (TextView) findViewById(R.id.mobileNo);
        totalVisitors = (EditText) findViewById(R.id.totVisitors);
        Vone = (EditText) findViewById(R.id.one);
        Vtwo = (EditText) findViewById(R.id.two);
        Vthree = (EditText) findViewById(R.id.three);
        Vfour = (EditText) findViewById(R.id.four);
        from = (EditText) findViewById(R.id.from);
        VPurpose = (Spinner) findViewById(R.id.purpose);
        Vname = name.getText().toString();

        vPhoto = (ImageView) findViewById(R.id.visitorPhoto);
        iPhoto = (ImageView) findViewById(R.id.idPhoto);
        text = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
        String Date = dateFormat.format(new Date());
        String Time = dateFormat1.format(new Date());
        CreationDate = Date + " " + Time;
        getValuesFromActivity();
        vPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                // TODO Auto-generated method stub
//                SendGetStartedNotification(ParseUser user);
            }
        });
        iPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                // TODO Auto-generated method stub
//                SendGetStartedNotification(ParseUser user);
            }
        });
        //=======================================================
        Vone.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (Vone.getText().toString().length() == 2)     //size as per your requirement
                {
                    Vtwo.requestFocus();
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
        Vtwo.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (Vtwo.getText().toString().length() == 2)     //size as per your requirement
                {
                    Vthree.requestFocus();
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
        Vthree.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (Vthree.getText().toString().length() == 2)     //size as per your requirement
                {
                    Vfour.requestFocus();
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
        Vfour.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (Vfour.getText().toString().length() == 4)     //size as per your requirement
                {
                    from.requestFocus();
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
    }
//

    private void getValuesFromActivity() {


        Intent i = getIntent();
        stat = i.getStringExtra("stat");
        mob = i.getStringExtra("mobile");
        String mask = mob.replaceAll("\\w(?=\\w{3})", "*");
        mobileNo.setText(mask);
        phone = mob;
        if (stat.equalsIgnoreCase("Y")) {
            new GetLongLat("K").execute();
        } else {

            fname = i.getStringExtra("fname");
            lname = i.getStringExtra("lname");
            name.setText(fname + " " + lname);
            visphoto = (Bitmap) i.getParcelableExtra("visitorphoto");
            idsphoto = (Bitmap) i.getParcelableExtra("idphoto");

            if (visphoto != null) {
                vPhoto.setImageBitmap(visphoto);

                Bitmap bitmap = ((BitmapDrawable) vPhoto.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                vbyteimage = stream.toByteArray();
            }
            if (idsphoto != null) {
                iPhoto.setImageBitmap(idsphoto);

                Bitmap bitmap = ((BitmapDrawable) iPhoto.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                idbyteimage = stream.toByteArray();
            }
        }
    }

    public void minus(View v) {
        String count = totalVisitors.getText().toString();
        int tv = Integer.parseInt(count);
        if (tv > 1) {
            tv = tv - 1;
            totalVisitors.setText("" + tv);
        }
    }

    public void plus(View v) {
        String count = totalVisitors.getText().toString();
        int tv = Integer.parseInt(count);
        if (tv >= 1) {
            tv = tv + 1;
            totalVisitors.setText("" + tv);
        }
    }

    public void flatClose(View v) {
        text.setText(null);
        OwnerDetails = null;
    }

    public void vehClose(View v) {
        Vone.setText(null);
        Vtwo.setText(null);
        Vthree.setText(null);
        Vfour.setText(null);
        vehicleNo = null;
    }

    private void showName(ResultSet rs, ResultSet rs1, String flag) {
        try {
            if (flag.equalsIgnoreCase("E")) {
                try {
                    ArrayList<String> purpose = new ArrayList<String>();
                    purpose.add("SELECT PURPOSE");
                    while (rs.next()) {
                        purpose.add(rs.getString("Purpose"));
                    }
                    purpose_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, purpose);
                    VPurpose.setAdapter(purpose_adapter);
                } catch (Exception e) {
                    e.toString();
                }
            } else if (flag.equalsIgnoreCase("Check")) {
                try {
                    if (!rs.next()) {
                        new GetLongLat("SPDetails").execute();
                    } else {

                        Intent i = new Intent(AddVisitorTwo.this, SavedScreen.class);
                        startActivity(i);

                        finish();
                    }
                } catch (Exception e) {
                    e.toString();
                }
            } else if (flag.equalsIgnoreCase("SPDetails")) {
                try {

                    Intent i = new Intent(AddVisitorTwo.this, SavedScreen.class);
                    startActivity(i);
                    finish();

                } catch (Exception e) {
                    e.toString();
                }
            } else if (flag.equalsIgnoreCase("D")) {
                try {
                    ArrayList<String> flat = new ArrayList<String>();

                    if(rs.next()){
                        flat.add(rs.getString("BuildingName"));
                    }
                    while (rs.next()) {
                        flat.add(rs.getString("BuildingName") + "- " + rs.getString("Wing") + rs.getString("FlatNo") + "- " + rs.getString("OwnerName") + " - " + rs.getString("OwnerType"));
                    }
                    flat_adapter = new SearchableAdapter(this, flat);
                    text.setAdapter(flat_adapter);
                    text.setThreshold(1);

                } catch (Exception e) {
                    e.toString();
                }
            } else if (flag.equalsIgnoreCase("S")){
                Vname = fname + " " + lname;
                new GetLongLat("Check").execute();

            } else if (flag.equalsIgnoreCase("K")){
                try {
                    if (rs.next()){
                        if (rs.getBytes("VisitorPhoto") != null){
                            vbyteimage = rs.getBytes("VisitorPhoto");
                            Bitmap bmp = BitmapFactory.decodeByteArray(vbyteimage, 0, vbyteimage.length);
                            vPhoto.setImageBitmap(bmp);
                        }
                        if (rs.getBytes("VisitorIdPhoto") != null){
                            idbyteimage = rs.getBytes("VisitorIdPhoto");
                            Bitmap bmp = BitmapFactory.decodeByteArray(idbyteimage, 0, idbyteimage.length);
                            iPhoto.setImageBitmap(bmp);
                        }
                        name.setText(rs.getString("VisitorName"));
                        fname = rs.getString("VisitorFirstName");
                        lname = rs.getString("VisitorLastName");
                        String compareValue = rs.getString("Purpose");
                        VPurpose.setSelection(purpose_adapter.getPosition(compareValue));
                        from.setText(rs.getString("VAddress"));

                        String string = rs.getString("VehicleNumber");
                        String[] parts = string.split(" ");
                        String part1 = parts[0]; // 004
                        String part2 = parts[1];
                        String part3 = parts[2];
                        String part4 = parts[3];

                        Vone.setText(part1);
                        Vtwo.setText(part2);
                        Vthree.setText(part3);
                        Vfour.setText(part4);
                    }
                } catch (Exception e) {
                    e.toString();
                }
            } else if (flag.equalsIgnoreCase("Uname")) {
                try {

                    Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.toString();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void addVisitor(View v) {
        Vname = fname + " " + lname;

        OwnerDetails = text.getText().toString().toString().trim();
        if(!OwnerDetails.contains("-")){
            OwnerBuildingName = OwnerDetails;
        }else{
            String[] split = OwnerDetails.split("-");
            OwnerDetails = split[1].trim();
            OwnerBuildingName = split[0].trim();

        }

        purpose = VPurpose.getSelectedItem().toString();
        place = from.getText().toString();
        totVisitors = totalVisitors.getText().toString();

        if (!Vname.equalsIgnoreCase("")) {
            if (!OwnerDetails.equalsIgnoreCase("")) {

                final Bitmap bmap = ((BitmapDrawable) vPhoto.getDrawable()).getBitmap();
                Drawable myDrawable = getResources().getDrawable(R.drawable.profile);
                final Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();
                if (!bmap.sameAs(myLogo)) {

                    if (VPurpose.getSelectedItem() != "SELECT PURPOSE") {
                        if (!Vone.getText().toString().equalsIgnoreCase("") || !Vtwo.getText().toString().equalsIgnoreCase("") || !Vthree.getText().toString().equalsIgnoreCase("") || !Vfour.getText().toString().equalsIgnoreCase("")) {
                            if (!Vone.getText().toString().equalsIgnoreCase("") && !Vtwo.getText().toString().equalsIgnoreCase("") && !Vthree.getText().toString().equalsIgnoreCase("") && !Vfour.getText().toString().equalsIgnoreCase("")) {
                                vehicleNo = Vone.getText().toString() + " " + Vtwo.getText().toString() + " " + Vthree.getText().toString() + " " + Vfour.getText().toString();
                                new GetLongLat("S").execute();
                            } else {
                                Toast.makeText(this, "Please enter correct Vehicle Number", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            new GetLongLat("S").execute();
                        }
                    } else {
                        Toast.makeText(this, "Select Purpose", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Take Visitor's Photo", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Select Flat No. or Owner Name", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void editName(View v) {
        LayoutInflater factory = LayoutInflater.from(this);

        final View textEntryView = factory.inflate(R.layout.text_entry, null);
        //text_entry is an Layout XML file containing two text field to display in alert dialog

        VFname = (EditText) textEntryView.findViewById(R.id.EditText1);
        VLname = (EditText) textEntryView.findViewById(R.id.EditText2);


//        input1.setText("DefaultValue", TextView.BufferType.EDITABLE);
//        input2.setText("DefaultValue", TextView.BufferType.EDITABLE);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(
                "Enter: ").setView(
                textEntryView).setPositiveButton("Update",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        ffname = VFname.getText().toString();
                        llname = VLname.getText().toString();
                        if (!ffname.equals("") && !llname.equals("")) {
//                                new GetLongLat("Uname").execute();
                            fname = VFname.getText().toString();
                            lname = VLname.getText().toString();

                            Vname = fname + " " + lname;
                            name.setText(Vname);
                        } else if (!ffname.equals("") && llname.equals("")) {
//                                new GetLongLat("Uname").execute();

                            fname = VFname.getText().toString();
                            Vname = fname + " " + lname;
                            name.setText(Vname);
                        } else if (ffname.equals("") && !llname.equals("")) {
//                                new GetLongLat("Uname").execute();

                            lname = VLname.getText().toString();
                            Vname = fname + " " + lname;
                            name.setText(Vname);

                        } else {
                            Toast.makeText(AddVisitorTwo.this, "Please enter required details", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                    }
                });
        alert.show();


    }

    public void editVP(View v) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    public void editIDP(View v) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {

                visphoto = (Bitmap) data.getExtras().get("data");
                vPhoto.setImageBitmap(visphoto);
                Bitmap bitmap = ((BitmapDrawable) vPhoto.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                vbyteimage = stream.toByteArray();
            } else if (resultCode == RESULT_CANCELED) {

            }
        } else if (requestCode == CAMERA_REQUEST1) {
            if (resultCode == RESULT_OK && data != null) {

                idsphoto = (Bitmap) data.getExtras().get("data");
                iPhoto.setImageBitmap(idsphoto);
                Bitmap bitmap = ((BitmapDrawable) iPhoto.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                idbyteimage = stream.toByteArray();
            }
        }
    }
    @Override
    public void onResume(){
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
            progressDialog = new ProgressDialog(AddVisitorTwo.this);
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
                    ret = "Internet Connection Error";
                } else {

                    if (flag.equalsIgnoreCase("E")) {
                        String query = "SELECT * FROM VisitorPurpose  ";
                        Statement stmt = conn.createStatement();
                        rs = stmt.executeQuery(query);
                        ret = "1";

                    } else if (flag.equalsIgnoreCase("D")) {
                        String query = "SELECT * FROM OwnerDetails where  SocietyName='" +
                                "" + societyname + "' ";
                        Statement stmt = conn.createStatement();
                        rs = stmt.executeQuery(query);
                        ret = "1";

                    } else if (flag.equalsIgnoreCase("Check")) {
                        String query = "Select Top 1 SPName from ServiceprovidersDetails where SPName='" + Vname + "' and (Category = 'Carpenter' OR" +
                                "  Category = 'Electrician' OR  Category = 'Internet' OR  Category = 'Medical' OR  Category = 'Painting' OR  Category = 'Pest Control' OR  Category = 'Plumber')";
                        Statement stmt = conn.createStatement();
                        rs = stmt.executeQuery(query);
                        ret = "1";

                    } else if (flag.equalsIgnoreCase("Uname")) {

                        String sql = "update VisitorDetails set VisitorFirstName=? , VisitorLastName=?  where MobileNo=? and  SocietyName='" +
                                "" + societyname + "' ";

                        PreparedStatement preparedStatement = conn.prepareStatement(sql);

                        preparedStatement.setString(1, ffname);
                        preparedStatement.setString(2, llname);
                        preparedStatement.setString(3, phone);
                        int i = preparedStatement.executeUpdate();
                        ret = "" + i;
                    } else if (flag.equalsIgnoreCase("S")) {

                        PreparedStatement stmt = conn.prepareStatement("insert into VisitorDetails" + "(VisitorFirstName,VisitorLastName, OwnerDetails, OwnerFlatNo,VehicleNumber, Purpose, MobileNo, EnteredAt,VisitorPhoto,VisitorIdPhoto,VisitorName,Active,VAddress,PredefinedVisitor,TotalVisitors,SocietyName, OwnerBuildingName,AcceptedVisitor,OTP)" + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                        //  PreparedStatement stmt = conn.prepareStatement("insert into VisitorDetails" + "(VisitorName,OwnerDetails,VehicleNumber,Purpose)" + " values(?,?,?,?)");
                        stmt.setString(1, fname);
                        stmt.setString(2, lname);
                        stmt.setString(3, OwnerDetails);
                        stmt.setString(4, OwnerDetails);
                        stmt.setString(5, vehicleNo);
                        stmt.setString(6, purpose);
                        stmt.setString(7, phone);
                        stmt.setTimestamp(8, java.sql.Timestamp.valueOf(CreationDate));
                        stmt.setBytes(9, vbyteimage);
                        stmt.setBytes(10, idbyteimage);
                        stmt.setString(11, Vname);
                        stmt.setString(12, "0");
                        stmt.setString(13, place);
                        stmt.setString(14, "N");
                        stmt.setString(15, totVisitors);
                        stmt.setString(16, societyname);
                        stmt.setString(17, OwnerBuildingName);
                        stmt.setString(18, "N");
                        stmt.setString(19, "000000");
                        int i = stmt.executeUpdate();
                        ret = "" + i;

                    } else if (flag.equalsIgnoreCase("SPDetails")) {

                        PreparedStatement stmt = conn.prepareStatement("insert into ServiceprovidersDetails" + "(SPName,Category, Quality, Price, Punctuality, SocietyRating, OverAllRating,SocietyName,BuildingName)" + " values(?,?,?,?,?,?,?,?,?)");
                        //  PreparedStatement stmt = conn.prepareStatement("insert into VisitorDetails" + "(VisitorName,OwnerDetails,VehicleNumber,Purpose)" + " values(?,?,?,?)");
                        stmt.setString(1, Vname);
                        stmt.setString(2, purpose);
                        stmt.setString(3, "5");
                        stmt.setString(4, "5");
                        stmt.setString(5, "5");
                        stmt.setString(6, "5");
                        stmt.setString(7, "5");
                        stmt.setString(8, societyname);
                        stmt.setString(9, OwnerBuildingName);

                        int i = stmt.executeUpdate();
                        ret = "" + i;

                    } else if (flag.equalsIgnoreCase("K")) {
                        String query = "SELECT TOP 1 * FROM VisitorDetails where MobileNo='" + mob + "' AND SocietyName='"+societyname+"'  ORDER BY SrNo DESC";
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
