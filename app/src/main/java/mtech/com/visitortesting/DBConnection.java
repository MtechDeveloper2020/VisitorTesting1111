package mtech.com.visitortesting;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by cd02232 on 27-01-2017.
 */

public class DBConnection {

    String ip = "", db = "", un = "", ps = "", ip2 = "", db2 = "", un2 = "", ps2 = "";

    public DBConnection() {

        this.ip = "103.14.97.220";
        this.db = "StudentTrackingAtt";
        this.un = "MtechGPRS";
        this.ps = "Mtech@GPRS$";

/*        this.ip = "103.14.98.186";
        this.db = "mtechaadhaar";
        this.un = "mtechaadhaar";
        this.ps = "tyym~GuQKR5N?m}";*/

    }

    public DBConnection(String temp) {
        this.ip2 = "103.1.115.192";
        this.db2 = "mtechinn_webmacs";
        this.un2 = "webmacs";
        this.ps2 = "tyym~GuQKR5N?m}";

    }

    //------------------------------------------------------------------------------------------------
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public Connection connectionclass() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + ip + "/" + db + ";user=" + un + ";password=" + ps + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        } catch (SQLException se) {
            Log.e("error here 1 : ", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("error here 2 : ", e.getMessage());
        } catch (Exception e) {
            Log.e("error here 3 : ", e.getMessage());
        }
        return connection;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public Connection connectionclass2() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + ip2 + "/" + db2 + ";user=" + un2 + ";password=" + ps2 + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        } catch (SQLException se) {
            Log.e("error here 1 : ", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("error here 2 : ", e.getMessage());
        } catch (Exception e) {
            Log.e("error here 3 : ", e.getMessage());
        }
        return connection;
    }
//------------------------------------------------------------------------------------------------
}