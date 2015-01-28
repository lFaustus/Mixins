package com.faustus.mixins.fragments;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bulletnoid.android.widget.StaggeredGridView.StaggeredGridView;
import com.bulletnoid.android.widget.StaggeredGridView.StaggeredGridView.OnLoadmoreListener;
import com.bulletnoid.android.widget.StaggeredGridView.StaggeredGridView.OnScrollListener;
import com.faustus.mixins.R;
import com.faustus.mixins.STGVImageView;
import com.faustus.mixins.myInterface.FlipOnSelectedItemListener;
import com.faustus.mixins.database.DataAdapter;
import com.faustus.mixins.database.DataSet;
import com.faustus.mixins.database.LiquorList;
import com.faustus.navigationdrawer.NavDrawerListAdapter;

public class Main extends Fragment implements OnLoadmoreListener
{

	private StaggeredGridView stgv;
	private DataAdapter mAdapter;
	private NavDrawerListAdapter mNavAdapter;
	private FlipOnSelectedItemListener FlipOnSelect;
	private ListView slideDrawer;
	private DrawerLayout mDrawerLayout;
	private int DRAWER_ITEM_CLICKED = -1;
	
	public Main()
	{
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		Log.e("onAttach", "onAttach");
		FlipOnSelect = (FlipOnSelectedItemListener) activity;

		/*
		 * initialize data and data adapter
		 */
		mAdapter = new DataAdapter(activity);
		mNavAdapter = new NavDrawerListAdapter(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);	
		if (savedInstanceState == null)
		{
			Log.e("onCreate", "onCreate");
			mAdapter.LoadItems();

		} else
		{
			Log.e("onCreate", "onCreateelse");
			WeakReference<DataAdapter> temp = new WeakReference<DataAdapter>((DataAdapter) savedInstanceState.getParcelable("datalist"));
			if (temp != null) 
				mAdapter.setLiquorList(temp.get().getLiquorList());
			//else
				//Log.e("savedInstanceState getparcelable is",String.valueOf(temp.get()));
			//temp = null;
			//savedInstanceState.remove("datalist");
			//savedInstanceState.clear();
			
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.ac_stgv, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		Log.e("onActivityCreated", "onActivityCreated");

		stgv = (StaggeredGridView) getView().findViewById(R.id.stgv);
		
		mDrawerLayout = (DrawerLayout) getView().findViewById(R.id.drawer_layout);
		slideDrawer = (ListView) getView().findViewById(R.id.list_slidermenu);
		slideDrawer.setAdapter(mNavAdapter);
		slideDrawer.setOnItemClickListener(new SlideMenuClickListener());

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			stgv.setColumnCount(1);
			/*if(DataSet.getPictures().size()/2 <10)
				stgv.setColumnCount(1);
			else
				stgv.setColumnCount(2);*/
		} 
		else
		{
			/*if(DataSet.getPictures().size()/2 <10)
				stgv.setColumnCount(2);
			else
				stgv.setColumnCount(3);*/
		}
			
		

		int margin = getResources().getDimensionPixelSize(R.dimen.stgv_margin);

		stgv.setItemMargin(margin);
		stgv.setPadding(margin, 0, margin, 0);

		/*
		 * sets loading-indertiminate when loading more items
		 */
		View footerView = LayoutInflater.from(getActivity()).inflate(
				R.layout.layout_loading_footer, null);
		stgv.setFooterView(footerView);

		/*
		 * sets the adapter
		 */
		stgv.setAdapter(mAdapter);
		
		
		/*
		 * sets onLoadmore Listener load more items when overscroll check below
		 * for implementations
		 */
		stgv.setOnLoadmoreListener(this);
		
		stgv.setOnScrollListener(new OnScrollListener()
		{
			
			@Override
			public void onScrollStateChanged(ViewGroup view, int scrollState)
			{
				if(scrollState == SCROLL_STATE_IDLE)
				{
					stgv.invalidate();
					mAdapter.setSCROLL_STATE(SCROLL_STATE_IDLE);
					Log.w("scroll state","idle");
				}
					
				
			}
			
			@Override
			public void onScroll(ViewGroup view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount)
			{
				// TODO Auto-generated method stub
				
			}
		});

		stgv.setOnItemClickListener(new StaggeredOnClickListener());
		
		mDrawerLayout.setDrawerListener(new DrawerListener()
		{
			
			@Override
			public void onDrawerStateChanged(int arg0)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDrawerSlide(View arg0, float arg1)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDrawerOpened(View arg0)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDrawerClosed(View arg0)
			{
				switch(DRAWER_ITEM_CLICKED)
				{
					case 0:
						stgv.setSelectionToTop();
						FlipOnSelect.onFlipSelectedItem(mAdapter,"AddNewDrink",
								getFragmentManager().getBackStackEntryCount() > 0);
						break;
					case 1:
						break;
				}
				
				DRAWER_ITEM_CLICKED = -1;
			}
		});

	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putParcelable("datalist", mAdapter);
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		this.mAdapter = null;
		Log.e("onDetach", "onDetach");
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		Log.e("onDestroyView", "onDestroyView");
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.e("onDestroy", "onDestroy");
		//stgv.setOnClickListener(null);
		//stgv.setOnScrollListener(null);
		//mDrawerLayout.setDrawerListener(null);
		stgv = null;
		mAdapter = null;
		mNavAdapter = null;
		FlipOnSelect = null;
		slideDrawer = null;
		mDrawerLayout = null;
	}

	@Override
	public void onLoadmore()
	{
		new LoadMoreTask().execute();
	}


	public class LoadMoreTask extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			if(mAdapter!=null)
			{
				mAdapter.LoadItems();
				mAdapter.notifyDataSetChanged();
			}
			super.onPostExecute(result);
		}

	}
	
	/*
	 * Slide menu item click listener
	 */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener
	{
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			if (mDrawerLayout.isDrawerOpen(slideDrawer))
			{
				mDrawerLayout.closeDrawer(slideDrawer);
				
			}
			DRAWER_ITEM_CLICKED = position;
		}
	}
	
	/*
	 *Staggered Grid View Item Click Listener 
	 */
	private class StaggeredOnClickListener implements StaggeredGridView.OnItemClickListener
	{

		@Override
		public void onItemClick(StaggeredGridView staggeredGridViewFlux,
				View view, int position, long id)
		{
			if (FlipOnSelect != null)
			{
				/*
				 * ang view nalang ako gigamit kay direct
				 * kaysa mo point pa ko sa pinakaparent(staggeredgridview) 
				 * nig getChildAt mo null pointer siya kay tungod sa header og footer view
				 * additional children
				 * 
				 * RelativeLayout rl =
				 * (RelativeLayout)staggeredGridViewFlux.
				 * getChildAt(position);
				 */
				stgv.setSelectionToTop();
				WeakReference<RelativeLayout> rl = new WeakReference<RelativeLayout>((RelativeLayout) view);
				WeakReference<STGVImageView> img = new WeakReference<STGVImageView>((STGVImageView) rl.get().findViewById(R.id.img_content));
				WeakReference<LiquorList> liq = new WeakReference<LiquorList>((LiquorList) img.get().getTag());
				//mAdapter.clearLiquorList();
				//mAdapter.LoadItems();
				//mAdapter.notifyDataSetChanged();
				FlipOnSelect.onFlipSelectedItem(liq.get(),"Info",
						getFragmentManager().getBackStackEntryCount() > 0);
				
			}
			
		}
		
	}
}
