package com.lazooz.lbm.communications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.lazooz.lbm.cfg.StaticParms;
import com.lazooz.lbm.utils.Utils;

import android.content.Context;
import android.util.Log;

public class ServerCom {
	
	private JSONObject returnObject;
	private byte[] returnBuffer;
	private Context mContext;
	
	public ServerCom(Context context){
		mContext = context;
	} 
	

	public void registerToServer(String cellphone)
	{
		String url = StaticParms.BASE_SERVER_URL + "api_register";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("cellphone", cellphone ));
		
		this.postRequestToServer(-1, -1, url, params);
		
	}
	
	public void setLocation1(String UserId, String UserSecret, String data)
	{
		String url = StaticParms.BASE_SERVER_URL + "api_set_location";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", UserId ));
		params.add(new BasicNameValuePair("user_secret", UserSecret ));
		params.add(new BasicNameValuePair("location_list", data ));
		
		this.postRequestToServer(-1, -1, url, params);
	}
	
	public void getUserKeyData(String UserId, String UserSecret)
	{
		String url = StaticParms.BASE_SERVER_URL + "api_get_user_key_data";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", UserId ));
		params.add(new BasicNameValuePair("user_secret", UserSecret ));
				
		this.postRequestToServer(-1, -1, url, params);
	}

	
	
	
	
	public void setLocationZip(String UserId, String UserSecret, byte[] data)throws Exception
	{
		String url = StaticParms.BASE_SERVER_URL + "api_set_location";
		String TAG = "run";
    	try { 
	          HttpClient client = new DefaultHttpClient();
	          
	          HttpPost request = new HttpPost();
	          request.setURI(new URI(url));
	        
	          String md5 = "";
			try {
				md5 = Utils.md5ThePHPWay(data);
			} catch (NoSuchAlgorithmException e) {}
	          
	          MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	          
	          multipartEntity.addPart("user_id", new StringBody(UserId));
	          multipartEntity.addPart("user_secret", new StringBody(UserSecret));
	          multipartEntity.addPart("md5", new StringBody(md5));
	          multipartEntity.addPart("location_data", new ByteArrayBody(data, "application/zip", "data.zip"));
	          request.setEntity(multipartEntity);
	 
	          HttpResponse response = client.execute(request);
	    	          
	    	  this.responseProcess(response);
    	          
    	    }catch(URISyntaxException e){
    	          Log.e(TAG,"myHttpGetHttpGet URISyntaxException");

    	      }catch(IOException e){
    	          Log.e(TAG,"myHttpGetHttpGet IOException : " + e.getMessage());
    	      }catch(IllegalStateException e){
    	          Log.e(TAG,"myHttpGetHttpGet IllegalStateException");
    	      }catch (JSONException e) {
				e.printStackTrace();
    	      
			}
		
	}
	
	public void setContactsZip(String UserId, String UserSecret, byte[] data)throws Exception
	{
		String url = StaticParms.BASE_SERVER_URL + "api_set_contacts";
		String TAG = "run";
    	try { 
	          HttpClient client = new DefaultHttpClient();
	          
	          HttpPost request = new HttpPost();
	          request.setURI(new URI(url));
	        
	          String md5 = "";
			try {
				md5 = Utils.md5ThePHPWay(data);
			} catch (NoSuchAlgorithmException e) {}
	          
	          MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	          
	          multipartEntity.addPart("user_id", new StringBody(UserId));
	          multipartEntity.addPart("user_secret", new StringBody(UserSecret));
	          multipartEntity.addPart("md5", new StringBody(md5));
	          multipartEntity.addPart("contacts_data", new ByteArrayBody(data, "application/zip", "data.zip"));
	          request.setEntity(multipartEntity);
	 
	          HttpResponse response = client.execute(request);
	    	          
	    	  this.responseProcess(response);
    	          
    	    }catch(URISyntaxException e){
    	          Log.e(TAG,"myHttpGetHttpGet URISyntaxException");

    	      }catch(IOException e){
    	          Log.e(TAG,"myHttpGetHttpGet IOException : " + e.getMessage());
    	      }catch(IllegalStateException e){
    	          Log.e(TAG,"myHttpGetHttpGet IllegalStateException");
    	      }catch (JSONException e) {
				e.printStackTrace();
    	      
			}
		
	}
	
	public void registerValidationToServer(String requestId, String token) {
		String url = StaticParms.BASE_SERVER_URL + "api_register_validation";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("registration_request_id", requestId ));
		params.add(new BasicNameValuePair("registration_request_token", token ));
		
		this.postRequestToServer(-1, -1, url, params);
		
	}
	
	
	public void setFriendRecommend(String UserId, String UserSecret, String data) {
		String url = StaticParms.BASE_SERVER_URL + "api_set_friend_recommend";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", UserId ));
		params.add(new BasicNameValuePair("user_secret", UserSecret ));
		params.add(new BasicNameValuePair("friend_recommend_request_list", data ));
		
		this.postRequestToServer(-1, -1, url, params);
		
	}

	
	public void getScreenInfoText() {
		String url = StaticParms.BASE_SERVER_URL + "api_get_screen_texts";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		this.postRequestToServer(-1, -1, url, params);
		
	}
		
	
	public void doSetLocation(String productId, String token, String latitude, String longitude,
			String locationTime, String locationAccuracy)
	{
		String url = StaticParms.BASE_SERVER_URL + "app_info/set_location";
			
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("product_id", productId + ""));
		params.add(new BasicNameValuePair("token", token + ""));
		params.add(new BasicNameValuePair("location_latitude", latitude + ""));
		params.add(new BasicNameValuePair("location_longitude", longitude + ""));
		params.add(new BasicNameValuePair("device_location_time", locationTime + ""));
		params.add(new BasicNameValuePair("device_location_accuracy", locationAccuracy + ""));
		params.add(new BasicNameValuePair("androidBuildNum", Utils.getVersionCode(mContext) + ""));
		
		this.postRequestToServer(-1, -1, url, params);
		
	}

	public void doActivateNewProduct(String activationCode, String productType, String deviceName)throws Exception
	{
			String url = StaticParms.BASE_SERVER_URL + "app_activation/activate_new_product";
				
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("activation_code", activationCode + ""));
			params.add(new BasicNameValuePair("product_type", productType + ""));
			params.add(new BasicNameValuePair("device_name", deviceName + ""));
			params.add(new BasicNameValuePair("androidBuildNum", Utils.getVersionCode(mContext) + ""));
			this.postRequestToServer(-1, -1, url, params);
		
	}
	
	public void setClientException(String product_id, String token, String exeptionData)
	{
		String url = StaticParms.BASE_SERVER_URL + "app_info/setClientException";
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("product_id", product_id ));
		params.add(new BasicNameValuePair("token", token ));
		params.add(new BasicNameValuePair("exeptionTime", Utils.getNowTimeInGMT()));
		params.add(new BasicNameValuePair("exeptionData", exeptionData));
	
		this.postRequestToServer(-1, -1, url,params);
		
	}
		
	public void doGetStatus(String product_id, String token, String isLockAllowed, String deviceName)
	{
		String url = StaticParms.BASE_SERVER_URL + "app_activation/get_status";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("product_id", product_id ));
		params.add(new BasicNameValuePair("token", token ));
		params.add(new BasicNameValuePair("is_lock_device_allowed", isLockAllowed + ""));
		params.add(new BasicNameValuePair("device_name", deviceName + ""));
		params.add(new BasicNameValuePair("androidBuildNum", Utils.getVersionCode(mContext) + ""));
		this.postRequestToServer(-1, -1, url, params);
		
	}

	

	
	public byte[] getReturnBuffer(){
		return returnBuffer;
	}
	
	public JSONObject getReturnObject() {
		return returnObject;
	}

	public String postRequestToServer(int connTimeout, int SoTimeout, String apiUrl, List<NameValuePair> params)
	{
		
		String TAG = "server";
		Log.e(TAG,"postRequestToServer: " + apiUrl);
		Log.e(TAG,"postRequestToServerParams: " + params.toString());

    	try { 
    	          HttpClient client = new DefaultHttpClient();
    	          
    	          if (connTimeout > -1)
    	        	  HttpConnectionParams.setConnectionTimeout(client.getParams(), connTimeout);
    	          if (SoTimeout > -1)
    	        	  HttpConnectionParams.setSoTimeout(client.getParams(), SoTimeout);
    	          
    	          HttpPost request = new HttpPost();
    	          request.setURI(new URI(apiUrl));
    	          
    	          request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

    	          Log.e(TAG,apiUrl);
    	          
    	          HttpResponse response = client.execute(request);
    	          
    	          
    	          
    	          this.responseProcess(response);
    	          
    	          
    	      }catch(URISyntaxException e){
    	          Log.e(TAG,"myHttpGetHttpGet URISyntaxException");

    	      }catch(IOException e){
    	          Log.e(TAG,"myHttpGetHttpGet IOException : " + e.getMessage());
    	      }catch(IllegalStateException e){
    	          Log.e(TAG,"myHttpGetHttpGet IllegalStateException");
    	      }catch (JSONException e) {
				e.printStackTrace();
    	      }
			
		
		return "";
	}
	
	private void responseProcess(HttpResponse response) throws JSONException{
  	  BufferedReader inBuff = null;
  	  final String TAG = "responseProcess";
  	  try{  
  		  
  		  inBuff = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
  	      StringBuffer stringBuf = new StringBuffer("");
  	      String line = null;
  	      String NewLine = System.getProperty("line.separator");
  	      while ((line = inBuff.readLine()) != null) {
  	          stringBuf.append(line + NewLine);
  	      }
  	      inBuff.close();
  	      String page = stringBuf.toString();
  	      
          Log.e("server","postRequestToServerResponse: " + page);

  	      

  	    	 JSONObject object = (JSONObject) new JSONTokener(page).nextValue();
  	    	 
  	    	 this.returnObject = object;
  	    	 

  	  }catch(IOException e){
  	       Log.e(TAG," IOException");

  	  }
  	  finally {
  	    if (inBuff != null) {
  	     try {
  	      inBuff.close();
  	           }catch (IOException e) {
  	              Log.e(TAG,"IOException closing buffer");
  	           }
  	       }
  	  }
  	  
  	  
  	}





	
	



	
	
	
	
}	
	
