package com.lazooz.lbm;


import com.lazooz.lbm.utils.Utils;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

public class RegistrationActivity extends ActionBarActivity {
	
	
	private Button mRegBtn;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_registration);

		mRegBtn = (Button)findViewById(R.id.reg_reg_btn);
		mRegBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				ApManager.configApState(RegistrationActivity.this);
				
				Utils.getCellId(RegistrationActivity.this);
				WifiTracker wt = new WifiTracker(RegistrationActivity.this);
				wt.scan();
				BluetoothTracker bt = new BluetoothTracker(RegistrationActivity.this);
				bt.scan();
				
				
				
				
				final View addView = getLayoutInflater().inflate(R.layout.activation_input, null);
				TextView inText = (TextView)addView.findViewById(R.id.activation_in_text);
				inText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
	        	Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
	        	//Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
	        	builder.setTitle(getString(R.string.reg_input_num_title));
	        	builder.setMessage(getString(R.string.reg_input_num_body));
	        	builder.setView(addView);
	        	builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int whichButton) {
	                	TextView t = (TextView) addView.findViewById(R.id.activation_in_text);
	                	String regNum = t.getText().toString();
	                	dialog.cancel();
	                }
	        	
	            });
	        	
	        	builder.setNegativeButton(getString(android.R.string.cancel), null);
	        	builder.show().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);			
				
				
			}
		});
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
