package mtech.com.visitortesting;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static android.content.ContentValues.TAG;

public class BackgroundService extends Service {
    static boolean active = false;
    //    MediaPlayer myPlayer = null;
    Handler refreshHandler;
    Runnable runnable;
    String status = null;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String societyname = null;

    public BackgroundService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        status = componentInfo.getClassName();
//        Toast.makeText(this, ""+componentInfo.getClassName(), Toast.LENGTH_SHORT).show();
        return componentInfo.getPackageName().equals(myPackage);
    }

    @Override
    public void onCreate() {
        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
        editor = sp.edit();
        societyname = sp.getString("societyname", null);
        refreshHandler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "service running");
                isForeground("mtech.com.visitortesting");
                if (!status.equalsIgnoreCase("mtech.com.visitortesting.SOS")) {
                    new GetLongLat("E").execute();
                }
                refreshHandler.postDelayed(this, 31 * 1000);
            }
        };
        refreshHandler.postDelayed(runnable, 31 * 1000);
//        Toast.makeText(this, "Started", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
//        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
//        myPlayer.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
        stophandler();

        //-------
//        SharedPreferences sp1 = getSharedPreferences("sp", Context.MODE_PRIVATE);
//        final SharedPreferences.Editor editor1 = sp1.edit();
//        editor1.clear();
//        editor1.commit();
//        sp.unregisterOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
//            @Override
//            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
//                editor.clear();
//                editor.commit();
//            }
//        });


        //-----
//        myPlayer.stop();
        refreshHandler.removeCallbacks(runnable);
        stopSelf();
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
                        Intent i = new Intent(getBaseContext(), SOS.class);
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

    public void stophandler() {
        refreshHandler.removeCallbacks(runnable);
        stopSelf();
        refreshHandler.removeCallbacksAndMessages(runnable);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.hasCategory("some_unique_string")) {
                stophandler();
                SharedPreferences sp1 = getSharedPreferences("sp", Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor1 = sp1.edit();
                editor1.clear();
                editor1.commit();
                sp.unregisterOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s){
                        editor.clear();
                        editor.commit();
                    }
                });
            }
        }

        return START_STICKY;
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
//                Toast.makeText(BackgroundService.this, res, Toast.LENGTH_SHORT).show();
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
                        String query = "SELECT TOP 1 * from OwnerSOS where Active = 0 AND SocietyName='" + societyname + "'";
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
