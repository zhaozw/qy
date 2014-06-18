package ui.adapter;

import java.util.List;

import bean.TopicOptionEntity;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vikaa.mycontact.R;

import config.CommonValue;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class TopicOptionAdapter extends BaseAdapter {

	private Context context;
	private List<TopicOptionEntity> datas;
	private LayoutInflater inflater;
	
	public static class ViewHolder {
		TextView topicNameTV;
		ImageView topicCB;
		ImageView topicIcon;
	}
	
	public TopicOptionAdapter(Context context, List<TopicOptionEntity> datas) {
		this.context = context;
		this.datas = datas;
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.topic_option_cell, null);
			viewHolder = new ViewHolder();
			viewHolder.topicIcon = (ImageView) convertView.findViewById(R.id.topicIcon); 
			viewHolder.topicNameTV = (TextView) convertView.findViewById(R.id.topicNameTV);
			viewHolder.topicCB = (ImageView) convertView.findViewById(R.id.topicCB);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		TopicOptionEntity model = datas.get(position);
		ImageLoader.getInstance().displayImage(model.thumb, viewHolder.topicIcon, CommonValue.DisplayOptions.default_options);
		viewHolder.topicNameTV.setText(model.title);
		if (model.isChosen) {
			viewHolder.topicCB.setBackgroundResource(R.drawable.topic_option_selected);
		} else {
			viewHolder.topicCB.setBackgroundResource(R.drawable.topic_option_unselected);
		}
		return convertView;
	}

}
