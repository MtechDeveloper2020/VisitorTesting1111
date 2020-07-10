package mtech.com.visitortesting.resident;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.mantra.mfs100.FingerData;
import com.mantra.mfs100.MFS100;
import com.mantra.mfs100.MFS100Event;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mtech.com.visitortesting.ConnectionDetector;
import mtech.com.visitortesting.DBConnection;
import mtech.com.visitortesting.R;
import mtech.com.visitortesting.otp.JSONParser_Post;
import mtech.com.visitortesting.otp.Model_URIs;

public class ResidentRegistration extends Activity implements MFS100Event {

    Button btnInit;
    Button btnUninit;
    Button btnSyncCapture;
    Button btnStopCapture;
    Button btnMatchISOTemplate;
    Button btnExtractISOImage;
    Button btnExtractAnsi;
    Button btnExtractWSQImage;
    Button btnClearLog;
    TextView lblMessage;
    EditText txtEventLog;
    EditText staffCode;
    ImageView imgFinger;
    CheckBox cbFastDetection;
    byte[] rtemp;
    String StaffID=null;
    String finger=null;
    String staffc=null;
    FingerData fingerData;
    List<byte[]> temp_arrays= new ArrayList<>();
    List<byte[]> dup_arrays= null;
    List<String> staff_arrays= null;
    List<String> staffc_arrays= null;
    String staff=null;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    String TransactionLog_GetJSON;

    private enum ScannerAction {
        Capture, Verify
    }

    byte[] Enroll_Template;
    byte[] Verify_Template;
    String et=null, vt=null;
    private FingerData lastCapFingerData = null;
    ScannerAction scannerAction = ScannerAction.Capture;
    int timeout = 10000;
    MFS100 mfs100 = null;
    int capture_status=0;
    private boolean isCaptureRunning = false;
    EditText name,  mobileno;
    Spinner category, flatno;
    String rName= null, fno=null,catgry=null,phno=null,otp=null;
    String[] categoryarray = {"--Select Category--","Resident", "Helper", "House maid"};
    String[] flatarray = {"--Select FlatNo--", "A102", "A103", "A104", "A105"};
    public ArrayAdapter<String> adapter;
    JSONParser_Post jsnp_post = new JSONParser_Post();
    Model_URIs URLs = new Model_URIs();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mfs100_sample);
        cd = new ConnectionDetector(this);
        if (android.os.Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        name= (EditText) findViewById(R.id.name);
        flatno= (Spinner) findViewById(R.id.flatNo);
        mobileno= (EditText) findViewById(R.id.mobileno);
        category= (Spinner) findViewById(R.id.category);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categoryarray);
        category.setAdapter(adapter);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, flatarray);
        flatno.setAdapter(adapter1);
        dup_arrays= new ArrayList<>();
        staff_arrays= new ArrayList<>();
        staffc_arrays= new ArrayList<>();
        new GetLongLat("E").execute();
        new GetLongLat("D").execute();
        FindFormControls();
        try {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } catch (Exception e){
            Log.e("Error", e.toString());
        }
    }

    @Override
    protected void onStart(){
        if (mfs100 == null){
            mfs100 = new MFS100(this);
            mfs100.SetApplicationContext(ResidentRegistration.this);
        } else {
            InitScanner();
        }
        super.onStart();
    }

    protected void onStop(){
        UnInitScanner();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mfs100 != null) {
            mfs100.Dispose();
        }
        super.onDestroy();
    }

    public void FindFormControls() {
        btnInit = (Button) findViewById(R.id.btnInit);
        btnUninit = (Button) findViewById(R.id.btnUninit);
        btnMatchISOTemplate = (Button) findViewById(R.id.btnMatchISOTemplate);
        btnExtractISOImage = (Button) findViewById(R.id.btnExtractISOImage);
        btnExtractAnsi = (Button) findViewById(R.id.btnExtractAnsi);
        btnExtractWSQImage = (Button) findViewById(R.id.btnExtractWSQImage);
        btnClearLog = (Button) findViewById(R.id.btnClearLog);
        lblMessage = (TextView) findViewById(R.id.lblMessage);
        txtEventLog = (EditText) findViewById(R.id.txtEventLog);
        imgFinger = (ImageView) findViewById(R.id.imgFinger);
        btnSyncCapture = (Button) findViewById(R.id.btnSyncCapture);
        btnStopCapture = (Button) findViewById(R.id.btnStopCapture);
        cbFastDetection = (CheckBox) findViewById(R.id.cbFastDetection);
//        staffCode = (EditText) findViewById(R.id.staffid);
    }

    public void onControlClicked(View v){

        switch (v.getId()){
            case R.id.btnInit:
                InitScanner();
                break;
            case R.id.btnUninit:
                UnInitScanner();
                break;
            case R.id.btnSyncCapture:
                scannerAction = ScannerAction.Capture;
                if (!isCaptureRunning){
                    StaffID = name.getText().toString();
                    if(!StaffID.equalsIgnoreCase("")){
                        StartSyncCapture();
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }

//                    new GetLongLat("A").execute();
//                    new GetLongLat("C").execute();
//                    new GetLongLat("D").execute();
//                        if(capture_status == 1){
                        SetData3(fingerData);
//                            capture_status=0;
//                        }
                    }else{
                        Toast.makeText(this, "Enter Staff Id", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case R.id.btnStopCapture:
                StopCapture();
                break;
            case R.id.btnMatchISOTemplate:

                scannerAction = ScannerAction.Verify;
                if (!isCaptureRunning){
                    StaffID = name.getText().toString();
                    StartSyncCapture();

//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
                break;
            case R.id.btnExtractISOImage:
                ExtractISOImage();
                break;
            case R.id.btnExtractAnsi:
                ExtractANSITemplate();
                break;
            case R.id.btnExtractWSQImage:
                ExtractWSQImage();
                break;
            case R.id.btnClearLog:
                ClearLog();
                break;
            default:
                break;
        }
    }

    private void InitScanner(){
        try {
            int ret = mfs100.Init();
            if (ret != 0) {
                SetTextOnUIThread(mfs100.GetErrorMsg(ret));
            } else {
                SetTextOnUIThread("Init success");
                String info = "Serial: " + mfs100.GetDeviceInfo().SerialNo()
                        + " Make: " + mfs100.GetDeviceInfo().Make()
                        + " Model: " + mfs100.GetDeviceInfo().Model()
                        + "\nCertificate: " + mfs100.GetCertification();
                SetLogOnUIThread(info);
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Init failed, unhandled exception",
                    Toast.LENGTH_LONG).show();
            SetTextOnUIThread("Init failed, unhandled exception");
        }
    }

    private void StartSyncCapture() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                SetTextOnUIThread("");
                isCaptureRunning = true;
                capture_status=1;
                try {
                    fingerData = new FingerData();
                    int ret = mfs100.AutoCapture(fingerData, timeout, cbFastDetection.isChecked());
                    Log.e("StartSyncCapture.RET", ""+ret);
                    if (ret != 0) {
                        SetTextOnUIThread(mfs100.GetErrorMsg(ret));
                    } else {
                        lastCapFingerData = fingerData;
                        final Bitmap bitmap = BitmapFactory.decodeByteArray(fingerData.FingerImage(), 0,
                                fingerData.FingerImage().length);
//                        createDirectoryAndSaveFile(bitmap,"Template");


                        ResidentRegistration.this.runOnUiThread(new Runnable(){
                            @Override
                            public void run() {

                                imgFinger.setImageBitmap(bitmap);

                            }
                        });
                        SetTextOnUIThread("Capture Success");
                        Enroll_Template = new byte[fingerData.ISOTemplate().length];
//                        et=Enroll_Template.toString();

                        String log = "\nQuality: " + fingerData.Quality()
                                + "\nNFIQ: " + fingerData.Nfiq()
                                + "\nWSQ Compress Ratio: "
                                + fingerData.WSQCompressRatio()
                                + "\nImage Dimensions (inch): "
                                + fingerData.InWidth() + "\" X "
                                + fingerData.InHeight() + "\""
                                + "\nImage Area (inch): " + fingerData.InArea()
                                + "\"" + "\nResolution (dpi/ppi): "
                                + fingerData.Resolution() + "\nGray Scale: "
                                + fingerData.GrayScale() + "\nBits Per Pixal: "
                                + fingerData.Bpp() + "\nWSQ Info: "
                                + fingerData.WSQInfo();
                        SetLogOnUIThread(log);
                        SetData2(fingerData);

                    }
                } catch (Exception ex) {
                    SetTextOnUIThread("Error");
                } finally {
                    isCaptureRunning = false;
                }
            }
        }).start();
    }

    private void StopCapture() {
        try {
            mfs100.StopAutoCapture();
        } catch (Exception e) {
            SetTextOnUIThread("Error");
        }
    }

    private void ExtractANSITemplate() {
        try {
            if (lastCapFingerData == null) {
                SetTextOnUIThread("Finger not capture");
                return;
            }
            byte[] tempData = new byte[2000]; // length 2000 is mandatory
            byte[] ansiTemplate;
            int dataLen = mfs100.ExtractANSITemplate(lastCapFingerData.RawData(), tempData);
            if (dataLen <= 0) {
                if (dataLen == 0) {
                    SetTextOnUIThread("Failed to extract ANSI Template");
                } else {
                    SetTextOnUIThread(mfs100.GetErrorMsg(dataLen));
                }
            } else {
                ansiTemplate = new byte[dataLen];
                System.arraycopy(tempData, 0, ansiTemplate, 0, dataLen);
                WriteFile("ANSITemplate.ansi", ansiTemplate);
                SetTextOnUIThread("Extract ANSI Template Success");
            }
        } catch (Exception e){
            Log.e("Error", "Extract ANSI Template Error", e);
        }
    }

    private void ExtractISOImage() {
        try {
            if (lastCapFingerData == null) {
                SetTextOnUIThread("Finger not capture");
                return;
            }
            byte[] tempData = new byte[(mfs100.GetDeviceInfo().Width() * mfs100.GetDeviceInfo().Height()) + 1078];
            byte[] isoImage;
            int dataLen = mfs100.ExtractISOImage(lastCapFingerData.RawData(), tempData);
            if (dataLen <= 0) {
                if (dataLen == 0) {
                    SetTextOnUIThread("Failed to extract ISO Image");
                } else {
                    SetTextOnUIThread(mfs100.GetErrorMsg(dataLen));
                }
            } else {
                isoImage = new byte[dataLen];
                System.arraycopy(tempData, 0, isoImage, 0, dataLen);
                WriteFile("ISOImage.iso", isoImage);
                SetTextOnUIThread("Extract ISO Image Success");
            }
        } catch (Exception e) {
            Log.e("Error", "Extract ISO Image Error", e);
        }
    }

    private void ExtractWSQImage(){
        try {
            if (lastCapFingerData == null){
                SetTextOnUIThread("Finger not capture");
                return;
            }
            byte[] tempData = new byte[(mfs100.GetDeviceInfo().Width() * mfs100.GetDeviceInfo().Height()) + 1078];
            byte[] wsqImage;
            int dataLen = mfs100.ExtractWSQImage(lastCapFingerData.RawData(), tempData);
            if (dataLen <= 0) {
                if (dataLen == 0) {
                    SetTextOnUIThread("Failed to extract WSQ Image");
                } else {
                    SetTextOnUIThread(mfs100.GetErrorMsg(dataLen));
                }
            } else {
                wsqImage = new byte[dataLen];
                System.arraycopy(tempData, 0, wsqImage, 0, dataLen);
                WriteFile("WSQ.wsq", wsqImage);
                SetTextOnUIThread("Extract WSQ Image Success");
            }
        } catch(Exception e){
            Log.e("Error", "Extract WSQ Image Error", e);
        }
    }

    private void UnInitScanner(){
        try {
            int ret = mfs100.UnInit();
            if (ret != 0) {
                SetTextOnUIThread(mfs100.GetErrorMsg(ret));
            } else {
                SetLogOnUIThread("Uninit Success");
                SetTextOnUIThread("Uninit Success");
                lastCapFingerData = null;
            }
        } catch (Exception e) {
            Log.e("UnInitScanner.EX", e.toString());
        }
    }

    private void WriteFile(String filename, byte[] bytes) {
        try {
            String path = Environment.getExternalStorageDirectory()
                    + "//FingerData";
            File file = new File(path);
            if (!file.exists()){
                file.mkdirs();
            }
            path = path + "//" + filename;
            file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream stream = new FileOutputStream(path);
            stream.write(bytes);
            stream.close();
        } catch (Exception e1){
            e1.printStackTrace();
        }
    }

    private void ClearLog() {
        txtEventLog.post(new Runnable() {
            public void run() {
                txtEventLog.setText("", BufferType.EDITABLE);
            }
        });
    }

    private void SetTextOnUIThread(final String str) {

        lblMessage.post(new Runnable() {
            public void run() {
                lblMessage.setText(str);
            }
        });
    }

    private void SetLogOnUIThread(final String str) {

        txtEventLog.post(new Runnable() {
            public void run() {
                txtEventLog.append("\n" + str);
            }
        });
    }

    public void SetData2(FingerData fingerData){
        if (scannerAction.equals(ScannerAction.Capture)){
            Enroll_Template = new byte[fingerData.ISOTemplate().length];
            System.arraycopy(fingerData.ISOTemplate(), 0, Enroll_Template, 0,
                    fingerData.ISOTemplate().length);
            et=Enroll_Template.toString();
//            new GetLongLat("A").execute();
        } else if (scannerAction.equals(ScannerAction.Verify)){
            Verify_Template = new byte[fingerData.ISOTemplate().length];
            System.arraycopy(fingerData.ISOTemplate(), 0, Verify_Template, 0,
                    fingerData.ISOTemplate().length);
            for(int i = 0; i<temp_arrays.size(); i++){
                int ret = mfs100.MatchISO(temp_arrays.get(i), Verify_Template);
                if (ret < 0) {
//                    Toast.makeText(this, "Matched", Toast.LENGTH_SHORT).show();
                    SetTextOnUIThread("Error: " + ret + "(" + mfs100.GetErrorMsg(ret) + ")");
                } else {
                    if (ret >= 1400){
//                        Toast.makeText(this, "Matched", Toast.LENGTH_SHORT).show();
                        SetTextOnUIThread("Finger matched with score: " + ret+" "+ staffc_arrays.get(i));
                        break;
                    } else {
//                        Toast.makeText(this, "Not Matched", Toast.LENGTH_SHORT).show();
                        SetTextOnUIThread("Finger not matched, score: " + ret);
                    }
                }
            }
//            int ret = mfs100.MatchISO(rtemp, Verify_Template);
//            if (ret < 0) {
//                SetTextOnUIThread("Error: " + ret + "(" + mfs100.GetErrorMsg(ret) + ")");
//            } else {
//                if (ret >= 1400){
//                    SetTextOnUIThread("Finger matched with score: " + ret);
//                } else {
//                    SetTextOnUIThread("Finger not matched, score: " + ret);
//                }
//            }
        }

        WriteFile("Raw.raw", fingerData.RawData());
        WriteFile("Bitmap.bmp", fingerData.FingerImage());
        WriteFile("ISOTemplate.iso", fingerData.ISOTemplate());
        rtemp=null;
    }

    @Override
    public void OnDeviceAttached(int vid, int pid, boolean hasPermission) {
        int ret;
        if (!hasPermission) {
            SetTextOnUIThread("Permission denied");
            return;
        }
        if (vid == 1204 || vid == 11279) {
            if (pid == 34323) {
                ret = mfs100.LoadFirmware();
                if (ret != 0) {
                    SetTextOnUIThread(mfs100.GetErrorMsg(ret));
                } else {
                    SetTextOnUIThread("Load firmware success");
                }
            } else if (pid == 4101) {
                String key = "Without Key";
                ret = mfs100.Init();
                if (ret == 0) {
                    showSuccessLog(key);
                } else {
                    SetTextOnUIThread(mfs100.GetErrorMsg(ret));
                }

            }
        }
    }

    private void showSuccessLog(String key) {
        SetTextOnUIThread("Init success");
        String info = "\nKey: " + key + "\nSerial: "
                + mfs100.GetDeviceInfo().SerialNo() + " Make: "
                + mfs100.GetDeviceInfo().Make() + " Model: "
                + mfs100.GetDeviceInfo().Model()
                + "\nCertificate: " + mfs100.GetCertification();
        SetLogOnUIThread(info);
    }

    @Override
    public void OnDeviceDetached() {
        UnInitScanner();
        SetTextOnUIThread("Device removed");
    }

    @Override
    public void OnHostCheckFailed(String err) {
        try {
            SetLogOnUIThread(err);
            Toast.makeText(this, err, Toast.LENGTH_LONG).show();
        } catch (Exception ignored) {
        }
    }
    public class GetLongLat extends AsyncTask<String, String, String> {
        Connection conn;
        ResultSet rs=null, rs1=null;
        ProgressDialog progressDialog;
        DBConnection dbConnection = new DBConnection();
        String ret = "", flag = "";


        public GetLongLat(String flag) {
            this.flag = flag;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ResidentRegistration.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String res) {
            progressDialog.dismiss();
            if (res.equalsIgnoreCase("1")){

                showName(rs, flag);
                //Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
//                msg.setTextColor(Color.RED);
//                msg.setText("Internet Connection Error ");
//                btn_Status.setVisibility(onCreatePanelView(-1).INVISIBLE);
                Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params){
            try {
                conn = dbConnection.connectionclass(); //Connect to database
                if (conn == null){
                    ret = "Internet Connection error";
                } else {
                    if (flag.equalsIgnoreCase("U")){
                        PreparedStatement stmt = conn.prepareStatement("insert into MFSTestTemplate" + "("+finger+",StaffId,FlatNo,Category,MobileNo,OTP)" + " values(?,?,?,?,?,?)");
                        stmt.setBytes(1, Enroll_Template);
                        stmt.setString(2, StaffID);
                        stmt.setString(3, fno);
                        stmt.setString(4, catgry);
                        stmt.setString(5, phno);
                        stmt.setString(6, otp);
                        int i = stmt.executeUpdate();
                        ret = "" + i;
                    } else  if (flag.equalsIgnoreCase("A")){
                        PreparedStatement stmt = conn.prepareStatement("Update MFSTestTemplate set "+finger+"=? where StaffId= ? ");
                        stmt.setBytes(1, Enroll_Template);
                        stmt.setString(2, StaffID);
                        int i = stmt.executeUpdate();
                        ret = "" + i;
                    } else if(flag.equalsIgnoreCase("E")){
                        String query = "SELECT * FROM MFSTestTemplate ";
                        Statement stmt = conn.createStatement();
                        rs = stmt.executeQuery(query);
                        ret = "1";
                    }
                    else if(flag.equalsIgnoreCase("C")){
                        String query = "SELECT TOP 1 * FROM MFSTestTemplate where StaffId='"+StaffID+"'";
                        Statement stmt = conn.createStatement();
                        rs = stmt.executeQuery(query);
                        ret = "1";
                    } else if(flag.equalsIgnoreCase("D")){
                        String query = "SELECT * FROM MFSTestTemplate";
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showName(ResultSet rs, String flag){

        try {
            if (flag.equalsIgnoreCase("A")){
                Toast.makeText(getApplicationContext(),"Template Inserted Succesfully", Toast.LENGTH_LONG).show();
                dup_arrays= null;
                staff_arrays= null;
                staffc_arrays= null;
                temp_arrays=null;
                dup_arrays= new ArrayList<>();
                staff_arrays= new ArrayList<>();
                temp_arrays = new ArrayList<>();
                staffc_arrays = new ArrayList<>();

                new GetLongLat("E").execute();
                new GetLongLat("D").execute();
            }
            else if(flag.equalsIgnoreCase("U")){
                Toast.makeText(getApplicationContext(),"Template Inserted Succesfully", Toast.LENGTH_LONG).show();
                dup_arrays= null;
                staff_arrays= null;
                temp_arrays=null;
                staffc_arrays= null;
                dup_arrays= new ArrayList<>();
                staff_arrays= new ArrayList<>();
                temp_arrays = new ArrayList<>();
                staffc_arrays = new ArrayList<>();


                new GetLongLat("E").execute();
                new GetLongLat("D").execute();
            }
            else if(flag.equalsIgnoreCase("E")){
                while(rs.next()) {
                    rtemp = rs.getBytes("Finger1");
                    staffc = rs.getString("StaffId");
                    if(rtemp != null){
                        temp_arrays.add(rtemp);
                        staffc_arrays.add(staffc);
                    }
                    rtemp=null;
                    rtemp = rs.getBytes("Finger2");

                    if(rtemp != null){
                        temp_arrays.add(rtemp);
                        staffc_arrays.add(staffc);
                        rtemp=null;
                    }
                    rtemp=null;
                    rtemp = rs.getBytes("Finger3");
                    if(rtemp != null){
                        temp_arrays.add(rtemp);
                        staffc_arrays.add(staffc);
                        rtemp=null;
                    }
                    rtemp=null;
                    rtemp = rs.getBytes("Finger4");
                    if(rtemp != null){
                        temp_arrays.add(rtemp);
                        staffc_arrays.add(staffc);
                        rtemp=null;
                    }
                    rtemp=null;
                    rtemp = rs.getBytes("Finger5");
                    if(rtemp != null){
                        temp_arrays.add(rtemp);
                        staffc_arrays.add(staffc);
                        rtemp=null;
                    }
                    rtemp=null;
                    rtemp = rs.getBytes("Finger6");
                    if(rtemp != null){
                        temp_arrays.add(rtemp);
                        staffc_arrays.add(staffc);
                        rtemp=null;
                    }
                    rtemp=null;
                    rtemp = rs.getBytes("Finger7");
                    if(rtemp != null){
                        temp_arrays.add(rtemp);
                        staffc_arrays.add(staffc);
                        rtemp=null;
                    }
                    rtemp=null;
                    rtemp = rs.getBytes("Finger8");
                    if(rtemp != null){
                        temp_arrays.add(rtemp);
                        staffc_arrays.add(staffc);
                        rtemp=null;
                    }
                    rtemp=null;
                    rtemp = rs.getBytes("Finger9");
                    if(rtemp != null){
                        temp_arrays.add(rtemp);
                        staffc_arrays.add(staffc);
                        rtemp=null;
                    }
                    rtemp=null;
                    rtemp = rs.getBytes("Finger10");
                    if(rtemp != null){
                        temp_arrays.add(rtemp);
                        staffc_arrays.add(staffc);
                        rtemp=null;
                    }
                    staffc=null;
                }

            }
            else if(flag.equalsIgnoreCase("D")){

                while(rs.next()) {
                    rtemp = rs.getBytes("Finger1");
                    staff= rs.getString("StaffId");
                    if (rtemp != null) {
                        dup_arrays.add(rtemp);
                        staff_arrays.add(staff);
                    }
                    rtemp = null;
                    rtemp = rs.getBytes("Finger2");
                    if (rtemp != null) {
                        dup_arrays.add(rtemp);
                        staff_arrays.add(staff);
                        rtemp = null;
                    }
                    rtemp = null;
                    rtemp = rs.getBytes("Finger3");
                    if (rtemp != null) {
                        dup_arrays.add(rtemp);
                        staff_arrays.add(staff);
                        rtemp = null;
                    }
                    rtemp = null;
                    rtemp = rs.getBytes("Finger4");
                    if (rtemp != null) {
                        dup_arrays.add(rtemp);
                        staff_arrays.add(staff);
                        rtemp = null;
                    }
                    rtemp = null;
                    rtemp = rs.getBytes("Finger5");
                    if (rtemp != null) {
                        dup_arrays.add(rtemp);
                        staff_arrays.add(staff);
                        rtemp = null;
                    }
                    rtemp = null;
                    rtemp = rs.getBytes("Finger6");
                    if (rtemp != null) {
                        dup_arrays.add(rtemp);
                        staff_arrays.add(staff);
                        rtemp = null;
                    }
                    rtemp = null;
                    rtemp = rs.getBytes("Finger7");
                    if (rtemp != null) {
                        dup_arrays.add(rtemp);
                        staff_arrays.add(staff);
                        rtemp = null;
                    }
                    rtemp = null;
                    rtemp = rs.getBytes("Finger8");
                    if (rtemp != null) {
                        dup_arrays.add(rtemp);
                        staff_arrays.add(staff);
                        rtemp = null;
                    }
                    rtemp = null;
                    rtemp = rs.getBytes("Finger9");
                    if (rtemp != null) {
                        dup_arrays.add(rtemp);
                        staff_arrays.add(staff);
                        rtemp = null;
                    }
                    rtemp = null;
                    rtemp = rs.getBytes("Finger10");
                    if (rtemp != null) {
                        dup_arrays.add(rtemp);
                        staff_arrays.add(staff);
                        rtemp = null;

                    }
                    staff=null;
                }

//                    rtemp = rs.getBytes("Finger1");
//                        dup_arrays.add(rtemp);
//                    rtemp=null;
//                    rtemp = rs.getBytes("Finger2");
//                        dup_arrays.add(rtemp);
//                    rtemp=null;
//                    rtemp = rs.getBytes("Finger3");
//                        dup_arrays.add(rtemp);
//                    rtemp=null;
//                    rtemp = rs.getBytes("Finger4");
//                        dup_arrays.add(rtemp);
//                    rtemp=null;
//                    rtemp = rs.getBytes("Finger5");
//                        dup_arrays.add(rtemp);
//                    rtemp=null;
//                    rtemp = rs.getBytes("Finger6");
//                        dup_arrays.add(rtemp);
//                    rtemp=null;
//                    rtemp = rs.getBytes("Finger7");
//                        dup_arrays.add(rtemp);
//                    rtemp=null;
//                    rtemp = rs.getBytes("Finger8");
//                        dup_arrays.add(rtemp);
//                    rtemp=null;
//                    rtemp = rs.getBytes("Finger9");
//                        dup_arrays.add(rtemp);
//                    rtemp=null;
//                    rtemp = rs.getBytes("Finger10");
//                        dup_arrays.add(rtemp);
//                    rtemp=null;
//                }
//                    String r_staffid= rs.getString("StaffId");
//                    StartSyncCapture();
            }
            else if(flag.equalsIgnoreCase("C")){

                if(rs.next()){
                    if(rs.getBytes("Finger1") != null && rs.getBytes("Finger2")== null){
                        finger="Finger2";
                        new GetLongLat("A").execute();
                    }else if(rs.getBytes("Finger1")!= null && rs.getBytes("Finger2")!= null &&rs.getBytes("Finger3")== null){
                        finger="Finger3";
                        new GetLongLat("A").execute();
                    }else if(rs.getBytes("Finger1")!= null && rs.getBytes("Finger2")!= null &&rs.getBytes("Finger3")!= null &&rs.getBytes("Finger4")== null){
                        finger="Finger4";
                        new GetLongLat("A").execute();
                    }else if(rs.getBytes("Finger1")!= null && rs.getBytes("Finger2")!= null &&rs.getBytes("Finger3")!= null &&rs.getBytes("Finger4")!= null && rs.getBytes("Finger5")== null){
                        finger="Finger5";
                        new GetLongLat("A").execute();
                    }else if(rs.getBytes("Finger1")!= null && rs.getBytes("Finger2")!= null &&rs.getBytes("Finger3")!= null &&rs.getBytes("Finger4")!= null && rs.getBytes("Finger5")!= null && rs.getBytes("Finger6")== null){
                        finger="Finger6";
                        new GetLongLat("A").execute();
                    }else if(rs.getBytes("Finger1")!= null && rs.getBytes("Finger2")!= null &&rs.getBytes("Finger3")!= null &&rs.getBytes("Finger4")!= null && rs.getBytes("Finger5")!= null && rs.getBytes("Finger6")!= null && rs.getBytes("Finger7")== null){
                        finger="Finger7";
                        new GetLongLat("A").execute();
                    }else if(rs.getBytes("Finger1")!= null && rs.getBytes("Finger2")!= null &&rs.getBytes("Finger3")!= null &&rs.getBytes("Finger4")!= null && rs.getBytes("Finger5")!= null && rs.getBytes("Finger6")!= null && rs.getBytes("Finger7")!= null && rs.getBytes("Finger8")== null){
                        finger="Finger8";
                        new GetLongLat("A").execute();
                    }else if(rs.getBytes("Finger1")!= null && rs.getBytes("Finger2")!= null &&rs.getBytes("Finger3")!= null &&rs.getBytes("Finger4")!= null && rs.getBytes("Finger5")!= null && rs.getBytes("Finger6")!= null && rs.getBytes("Finger7")!= null && rs.getBytes("Finger8")!= null && rs.getBytes("Finger9")== null){
                        finger="Finger9";
                        new GetLongLat("A").execute();
                    }else if(rs.getBytes("Finger1")!= null && rs.getBytes("Finger2")!= null &&rs.getBytes("Finger3")!= null &&rs.getBytes("Finger4")!= null && rs.getBytes("Finger5")!= null && rs.getBytes("Finger6")!= null && rs.getBytes("Finger7")!= null && rs.getBytes("Finger8")!= null && rs.getBytes("Finger9")!= null && rs.getBytes("Finger10")== null){
                        finger="Finger10";
                        new GetLongLat("A").execute();
                    }else{
                        Toast.makeText(this, "Templates Already Inserted", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    finger="Finger1";
                    fno= flatno.getSelectedItem().toString();
                    catgry  = category.getSelectedItem().toString();
                    phno= mobileno.getText().toString();
                    int randomPin   =(int)(Math.random()*900000)+100000;
                    otp  =String.valueOf(randomPin);
//                    Toast.makeText(this, ""+otp, Toast.LENGTH_SHORT).show();
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent == true){
                        JSONObject jsonObj = new JSONObject();

                        try {

                            jsonObj.put("msg", otp+" is your Visitor Verification Code.");
                            jsonObj.put("mobile", phno);
                            jsonObj.put("Respose", "TRUE");

                            TransactionLog_GetJSON = jsonObj.toString();
                            new Perform_log(URLs.getPerform_URI(), TransactionLog_GetJSON).execute();

                            //  new Perform_log("", TransactionLog_GetJSON).execute();

                        }catch (final Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    new GetLongLat("U").execute();

                }
//                    rtemp = rs.getBytes("Template");
//                    temp_arrays.add(rtemp);
            }
//                    String r_staffid= rs.getString("StaffId");
//                    StartSyncCapture();


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }
//=========================================================================================================
//-------------------------------------------------------------------------------------------
public class Perform_log extends AsyncTask<Void, Integer, Void> {
    ProgressDialog progressDialog;
    String final_out1 = "";
    String URL = "";
    String jsonString = "";

    public Perform_log(String url, String json){
        this.URL = url;
        this.jsonString = json;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        // TODO Auto-generated method stub
        try {
            final_out1 = jsnp_post.makeHttpRequest(URL, "POST", jsonString);

        } catch (Exception e) {
            // TODO: handle exception
            Toast.makeText(getApplicationContext(), "An unexpected error occurred", Toast.LENGTH_LONG).show();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void result){

        progressDialog.dismiss();
        try {
            if (final_out1.equalsIgnoreCase("")) {
                Toast.makeText(ResidentRegistration.this, "OTP not Sent", Toast.LENGTH_SHORT).show();
            }
            else {
                //attribute success then save else error message
//
//                    new  GetLongLat("C").execute();
//                    verifyotpbutton.setVisibility(View.VISIBLE);
//                    verifyotp.setVisibility(View.VISIBLE);
//                    phone.setEnabled(false);
//                    mCbShowPwd.setVisibility(View.VISIBLE);
//                    Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_LONG).show();
            }
//                    Toast.makeText(getApplicationContext(), final_out1, Toast.LENGTH_LONG).show();
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
        }
        super.onPostExecute(result);
    }
    @Override
    protected void onPreExecute(){
        // TODO Auto-generated method stub
        progressDialog = new ProgressDialog(ResidentRegistration.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        //super.onPreExecute();
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
//=========================================================================================================
    private void SetData3(FingerData fingerData) {
        boolean status=true;
        Verify_Template = new byte[fingerData.ISOTemplate().length];
        System.arraycopy(fingerData.ISOTemplate(), 0, Verify_Template, 0,
                fingerData.ISOTemplate().length);
        for(int i = 0; i < dup_arrays.size(); i++){
            int ret = mfs100.MatchISO(dup_arrays.get(i), Verify_Template);
            if (ret < 0) {
                status=true;
//                    Toast.makeText(this, "Matched", Toast.LENGTH_SHORT).show();
                SetTextOnUIThread("Error: " + ret + "(" + mfs100.GetErrorMsg(ret) + ")");
            } else {
                if (ret >= 1400){
                    status=false;
//                        Toast.makeText(this, "Matched", Toast.LENGTH_SHORT).show();
                    SetTextOnUIThread("Finger matched with score: " + ret+"  "+ staff_arrays.get(i).toString());
//                    Toast.makeText(this, "StaffCode"+ staff_arrays.get(i).toString(), Toast.LENGTH_SHORT).show();
                    break;
                } else if(ret <=20 && ret >=0){
                    //                        Toast.makeText(this, "Not Matched", Toast.LENGTH_SHORT).show();
                    SetTextOnUIThread("Finger not matched, score: " + ret);
                    status=true;

                }
            }
        }
        if(status){
            new GetLongLat("C").execute();
        }else {
            Toast.makeText(this, "Template Already Exist", Toast.LENGTH_SHORT).show();
        }

    }
    ///==========================================Code to save image===================//
    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this, new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });



//        String roo+ri.parse("file://" + Environment.getExternalStorageDirectory())));
//        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }
    ///==========================================Code to save image===================//

//    private static Bitmap codec(Bitmap src, Bitmap.CompressFormat format,
//                                int quality) {
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        src.compress(format, quality, os);
//
//        byte[] array = os.toByteArray();
//        return BitmapFactory.decodeByteArray(array, 0, array.length);
//    }

    public void imageTosaved(Bitmap bmp,String fileName){
        File direct = new File(Environment.getExternalStorageDirectory() + "/Templates");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/Templates/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("/sdcard/Templates/"), fileName);
        if (file.exists()){
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
