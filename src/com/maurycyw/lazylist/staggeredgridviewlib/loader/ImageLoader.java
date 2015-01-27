package com.maurycyw.lazylist.staggeredgridviewlib.loader;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;


/**
 * Using LazyList via https://github.com/thest1/LazyList/tree/master/src/com/fedorvlasov/lazylist
 * for the example since its super lightweight
 * I barely modified this file
 */
public class ImageLoader {
    
    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Handler handler=new Handler();//handler to display images in UI thread
    
    
    public ImageLoader(Context context){
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
    }
    
    public ImageLoader(){}
    
//    final int stub_id= android.R.drawable.alert_dark_frame;
    
    public void DisplayImage(String url, ImageView imageView,int imgWidth, int imgHeight)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null){
        	imageView.setScaleType(ScaleType.FIT_XY);
        	imageView.setImageBitmap(bitmap);
        	
        }
            
        else
        {
            queuePhoto(url, imageView,imgWidth,imgHeight);
            imageView.setScaleType(ScaleType.FIT_XY);
            /*
             * serves as placeholder(image to be shown while waiting for the decoded image to be loaded)
             */
            //imageView.setBackgroundResource(R.drawable.pic4);
             imageView.setImageBitmap(null);
        }
    }
    
    public void DisplayImage(byte[] imgbyte, ImageView imageView,int imgWidth,int imgHeight,String name)
    {
    	//String imageViewidentifier =String.valueOf(name); 
    	imageViews.put(imageView, name);
    	Bitmap bitmap = memoryCache.get(name);
    	if(bitmap!=null)
    	{
    		imageView.setScaleType(ScaleType.FIT_XY);
        	imageView.setImageBitmap(bitmap);
        	Log.d("ENTERED REC DISP","TEST");
    	}
    	else
    	{
    		 queuePhoto(imgbyte, imageView,imgWidth,imgHeight,name);
             imageView.setScaleType(ScaleType.FIT_XY);
             /*
              * serves as placeholder(image to be shown while waiting for the decoded image to be loaded)
              */
             //imageView.setBackgroundResource(R.drawable.pic4);
              imageView.setImageBitmap(null);
    	}
    	
    }
    
    public void DisplayImage(byte[] imgbyte, ImageView imageView,String name)
    {
    	//String imageViewidentifier =String.valueOf(name); 
    	imageViews.put(imageView, name);
    	Bitmap bitmap = memoryCache.get(name);
    	if(bitmap!=null)
    	{
    		imageView.setScaleType(ScaleType.FIT_XY);
        	imageView.setImageBitmap(bitmap);
        	Log.d("ENTERED REC DISP","TEST");
    	}
    	else
    	{
    		 queuePhoto(imgbyte, imageView,name);
             imageView.setScaleType(ScaleType.FIT_XY);
             /*
              * serves as placeholder(image to be shown while waiting for the decoded image to be loaded)
              */
             //imageView.setBackgroundResource(R.drawable.pic4);
              imageView.setImageBitmap(null);
    	}
    	
    }
    
    public void DisplayImage(String url, ImageView imageView,String name)
    {
    	//String imageViewidentifier =String.valueOf(name); 
    	imageViews.put(imageView, url);
    	Bitmap bitmap = memoryCache.get(url);
    	if(bitmap!=null)
    	{
    		imageView.setScaleType(ScaleType.FIT_XY);
        	imageView.setImageBitmap(bitmap);
        	Log.d("Bitmap From","Memory Cache");
    	}
    	else
    	{
    		 queuePhoto(url, imageView,name);
             imageView.setScaleType(ScaleType.FIT_XY);
             /*
              * serves as placeholder(image to be shown while waiting for the decoded image to be loaded)
              */
             //imageView.setBackgroundResource(R.drawable.pic4);
              imageView.setImageBitmap(null);
    	}
    	
    }
    
    
        
    private void queuePhoto(String url, ImageView imageView,int imgWidth,int imgHeight)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView,imgWidth,imgHeight);
        executorService.submit(new PhotosLoader(p));
    }
    
    private void queuePhoto(String url, ImageView imageView,String name)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView, name);
        executorService.submit(new PhotosLoader(p));
    }
    
    private void queuePhoto(byte[] imgbyte, ImageView imageView,int imgWidth,int imgHeight,String name)
    {
    	PhotoToLoad p=new PhotoToLoad(imgbyte, imageView,imgWidth,imgHeight,name);
        executorService.submit(new PhotosLoader(p));
    }
    
    private void queuePhoto(byte[] imgbyte, ImageView imageView,String name)
    {
    	PhotoToLoad p=new PhotoToLoad(imgbyte, imageView,name);
        executorService.submit(new PhotosLoader(p));
    }
    
    private Bitmap getBitmapWithBytes(PhotoToLoad photo)
    {
    	 File f=fileCache.getFile(photo.name);
    	 Bitmap b = decodeFile(f);
    	 
    	 if(b!=null)
    	 {
    		 Log.d("YE","YE");
    		 return b;
    		 
    	 }
    			 
    	 try
         {
         	Bitmap bitmap = null;
         	OutputStream os = new FileOutputStream(f);
            os.write(photo.imgbyte);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
         	
         }catch (Throwable ex){
             ex.printStackTrace();
             if(ex instanceof OutOfMemoryError)
                 memoryCache.clear();
            	 //clearCache();
             return null;
          }
    }
    
    private Bitmap getBitmap(PhotoToLoad photo) 
    {
        File f=fileCache.getFile(photo.url);
        
        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null)
        {
        	Log.d("Bitmap From","SD Cache");
            return b;
        }
        
        
        /*
         * read bitmap from file using absolute path
         * update by flux
         */
        try
        {
        	Bitmap bitmap = null;
        	InputStream is = new FileInputStream(photo.url);
        	OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            //bitmap = Picasso.with(context).load(photo.url).get();
            return bitmap;
        	
        }catch (Throwable ex){
            ex.printStackTrace();
            if(ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
         }
    }
    
    private Bitmap getBitmapFromURL(PhotoToLoad photo)
    {
    	 File f=fileCache.getFile(photo.url);
         
         //from SD cache
         Bitmap b = decodeFile(f);
         if(b!=null)
             return b;
         
         //from web
         try {
             Bitmap bitmap=null;
             URL imageUrl = new URL(photo.url);
             HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
             conn.setConnectTimeout(30000);
             conn.setReadTimeout(30000);
             conn.setInstanceFollowRedirects(true);
             InputStream is=conn.getInputStream();
             OutputStream os = new FileOutputStream(f);
             Utils.CopyStream(is, os);
             os.close();
             bitmap = decodeFile(f);
             return bitmap;
         } catch (Throwable ex){
            ex.printStackTrace();
            if(ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
         }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1=new FileInputStream(f);
            BitmapFactory.decodeStream(stream1,null,o);
            stream1.close();
            
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=100;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            
            if(scale>=2){
            	scale/=2;
            }
               
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            FileInputStream stream2=new FileInputStream(f);
            Bitmap bitmap=BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Bitmap decodeFromByte(byte[] imgbyte,int samplesize)
    {
    	BitmapFactory.Options o = new BitmapFactory.Options();
    	o.inJustDecodeBounds = true;
    	BitmapFactory.decodeByteArray(imgbyte, 0, imgbyte.length,o); 
    	
    	 final int REQUIRED_SIZE=samplesize;
         int width_tmp=o.outWidth, height_tmp=o.outHeight;
         int scale=1;
         while(true){
             if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                 break;
             width_tmp/=2;
             height_tmp/=2;
             scale*=2;
             Log.d("Scale",scale+"");
         }
         
         if(scale>=2){
         	scale/=2;
         }
         
         o.inSampleSize = scale;
         o.inJustDecodeBounds = false;
         return BitmapFactory.decodeByteArray(imgbyte, 0, imgbyte.length,o);    
    }
    
    
    
    public Bitmap decodeFromFile(File f,int samplesize)
    {
    	 try {
             //decode image size
             BitmapFactory.Options o = new BitmapFactory.Options();
             o.inJustDecodeBounds = true;
             FileInputStream stream1=new FileInputStream(f);
             BitmapFactory.decodeStream(stream1,null,o);
             stream1.close();
             
             //Find the correct scale value. It should be the power of 2.
             final int REQUIRED_SIZE=samplesize;
             int width_tmp=o.outWidth, height_tmp=o.outHeight;
             int scale=1;
             while(true){
                 if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                     break;
                 width_tmp/=2;
                 height_tmp/=2;
                 scale*=2;
                 Log.d("Scale",scale+"");
             }
             
             if(scale>=2){
             	scale/=2;
             }
             
             BitmapFactory.Options o2 = new BitmapFactory.Options();
             o2.inSampleSize=scale;
             FileInputStream stream2=new FileInputStream(f);
             Bitmap bitmap=BitmapFactory.decodeStream(stream2, null, o2);
             stream2.close();
             return bitmap;
         } catch (FileNotFoundException e) {
         } 
         catch (IOException e) {
             e.printStackTrace();
         }
         return null;
    }
    
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        private int imgHeight;
        private int imgWidth;
        private byte[] imgbyte;
        private String hashcode;
        private String name;
        
        public PhotoToLoad(String u, ImageView i,int imgWidth,int imgHeight){
            url=u; 
            imageView=i;
            this.imgWidth = imgWidth;
            this.imgHeight = imgHeight;
        }
        
        public PhotoToLoad(byte[] imgbyte,ImageView i,int imgWidth,int imgHeight,String name)
        {
        	imageView=i;
            this.imgWidth = imgWidth;
            this.imgHeight = imgHeight;
            this.imgbyte = imgbyte;
            //this.hashcode = String.valueOf(this.imgbyte.hashCode());
            this.hashcode = String.valueOf(name.hashCode());
            this.name = name;
        }
        
        public PhotoToLoad(byte[] imgbyte,ImageView i,String name)
        {
        	imageView=i;
            this.imgbyte = imgbyte;
            //this.hashcode = String.valueOf(this.imgbyte.hashCode());
            this.hashcode = String.valueOf(name.hashCode());
            this.name = name;
        }
        
        public PhotoToLoad(String url,ImageView i,String name)
        {
        	imageView=i;
        	this.url = url;
            this.hashcode = String.valueOf(name.hashCode());
            this.name = name;
        }
    }
    
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
            try{
                if(imageViewReused(photoToLoad))
                    return;
                Bitmap bmp=getBitmap(photoToLoad);
                //String imageViewidentifier =String.valueOf(photoToLoad.imgbyte.hashCode()); 
                memoryCache.put(photoToLoad.url, bmp);
                if(imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
               
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageDrawable(null);
           
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}
