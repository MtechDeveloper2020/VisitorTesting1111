package mtech.com.visitortesting;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static android.content.ContentValues.TAG;

public class IncorrectService extends Service {
    MediaPlayer myPlayer;
    Handler refreshHandler;
    Runnable runnable;

    public IncorrectService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        refreshHandler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "incorrect service running");
                new GetLongLat("E").execute();
                refreshHandler.postDelayed(this, 5 * 1000);
            }
        };
        refreshHandler.postDelayed(runnable, 5 * 1000);


//        Toast.makeText(this, "Started", Toast.LENGTH_LONG).show();


    }

    @Override
    public void onStart(Intent intent, int startid) {
//        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
//        myPlayer.start();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        myPlayer.stop();
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
//            progressDialog = new ProgressDialog(BackgroundService.this);
//            progressDialog.setMessage("Please wait...");
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String res) {
//            progressDialog.dismiss();
            if (res.equalsIgnoreCase("1")) {

                showName(rs, rs1, flag);
                //Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(IncorrectService.this, res, Toast.LENGTH_SHORT).show();
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
                    ret = "Internet connection Error";
                } else {

                    if (flag.equalsIgnoreCase("E")) {
                        String query = "SELECT OwnerDetails,SrNo from VisitorDetails where Active = 2 and IncorrectService='Y'";
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
        boolean status = false;
        String SrNo = null, OwnerDetails = null;

        try {
            if (flag.equalsIgnoreCase("E")) {
                try {

                    while (rs.next()) {
                        OwnerDetails = rs.getString("OwnerDetails");
                        SrNo = rs.getString("SrNo");
                        status = true;
                    }
                    if (status) {
                        Log.e(TAG, "before intent");
                        Intent i = new Intent(getBaseContext(), SearchVisitors.class);
                        i.putExtra("ownerDetails", OwnerDetails);
                        i.putExtra("srNo", SrNo);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getBaseContext().startActivity(i);

//                        startActivity(i);
//                        myPlayer = MediaPlayer.create(this, R.raw.sos);
//                        myPlayer.setLooping(false);
//                        myPlayer.start();
                    }
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


}
