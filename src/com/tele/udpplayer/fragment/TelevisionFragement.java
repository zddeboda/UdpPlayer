package com.tele.udpplayer.fragment;

import java.util.ArrayList;
import java.util.List;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.tele.udpplayer.R;
import com.tele.udpplayer.activity.VlcVideoPlayActivity;
import com.tele.udpplayer.adapter.BaseGridAdapter;
import com.tele.udpplayer.adapter.PagerViewAdapter;
import com.tele.udpplayer.bean.TeleVisionBean;
import com.tele.udpplayer.utils.DataUtils;
import com.tele.udpplayer.utils.LogUtils;
import com.tele.udpplayer.view.MyViewPager;
import com.tele.udpplayer.view.MyViewPager.OnPageChangeListener;

public class TelevisionFragement extends Fragment {

	private MyViewPager viewpager;
	private ImageView point01, point02;

	private View mViewOne;
	private View mViewTwo;

	private PagerViewAdapter adapter;

	private GridView gridView1, gridView2;
	private List<View> mListView = new ArrayList<View>();

	private List<TeleVisionBean> mList1 = new ArrayList<TeleVisionBean>();
	private List<TeleVisionBean> mList2 = new ArrayList<TeleVisionBean>();
	
	private BaseGridAdapter adapter1,adapter2;
	
	private boolean isOnLeft = false;//gridView2选着的图标在左侧，可以左滑动
	private boolean isOnRight = false;//gridView1选着的图标在右侧，可以右滑动
	
	
	private int selected; //选择的position的值 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_television, null);
		initView(view);
		initData();
		return view;
	}

	private void initView(View view) {
		viewpager = (MyViewPager) view.findViewById(R.id.viewpager);
		point01 = (ImageView) view.findViewById(R.id.point01);
		point02 = (ImageView) view.findViewById(R.id.point02);

	}

	// init data
	private void initData() {
		mViewOne = View.inflate(getActivity(), R.layout.layout_view_one, null);
		mViewTwo = View.inflate(getActivity(), R.layout.layout_view_two, null);
		gridView1 = (GridView) mViewOne.findViewById(R.id.gridView1);
		gridView2 = (GridView) mViewTwo.findViewById(R.id.gridView2);

		mListView.add(mViewOne);
		mListView.add(mViewTwo);

		adapter = new PagerViewAdapter(mListView);
		viewpager.setMinPageOffset(0.1f);//设置滑动偏移量
		viewpager.setAdapter(adapter);
		viewpager.setCurrentItem(0);
		viewpager.setOnPageChangeListener(listener);

		List<TeleVisionBean> mList = DataUtils.getListData();
		LogUtils.i("mList.size()==" + mList.size());
		for (int i = 0; i < mList.size(); i++) {
			if (i < 15) {
				mList1.add(mList.get(i));
			} else {
				mList2.add(mList.get(i));
			}
		}
		adapter1 = new BaseGridAdapter(getActivity(), mList1,gridView1);
		gridView1.setAdapter(adapter1);
		gridView1.setOnItemClickListener(itemClicklistener1);
		gridView1.setOnItemSelectedListener(itemSelectedListener1);
		gridView1.setOnKeyListener(onKeyListener1);
		gridView1.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		adapter2 = new BaseGridAdapter(getActivity(), mList2,gridView2);
		gridView2.setAdapter(adapter2);
		gridView2.setOnItemClickListener(itemClicklistener2);
		gridView2.setOnItemSelectedListener(itemSelectedListener2);
		gridView2.setOnKeyListener(onKeyListener2);
		gridView2.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
	}
	
	public void displayFragement() {
		gridView1.requestFocusFromTouch();
		gridView1.setSelection(0);
	}
		
	private OnPageChangeListener listener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			LogUtils.i("OnPageChangeListener position="+position);
			switch (position) {
			case 0:
				point01.setBackgroundResource(R.drawable.pointer_select);
				point02.setBackgroundResource(R.drawable.pointer_normal);
				break;
			case 1:
				point01.setBackgroundResource(R.drawable.pointer_normal);
				point02.setBackgroundResource(R.drawable.pointer_select);
				break;
			}

		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	// gridView1的点击事件
	private OnItemClickListener itemClicklistener1 = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position, long offset) {
				LogUtils.i("itemClicklistener1 position=="+position);
				selected = position;
				gridView1.requestFocusFromTouch();
				gridView1.setSelection(selected);
				//startPlayerUdp(mList1.get(position).getUrl());
		}
	};

	// gridView2的点击事件
	private OnItemClickListener itemClicklistener2 = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position, long offset) {
			LogUtils.i("itemClicklistener2 position=="+position);
			selected = position;
			gridView2.requestFocusFromTouch();
			gridView2.setSelection(selected);
			//startPlayerUdp(mList2.get(position).getUrl());
		}
	};
	
	// gridView1的选择事件
	private OnItemSelectedListener itemSelectedListener1 = new OnItemSelectedListener() {
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
		
		@Override
		public void onItemSelected(AdapterView<?> adapter, View view, int position, long offset) {
			LogUtils.i("itemSelectedListener1 position"+position);
			selected = position;
		}
	};
	
	// gridView2的选择事件
	private OnItemSelectedListener itemSelectedListener2 = new OnItemSelectedListener() {
		
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
		
		@Override
		public void onItemSelected(AdapterView<?> adapter, View view, int position, long offset) {
			LogUtils.i("itemSelectedListener2 position"+position);
			selected = position;
		}
	};
	
	private OnKeyListener onKeyListener1 = new OnKeyListener() {
		@Override
		public boolean onKey(View view, int arg1, KeyEvent keyevent) {
			if (isAllPageRight() && keyevent.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT
					&& keyevent.getAction() == keyevent.ACTION_DOWN) {
				//isOnRight = true;
					viewpager.setCurrentItem(1);
					gridView2.requestFocusFromTouch();  
					gridView2.setSelection(selected-4); 
					point01.setBackgroundResource(R.drawable.pointer_normal);
					point02.setBackgroundResource(R.drawable.pointer_select);
			}else if (keyevent.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER
				&& keyevent.getAction() == keyevent.ACTION_DOWN) {
				startPlayerUdp(mList1.get(selected).getUrl());
			}
			return false;
		}
	};
	
	private OnKeyListener onKeyListener2 = new OnKeyListener() {
		@Override
		public boolean onKey(View view, int arg1, KeyEvent keyevent) {
			if (isAllPageLeft() && keyevent.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT
					&& keyevent.getAction() == keyevent.ACTION_DOWN) {
				viewpager.setCurrentItem(0);
				gridView1.requestFocusFromTouch();  
				gridView1.setSelection(selected+4); 
				point01.setBackgroundResource(R.drawable.pointer_select);
				point02.setBackgroundResource(R.drawable.pointer_normal);
			}else if (keyevent.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER
					&& keyevent.getAction() == keyevent.ACTION_DOWN) {
					startPlayerUdp(mList2.get(selected).getUrl());
			}
			return false;
		}
	};
	
	// 是否在某一页的左边  
    private boolean isAllPageLeft() {
    	//3*5排列 所以第0 5 10 位置为在右侧
    	//这里先写固定值，后续需要做动态的判断，需要优化
    	if (selected == 0 || selected == 5 || selected == 10) {
    		return true ;
		}
    	return false;
    }
    
    // 是否在某一页的右边 
    private boolean isAllPageRight() { 
    	if (selected == 4 || selected == 9 || selected == 14) {
			return true ;
		}
    	return false;
    }

	private void startPlayerUdp(String path) {
		Intent intent = new Intent(getActivity(), VlcVideoPlayActivity.class);
		intent.putExtra("path", path);
		getActivity().startActivity(intent);
	}


}
