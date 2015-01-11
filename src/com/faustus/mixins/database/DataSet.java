package com.faustus.mixins.database;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.android.backups.Converter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class DataSet
{
	private static String ExternalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
	private static String targetPath = ExternalStoragePath + "/thebartenderdrinks/";
	private static File targetFiles = new File(targetPath);
	private static String movedFiles = ExternalStoragePath +"/movedFiles/";
	
	public static ArrayList<LiquorList> getPictures()
	{
		
		File[] mPictures = targetFiles.listFiles();
		
					
		/*
		 * Sorts the mPictures in ascending order
		 */
		Arrays.sort(mPictures,new Comparator<File>()
        		{

        			@Override
        			public int compare(File f1, File f2)
        			{
        				int n1 = extractNumber(f1.getName());
                        int n2 = extractNumber(f2.getName());
                        return n1 - n2;
        			}
        			
        			 private int extractNumber(String name) {
        	                int i = 0;
        	                try {
        	                    int s = name.indexOf('c')+1;
        	                    int e = name.lastIndexOf('.');
        	                    String number = name.substring(s, e);
        	                    i = Integer.parseInt(number);
        	                } catch(Exception e) {
        	                    i = 0; // if filename does not match the format
        	                           // then default to 0
        	                }
        	                return i;
        	            }
        		});
		
			ArrayList<LiquorList> newLiquorList = new ArrayList<LiquorList>();
			for(File f:mPictures)
			{
				LiquorList liq = new LiquorList();
				StringBuilder sb = new StringBuilder(f.getName());
				int start = f.getName().indexOf('.');
				int end = f.getName().length();
                sb.delete(start, end);
				liq.setName(sb.toString());
				liq.setUrl(f.getPath());
				//byte[] imgbyte = Converter.ConvertFileToByteArray(f);
				//liq.setImg(imgbyte);
				newLiquorList.add(liq);
			}
			
			return newLiquorList;
			
	}
	

	
	public static ArrayList<String> getAllLiquorsFromDB(Context context)
	{
		DBAdapter dbhelper = new DBAdapter(context);
		ArrayList<String> temp = new ArrayList<String>();
		temp = dbhelper.getAllLiquors();
		return temp;
	}
	
	
	 public static int width[] = {
	            468,
	            664,
	            536,
	            450,
	            536,
	            498,
	            468,
	            536,
	            666,
	            510,
	            640,
	            398,
	            610,
	            468,
	            486,
	            497,
	            600,
	            600,
	            420,
	            323
	    };
	    public static int height[] = {
	            735,
	            800,
	            551,
	            619,
	            553,
	            750,
	            624,
	            745,
	            800,
	            475,
	            427,
	            900,
	            800,
	            334,
	            658,
	            750,
	            800,
	            450,
	            630,
	            400
	    };
	

}
