package com.android.backups;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Converter
{
	public static byte[] ConvertFileToByteArray(File file)
	{
		FileInputStream inputStream = null;
		byte[] byteFile = new byte[(int)file.length()];
		
		try
		{
			inputStream = new FileInputStream(file);
			inputStream.read(byteFile);
			inputStream.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return byteFile;
	}
}
