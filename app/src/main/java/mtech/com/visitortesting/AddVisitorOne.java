package mtech.com.visitortesting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;

public class AddVisitorOne extends Activity {

    private static final int CAMERA_REQUEST = 1890;
    private static final int CAMERA_REQUEST1 = 1891;
    TextView mobileNo;
    Bitmap visphoto, idsphoto;
    ImageView visitorPhoto, IdPhoto;
    EditText fname, lname;
    String phone = null;
    byte[] vbyteimage = null, idbyteimage = null;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_visitor_one);
        cd = new ConnectionDetector(AddVisitorOne.this);
        Intent i = getIntent();
        phone = i.getStringExtra("phone");
        mobileNo = (TextView) findViewById(R.id.mobileNo);
        visitorPhoto = (ImageView) this.findViewById(R.id.visitorPhoto);
        IdPhoto = (ImageView) this.findViewById(R.id.idPhoto);
        fname = (EditText) this.findViewById(R.id.fname);
        lname = (EditText) this.findViewById(R.id.lname);
        visitorPhoto.setImageResource(R.drawable.profile);
        //masking mobile number
        String number = phone;
        String mask = number.replaceAll("\\w(?=\\w{3})", "*");
        mobileNo.setText(mask);
    }

    public void visitorPhoto(View v){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    public void IdPhoto(View v){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == CAMERA_REQUEST){
            if (resultCode == RESULT_OK && data != null) {

                visphoto = (Bitmap) data.getExtras().get("data");
                visitorPhoto.setImageBitmap(visphoto);
                Bitmap bitmap = ((BitmapDrawable) visitorPhoto.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                vbyteimage = stream.toByteArray();

            } else if (resultCode == RESULT_CANCELED){

            }
        } else if (requestCode == CAMERA_REQUEST1){
            if (resultCode == RESULT_OK && data != null){

                idsphoto = (Bitmap) data.getExtras().get("data");
                IdPhoto.setImageBitmap(idsphoto);
                Bitmap bitmap = ((BitmapDrawable) IdPhoto.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                idbyteimage = stream.toByteArray();
            }
        }
    }

    public void addVisitor(View v) {
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent == true) {
            String firstName = fname.getText().toString();
            String lastName = lname.getText().toString();
            String mobile = mobileNo.getText().toString();

            if (!firstName.equals("")) {
                if (!lastName.equals("")) {
                    final Bitmap bmap = ((BitmapDrawable) visitorPhoto.getDrawable()).getBitmap();
                    Drawable myDrawable = getResources().getDrawable(R.drawable.profile);
                    final Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();
                    if (bmap.sameAs(myLogo)) {
                        Toast.makeText(this, "Visitor's Photo Required", Toast.LENGTH_SHORT).show();
                    } else {

                        Intent i = new Intent(AddVisitorOne.this, AddVisitorTwo.class);
                        i.putExtra("fname", firstName);
                        i.putExtra("lname", lastName);
                        i.putExtra("mobile", phone);
                        i.putExtra("stat", "N");
                        i.putExtra("visitorphoto", visphoto);
                        i.putExtra("idphoto", idsphoto);
                        startActivity(i);
                        finish();
                    }

                } else {
                    Toast.makeText(this, "Enter Last Name", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Enter First Name", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Internet Connection Error", Toast.LENGTH_SHORT).show();
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
