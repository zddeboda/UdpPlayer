package com.tele.udpplayer.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.tele.udpplayer.R;
import com.tele.udpplayer.utils.BroadcastUtils;
import com.tele.udpplayer.utils.Constans;

public class HomeFragment extends Fragment implements OnClickListener{
	
	private TextView television;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, null);
		initView(view);
		return view;
	}
	
	private void initView(View view) {
		television = (TextView) view.findViewById(R.id.television);
		television.setOnClickListener(this);
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {
			Constans.isHomeDisplay = false;
		}else {
			Constans.isHomeDisplay = true;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.television:
			BroadcastUtils.sendBro(getActivity(), Constans.BRO_SWITCH_TELEVISION);
			break;

		default:
			break;
		}
		
	}
	
	

}
