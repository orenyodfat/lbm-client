package com.lazooz.lbm.components;



import com.lazooz.lbm.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MyProgBar extends LinearLayout {

    private ProgressBar mProgBar;
	private ProgressBar mSeekBar;

	public MyProgBar(Context context) {
        this(context, null);
    }

    public MyProgBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.merge_prog_bar, this);
        
        mProgBar = (ProgressBar)findViewById(R.id.progressBar1);
        mSeekBar = (ProgressBar)findViewById(R.id.seekBar1);
        mSeekBar.setEnabled(false);
        
        
        
    }
    
    
    public void setPrgress(int val){
    	mProgBar.setProgress(val);
    	mSeekBar.setProgress(val);
    }
    
    public void setMaxVal(int val){
    	mProgBar.setMax(val);
    	mSeekBar.setMax(val);
    }
}