package com.android.backups;

import java.io.File;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable
{
	private File file;
	private String url;
	private String Filename;
	private int width;
	private int height;

	public Item()
	{

	}

	public Item(File file, String url, String Filename, int width, int height)
	{
		this.file = file;
		this.url = url;
		this.Filename = Filename;
		this.width = width;
		this.height = height;
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getFilename()
	{
		return Filename;
	}

	public void setFilename(String filename)
	{
		Filename = filename;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}
	
	private Item(Parcel parcel)
	{
		file = (File) parcel.readValue(File.class.getClassLoader());
		url = parcel.readString();
		Filename = parcel.readString();
		width = parcel.readInt();
		height = parcel.readInt();
	}
	
	public static Parcelable.Creator<Item> Creator = new Creator<Item>()
	{
		
		@Override
		public Item[] newArray(int size)
		{
			return new Item[size];
		}
		
		@Override
		public Item createFromParcel(Parcel source)
		{
			return new Item(source);
		}
	};

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeValue(file);
		dest.writeString(url);
		dest.writeString(Filename);
		dest.writeInt(width);
		dest.writeInt(height);
	}
}
