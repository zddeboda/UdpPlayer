package com.tele.udpplayer.adapter;

import java.util.List;

import com.tele.udpplayer.R;
import com.tele.udpplayer.bean.TeleVisionBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class BaseGridAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<TeleVisionBean> mList;

    public BaseGridAdapter(Context mContext, List<TeleVisionBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.tele_item, null);
            viewHolder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //L.i("url=="+mList.get(position).getUrl());
        viewHolder.iv_pic.setImageResource(mList.get(position).getPic_id());
       
        return convertView;
    }

    class ViewHolder {
        private ImageView iv_pic;     
        
    }
}
