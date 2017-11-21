package com.tele.udpplayer.adapter;

import java.util.List;

import com.tele.udpplayer.view.MyViewPager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class PagerViewAdapter extends PagerAdapter{

	private List<View> mList;
	
	public PagerViewAdapter(List<View> mList) {
		this.mList = mList;
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		((MyViewPager) container).addView(mList.get(position));
		return mList.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((MyViewPager) container).removeView(mList.get(position));
	}

}
