package com.faustus.mixins.fragments;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.faustus.mixins.R;
import com.faustus.mixins.STGVImageView;
import com.faustus.mixins.circularseekbar.CircularSeekBar;
import com.faustus.mixins.circularseekbar.CircularSeekBar.OnCircularSeekBarChangeListener;
import com.faustus.mixins.database.DBAdapter;
import com.faustus.mixins.database.DataAdapter;
import com.faustus.mixins.filechooser.FileChooser;
import com.maurycyw.lazylist.staggeredgridviewlib.loader.ImageLoader;

public class AddDrinkToDB extends Fragment
{
	private SeekBar seekbar;
	private CircularSeekBar cSeekbar;
	private TextView seekbarValue;
	private TextView label;
	private Map<String, SeekBar> seekbars = Collections.synchronizedMap(new HashMap<String, SeekBar>());
	private Map<String, CircularSeekBar> cSeekbars = Collections.synchronizedMap(new HashMap<String, CircularSeekBar>());
	private Map<String, TextView> textlabels = Collections.synchronizedMap(new HashMap<String, TextView>());
	//private ArrayList<String> order = new ArrayList<String>();
	private Map<String,String> order = Collections.synchronizedMap(new LinkedHashMap<String, String>());
	private Button button;
	private String[] ingredients = new String[]{"Milk","Choco","Vanilla","Caramel","Cheese","Yogurt"};
	private STGVImageView imgview;
	private String imgviewURI = "";
	private WeakReference<DataAdapter> mAdapter;
	private JSONObject JSONLiquorAttrib;
	private JSONArray JSONLiquorOrder;
	private WeakReference<String[]> tag;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		if((getResources().getDisplayMetrics().widthPixels <= 800 || getResources().getDisplayMetrics().widthPixels <= 480) 
				&& (getResources().getDisplayMetrics().heightPixels <= 480 || getResources().getDisplayMetrics().heightPixels <= 800))
			return inflater.inflate(R.layout.new_drink_to_db, container, false);
		return inflater.inflate(R.layout.new_drink_to_db_large, container, false);
	}
	
	public WeakReference<DataAdapter> getmAdapter()
	{
		return mAdapter;
	}

	public void setDataAdapter(DataAdapter dataAdapter)
	{
		//this.mAdapter = dataAdapter;
		mAdapter = new WeakReference<DataAdapter>(dataAdapter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		
		imgview = (STGVImageView)getView().findViewById(R.id.img_wine);
		imgview.enableCustomImageViewSize(true);
		imgview.setWidth(200);
		imgview.setHeight(200);
		imgview.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent filechooser = new Intent(getActivity(),FileChooser.class);
				startActivityForResult(filechooser, 1);
			}
		});
		button = (Button)getView().findViewById(R.id.addDBButton);
		
		initializeSeekBars();
		initializeLabels();
		button.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				
				JSONLiquorAttrib = new JSONObject();
				try
				{
					JSONLiquorAttrib.put("liquor_description", String.valueOf(textlabels.get(String.valueOf(R.id.label_liquor_description)).getText()));
					JSONLiquorAttrib.put("liquor_url", imgviewURI);
					JSONLiquorAttrib.put("liquor_name", String.valueOf(textlabels.get(String.valueOf(R.id.label_liquor_name)).getText()));
					
					for(Map.Entry<String, String> map: order.entrySet())
					{
						if(!map.getKey().contains(" measurement"))
							JSONLiquorAttrib.put(map.getKey(),seekbars.get(map.getKey()).getProgress());
					}
					
					JSONLiquorAttrib.put("Order", JSONLiquorOrder);
					DBAdapter dbhelper = new DBAdapter(getActivity());
					dbhelper.insertData(JSONLiquorAttrib);
					mAdapter.get().clearLiquorList();
					mAdapter.get().LoadItems();
					mAdapter.get().notifyDataSetChanged();
				} 
				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		Log.i("eN","Success");
		if(requestCode == 1)
		{
			if(resultCode == Activity.RESULT_OK)
			{		
				ImageLoader mLoader = new ImageLoader();
				imgviewURI = data.getStringExtra("choosenImage");
				imgview.setImageBitmap(mLoader.decodeFromFile(new File(imgviewURI), 100));
				mLoader = null;
			}
		}
	}

	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		garbageCollect();
	}
	
	private void initializeSeekBars()
	{
		int counter = 1;
		for(Field f: R.id.class.getFields())
		{
			if(f.getName().startsWith("seekBar"))
			{
				try
				{
					if(getView().findViewById(f.getInt(f.getName())) instanceof SeekBar)
					{
						seekbar = (SeekBar)getView().findViewById(f.getInt(f.getName()));
						seekbar.setTag(new String[]{ingredients[counter-1], "seekBarValue"+counter, String.valueOf(counter)});
						seekbar.setOnSeekBarChangeListener(new SeekBarProgressListener());
						seekbars.put(ingredients[counter-1], seekbar);
						counter ++;
					}
				   if(getView().findViewById(f.getInt(f.getName())) instanceof CircularSeekBar)
					{
						cSeekbar = (CircularSeekBar)getView().findViewById(f.getInt(f.getName()));
						cSeekbar.setTag(new String[]{ingredients[counter-1], "seekBarValue"+counter, String.valueOf(counter)});
						cSeekbar.setOnSeekBarChangeListener(new CircularSeekbarProgressListener());
						cSeekbars.put(ingredients[counter-1], cSeekbar);
						counter ++;
					}
					
					
				}
				catch(IllegalArgumentException e)
				{
					e.printStackTrace();
				} 
				catch (IllegalAccessException e)
				{	
					e.printStackTrace();
				}
			}
			
			
			if(f.getName().startsWith("seekBarValue"))
			{
				try
				{
					seekbarValue = (TextView)getView().findViewById(f.getInt(f.getName()));
					textlabels.put(f.getName(),seekbarValue);
				} 
				catch (IllegalAccessException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (IllegalArgumentException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	private void initializeLabels()
	{
		int counter = 0;
		for(Field f:R.id.class.getFields())
		{
			if(f.getName().startsWith("ingredient"))
			{
				try
				{
					if (getView().findViewById(f.getInt(f.getName())) instanceof TextView)
					{
						label = (TextView) getView().findViewById(
								f.getInt(f.getName()));
						label.setText(ingredients[counter]);
						textlabels.put(f.getName(), label);
						counter ++;
					}
					
				} 
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				} 
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				}
			}
			
			if(f.getName().startsWith("label_"))
			{
				
				try
				{
					if (getView().findViewById(f.getInt(f.getName())) instanceof TextView)
					{
						label = (TextView) getView().findViewById(
								f.getInt(f.getName()));
						label.setOnClickListener(new OnClickTextViewListener());
						textlabels.put(String.valueOf(f.getInt(f.getName())), label);
					}
				} 
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				} 
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				}
				
			}
		}
	}
	
	
	/*
	 * SeekBar on Progress Listener
	 */
	private class SeekBarProgressListener implements OnSeekBarChangeListener
	{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser)
		{
			seekbarValue = textlabels.get(tag.get()[1]);
			
			seekbarValue.setText(String.valueOf(progress) + " ml");
			//RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
			//layoutparams.leftMargin = 500;
			//seekbarValue.setPadding(2000, 2000, 2000, 2000);
			//seekbarValue.setLayoutParams(layoutparams);
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar)
		{
			tag = new WeakReference<String[]>((String[]) seekBar.getTag());
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar)
		{
			
			/*if(!order.contains(tag.get()[0]) && seekBar.getProgress() != 0)
				order.add(tag.get()[0]);
			
			else if(order.contains(tag.get()[0]) && seekBar.getProgress() == 0)
					order.remove(tag.get()[0]);*/
			if(!order.containsKey(tag.get()[0]) && seekBar.getProgress() != 0)
			{
				order.put(tag.get()[0], tag.get()[2]);
				order.put(tag.get()[0]+" measurement", String.valueOf(Character.toChars(seekBar.getProgress()+48)));
				System.out.println(Character.toChars(seekBar.getProgress()+48));
			}
				
			else if(order.containsKey(tag.get()[0]) && seekBar.getProgress() == 0)
			{
				order.remove(tag.get()[0]);
				order.remove(tag.get()[0]+ " measurement");
			}
				
			
			JSONLiquorOrder = new JSONArray(order.values());
			//System.out.println(String.valueOf(order.containsKey(tag.get()[0]))+" " + order.size()+"");
			
		}
		
	}
	
	/*
	 * Circular Seekbar Listener
	 */
	
	private class CircularSeekbarProgressListener implements OnCircularSeekBarChangeListener
	{

		@Override
		public void onProgressChanged(CircularSeekBar circularSeekBar,
				int progress, boolean fromUser)
		{
			seekbarValue = textlabels.get(tag.get()[1]);
			seekbarValue.setText(String.valueOf(progress) + " ml");
			
		}
		
		@Override
		public void onStartTrackingTouch(CircularSeekBar seekBar)
		{
			tag = new WeakReference<String[]>((String[]) seekBar.getTag());
		}

		@Override
		public void onStopTrackingTouch(CircularSeekBar seekBar)
		{
			
			if(!order.containsKey(tag.get()[0]) && seekBar.getProgress() != 0)
			{
				order.put(tag.get()[0], tag.get()[2]);
				order.put(tag.get()[0]+" measurement", String.valueOf(Character.toChars(seekBar.getProgress()+48)));
				System.out.println(Character.toChars(seekBar.getProgress()+48));
			}
				
			else if(order.containsKey(tag.get()[0]) && seekBar.getProgress() == 0)
			{
				order.remove(tag.get()[0]);
				order.remove(tag.get()[0]+ " measurement");
			}
				
			
			JSONLiquorOrder = new JSONArray(order.values());
		}

		
		
	}
	
	/*
	 * TextView On click listener
	 */
	private class OnClickTextViewListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			inputDialog(v);
		}
		
		private void inputDialog(final View TextViewPressed)
		{
			View promptView = getActivity().getLayoutInflater().inflate(R.layout.input_dialog, null);
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
			alertDialogBuilder.setView(promptView);
			
			final TextView title = (TextView)promptView.findViewById(R.id.input_dialog_title);
			//TextView txtview = null;
			final EditText input = (EditText)promptView.findViewById(R.id.input_dialog_edittext);
			
			label = textlabels.get(String.valueOf(TextViewPressed.getId()));
			
			switch(TextViewPressed.getId())
			{
				
				case R.id.label_liquor_name:
					
					title.setText("Name me");
					break;
				
				case R.id.label_liquor_description:
					
					title.setText("Describe Me");
					break;
			}
			
			
			
			
			alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener()
							{

								@Override
								public void onClick(DialogInterface dialog,
										int which)
								{
									  if(label != null)
										label.setText(input.getText());

								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener()
							{

								@Override
								public void onClick(DialogInterface dialog,
										int which)
								{
									dialog.cancel();

								}
							});
			alertDialogBuilder.create().show();
		}
		
	}
	
	private void garbageCollect()
	{
		seekbar = null;
		seekbarValue = null;
		label = null;
		seekbars = null;
		textlabels = null;
		//private ArrayList<String> order = new ArrayList<String>();
		order = null;
		button = null;
		ingredients = null;
		imgview = null;
		imgviewURI = null;
		mAdapter = null;
		JSONLiquorAttrib = null;
		JSONLiquorOrder = null;
		tag = null;
	}
}
