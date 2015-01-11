package com.android.backups;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Message;

import com.faustus.mixins.STGVImageView;
import com.maurycyw.lazylist.staggeredgridviewlib.loader.ImageLoader;

public class LoadImageOnIdle extends PauseHandler
	{
		ImageLoader mLoader2;
		public LoadImageOnIdle(Context context)
		{
			mLoader2 = new ImageLoader(context);
		}
		
		@Override
		protected void processMessage(Message msg)
		{
			WeakReference<STGVImageView> img = new WeakReference<STGVImageView>((STGVImageView)msg.obj);
			img.get().setImageBitmap(null);
			Item item = (Item) img.get().getTag();
			mLoader2.DisplayImage(item.getUrl(), img.get(), item.getWidth(), item.getHeight());
			img.get().invalidate();
			
		}
		
	}