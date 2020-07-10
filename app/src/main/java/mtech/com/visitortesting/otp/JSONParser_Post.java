package mtech.com.visitortesting.otp;

import android.content.Context;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class JSONParser_Post{

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    static String final_resultL="";
    private static Context context;
    String line="";
    String result = "";
    HttpResponse httpResponse;

    public JSONParser_Post(){

    }
    // function get json from url
    // by making HTTP POST or GET method
    public String makeHttpRequest(String url, String method, String jsonString){
        Log.e("url", url);
        // Making HTTP request
        try {
            final_resultL="";
            // check for request method
            if(method == "POST"){
                // request method is POST
                // defaultHttpClient
                HttpClient httpclient = new DefaultHttpClient();
                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(url);
                // 3. build jsonObject
                // 4. convert JSONObject to JSON to String
                //json = jsonObject.toString();
                Log.e("log",json);
                // 5. set json to StringEntity
                StringEntity se = new StringEntity(jsonString);
                // 6. set httpPost Entity
                httpPost.setEntity(se);
                Log.e("setEntity", "setEntity");
                httpResponse = httpclient.execute(httpPost);
                httpResponse.getStatusLine().getStatusCode();
                Log.e("code", ""+httpResponse.getStatusLine().getStatusCode());
            }else if(method == "GET"){
                // request method is GET
            }
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch(ClientProtocolException e) {
            e.printStackTrace();
        } catch(IOException e){
            final_resultL = "11";
            e.printStackTrace();
            return final_resultL;
        }
        try{
            Log.e("setEntity", "setEntity");
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"),8);
            result = "";  line="";
            Log.e("setEntity", "setEntity");
            while((line = reader.readLine()) != null){
                Log.e("setEntity", ""+line.toString());
                result += line;
            }
            Log.e("setEntity", ""+result);
            int lentgh1=result.length()-1;
            Log.e("len", ""+lentgh1);
            String str="";
            str=result.substring(1, lentgh1);
            Log.e("setEntity", ""+str);
            final_resultL="";
            final_resultL=str.toString();
           /* Log.e("setEntity", ""+result);
            int lentgh1=result.length()-1;
            String str="";
	       str=result.substring(1, lentgh1);

	       final_resultL="";
	      final_resultL=str;*/

        } catch (Exception e){
            Log.e("setEntity", ""+result);
            Log.e("Buffer Error", "Error converting result " + e.toString());
            final_resultL="";
        }
        return final_resultL;
    }
}