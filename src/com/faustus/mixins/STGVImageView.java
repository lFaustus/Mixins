package com.faustus.mixins;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

public class STGVImageView extends ImageView
{
	private int mWidth = 0;
	private int mHeight = 0;
	boolean mDownTouch = false;
	private boolean scaleToWidth = false;
	private boolean setCustomImageViewSize = false;
	

	public STGVImageView(Context context)
	{
		super(context);
	}

	public STGVImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public STGVImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public void setHeight(int height)
	{
		mHeight = height;
	}

	public void setWidth(int width)
	{
		mWidth = width;
	}
	
	public void enableCustomImageViewSize(boolean isCustom)
	{
		this.setCustomImageViewSize = isCustom;
	}

	// Listening for the down and up touch events

	/*
	 * @Override public boolean onTouchEvent(MotionEvent event) {
	 * if(event.getAction() == MotionEvent.ACTION_DOWN) {
	 * //startAnimation(btnAnimation); //Log.e("DOWN","DOWN"); }
	 * 
	 * if(event.getAction() == MotionEvent.ACTION_UP) { //clearAnimation();
	 * performClick(); if(flip!=null) { /* if backstack count is 0 it means that
	 * the first fragment which is our front ui is showing
	 * 
	 * isShowingBack =
	 * activity.getSupportFragmentManager().getBackStackEntryCount() > 0;
	 * flip.flip(isShowingBack); } //Log.e("UP","UP"); }
	 * 
	 * if(event.getAction() == MotionEvent.ACTION_MOVE) { //TODO scrollmove
	 * //Log.e("MOVE","MOVE");
	 * 
	 * } else if(event.getAction() == MotionEvent.ACTION_CANCEL) { //TODO
	 * scrollmove //clearAnimation(); //Log.e("CANCEL","CANCEL");
	 * 
	 * 
	 * } return true; }
	 */

	@Override
	public boolean performClick()
	{
		super.performClick();
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		if(setCustomImageViewSize)
		{
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int heightC = width * mHeight / mWidth;
			setMeasuredDimension(width, heightC);
			return;
		}
		
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		/**
		 * if both width and height are set scale width first. modify in future if necessary
		 */
		
		if(widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST){
			scaleToWidth = true;
		}else if(heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST){
			scaleToWidth = false;
		}else throw new IllegalStateException("width or height needs to be set to match_parent or a specific dimension");
		
		if(getDrawable()==null || getDrawable().getIntrinsicWidth()==0 ){
			// nothing to measure
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}else{
			if(scaleToWidth){
				int iw = this.getDrawable().getIntrinsicWidth();
				int ih = this.getDrawable().getIntrinsicHeight();
				int heightC = width*ih/iw;
				if(height > 0)
				if(heightC>height){
					// dont let hegiht be greater then set max
					heightC = height;
					width = heightC*iw/ih;
				}
				
				this.setScaleType(ScaleType.CENTER_CROP);
				setMeasuredDimension(width, heightC);
				
			}else{
				// need to scale to height instead
				int marg = 0;
				if(getParent()!=null){
					if(getParent().getParent()!=null){
						marg+= ((RelativeLayout) getParent().getParent()).getPaddingTop();
						marg+= ((RelativeLayout) getParent().getParent()).getPaddingBottom();
					}
				}
				
				int iw = this.getDrawable().getIntrinsicWidth();
				int ih = this.getDrawable().getIntrinsicHeight();

				width = height*iw/ih;
				height-=marg;
				setMeasuredDimension(width, height);
			}

		}

	}

	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
	}

	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();
	}
}
