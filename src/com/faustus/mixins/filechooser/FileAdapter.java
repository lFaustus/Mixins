package com.faustus.mixins.filechooser;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.faustus.mixins.R;
import com.maurycyw.lazylist.staggeredgridviewlib.loader.ImageLoader;

public class FileAdapter extends ArrayAdapter<Items>
{
	private Context context;
	private int layoutid;
	private List<Items> mItems;
	ImageLoader mImageLoader = new ImageLoader(context);
	public FileAdapter(Context context, int resource, List<Items> objects)
	{
		super(context, resource, objects);
		this.context = context;
		mItems = objects;
		layoutid = resource;
	}

	@Override
	public Items getItem(int position)
	{
		return mItems.get(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		ViewHolder viewholder = null;
		
		final Items item = mItems.get(position);
			if(view == null)
			{
				view = LayoutInflater.from(context).inflate(layoutid, parent,false);
				viewholder= new ViewHolder(view);
				view.setTag(viewholder);
			}
			else
				viewholder = (ViewHolder)view.getTag();
			
			viewholder.txtname.setText(item.getName());
			viewholder.txtpath.setText(item.getImgPath());
			mImageLoader.DisplayImage(item.getImgPath(), viewholder.imgview, item.getName());
			
		return view;
	}
	
	class ViewHolder
	{
		ImageView imgview;
		TextView txtname,txtpath;
		Typeface customFont;
		
		public ViewHolder(View v)
		{
			customFont = Typeface.createFromAsset(context.getAssets(), "danielabold.ttf");
			imgview = (ImageView) v.findViewById(R.id.imageView1_newDrink);
			txtname = (TextView) v.findViewById(R.id.imgname);
			txtpath = (TextView) v.findViewById(R.id.imgpath);
		}
	}

}
