package com.faustus.mixins.filechooser;

public class Items implements Comparable<Items>
{
	String name;
	String imgPath;
	
	public Items(String name,String imgPath)
	{
		this.name = name;
		this.imgPath = imgPath;
		
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getImgPath()
	{
		return imgPath;
	}

	public void setImgPath(String imgPath)
	{
		this.imgPath = imgPath;
	}

	@Override
	public int compareTo(Items another)
	{
		if(this.name != null)
			return this.name.toLowerCase().compareTo(another.getName().toLowerCase()); 
		else 
			throw new IllegalArgumentException();
	}

}
