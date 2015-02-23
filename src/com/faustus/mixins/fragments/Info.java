package com.faustus.mixins.fragments;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.faustus.mixins.MainActivity;
import com.faustus.mixins.R;
import com.faustus.mixins.database.LiquorList;
import com.maurycyw.lazylist.staggeredgridviewlib.loader.ImageLoader;

public class Info extends Fragment
{
	
	private TextView label, description, extra,text1,text2;
	private ImageView imgview;
	private Button btn;
	//private FlipOnSelectedItemListener FlipOnSelect;
	public Bitmap bmp;
	private String name;
	private String descript;
	private Typeface daniela,segoe;
	private Handler handler;
	private LiquorList liquorList;

	public Info()
	{
		
	}
	
	public Info(LiquorList liquorlist)
	{
		this.liquorList = liquorlist;
	}

	public void setInfo(Bitmap bmp, String name, String descript)
	{

		this.bmp = bmp;
		this.name = name;
		this.descript = descript;
		imgview.setImageBitmap(bmp);
		label.setText(name);
		extra.setText(name);
		description.setText(descript);
		
	}
	


	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		//FlipOnSelect = (FlipOnSelectedItemListener) activity;
		//handler = new Handler((Callback) activity);
		handler = ((MainActivity)activity).getHandler();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.info_view, container, false);
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		daniela = Typeface.createFromAsset(getActivity().getAssets(), "danielabold.ttf");
		segoe = Typeface.createFromAsset(getActivity().getAssets(), "segoeuisl.ttf");
		btn = (Button) getView().findViewById(R.id.button1);
		imgview = (ImageView) getView().findViewById(R.id.img_wine);
		label = (TextView) getView().findViewById(R.id.label_text);
		description = (TextView) getView().findViewById(R.id.description_text);
		extra = (TextView) getView().findViewById(R.id.extra_text);
		text1 = (TextView)getView().findViewById(R.id.text1);
		text2 = (TextView)getView().findViewById(R.id.text2);
		imgview.setImageBitmap(new ImageLoader().decodeFromFile(new File(liquorList.getUrl()), 70));
		
		if(((BitmapDrawable)imgview.getDrawable()).getBitmap() == null)
		{
			imgview.setImageBitmap(new ImageLoader(getActivity().getApplicationContext()).DecodeFromResource(R.drawable.winemartini, 300));
		}
		
		label.setText(liquorList.getName());
		extra.setText(liquorList.getName());
		description.setText(liquorList.getDescription());
		label.setTypeface(daniela);
		extra.setTypeface(daniela);
		description.setTypeface(daniela);
		text1.setTypeface(segoe);
		text2.setTypeface(segoe);
		//btn.setVisibility(View.GONE);
		btn.setTypeface(segoe);
		btn.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Message msg = Message.obtain();
				JSONArray arr = null;
				ByteArrayOutputStream outstream = null;
				DataOutputStream dataOutstream  = null;
				try
				{
					arr = liquorList.getJSON().getJSONArray("Order");
					outstream = new ByteArrayOutputStream();
					dataOutstream = new DataOutputStream(outstream);
					
					for(int i = 0; i < arr.length(); i++)
					{
						try
						{
						dataOutstream.writeUTF(arr.get(i).toString());
						} 
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
					msg.obj = outstream.toByteArray();
					handler.sendMessage(msg);
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				finally
				{
					arr = null;
					outstream = null;
					dataOutstream = null;
					msg = null;
				}
				
				/*try
				{
					WeakReference<JSONArray> JSONarr = new WeakReference<JSONArray>(liquorList.getJSON().getJSONArray("Order"));
					for(int i = 0 ; i < JSONarr.get().length() ; i++)
					{
						System.out.println(JSONarr.get().get(i));
						System.out.println(liquorList.getJSON().get((String) JSONarr.get().get(i)));
					}
				} 
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
		});

	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		garbageCollect();
		Log.e("ENTERED","ONDESTROY");
		
	}

	private void garbageCollect()
	{
		label = null; 
		description = null; 
		extra = null;		
		text1 = null;
		text2 = null;
		imgview = null;
		btn = null;
		bmp = null;
		name = null;
		descript = null;
		daniela = null;
		segoe = null;
		liquorList = null;
	}
	
}
