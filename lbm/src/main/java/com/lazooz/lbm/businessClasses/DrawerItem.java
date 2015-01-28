package com.lazooz.lbm.businessClasses;

public class DrawerItem {
	private String mText;
	private int mImageResource;
	
	public DrawerItem(String theText, int ImageId){
		mImageResource = ImageId;
		mText = theText;
	}
	
	public int getImageResource() {
		return mImageResource;
	}
	public void setImageResource(int imageResource) {
		mImageResource = imageResource;
	}
	public String getText() {
		return mText;
	}
	public void setText(String text) {
		mText = text;
	}
	

}
