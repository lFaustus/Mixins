package com.faustus.mixins;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothTask extends AsyncTask<Void, String, Void> implements Parcelable
{
	BluetoothAdapter btAdapter = null;
	OutputStream outStream = null;
	BluetoothDevice device = null;
	BluetoothSocket btSocket = null;
	Activity activity = null;
	private WeakReference<TextView> weaktxt = null;
	
	private static final String TAG = "BlueToothConnect";
	
	// SPP UUID service
	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// MAC-address of Bluetooth module (you must edit this line)
	private static String address = "98:D3:31:40:0B:46";
		
	public BluetoothTask(Activity activity,TextView txt)
	{
		this.activity = activity;
		weaktxt = new WeakReference<TextView>(txt);
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
		device =  btAdapter.getRemoteDevice(address);
		
		// Two things are needed to make a connection:
	    //   A MAC address, which we got above.
	    //   A Service ID or UUID.  In this case we are using the
	    //     UUID for SPP.
		try {
			btSocket = createBluetoothSocket(device);
		} catch (IOException e) {
			errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
		}
		
		// Discovery is resource intensive. Make sure it isn't going on
		// when you attempt to connect and pass your message.
		btAdapter.cancelDiscovery();
		
		// Establish the connection.  This will block until it connects.
		publishProgress("...Connecting...");
		sleep(1500);
		try
		{
			btSocket.connect();
			publishProgress("....Connection ok...");
			sleep(1500);
		} 
		catch (IOException e)
		{
			try
			{
				btSocket.close();
				publishProgress("....Closing Connection..");
			} 
			catch (IOException e2)
			{
				errorExit("Fatal Error",
						"In onResume() and unable to close socket during connection failure"
								+ e2.getMessage() + ".");
			}
		}
		
		 // Create a data stream so we can talk to server.
	    publishProgress("...Create Socket...");
	    
	    //to be changed later for 2 way communication
	    try {
		      outStream = btSocket.getOutputStream();
		    } catch (IOException e) {
		      errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
		    }
	    
	    
		return null;
	}
	
	

	@Override
	protected void onProgressUpdate(String... values)
	{
			weaktxt.get().setText(values[0]);
	}



	@Override
	protected void onPostExecute(Void result)
	{
		sleep(1500);
		activity.startActivity(new Intent(activity, MainActivity.class).putExtra(TAG, BluetoothTask.this));
		activity.finish();
		
	}
	
	
	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeValue(btAdapter);
		dest.writeValue(outStream);
		dest.writeValue(device);
		dest.writeValue(btSocket);
		
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
			//return false;
		} 
		else
		{
			if (btAdapter.isEnabled())
			{
				//Log.d(TAG, "...Bluetooth ON...");
				publishProgress("...Bluetooth ON...");
				//return true;
			} 
			else
			{
				// Prompt user to turn on Bluetooth
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				activity.startActivityForResult(enableBtIntent, 1);
				//return false;
			}
		}
	}
	
	private void errorExit(String title, String message)
	{
		Toast.makeText(activity, title + " - " + message,
				Toast.LENGTH_LONG).show();
		activity.finish();
	}
	
	private void sleep(final int millisec) 
	{
		try
		{
			Thread.sleep(millisec);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*
	 * 
	 * Parcel
	 * 
	 */
	private BluetoothTask(Parcel parcel)
	{
		btAdapter = (BluetoothAdapter) parcel.readValue(BluetoothAdapter.class.getClassLoader());
		outStream = (OutputStream) parcel.readValue(OutputStream.class.getClassLoader());
		device = (BluetoothDevice) parcel.readValue(BluetoothDevice.class.getClassLoader());
		btSocket = (BluetoothSocket) parcel.readValue(BluetoothSocket.class.getClassLoader());
	}
	
	
	public static final Parcelable.Creator<BluetoothTask> CREATOR = new Creator<BluetoothTask>()
	{
		
		@Override
		public BluetoothTask[] newArray(int size)
		{
			return new BluetoothTask[size];
		}
		
		@Override
		public BluetoothTask createFromParcel(Parcel source)
		{

			return new BluetoothTask(source);
		}
	};


	@Override
	public int describeContents()
	{
		return 0;
	}

	
	
}