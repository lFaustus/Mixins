package com.faustus.mixins.filechooser;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.faustus.mixins.R;
import com.faustus.mixins.filechooser.FileAdapter.ViewHolder;

public class FileChooser extends ListActivity
{
	private static String ExternalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
	private static String targetPath = ExternalStoragePath + "/thebartenderdrinks/";
	private static File targetFiles;
	private FileAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		targetFiles = new File(targetPath);
		fill(targetFiles);
		getListView().setRecyclerListener(new RecyclerListener()
		{
			
			@Override
			public void onMovedToScrapHeap(View view)
			{
				WeakReference<ImageView> imageview = new WeakReference<ImageView>((ImageView)view.findViewById(R.id.imageView1_newDrink));
				//final Bitmap bitmap = ((BitmapDrawable)imageview.get().getDrawable()).getBitmap();
				imageview.get().setImageBitmap(null);
				//bitmap.recycle();
				
							}
		});
		
		getListView().setOnScrollListener(new OnScrollListener()
		{
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				//int first = view.getFirstVisiblePosition();
			    //int count = view.getChildCount();
				//if(scrollState == SCROLL_STATE_IDLE || (first + count > mAdapter.getCount())){
				//Log.e(String.valueOf(first+count),String.valueOf(mAdapter.getCount()));
				if(scrollState == SCROLL_STATE_IDLE){
					getListView().invalidateViews();
					Log.e("idle", "idle");
				}
				mAdapter.scrollState(scrollState);
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount)
			{
				// TODO Auto-generated method stub
				
			}
		});
		//getLoaderManager().initLoader(arg0, arg1, arg2)
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		Intent returnIntent = new Intent();
		returnIntent.putExtra("choosenImage", mAdapter.getItem(position).imgPath);
		setResult(RESULT_OK,returnIntent);
		finish();
		
	}
	@Override
	public void finish()
	{
		super.finish();
		mAdapter.notifyDataSetInvalidated();
		mAdapter.clear();
		setListAdapter(null);
		Log.e("FINISH","FINISH");
	}
	
	private void fill(File f)
	{
		File[] pictures = f.listFiles();
		List<Items> item = new ArrayList<Items>();
		for(File file:pictures)
		{
			item.add(new Items(file.getName(),file.getPath()));
		}
		Collections.sort(item);
		mAdapter = new FileAdapter(this, R.layout.filechooser_items, item);
		setListAdapter(mAdapter);
	}
	
}
