package mtech.com.visitortesting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends Activity {
    EditText phone;
    Button addVisitor, searchVisitor, searchVehicles;
    String phoneNo = null;
    byte[] vbyteimage = null, idbyteimage = null;
    Bitmap vpic, idpic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        phone = (EditText) findViewById(R.id.phonenumber);
    }

    public void addVisitor(View v) {
        phoneNo = phone.getText().toString();
        if (!phoneNo.equals("")) {
            if (phoneNo.length() == 10) {
                new GetLongLat("E").execute();
            } else {
                Toast.makeText(this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchVisitor(View v) {
        Intent i = new Intent(MainActivity.this, SearchVisitors.class);
        startActivity(i);

    }

    public void incorrectVisitor(View v) {
        Intent i = new Intent(MainActivity.this, IncorrectVisitors.class);
        startActivity(i);

    }

    public void searchVehicles(View v) {
        Intent i = new Intent(MainActivity.this, SearchVehicles.class);
        startActivity(i);

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
            progressDialog = new ProgressDialog(MainActivity.this);
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

                        String query = "select TOP 1 * from VisitorDetails where MobileNo='" + phoneNo + "' ";
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
                String name = null;
                while (rs.next()) {

                    name = rs.getString("VisitorName");
                    Toast.makeText(this, "" + name, Toast.LENGTH_SHORT).show();
//                    vbyteimage = rs.getBytes("VisitorPhoto");
//                    idbyteimage = rs.getBytes("VisitorIdPhoto");
//                        vpic = BitmapFactory.decodeByteArray(vbyteimage, 0, vbyteimage.length);
//                        idpic = BitmapFactory.decodeByteArray(vbyteimage, 0, idbyteimage.length);
                    //  image.setImageBitmap(bmp);

//
//                    Intent i = new Intent(MainActivity.this, AddVisitorTwo.class);
//                    i.putExtra("fullname",name);
////                    i.putExtra("visitorphoto", vpic);
////                    i.putExtra("idphoto",idpic);
//                    i.putExtra("mobile", phoneNo);
//                    startActivity(i);
                }
//                else{
//                    Toast.makeText(this, ""+name, Toast.LENGTH_SHORT).show();
////                    Intent i = new Intent(MainActivity.this, AddVisitorOne.class);
////                    i.putExtra("phone", phoneNo);
////                    startActivity(i);
//                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

}
