package com.faustus.mixins.database;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class LiquorList implements Parcelable
{
	private int id;
	private String name;
	private int Height;
	private int Width;
	private int hashcode;
	private String url;
	private String JSON;
	private String description;
	private JSONObject JSONobj;
	
	public LiquorList(){}
	
	public LiquorList(int id,String name,int width,int height,int hashcode,String url)
	{
		this.id = id;
		this.name = name;
		this.Width = width;
		this.Height = height;
		this.hashcode = hashcode;
		this.url = url;
	}

	public LiquorList(String name, String url,String description)
	{
		this.name = name;
		this.url = url;
		this.description = description;
	}
	
	public LiquorList(JSONObject json)
	{
		this.JSONobj = json;
		try
		{
			this.name = json.getString("liquor_name");
			this.url = json.getString("liquor_url");
			this.description = json.getString("liquor_description");
		} 
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void setJSON(String jSON)
	{
		JSON = jSON;
	}

	public JSONObject getJSON()
	{
		return JSONobj;
	}

	public void setJSON(JSONObject jSON)
	{
		JSONobj = jSON;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public int getHashcode()
	{
		return hashcode;
	}

	public void setHashcode(int hashcode)
	{
		this.hashcode = hashcode;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getHeight()
	{
		return Height;
	}

	public void setHeight(int height)
	{
		Height = height;
	}

	public int getWidth()
	{
		return Width;
	}

	public void setWidth(int width)
	{
		Width = width;
	}
	
	private LiquorList(Parcel parcel)
	{
		
		id = parcel.readInt();
		name = parcel.readString();
		Width = parcel.readInt();
		Height = parcel.readInt();
		url = parcel.readString();
		description = parcel.readString();
		JSON = parcel.readString();
		try
		{
			JSONobj = new JSONObject(JSON);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	public static Parcelable.Creator<LiquorList> Creator = new Creator<LiquorList>()
	{

		@Override
		public LiquorList createFromParcel(Parcel source)
		{
			return new LiquorList(source);
		}

		@Override
		public LiquorList[] newArray(int size)
		{
			// TODO Auto-generated method stub
			return new LiquorList[size];
		}
		
	};

	@Override
	public int describeContents()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		JSON = JSONobj.toString();
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeInt(Width);
		dest.writeInt(Height);
		dest.writeString(url);
		dest.writeString(description);
		dest.writeString(JSON);
		
	}
	
	
}
