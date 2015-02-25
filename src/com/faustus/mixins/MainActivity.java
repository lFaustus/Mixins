package com.faustus.mixins;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.faustus.mixins.myInterface.FlipOnSelectedItemListener;
import com.faustus.mixins.database.DataAdapter;
import com.faustus.mixins.database.LiquorList;
import com.faustus.mixins.fragments.AddDrinkToDB;
import com.faustus.mixins.fragments.Info;
import com.faustus.mixins.fragments.Main;
import com.faustus.mixins.fragments.MixOnTheSpot;
import com.maurycyw.lazylist.staggeredgridviewlib.loader.ImageLoader;

public class MainActivity extends SherlockFragmentActivity implements
		FlipOnSelectedItemListener, Handler.Callback
{

	FlipOnSelectedItemListener flip = (FlipOnSelectedItemListener) this;
	private static TextView txt;
	private static Typeface tf;
	private static RelativeLayout relativelayout;
	BluetoothAdapter btAdapter = null;
	private OutputStream outStream = null;
	private InputStream inputStream = null;
	private BluetoothDevice device = null;
	private BluetoothSocket btSocket = null;
	private SaveBluetoothConfig saveBTconfig = null;
	private Handler handler = new Handler(this);
	Bundle savedInstanceStateBundle = null;
	Bitmap bitmap = null;
	//Info infofrag;

	// SPP UUID service
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// MAC-address of Bluetooth module (you must edit this line)
	private static String address = "98:D3:31:40:0B:46";

	private static final String TAG = "BlueToothConnect";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// removes notification bar
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		if (savedInstanceState == null)
		{
			setContentView(R.layout.splashscreen);
			txt = (TextView) findViewById(R.id.loadingtext);
			relativelayout = (RelativeLayout)findViewById(R.id.splash_root);
			bitmap = new ImageLoader(getApplicationContext()).DecodeFromResource(R.drawable.wine_glass, 250);
			Drawable drawable = new BitmapDrawable(bitmap);
		    relativelayout.setBackgroundDrawable(drawable);
		     
			
			tf = Typeface.createFromAsset(getAssets(), "segoeui.ttf");
			txt.setTypeface(tf);
			savedInstanceStateBundle = savedInstanceState;
			checkBTState(savedInstanceStateBundle);

			return;
		}

		setContentView(R.layout.root_view);
		saveBTconfig = (SaveBluetoothConfig) getLastCustomNonConfigurationInstance();
		btAdapter = saveBTconfig.getBluetoothAdapter();
		btSocket = saveBTconfig.getBluetoothSocket();
		outStream = saveBTconfig.getOutStream();
		inputStream = saveBTconfig.getInputStream();
		device = saveBTconfig.getBluetoothDevice();
		/*if (savedInstanceState == null)
		{

			getFragmentManager().beginTransaction()
					.add(R.id.rootview, new Main(), "MainFrag").commit();
		}*/

	}

	@Override
	public Object onRetainCustomNonConfigurationInstance()
	{
		return saveBTconfig;
	}
	
	@Override
	public void onFlipSelectedItem(Object obj,String fragmentName,
			boolean isShowingBack)
	{
		if (isShowingBack)
		{
			getFragmentManager().popBackStack();
			return;
		}
		
		if(fragmentName == "Info")
		{
			Info infofrag = new Info((LiquorList)obj);
			getFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.animator.fade_right_in,
							R.animator.fade_right_out,
							R.animator.fade_left_in,
							R.animator.fade_left_out)
					.replace(R.id.rootview, infofrag, "InfoFrag")
					.addToBackStack(null).commit();

			getFragmentManager().executePendingTransactions();
		}
		else if(fragmentName == "AddNewDrink")
		{
			AddDrinkToDB newDrink = new AddDrinkToDB();
			newDrink.setDataAdapter((DataAdapter)obj);
			getFragmentManager().beginTransaction()
			.setCustomAnimations(R.animator.fade_right_in,
			R.animator.fade_right_out,
			R.animator.fade_left_in,
			R.animator.fade_left_out)
			.replace(R.id.rootview, newDrink)
			.addToBackStack(null).commit();
		}
		else if(fragmentName == "MixOnTheSpot")
		{
			MixOnTheSpot mots = new MixOnTheSpot();
			getFragmentManager().beginTransaction()
			.setCustomAnimations(R.animator.fade_right_in,
			R.animator.fade_right_out,
			R.animator.fade_left_in,
			R.animator.fade_left_out)
			.replace(R.id.rootview, mots)
			.addToBackStack(null).commit();
		}
				
	}

	@Override
	public void onBackPressed()
	{
		if (getFragmentManager().getBackStackEntryCount() > 0)
		{
			flip.onFlipSelectedItem(null, null, getFragmentManager()
					.getBackStackEntryCount() > 0);
			return;
		}
		super.onBackPressed();
		finish();
	}
	
	
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == 1)
		{
			if(resultCode == Activity.RESULT_OK)
			{
				
				new BluetoothTask(savedInstanceStateBundle).execute();
				
			}
		}
	}
	
	
	
	@Override
	protected void onDestroy()
	{
		Log.i("null","null");
		super.onDestroy();
		flip = null;
		btAdapter = null;
		outStream = null;
		inputStream = null;
		device = null;
		btSocket = null;
		saveBTconfig = null;
		savedInstanceStateBundle = null;
	}
	
	@Override
	public void onAttachFragment(android.app.Fragment fragment)
	{
		// TODO Auto-generated method stub
		super.onAttachFragment(fragment);
		if(bitmap !=null)
			bitmap.recycle();
		relativelayout.setBackgroundDrawable(null);
		txt = null;
		tf = null;
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		sendData((byte[])msg.obj);
		ByteArrayInputStream bais = new ByteArrayInputStream((byte[])msg.obj);
		DataInputStream in = new DataInputStream(bais);
		try
		{
			while (in.available() > 0) {
				System.out.println(in.available());
			    String element = in.readUTF();
			    System.out.print(element);
			    
			}
			in.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return true;
	}

	public void SendMessage(Message msg)
	{
		handler.sendMessage(msg);
	}

	public Handler getHandler()
	{
		return handler;
	}
	
	public InputStream getInputStream()
	{
		return inputStream;
	}
	
	private void sendData(byte[] msgBuffer)
	{
		try
		{
			outStream.write(msgBuffer);
			new DispenseTask(MainActivity.this).execute();
			outStream.flush();
		} catch (IOException e)
		{
			String msg = "In onResume() and an exception occurred during write: "
					+ e.getMessage();
			if (address.equals("00:00:00:00:00:00"))
				msg = msg
						+ ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
			msg = msg + ".\n\nCheck that the SPP UUID: " + MY_UUID.toString()
					+ " exists on server.\n\n";

			errorExit("Fatal Error", msg);
		}
	}

	private void errorExit(String title, String message)
	{
		Toast.makeText(getBaseContext(), title + " - " + message,
				Toast.LENGTH_LONG).show();
		finish();
	}
	
	private void checkBTState(Bundle savedInstanceState)
	{
		// Check for Bluetooth support and then check to make sure it is
		// turned on
		// Emulator doesn't support Bluetooth and will return null
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null)
		{
			errorExit("Fatal Error", "Bluetooth not support");
			// return false;
		} 
		else
		{
			if (btAdapter.isEnabled())
				new BluetoothTask(savedInstanceState).execute();
			else
			{
				// Prompt user to turn on Bluetooth
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, 1);
			}
		}
		
	}


	public class BluetoothTask extends AsyncTask<Void, String, Boolean>
	{
		
		public BluetoothTask(Bundle savedInstanceState)
		{
			//this.savedInstanceState = savedInstanceState;
			saveBTconfig = new SaveBluetoothConfig();
			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			else
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		@Override
		protected void onPreExecute()
		{

			//btAdapter = BluetoothAdapter.getDefaultAdapter();
			//checkBTState();
			
		}

		@Override
		protected Boolean doInBackground(Void... params)
		{
			
				publishProgress("...Bluetooth ON...");
				sleep(1500);
				
				device = btAdapter.getRemoteDevice(address);
	
				// Two things are needed to make a connection:
				// A MAC address, which we got above.
				// A Service ID or UUID. In this case we are using the
				// UUID for SPP.
				try
				{
					btSocket = createBluetoothSocket(device);
				} 
				
				catch (IOException e)
				{
					errorExit(
							"Fatal Error",
							"In onResume() and socket create failed: "
									+ e.getMessage() + ".");
				}
	
				// Discovery is resource intensive. Make sure it isn't going on
				// when you attempt to connect and pass your message.
				btAdapter.cancelDiscovery();
	
				// Establish the connection. This will block until it connects.
				publishProgress("...Connecting...");
				sleep(1500);
				
				try
				{
					btSocket.connect();
					publishProgress("....Connection ok...");
					sleep(1500);
					
					// Create a data stream so we can talk to server.
					publishProgress("...Create Socket...");
					sleep(1500);
					
					publishProgress("...Connected...");
					sleep(1500);
		
					// to be changed later for 2 way communication
					try
					{
						outStream = btSocket.getOutputStream();
						inputStream = btSocket.getInputStream();
						return true;
					} 
					
					catch (IOException e)
					{
						errorExit(
								"Fatal Error",
								"In onResume() and output stream creation failed:"
										+ e.getMessage() + ".");
					}
				
				} 
				
				catch (IOException e)
				{
					try
					{
						btSocket.close();
						publishProgress("....Paired Device Not Found...","Please Turn on The Bartender");
						sleep(1500);
						
						//publishProgress("....Closing Connection...");
						//sleep(1500);
						
						//return false;
					} 
					
					catch (IOException e2)
					{
						errorExit("Fatal Error",
								"In onResume() and unable to close socket during connection failure"
										+ e2.getMessage() + ".");
					}
				}
	
			return false;
		}

		@Override
		protected void onProgressUpdate(String... values)
		{
			txt.setText(values[0]);
			
			if(values.length == 2)
				errorExit("Failed to connect to paired Device", values[1]);

		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			if (savedInstanceStateBundle == null && result)
			{
				saveBTconfig.saveBluetoothAdapter(btAdapter);
				saveBTconfig.saveBluetoothDevice(device);
				saveBTconfig.saveBluetoothSocket(btSocket);
				saveBTconfig.saveOutputStream(outStream);
				saveBTconfig.saveInputStream(inputStream);
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
				setContentView(R.layout.root_view);
				getFragmentManager().beginTransaction()
						.add(R.id.rootview, new Main(), "MainFrag").commit();
			}

		}

		private BluetoothSocket createBluetoothSocket(BluetoothDevice device)
				throws IOException
		{
			if (Build.VERSION.SDK_INT >= 10)
			{
				try
				{
					final Method m = device.getClass().getMethod(
							"createInsecureRfcommSocketToServiceRecord",
							new Class[] { UUID.class });
					return (BluetoothSocket) m.invoke(device, MY_UUID);
				} catch (Exception e)
				{
					Log.e(TAG, "Could not create Insecure RFComm Connection", e);
				}
			}
			return device.createRfcommSocketToServiceRecord(MY_UUID);
		}

		
		private void sleep(final int millisec)
		{
			try
			{
				Thread.sleep(millisec);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
