package com.faustus.mixins.filechooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;

import com.faustus.mixins.R;

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
