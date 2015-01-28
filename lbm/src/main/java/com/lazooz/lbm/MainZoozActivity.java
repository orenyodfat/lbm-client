package com.lazooz.lbm;


import org.json.JSONException;
import org.json.JSONObject;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.NetworkParameters;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lazooz.lbm.businessClasses.ServerData;
import com.lazooz.lbm.communications.ServerCom;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;
import com.lazooz.lbm.utils.Utils;


import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images;



public class MainZoozActivity extends ActionBarActivity {


	private ImageView mQRPrivateIV, mQRPublicIV;
	private Bitmap mPublicBmp;
	private Bitmap mPrivateBmp;
	private String mScannedKey;
	private Button mExportPriKeyBtn;
	private Button mExportPubKeyBtn;
	private LinearLayout mPrivateKeyLL;
	private Button mShowPrivateKeyBtn;
	private TextView mZoozBalTV;
	private TextView mPotentialBalTV;
	private TextView mPublicKeyTV;
	private TextView mPrivateKeyTV;
	private ImageView mPrivateDotsIV;
	private ImageView mQRPrivatePixeldIV;
	protected boolean mIsPrivateShow;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		setContentView(R.layout.activity_main_zooz_new);
		
		Utils.setTitleColor(this, getResources().getColor(R.color.white));
		
		Button scanBtn = (Button)findViewById(R.id.zooz_scan_btn);
		scanBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				AlertDialog alertDialog = new AlertDialog.Builder(MainZoozActivity.this).create();
		     	alertDialog.setTitle(getString(R.string.scan_a_new_key));
		     	alertDialog.setMessage(getString(R.string.scan_a_new_key_msg));
			    alertDialog.setCanceledOnTouchOutside(false);
			    
			    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.scan_a_new_key), new DialogInterface.OnClickListener() {
			    	@Override
			        public void onClick(DialogInterface dialog, int which) {
			    		
						//Intent intent = new Intent(MainZoozActivity.this, CameraQRActivity.class);
			    		try {

			    		    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			    		    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

			    		    startActivityForResult(intent, 0);

			    		} catch (Exception e) {

			    		    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
			    		    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
			    		    startActivity(marketIntent);

			    		}
			    	}
			    });
			    
			    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
			    	@Override
			        public void onClick(DialogInterface dialog, int which) {
			    	}
			    });
			    alertDialog.show();
				
			}
		});

		
		mPrivateKeyLL = (LinearLayout)findViewById(R.id.public_key_ll);
		
		
		mExportPriKeyBtn = (Button)findViewById(R.id.export_pri_btn);
		mExportPriKeyBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sharePrivateImage();				
			}
		});
		
		mExportPubKeyBtn = (Button)findViewById(R.id.export_pub_btn);
		mExportPubKeyBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sharePublicImage();
			}
		});

		mShowPrivateKeyBtn = (Button)findViewById(R.id.show_private_btn);
		mShowPrivateKeyBtn.setText(getString(R.string.show));
		mIsPrivateShow = false;
		mShowPrivateKeyBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mIsPrivateShow){
					mIsPrivateShow = false;
					mShowPrivateKeyBtn.setText(getString(R.string.show));
					mPrivateDotsIV.setVisibility(View.VISIBLE);
					mPrivateKeyTV.setVisibility(View.INVISIBLE);
					mQRPrivatePixeldIV.setVisibility(View.VISIBLE);
					mQRPrivateIV.setVisibility(View.INVISIBLE);
				}
				else {
					mIsPrivateShow = true;
					mShowPrivateKeyBtn.setText(getString(R.string.hide));
					mPrivateDotsIV.setVisibility(View.INVISIBLE);
					mPrivateKeyTV.setVisibility(View.VISIBLE);
					mQRPrivatePixeldIV.setVisibility(View.INVISIBLE);
					mQRPrivateIV.setVisibility(View.VISIBLE);
				}
			}
		});
		
		
		
		mQRPublicIV = (ImageView) findViewById(R.id.qr_public_iv);
		mQRPrivateIV = (ImageView) findViewById(R.id.qr_private_iv);
		mQRPrivateIV.setVisibility(View.GONE);
		
		mQRPrivatePixeldIV = (ImageView) findViewById(R.id.qr_private_pixeled_iv);
		
		mZoozBalTV = (TextView)findViewById(R.id.zooz_balance_tv);
		mPotentialBalTV = (TextView)findViewById(R.id.zooz_poten_balance_tv);
		
		mPublicKeyTV = (TextView)findViewById(R.id.zooz_public_key_tv);
		mPrivateKeyTV = (TextView)findViewById(R.id.zooz_private_key_tv);

		mPrivateDotsIV = (ImageView) findViewById(R.id.zoo_private_dots_iv);
		
		
		
		
		UpdateGUI();
		
		
	}
	

	
	
	
	
	
	private void sharePublicImage(){
		String pathofBmp = Images.Media.insertImage(getContentResolver(), mPublicBmp,"", null);
        if (pathofBmp != null)
        {
		 Uri bmpUri = Uri.parse(pathofBmp);
		 final Intent intent = new Intent(     android.content.Intent.ACTION_SEND);
		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
		 intent.setType("image/png");
		 intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.my_zooz_address));
		 intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.my_zooz_address)+" :\n\n" + MySharedPreferences.getInstance().getPublicKey(this));
		 startActivity(Intent.createChooser(intent, getString(R.string.export_zooz_address)));
		 startActivity(intent);
        }
        else
        {
        	Toast.makeText(MainZoozActivity.this, "Share Public Key Fail", Toast.LENGTH_LONG).show();
        }
        
	}
	
	private void sharePrivateImageApproved(){
		String pathofBmp = Images.Media.insertImage(getContentResolver(), mPrivateBmp,"", null);
		if (pathofBmp != null)
        {
		 Uri bmpUri = Uri.parse(pathofBmp);
		 final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
	     intent.setType("image/png");
		 intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.my_private_key));
		 intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.my_private_key)+" :\n\n" + MySharedPreferences.getInstance().getPrivateKey(MainZoozActivity.this));
		 MainZoozActivity.this.startActivity(Intent.createChooser(intent, getString(R.string.export_private_key)));
		 MainZoozActivity.this.startActivity(intent);
        }
		else
        {
        	Toast.makeText(MainZoozActivity.this, "Share Private Key Fail", Toast.LENGTH_LONG).show();
        }
	}
	
	private void sharePrivateImage(){
		AlertDialog alertDialog = new AlertDialog.Builder(MainZoozActivity.this).create();
     	alertDialog.setTitle(getString(R.string.export_private_key));
     	alertDialog.setMessage(getString(R.string.export_private_key_msg));
	    alertDialog.setCanceledOnTouchOutside(false);
	    
	    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.export), new DialogInterface.OnClickListener() {
	    	@Override
	        public void onClick(DialogInterface dialog, int which) {
	    		sharePrivateImageApproved();
	    	}
	    });
	    
	    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
	    	@Override
	        public void onClick(DialogInterface dialog, int which) {
	    	}
	    });
	    alertDialog.show();
		
 
	}
	
	
	
	
	
	private Bitmap displayQR(ImageView iv, String data){
		if ((data == null)||(data.equals(""))){
			iv.setImageBitmap(null);
			return null;
		}
		QRCodeWriter writer = new QRCodeWriter();
		Bitmap bmp = null;
	    try {
	        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512);
	        int width = bitMatrix.getWidth();
	        int height = bitMatrix.getHeight();
	        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
	        for (int x = 0; x < width; x++) {
	            for (int y = 0; y < height; y++) {
	                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
	            }
	        }
	        iv.setImageBitmap(bmp);
	       
	    } catch (WriterException e) {
	        e.printStackTrace();
	    }
	    
	    return bmp;
	}
	
	
	public boolean isValidAddress(String address) {
		try {
			new Address(NetworkParameters.prodNet(), address);
			return true;
		} catch(AddressFormatException e) {
			return false;
		}
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if ((requestCode == 0)&& (resultCode == RESULT_OK)){
			if ((intent != null)&& (intent.hasExtra("SCAN_RESULT"))){
				
				mScannedKey = intent.getStringExtra("SCAN_RESULT");
				
				if (!isValidAddress(mScannedKey)){
					Utils.messageToUser(this, getString(R.string.scan), getString(R.string.scan_qr_msg));
					return;
				}
				
				
		     	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		     	alertDialog.setTitle(getString(R.string.import_zooz_address));
		     	
		     	
		     	Spannable wordtoSpan = new SpannableString(getString(R.string.new_zooz_address_scan_msg_1) + 
															mScannedKey +"\n"+ 
															getString(R.string.new_zooz_address_scan_msg_2));        

		     	wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 30, 30+mScannedKey.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		     	wordtoSpan.setSpan(new BackgroundColorSpan(Color.YELLOW), 30, 30+mScannedKey.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		     	
		     	
		     			
		     	alertDialog.setMessage(wordtoSpan);
			    alertDialog.setCanceledOnTouchOutside(false);
			    
			    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.yes), new DialogInterface.OnClickListener() {
			    	@Override
			        public void onClick(DialogInterface dialog, int which) {
			    		UpdateKeyToServerAsync(mScannedKey);
			    	}
			    });
			    
			    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.no), new DialogInterface.OnClickListener() {
			    	@Override
			        public void onClick(DialogInterface dialog, int which) {
			    	}
			    });
			    alertDialog.show();

				
				
				
			}
		}
		

          
	}







	protected void UpdateKeyToServerAsync(String key) {
		UpdateKeyToServer updateKeyToServer = new UpdateKeyToServer();
		updateKeyToServer.execute(key);
		
	}

	private class UpdateKeyToServer extends AsyncTask<String, Void, String> {


		@Override
		protected String doInBackground(String... params) {
			
          	ServerCom bServerCom = new ServerCom(MainZoozActivity.this);
          	String theKey = params[0];
              
        	JSONObject jsonReturnObj=null;
			try {
				MySharedPreferences msp = MySharedPreferences.getInstance();
				
				
				bServerCom.setUsetPublicKey(msp.getUserId(MainZoozActivity.this), msp.getUserSecret(MainZoozActivity.this), theKey);
				jsonReturnObj = bServerCom.getReturnObject();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        	
        	String serverMessage = "";
	
			try {
				if (jsonReturnObj == null)
					serverMessage = "ConnectionError";
				else {
					serverMessage = jsonReturnObj.getString("message");
					if (serverMessage.equals("success")){
						
					}
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
				serverMessage = "GeneralError";
			}
			
			
			return serverMessage;
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			if (result.equals("success")){
				Toast.makeText(MainZoozActivity.this, getString(R.string.the_key_updated), Toast.LENGTH_LONG).show();
				MySharedPreferences.getInstance().saveKeyPair(MainZoozActivity.this, "", mScannedKey);
				UpdateGUI();
			}
			else if (result.equals("credentials_not_valid")){
				Utils.restartApp(MainZoozActivity.this);
			}
			else
				Toast.makeText(MainZoozActivity.this, getString(R.string.key_update_fail), Toast.LENGTH_LONG).show();
		}
			
		
		@Override
		protected void onPreExecute() {
			
		}
	}

	public void UpdateGUI() {
		MySharedPreferences msp = MySharedPreferences.getInstance();
		ServerData sd = msp.getServerData(this);
		
		String privateKey = msp.getPrivateKey(this);
		String publicKey = msp.getPublicKey(this);

		mPublicKeyTV.setText(publicKey);
		mPrivateKeyTV.setText(privateKey);
		
		mPublicBmp = displayQR(mQRPublicIV, publicKey);
		mPrivateBmp = displayQR(mQRPrivateIV, privateKey);
		
		float zb = Float.valueOf(sd.getZoozBalance());
		float pzb = Float.valueOf(sd.getPotentialZoozBalance());
		
		mZoozBalTV.setText(String.format("%.2f", zb));
		mPotentialBalTV.setText(String.format("%.2f", pzb));
		
		if (mPrivateBmp != null){
			mPrivateKeyLL.setVisibility(View.VISIBLE);
			mQRPrivateIV.setVisibility(View.VISIBLE);
		}
		else{
			mPrivateKeyLL.setVisibility(View.INVISIBLE);
			mQRPrivateIV.setVisibility(View.GONE);
		}
		
			
		
	}
}
