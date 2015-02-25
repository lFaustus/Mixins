package com.faustus.mixins.database;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bulletnoid.android.widget.StaggeredGridView.StaggeredGridView;
import com.faustus.mixins.R;
import com.faustus.mixins.STGVImageView;
import com.faustus.mixins.fragments.Main;
import com.maurycyw.lazylist.staggeredgridviewlib.loader.ImageLoader;

public class DataAdapter extends BaseAdapter implements Parcelable
{
	private static Context context;
	private ImageLoader mLoader;
	private ArrayList<LiquorList> pictures;
	private final int SCROLL_STATE_ONLOAD = -1;
	private int SCROLL_STATE = -1;
	private boolean DeleteMode = false;

	public DataAdapter(Context context)
	{
		this.context = context;
		mLoader = new ImageLoader(context);
		pictures = new ArrayList<LiquorList>();
	}

	public void LoadItems()
	{
		
		//pictures.addAll(DataSet.getLiquorsFromDB(context));
		JSONObject JSONLiquor = null;
		for (String str : DataSet.getAllLiquorsFromDB(context))
		{
			try
			{
				JSONLiquor = new JSONObject(str);
				/*pictures.add(new LiquorList(
						JSONLiquor.getString("liquor_name"), JSONLiquor
								.getString("liquor_url"), JSONLiquor
								.getString("liquor_description")));*/
				pictures.add(new LiquorList(JSONLiquor));
			} 
			catch (JSONException e)
			{
				e.printStackTrace();
			}

		}
		
		Log.e("picture array size",pictures.size()+"");
		
	}
	
	
	
	public boolean isDeleteMode()
	{
		return DeleteMode;
	}

	public void setDeleteMode(boolean deleteMode)
	{
		DeleteMode = deleteMode;
	}

	public int getSCROLL_STATE()
	{
		return SCROLL_STATE;
	}

	public void setSCROLL_STATE(int sCROLL_STATE)
	{
		SCROLL_STATE = sCROLL_STATE;
	}

	public ArrayList<LiquorList> getLiquorList()
	{
		return pictures;
	}
	
	public void setLiquorList(ArrayList<LiquorList> liquorlist)
	{
		pictures = liquorlist;
	}
	
	public void clearLiquorList()
	{
		pictures.clear();
	}

	@Override
	public int getCount()
	{
		return pictures == null? 0: pictures.size();
	}

	@Override
	public Object getItem(int position)
	{
		return pictures.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		//System.out.println("getview:" + position + " " + convertView);
		View view = convertView;
		ViewHolder viewholder = null;
		final LiquorList liq= pictures.get(position);
		final int pos = position;
		
		if (view == null)
		{
			view = LayoutInflater.from(context).inflate(R.layout.cell_stgv,
					parent, false);
			viewholder = new ViewHolder(view);
			viewholder.btn.setOnClickListener(new View.OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					Log.e("delete Test",v.getTag().toString());
					DBAdapter dbhelper = new DBAdapter(context);
					dbhelper.deleteData(v.getTag().toString());
					clearLiquorList();
					LoadItems();
					Main.stgv.removeAllViews();
					notifyDataSetChanged();
					Main.updateTile().setAdapter(DataAdapter.this);	
				}
			});
			view.setTag(viewholder);
		} else
		{
			viewholder = (ViewHolder) view.getTag();
		}
		
		if(isDeleteMode())
			viewholder.btn.setVisibility(View.VISIBLE);
		else
			viewholder.btn.setVisibility(View.INVISIBLE);
		
		viewholder.img_content.setImageBitmap(null);
		viewholder.img_content.setTag(liq);
		viewholder.img_label.setText(liq.getName());
		viewholder.btn.setTag(liq.getName());
		if(getSCROLL_STATE() == StaggeredGridView.OnScrollListener.SCROLL_STATE_IDLE || getSCROLL_STATE() == SCROLL_STATE_ONLOAD)
		{
			mLoader.DisplayImage(liq.getUrl(), viewholder.img_content,liq.getName());
			if(((BitmapDrawable)viewholder.img_content.getDrawable()).getBitmap() == null)
				viewholder.img_content.setImageBitmap(new ImageLoader(context).DecodeFromResource(R.drawable.winemartini, 200));
		}
		return view;
	}
	
	static class ViewHolder
	{
		STGVImageView img_content;
		TextView img_label;
		Typeface customFont;
		Button btn;

		public ViewHolder(View v)
		{
			customFont = Typeface.createFromAsset(context.getAssets(), "danielabold.ttf");
			img_content = (STGVImageView) v.findViewById(R.id.img_content);
			img_label = (TextView) v.findViewById(R.id.img_label);
			img_label.setTypeface(customFont);
			btn = (Button)v.findViewById(R.id.deletebutton);
			btn.setTypeface(customFont);
		}

	}
	

	private DataAdapter(Parcel parcel)
	{
		ArrayList<LiquorList> temp = null;
		parcel.readList(temp, ArrayList.class.getClassLoader());
		pictures = temp;
	}
	
	public static final  Parcelable.Creator<DataAdapter> Creator = new Creator<DataAdapter>()
	{
		
		@Override
		public DataAdapter[] newArray(int size)
		{
			return new DataAdapter[size];
		}
		
		@Override
		public DataAdapter createFromParcel(Parcel source)
		{
			return new DataAdapter(source);
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
		dest.writeList(pictures);
	}
}
