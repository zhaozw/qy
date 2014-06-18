package ui;

import java.util.ArrayList;
import java.util.List;

import tools.Logger;
import tools.UIHelper;
import ui.adapter.PhonebookAdapter;
import ui.adapter.PhonebookAdapter.CellHolder;
import bean.ActivityListEntity;
import bean.Entity;
import bean.PhoneIntroEntity;
import bean.Result;
import bean.TopicEntity;

import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.vikaa.mycontact.R;

import config.AppClient;
import config.CommonValue;
import config.AppClient.ClientCallback;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class ActivityFragment extends Fragment {

	private Assistant activity;
	
	private List<PhoneIntroEntity> myQuns = new ArrayList<PhoneIntroEntity>();
	private List<PhoneIntroEntity> comQuns = new ArrayList<PhoneIntroEntity>();
	private List<List<PhoneIntroEntity>> quns = new ArrayList<List<PhoneIntroEntity>>();
	private PhonebookAdapter phoneAdapter;
	
	public static ActivityFragment newInstance() {
		ActivityFragment fragment = new ActivityFragment();
        return fragment;
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quns.add(myQuns);
        quns.add(comQuns);
		phoneAdapter = new PhonebookAdapter(activity.context, quns);
        getActivityListFromCache();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	Logger.i("b");
    	View view = inflater.inflate(R.layout.activity_fragment, container, false);
    	ExpandableListView xlistView = (ExpandableListView) view.findViewById(R.id.xlistview);
        xlistView.setDividerHeight(0);
        xlistView.setGroupIndicator(null);
		xlistView.setAdapter(phoneAdapter);
		xlistView.expandGroup(0);
		xlistView.expandGroup(1);
		xlistView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2,
					long arg3) {
				return true;
			}
		});
		xlistView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView arg0, View convertView, int groupPosition, int childPosition, long arg4) {
				try {
					PhonebookAdapter.CellHolder cell = (CellHolder) convertView.getTag();
					PhoneIntroEntity model = (PhoneIntroEntity) cell.titleView.getTag();
					showPhonebook(model);
				}
				catch (Exception e) {
					Crashlytics.logException(e);
				}
				return true;
			}
		});
    	
        return view;
    }
    
    private void showPhonebook(PhoneIntroEntity entity) {
		EasyTracker easyTracker = EasyTracker.getInstance(activity);
		easyTracker.send(MapBuilder
	      .createEvent("ui_action",     // Event category (required)
	                   "button_press",  // Event action (required)
	                   "查看活动："+entity.link,   // Event label
	                   null)            // Event value
	      .build()
		);
		Intent intent = new Intent(activity, QYWebView.class);
		intent.putExtra(CommonValue.IndexIntentKeyValue.CreateView, entity.link);
	    startActivityForResult(intent, CommonValue.PhonebookViewUrlRequest.editPhoneview);
	}

    @Override
    public void onAttach(Activity activity) {
    	this.activity = (Assistant) activity;
    	super.onAttach(activity);
    }
    
    private void getActivityListFromCache() {
		String key = String.format("%s-%s", CommonValue.CacheKey.ActivityList, activity.appContext.getLoginUid());
		ActivityListEntity entity = (ActivityListEntity) activity.appContext.readObject(key);
		if(entity != null){
			handlerActivitySection(entity);
		}
		getActivityList();
	}
	
	private void getActivityList() {
//		indicatorImageView.setVisibility(View.VISIBLE);
//    	indicatorImageView.startAnimation(indicatorAnimation);
		AppClient.getActivityList(activity.appContext, new ClientCallback() {
			@Override
			public void onSuccess(Entity data) {
//				indicatorImageView.clearAnimation();
//				indicatorImageView.setVisibility(View.INVISIBLE);
				ActivityListEntity entity = (ActivityListEntity)data;
				switch (entity.getError_code()) {
				case Result.RESULT_OK:
					handlerActivitySection(entity);
					break;
				default:
					UIHelper.ToastMessage(activity, entity.getMessage(), Toast.LENGTH_SHORT);
					break;
				}
			}

			@Override
			public void onFailure(String message) {
//				indicatorImageView.clearAnimation();
//				indicatorImageView.setVisibility(View.INVISIBLE);
				UIHelper.ToastMessage(activity, message, Toast.LENGTH_SHORT);
			}
			@Override
			public void onError(Exception e) {
//				indicatorImageView.clearAnimation();
//				indicatorImageView.setVisibility(View.INVISIBLE);
				Crashlytics.logException(e);
			}
		});
	}
	
	private void handlerActivitySection(ActivityListEntity entity) {
		myQuns.clear();
		myQuns.addAll(entity.owned);
		myQuns.addAll(entity.joined);
		phoneAdapter.notifyDataSetChanged();
	}
}
