package com.android.backups;

import java.util.Vector;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public abstract class PauseHandler extends Handler
{
	final Vector<Message> MessageQueueBuffer = new Vector<Message>();
	private Activity activity = null;
	
	
	public final synchronized void resume(Activity activity)
	{
		this.activity = activity;
		while (MessageQueueBuffer.size() > 0)
		{
			final Message msg = MessageQueueBuffer.elementAt(0);
			MessageQueueBuffer.removeElementAt(0);
			sendMessage(msg);
		}
	}
	
	public final synchronized void pause()
	{
		activity = null;
	}
	
	protected abstract void processMessage(Message msg);
	
	@Override
	public final synchronized void handleMessage(Message msg)
	{
		if(activity != null)
		{
				Message msgCopy = Message.obtain();
				msgCopy.copyFrom(msg);
				MessageQueueBuffer.add(msgCopy);
		}
		else
			processMessage(msg);
		
	}
}
