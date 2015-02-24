package com.faustus.mixins;

import java.io.IOException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class DispenseTask extends AsyncTask<Void, String, Void>
{
	ProgressDialog prgdialog;
	Activity activity;
	public DispenseTask(Activity activity)
	{
		prgdialog = new ProgressDialog(activity,ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
		prgdialog.setIndeterminate(true);
		prgdialog.setCancelable(false);
		prgdialog.setMessage("Dispensing...");
		prgdialog.setTitle("Please Wait");
		this.activity = activity;
	}
	
	@Override
	protected void onPreExecute()
	{
		prgdialog.show();
	}

	@Override
	protected Void doInBackground(Void... arg0)
	{
		byte[] buffer = new byte[256];
		StringBuilder sb = new StringBuilder();
		int bytes;
		while(true)
		{
			try
			{
				
				bytes = ((MainActivity)activity).getInputStream().read(buffer);
				String strIncom = new String(buffer,0,bytes);	
				sb.append(strIncom);
				Log.e("Arduino Data String",strIncom);			
				if(sb.toString().equals("done"))
				{
					sb.delete(0, sb.length());
					publishProgress("SUCCESS");
					return null;
				}
				
			}
			catch(IOException e)
			{
				Log.e("CATCH DISPENSE",e.toString());
				Log.e("Arduino Data String",sb+" CATCH");
				return null;
			}
		
		}
	}
	
	
	
	@Override
	protected void onProgressUpdate(String... values)
	{
		prgdialog.dismiss();
	}

	@Override
	protected void onPostExecute(Void result)
	{
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}


}
