package com.lazooz.lbm;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lazooz.lbm.preference.MySharedPreferences;
import com.lazooz.lbm.utils.BBUncaughtExceptionHandler;


import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;



public class MainZoozActivity extends ActionBarActivity {


	private ImageView mQRPrivateIV, mQRPublicIV;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler( new BBUncaughtExceptionHandler(this));
		setContentView(R.layout.activity_main_zooz);
		
		
		Button scanBtn = (Button)findViewById(R.id.zooz_scan_btn);
		scanBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainZoozActivity.this, CameraQRActivity.class);
				startActivityForResult(intent, 1);
			}
		});

		
		mQRPublicIV = (ImageView) findViewById(R.id.qr_public_iv);
		mQRPrivateIV = (ImageView) findViewById(R.id.qr_private_iv);
		
		displayQR(mQRPublicIV, MySharedPreferences.getInstance().getPublicKey(this));
		displayQR(mQRPrivateIV, MySharedPreferences.getInstance().getPrivateKey(this));
		
		
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	private void displayQR(ImageView iv, String data){
		QRCodeWriter writer = new QRCodeWriter();
	    try {
	        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512);
	        int width = bitMatrix.getWidth();
	        int height = bitMatrix.getHeight();
	        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
	        for (int x = 0; x < width; x++) {
	            for (int y = 0; y < height; y++) {
	                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
	            }
	        }
	        iv.setImageBitmap(bmp);

	    } catch (WriterException e) {
	        e.printStackTrace();
	    }
	}
	
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 1) {
			if ((intent != null)&& (intent.hasExtra("DATA"))){
				String s = intent.getStringExtra("DATA");
				Toast.makeText(this, s,  Toast.LENGTH_LONG).show();
			}
		}
		

          
	}


}
