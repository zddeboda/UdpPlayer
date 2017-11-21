package com.tele.udpplayer.activity;


import java.util.Timer;
import java.util.TimerTask;

import com.tele.udpplayer.R;
import com.tele.udpplayer.fragment.HomeFragment;
import com.tele.udpplayer.fragment.TelevisionFragement;
import com.tele.udpplayer.utils.Constans;
import com.tele.udpplayer.utils.LogUtils;
import com.tele.udpplayer.utils.NetUtils;
import com.tele.udpplayer.utils.TimeUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;


public class MainActivity extends FragmentActivity {

	private HomeFragment homeFragment;
	private FragmentTransaction homeTransaction;

	private TelevisionFragement televisionFragement;
	private FragmentTransaction televisionTransaction;
	
	private MainReceiver receiver;
	
	private TextView tv_netStatus;//网络状态
	private TextView tv_time;//网络状态 
    
	 private static final int REQUEST_EXTERNAL_STORAGE = 1;

	private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };


    public static void verifyStoragePermissions(Activity activity) {
        try {
        //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
        // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initFragment();
		initReceiver();
		initview();
		verifyStoragePermissions(this);
		mHandler.sendEmptyMessage(103);//获取时间
	}
	
	private void initview(){
		tv_netStatus = (TextView) findViewById(R.id.net_status);
		tv_time = (TextView) findViewById(R.id.time);
		showFragment(homeFragment);
	}
	
	private void initReceiver() {
		receiver = new MainReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constans.BRO_SWITCH_HOME);
		intentFilter.addAction(Constans.BRO_SWITCH_TELEVISION);
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(receiver, intentFilter);
	}
	
	private class MainReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtils.i("action == "+intent.getAction());
			if (intent.getAction().equals(Constans.BRO_SWITCH_HOME)) {
				//切换主界面
				showFragment(homeFragment);
			}else if (intent.getAction().equals(Constans.BRO_SWITCH_TELEVISION)) {
				//切换television界面
				showFragment(televisionFragement);
				mHandler.sendEmptyMessageDelayed(105, 100);
			}else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {  
	            boolean netWorkState = NetUtils.getNetWorkState(context);  
	            if (netWorkState) {
					mHandler.sendEmptyMessage(101); 
				}else {
					mHandler.sendEmptyMessage(102); 
				}
	        } 
			
		}
	};
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 101://网络已连接
				tv_netStatus.setText("Network connection");
				break;
			case 102://网络未连接
				tv_netStatus.setText("Network unconnected");
				break;
			case 103:
				 tv_time.setText(TimeUtils.getTime());
				 Timer timer = new Timer();  
			     MyTask myTask = new MyTask();  
			     //timer.schedule(myTask, 1000, 1000);  
			     timer.scheduleAtFixedRate(myTask, 1000, 1000);  
				break;
			case 104:
				tv_time.setText(TimeUtils.getTime());
				break;
			case 105:
				televisionFragement.displayFragement();
				break;
			}
		}
	};
	
	private class MyTask extends TimerTask {  
	    @Override  
	    public void run() {  
	        mHandler.sendEmptyMessage(104); 
	    }  
	}

	// show fragment
	private void showFragment(Fragment fragment) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		hideAllFragment(transaction);
		transaction.show(fragment);
		transaction.commitAllowingStateLoss();
	}

	// init fragment
	private void initFragment() {
		// init home fragment
		homeTransaction = getSupportFragmentManager().beginTransaction();
		if (homeFragment == null) {
			homeFragment = new HomeFragment();
			homeTransaction.add(R.id.main_content_layout, homeFragment);
			homeTransaction.commit();
		}

		// init home fragment
		televisionTransaction = getSupportFragmentManager().beginTransaction();
		if (televisionFragement == null) {
			televisionFragement = new TelevisionFragement();
			televisionTransaction.add(R.id.main_content_layout, televisionFragement);
			televisionTransaction.commit();
		}
	}

	// hide fragment
	private void hideAllFragment(FragmentTransaction transaction) {
		if (homeFragment != null) {
			transaction.hide(homeFragment);
		}
		
		if (televisionFragement != null) {
			transaction.hide(televisionFragement);
		}
	}
	

	// 防止重叠
	@Override
	public void onAttachFragment(Fragment fragment) {
		if (homeFragment == null && fragment instanceof HomeFragment) {
			homeFragment = (HomeFragment) fragment;
		}
		if (televisionFragement == null && fragment instanceof TelevisionFragement) {
			televisionFragement = (TelevisionFragement) fragment;
		}
	}
	
	@Override
	public void onBackPressed() {
		LogUtils.i("onBackPressed");
		if (!Constans.isHomeDisplay) {
			showFragment(homeFragment);
		}else {
			finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

}
