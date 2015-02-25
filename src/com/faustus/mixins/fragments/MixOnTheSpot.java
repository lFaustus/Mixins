package com.faustus.mixins.fragments;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.faustus.mixins.MainActivity;
import com.faustus.mixins.R;
import com.faustus.mixins.STGVImageView;
import com.faustus.mixins.circularseekbar.CircularSeekBar;
import com.faustus.mixins.circularseekbar.CircularSeekBar.OnCircularSeekBarChangeListener;
import com.faustus.mixins.database.DBAdapter;
import com.faustus.mixins.filechooser.FileChooser;
import com.maurycyw.lazylist.staggeredgridviewlib.loader.ImageLoader;

public class MixOnTheSpot extends Fragment
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
	private String[] ingredients = new String[]{"Triple Sec","Vodka","Tequila","Lime Juice","Beer","Orange Juice"};
	private STGVImageView imgview;
	private String imgviewURI = "";
	private JSONObject JSONLiquorAttrib;
	private JSONArray JSONLiquorOrder;
	private WeakReference<String[]> tag;
	private Typeface customFont;
	private Handler handler;
	
	
	
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		handler = ((MainActivity)activity).getHandler();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		customFont = Typeface.createFromAsset(getResources().getAssets(), "danielabold.ttf");
		if((getResources().getDisplayMetrics().widthPixels <= 800 || getResources().getDisplayMetrics().widthPixels <= 480) 
				&& (getResources().getDisplayMetrics().heightPixels <= 480 || getResources().getDisplayMetrics().heightPixels <= 800))
			return inflater.inflate(R.layout.new_drink_to_db, container, false);
		return inflater.inflate(R.layout.new_drink_to_db_large, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		imgview = (STGVImageView)getView().findViewById(R.id.img_wine);
		imgview.enableCustomImageViewSize(true);
		imgview.setWidth(200);
		imgview.setHeight(200);
		
		imgview.setImageBitmap(new ImageLoader(getActivity().getApplicationContext())
															.DecodeFromResource(R.drawable.winemartini,300));
		/*imgview.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent filechooser = new Intent(getActivity(),FileChooser.class);
				startActivityForResult(filechooser, 1);
			}
		});*/
		button = (Button)getView().findViewById(R.id.addDBButton);
		button.setTypeface(customFont);
		button.setText("Pour");
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
						{
							if(seekbars.get(map.getKey()) != null)
								JSONLiquorAttrib.put(map.getKey(),seekbars.get(map.getKey()).getProgress());
							else
								JSONLiquorAttrib.put(map.getKey(),cSeekbars.get(map.getKey()).getProgress());
						}
					}
					
					JSONLiquorAttrib.put("Order", JSONLiquorOrder);
					
					Message msg = Message.obtain();
					JSONArray arr = null;
					ByteArrayOutputStream outstream = null;
					DataOutputStream dataOutstream  = null;
					try
					{
						arr = JSONLiquorAttrib.getJSONArray("Order");
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
						outstream.close();
						dataOutstream.close();
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
					catch (IOException e)
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
				} 
				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
		});
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
					if(seekbarValue.getTag() != null){
						if(seekbarValue.getTag().toString().contains("circular"))
						{
							if(seekbarValue.getTag().equals("circular1"))
								counter = 1;
							
							seekbarValue.setText(ingredients[counter-1]);
						}
					}
					seekbarValue.setTypeface(customFont);
					textlabels.put(f.getName(),seekbarValue);
					counter++;
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
						label.setTypeface(customFont);
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
						//label.setOnClickListener(new OnClickTextViewListener());
						label.setTypeface(customFont);
						label.setVisibility(View.GONE);
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
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar)
		{
			tag = new WeakReference<String[]>((String[]) seekBar.getTag());
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar)
		{
			if(!order.containsKey(tag.get()[0]) && seekBar.getProgress() != 0)
			{
				order.put(tag.get()[0], tag.get()[2]);
				order.put(tag.get()[0]+" measurement", String.valueOf(seekBar.getProgress()));
				
			}
				
			else if(order.containsKey(tag.get()[0]) && seekBar.getProgress() >= 0)
			{
				if(seekBar.getProgress()  == 0) {
					order.remove(tag.get()[0]);
					order.remove(tag.get()[0]+ " measurement");
				}
				else  {
					//order.remove(tag.get()[0]+ " measurement");
					order.put(tag.get()[0]+" measurement", String.valueOf(seekBar.getProgress()));
				}
			}
			//Log.i("values",order.values()+"");
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
			if(progress == 0)
			{
				seekbarValue.setText(tag.get()[0]);
				
			}
			else
			{
				seekbarValue.setText(String.valueOf(progress) + " ml");
				
			}
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
				order.put(tag.get()[0]+" measurement", String.valueOf(seekBar.getProgress()));
				//System.out.println(Character.toChars(seekBar.getProgress()+48));
			}
				
			else if(order.containsKey(tag.get()[0]) && seekBar.getProgress() >= 0)
			{
				if(seekBar.getProgress()  == 0) {
					order.remove(tag.get()[0]);
					order.remove(tag.get()[0]+ " measurement");
				}
				else  {
					//order.remove(tag.get()[0]+ " measurement");
					order.put(tag.get()[0]+" measurement", String.valueOf(seekBar.getProgress()));
				}
			}
			
			if(order.isEmpty())
			{
				button.setEnabled(false);
			}
			else
				button.setEnabled(true);
			
			Log.i("values",order.values()+"");
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
		JSONLiquorAttrib = null;
		JSONLiquorOrder = null;
		tag = null;
	}
	

}
