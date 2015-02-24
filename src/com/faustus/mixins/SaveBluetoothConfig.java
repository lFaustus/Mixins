package com.faustus.mixins;

import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class SaveBluetoothConfig
{
	private  BluetoothAdapter btAdapter = null;
	private  OutputStream outStream = null;
	private  InputStream inputStream = null;
	private  BluetoothDevice device = null;
	private  BluetoothSocket btSocket = null;
	
	public  void saveBluetoothAdapter(BluetoothAdapter BTadapter)
	{
		btAdapter = BTadapter;
	}
	
	public  void saveOutputStream(OutputStream outputStream)
	{
		outStream = outputStream;
	}
	
	public void saveBluetoothDevice(BluetoothDevice BTdevice)
	{
		device = BTdevice;
	}
	
	public  void saveBluetoothSocket(BluetoothSocket BTsocket)
	{
		btSocket = BTsocket;
	}

	public void saveInputStream(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}
		
	public InputStream getInputStream()
	{
		return inputStream;
	}

	public  OutputStream getOutStream()
	{
		return outStream;
	}

	public  BluetoothDevice getBluetoothDevice()
	{
		return device;
	}

	public  BluetoothSocket getBluetoothSocket()
	{
		return btSocket;
	}
	
	public  BluetoothAdapter getBluetoothAdapter()
	{
		return btAdapter;
	}	
}
