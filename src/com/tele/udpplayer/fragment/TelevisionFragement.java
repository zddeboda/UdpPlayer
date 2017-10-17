package com.tele.udpplayer.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.tele.udpplayer.R;
import com.tele.udpplayer.activity.VlcVideoPlayActivity;
import com.tele.udpplayer.adapter.BaseGridAdapter;
import com.tele.udpplayer.adapter.PagerViewAdapter;
import com.tele.udpplayer.bean.TeleVisionBean;
import com.tele.udpplayer.utils.ConnectIP;
import com.tele.udpplayer.utils.DataUtils;
import com.tele.udpplayer.utils.LogUtils;

public class TelevisionFragement extends Fragment implements OnClickListener {

	private ViewPager viewpager;
	private ImageView point01, point02;

	private View mViewOne;
	private View mViewTwo;

	private PagerViewAdapter adapter;
	
	private GridView gridView1,gridView2;
	private List<View> mListView = new ArrayList<View>();
	
	private List<TeleVisionBean> mList1 = new ArrayList<TeleVisionBean>();
	private List<TeleVisionBean> mList2 = new ArrayList<TeleVisionBean>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_television, null);
		initView(view);
		initData();
		return view;
	}

	private void initView(View view) {
		viewpager = (ViewPager) view.findViewById(R.id.viewpager);
		point01 = (ImageView) view.findViewById(R.id.point01);
		point02 = (ImageView) view.findViewById(R.id.point02);
		point01.setOnClickListener(this);
		point02.setOnClickListener(this);

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
		viewpager.setAdapter(adapter);
		viewpager.setCurrentItem(0);
		viewpager.addOnPageChangeListener(listener);
		
		List<TeleVisionBean> mList = DataUtils.getListData();
		LogUtils.i("mList.size()=="+mList.size());
		for (int i = 0; i < mList.size(); i++) {
			if (i < 15) {
				mList1.add(mList.get(i));
			}else {
				mList2.add(mList.get(i));
			}
		}
		BaseGridAdapter adapter1 = new BaseGridAdapter(getActivity(), mList1);
		gridView1.setAdapter(adapter1);
		gridView1.setOnItemClickListener(itemlistener1);
		BaseGridAdapter adapter2 = new BaseGridAdapter(getActivity(), mList2);
		gridView2.setAdapter(adapter2);
		gridView2.setOnItemClickListener(itemlistener2);
	}
	
	private OnPageChangeListener listener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int position) {
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
	
	//gridView的点击事件
	private  OnItemClickListener itemlistener1 =  new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position, long offset) {
			//LogUtils.i("mLocation===" + mLocation);
            //startPlayerUdp(ConnectIP.mLocation);
            startPlayerUdp(mList1.get(position).getUrl());
		}
	};
	
	//gridView的点击事件
	private  OnItemClickListener itemlistener2 =  new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position, long offset) {
            //startPlayerUdp("udp://@239.192.0.1:1234");
            startPlayerUdp(mList2.get(position).getUrl());
		}
	};
	
	   private void startPlayerUdp(String path) {
	        Intent intent = new Intent(getActivity(), VlcVideoPlayActivity.class);
	        intent.putExtra("path", path);
	        startActivity(intent);
	    }

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.point01:
			viewpager.setCurrentItem(0);
			break;
		case R.id.point02:
			viewpager.setCurrentItem(1);
			break;
		}

	}

}
