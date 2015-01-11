package com.faustus.mixins;

import android.graphics.Bitmap;

public class myInterface{
	
	public interface OnFlipListener
	{
		void flip(boolean isShowingBack);
	}
	
	public interface FlipOnSelectedItemListener
	{
		void onFlipSelectedItem(Object obj,String fragmentName,boolean isShowingBack);
	}
	
}
	
	
	

