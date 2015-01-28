package com.lazooz.lbm.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

	public interface OnScrollChangeListener {
		public void onScrollChanged(int l, int t, int oldl, int oldt);
	}
	
	private OnScrollChangeListener mOnScrollChangeListener;
	
	public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (mOnScrollChangeListener != null)
			mOnScrollChangeListener.onScrollChanged(l, t, oldl, oldt);
/*		 View view = (View) getChildAt(getChildCount()-1);
	        int diff = (view.getBottom()-(getHeight()+getScrollY()));// Calculate the scrolldiff
	        if( diff == 0 ){  // if diff is zero, then the bottom has been reached
	            Log.d(ScrollTest.LOG_TAG, "MyScrollView: Bottom has been reached" );
	        }*/
		super.onScrollChanged(l, t, oldl, oldt);
	}

	public OnScrollChangeListener getOnScrollChangeListener() {
		return mOnScrollChangeListener;
	}

	public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
		mOnScrollChangeListener = onScrollChangeListener;
	}
	
}
