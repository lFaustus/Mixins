package com.faustus.mixins;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
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

public class MainActivity extends SherlockFragmentActivity implements
		FlipOnSelectedItemListener, Handler.Callback
{

	FlipOnSelectedItemListener flip = (FlipOnSelectedItemListener) this;
	private static TextView txt;
	private Typeface tf;
	BluetoothAdapter btAdapter = null;
	OutputStream outStream = null;
	BluetoothDevice device = null;
	BluetoothSocket btSocket = null;
	SaveBluetoothConfig saveBTconfig = null;
	Handler handler = new Handler(this);
	
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
		 setContentView(R.layout.root_view);

		/*if (savedInstanceState == null)
		{
			setContentView(R.layout.splashscreen);
			txt = (TextView) findViewById(R.id.loadingtext);
			tf = Typeface.createFromAsset(getAssets(), "segoeui.ttf");
			txt.setTypeface(tf);
			new BluetoothTask(savedInstanceState).execute();

			return;
		}

		setContentView(R.layout.root_view);
		saveBTconfig = (SaveBluetoothConfig) getLastCustomNonConfigurationInstance();
		btAdapter = saveBTconfig.getBluetoothAdapter();
		btSocket = saveBTconfig.getBluetoothSocket();
		outStream = saveBTconfig.getOutStream();
		device = saveBTconfig.getBluetoothDevice();*/
		if (savedInstanceState == null)
		{

			getFragmentManager().beginTransaction()
					.add(R.id.rootview, new Main(), "MainFrag").commit();
		}

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
					.setCustomAnimations(R.animator.card_flip_right_in,
							R.animator.card_flip_right_out,
							R.animator.card_flip_left_in,
							R.animator.card_flip_left_out)
					.replace(R.id.rootview, infofrag, "InfoFrag")
					.addToBackStack(null).commit();

			getFragmentManager().executePendingTransactions();
		}
		else if(fragmentName == "AddNewDrink")
		{
			AddDrinkToDB newDrink = new AddDrinkToDB();
			newDrink.setDataAdapter((DataAdapter)obj);
			getFragmentManager().beginTransaction()
			.setCustomAnimations(R.animator.card_flip_right_in,
			R.animator.card_flip_right_out,
			R.animator.card_flip_left_in,
			R.animator.card_flip_left_out)
			.replace(R.id.rootview, newDrink)
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
			    System.out.println(element);
			    
			}
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

	private void sendData(byte[] msgBuffer)
	{
		try
		{
			outStream.write(msgBuffer);
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

	public class BluetoothTask extends AsyncTask<Void, String, Void>
	{

		Bundle savedInstanceState = null;

		public BluetoothTask(Bundle savedInstanceState)
		{
			this.savedInstanceState = savedInstanceState;
			saveBTconfig = new SaveBluetoothConfig();
			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			else
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		@Override
		protected void onPreExecute()
		{

			btAdapter = BluetoothAdapter.getDefaultAdapter();
			checkBTState();
			
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			device = btAdapter.getRemoteDevice(address);

			// Two things are needed to make a connection:
			// A MAC address, which we got above.
			// A Service ID or UUID. In this case we are using the
			// UUID for SPP.
			try
			{
				btSocket = createBluetoothSocket(device);
			} catch (IOException e)
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
			} catch (IOException e)
			{
				try
				{
					btSocket.close();
					publishProgress("....Closing Connection..");
				} catch (IOException e2)
				{
					errorExit("Fatal Error",
							"In onResume() and unable to close socket during connection failure"
									+ e2.getMessage() + ".");
				}
			}

			// Create a data stream so we can talk to server.
			publishProgress("...Create Socket...");
			sleep(1500);
			publishProgress("...Connected...");
			sleep(1500);

			// to be changed later for 2 way communication
			try
			{
				outStream = btSocket.getOutputStream();
			} catch (IOException e)
			{
				errorExit(
						"Fatal Error",
						"In onResume() and output stream creation failed:"
								+ e.getMessage() + ".");
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(String... values)
		{
			txt.setText(values[0]);

		}

		@Override
		protected void onPostExecute(Void result)
		{
			if (this.savedInstanceState == null)
			{
				saveBTconfig.saveBluetoothAdapter(btAdapter);
				saveBTconfig.saveBluetoothDevice(device);
				saveBTconfig.saveBluetoothSocket(btSocket);
				saveBTconfig.saveOutputStream(outStream);
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

		private void checkBTState()
		{
			// Check for Bluetooth support and then check to make sure it is
			// turned on
			// Emulator doesn't support Bluetooth and will return null
			if (btAdapter == null)
			{
				errorExit("Fatal Error", "Bluetooth not support");
				// return false;
			} else
			{
				if (btAdapter.isEnabled())
				{
					// Log.d(TAG, "...Bluetooth ON...");
					publishProgress("...Bluetooth ON...");
					sleep(1500);
					// return true;
				} else
				{
					// Prompt user to turn on Bluetooth
					Intent enableBtIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, 1);
					// return false;
				}
			}
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