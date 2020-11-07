package mtech.com.visitortesting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SOS extends ActionBarActivity{
    MediaPlayer myPlayer;
    String oD = null, srNo = null;
    TextView flat = null;
    static boolean active = false;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String societyname=null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
        editor = sp.edit();
        societyname= sp.getString("societyname",null);
        flat = (TextView) findViewById(R.id.flatNo);
        Intent i = getIntent();
        oD = i.getStringExtra("ownerDetails");
        srNo = i.getStringExtra("srNo");
        flat.setText(oD);
        myPlayer = MediaPlayer.create(this, R.raw.sos);
        myPlayer.setLooping(true);
        myPlayer.start();
    }
    @Override
    public void onStart(){
        super.onStart();
        active = true;
    }

    @Override
    public void onStop(){
        super.onStop();
        active = false;
    }
    public void stop(View v){
        myPlayer.stop();
        new GetLongLat("E").execute();
    }

    private void showName(ResultSet rs, ResultSet rs1, String flag) {
        boolean status = false;
        try {
            if (flag.equalsIgnoreCase("E")) {
                try {
                    Toast.makeText(this, "Emergency OUT", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, MobileActivity.class);
                    startActivity(i);
                    finish();
                } catch (Exception e) {
                    e.toString();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
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
            progressDialog = new ProgressDialog(SOS.this);
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
//                Toast.makeText(SOS.this, res, Toast.LENGTH_SHORT).show();
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
//                    ret = "Internet connection Error";
                } else {

                    if (flag.equalsIgnoreCase("E")){
                        PreparedStatement stmt = conn.prepareStatement("UPDATE OwnerSOS SET Active = ? WHERE OwnerDetails = ? AND SrNo=? AND SocietyName=?");
                        stmt.setString(1, "1");
                        stmt.setString(2, oD);
                        stmt.setString(3, srNo);
                        stmt.setString(4, societyname);
                        int i = stmt.executeUpdate();
                        ret = "" + i;
                    }
                }
            } catch (Exception ex) {
                ret = ex.getMessage();
            }
            return ret;
        }
    }
}
