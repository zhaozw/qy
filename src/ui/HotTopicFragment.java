package ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.LogRecord;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import tools.AppContext;
import tools.Logger;
import tools.UIHelper;
import ui.adapter.HotTopicAdapter;
import ui.adapter.TopicListAdapter;
import ui.adapter.TopicOptionAdapter;
import ui.adapter.TopicOptionAdapter.ViewHolder;
import bean.CardIntroEntity;
import bean.Entity;
import bean.FriendCardListEntity;
import bean.FunsEntity;
import bean.PhoneIntroEntity;
import bean.PhoneListEntity;
import bean.RecommendListEntity;
import bean.Result;
import bean.TopicEntity;
import bean.TopicListEntity;
import bean.TopicOptionEntity;
import bean.TopicOptionListEntity;

import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.vikaa.mycontact.R;

import config.AppClient;
import config.CommonValue;
import config.AppClient.ClientCallback;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;

public class HotTopicFragment extends Fragment implements OnItemClickListener, OnScrollListener, OnClickListener{
	private QunTopic activity;
	
//	private ListView optionListView;
	private ListView topicListView;
	
	private TopicOptionAdapter topicOptionAdapter;
	private List<TopicOptionEntity> topicTypes = new ArrayList<TopicOptionEntity>();
	
	private int lvDataState;
	private int currentPage;
	private List<TopicEntity> topicList = new ArrayList<TopicEntity>();
	private HotTopicAdapter topicListAdapter;
	private String ids;

    private ImageView indicatorImageView;
    private Animation indicatorAnimation;

	public static HotTopicFragment newInstance() {
		HotTopicFragment fragment = new HotTopicFragment();
		return fragment;
	}
	
	@Override
	public void onAttach(Activity activity) {
		this.activity = (QunTopic) activity;
		super.onAttach(activity);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        topicOptionAdapter = new TopicOptionAdapter(activity, topicTypes);
        topicListAdapter = new HotTopicAdapter(activity, topicList);
//        String key = String.format("%s-%s", CommonValue.CacheKey.UserTopicOptions, activity.appContext.getLoginUid());
//		TopicOptionListEntity entity = (TopicOptionListEntity) activity.appContext.readObject(key);
//		if(entity != null){
//			ids = "";
//			for (TopicOptionEntity model : entity.options) {
//				ids += "," + model.category_id;
//			}
//			ids = ids.substring(1, ids.length());
//			Logger.i(ids);
//			getTopicListFromCache(ids);
//		}
//		else {
//		    getTopicTypesFromCache();
//		}
//        new Handler().postDelayed(new Runnable(
//            public void run() {
//
//            }
//        ), 2000);
        Handler jumpHandler = new Handler();
        jumpHandler.postDelayed(new Runnable() {
            public void run() {
                getTopicListFromCache("");
            }
        }, 1000);

    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.topic_fragment, container, false);
//    	optionListView = (ListView) view.findViewById(R.id.xlistview);
//    	View header = inflater.inflate(R.layout.index_section, null);
//    	optionListView.addHeaderView(header);
//    	View footer = inflater.inflate(R.layout.topic_option_footer, null);
//    	Button topicOKButton = (Button) footer.findViewById(R.id.topicOKButton);
//    	topicOKButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				submitTopicType();
//			}
//		});
//    	optionListView.addFooterView(footer);
//    	optionListView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//					long arg3) {
//				if (position > 0) {
//					TopicOptionEntity model = topicTypes.get(position-1);
//					model.isChosen = !model.isChosen;
//					topicOptionAdapter.notifyDataSetChanged();
//				}
//			}
//		});
//    	optionListView.setAdapter(topicOptionAdapter);
        indicatorImageView = (ImageView) view.findViewById(R.id.xindicator);
        indicatorAnimation = AnimationUtils.loadAnimation(activity, R.anim.refresh_button_rotation);
        indicatorAnimation.setDuration(500);
        indicatorAnimation.setInterpolator(new Interpolator() {
            private final int frameCount = 10;
            @Override
            public float getInterpolation(float input) {
                return (float)Math.floor(input*frameCount)/frameCount;
            }
        });
    	topicListView = (ListView) view.findViewById(R.id.topiclistview);
    	topicListView.setAdapter(topicListAdapter);
    	topicListView.setOnScrollListener(this);
    	topicListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
            TopicEntity model = topicList.get(position);
            showTopic(model);
			}
		});
//    	String key = String.format("%s-%s", CommonValue.CacheKey.UserTopicOptions, activity.appContext.getLoginUid());
//		TopicOptionListEntity entity = (TopicOptionListEntity) activity.appContext.readObject(key);
//		if(entity != null){
			topicListView.setVisibility(View.VISIBLE);
//			optionListView.setVisibility(View.INVISIBLE);
//		}
//		else {
//			optionListView.setVisibility(View.INVISIBLE);
//			optionListView.setVisibility(View.VISIBLE);
//		}
        return view;
    }
	
	private void getTopicTypesFromCache() {
		String key = String.format("%s-%s", CommonValue.CacheKey.TopicTypes, activity.appContext.getLoginUid());
		TopicOptionListEntity entity = (TopicOptionListEntity) activity.appContext.readObject(key);
		if(entity != null){
			handleTopicTypes(entity);
		}
		activity.loadingPd = UIHelper.showProgress(activity, null, null, true);
		AppClient.getTopicTypes(activity.appContext, new ClientCallback() {
			
			@Override
			public void onSuccess(Entity data) {
				UIHelper.dismissProgress(activity.loadingPd);
				TopicOptionListEntity entity = (TopicOptionListEntity)data;
				switch (entity.getError_code()) {
				case Result.RESULT_OK:
					handleTopicTypes(entity);
					break;
				default:
					break;
				}
			}
			
			@Override
			public void onFailure(String message) {
				UIHelper.dismissProgress(activity.loadingPd);
			}
			
			@Override
			public void onError(Exception e) {
				UIHelper.dismissProgress(activity.loadingPd);
				Crashlytics.logException(e);
			}
		});
	}
	
	private void handleTopicTypes(TopicOptionListEntity entity) {
		topicTypes.clear();
		topicTypes.addAll(entity.options);
		topicOptionAdapter.notifyDataSetChanged();
	}
	
	private void submitTopicType() {
		TopicOptionListEntity types = new TopicOptionListEntity();
		for (TopicOptionEntity model : topicTypes) {
			if (model.isChosen) {
				types.options.add(model);
			}
		}
		if (types.options.isEmpty()) {
			activity.WarningDialog("请至少选择一个话题");
		}
		else {
			activity.appContext.saveObject(types, String.format("%s-%s", CommonValue.CacheKey.UserTopicOptions, activity.appContext.getLoginUid()));
//			optionListView.setVisibility(View.INVISIBLE);
			//显示topicListView
			String ids = "";
			for (TopicOptionEntity model : types.options) {
				ids += "," + model.category_id;
			}
			ids = ids.substring(1, ids.length());
			topicListView.setVisibility(View.VISIBLE);
			currentPage = 1;
			getTopicList(ids, currentPage+"", UIHelper.LISTVIEW_ACTION_INIT);
		}
	}
	
	private void getTopicListFromCache(String id) {
		currentPage = 1;
		String key = String.format("%s-%s", CommonValue.CacheKey.TopicLists, activity.appContext.getLoginUid());
		TopicListEntity entity = (TopicListEntity) activity.appContext.readObject(key);
		if(entity != null){
			handleTopics(entity, UIHelper.LISTVIEW_ACTION_INIT);
		}
		getTopicList(id, currentPage+"", UIHelper.LISTVIEW_ACTION_INIT);
	}
	
	private void getTopicList(String id, String page, final int action) {
//		if (topicList.isEmpty()) {
//			activity.loadingPd = UIHelper.showProgress(activity, null, null, true);
//		}
        if (null != indicatorImageView) {
            indicatorImageView.setVisibility(View.VISIBLE);
            indicatorImageView.startAnimation(indicatorAnimation);
        }
        else {

        }
		AppClient.getTopicList(activity.appContext, id, page, new ClientCallback() {
			
			@Override
			public void onSuccess(Entity data) {
                if (null != indicatorImageView && topicList.isEmpty()) {
                    indicatorImageView.setVisibility(View.INVISIBLE);
                    indicatorImageView.clearAnimation();
                }
//				UIHelper.dismissProgress(activity.loadingPd);
				TopicListEntity entity = (TopicListEntity) data;
				switch (entity.getError_code()) {
				case Result.RESULT_OK:
					handleTopics(entity, action);
					break;

				default:
					break;
				}
				
			}
			
			@Override
			public void onFailure(String message) {
                if (null != indicatorImageView) {
                    indicatorImageView.setVisibility(View.INVISIBLE);
                    indicatorImageView.clearAnimation();
                }
//				UIHelper.dismissProgress(activity.loadingPd);
			}
			
			@Override
			public void onError(Exception e) {
                if (null != indicatorImageView) {
                    indicatorImageView.setVisibility(View.INVISIBLE);
                    indicatorImageView.clearAnimation();
                }
//				UIHelper.dismissProgress(activity.loadingPd);
				Crashlytics.logException(e);
			}
		});
	}
	
	private void handleTopics(TopicListEntity entity, int action) {
		switch (action) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
			topicList.clear();
			topicList.addAll(entity.datas);
			break;

		default:
			topicList.addAll(entity.datas);
			break;
		}
		if(entity.next >= 1){					
			lvDataState = UIHelper.LISTVIEW_DATA_MORE;
		}
		else if (entity.next == -1) {
			lvDataState = UIHelper.LISTVIEW_DATA_FULL;
		}
		topicListAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View convertView, int position, long arg3) {
		Logger.i("a"+ parent.getAdapter());
		if (parent.getAdapter() == topicOptionAdapter) {
			if (position > 0) {
				TopicOptionEntity model = topicTypes.get(position-1);
				model.isChosen = !model.isChosen;
				topicOptionAdapter.notifyDataSetChanged();
			}
		}
	}
	
	private void showTopic(TopicEntity entity) {
		EasyTracker easyTracker = EasyTracker.getInstance(activity);
		easyTracker.send(MapBuilder
	      .createEvent("ui_action",     // Event category (required)
	                   "button_press",  // Event action (required)
	                   "查看话题："+entity.url,   // Event label
	                   null)            // Event value
	      .build()
		);
		Intent intent = new Intent(activity, QYWebView.class);
		intent.putExtra(CommonValue.IndexIntentKeyValue.CreateView, entity.url);
	    startActivityForResult(intent, CommonValue.PhonebookViewUrlRequest.editPhoneview);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (lvDataState != UIHelper.LISTVIEW_DATA_MORE) {
            return;
        }
        if (firstVisibleItem + visibleItemCount >= totalItemCount
                && totalItemCount != 0) {
        	lvDataState = UIHelper.LISTVIEW_DATA_LOADING;
        	currentPage++;
        	getTopicList(ids, currentPage+"", UIHelper.LISTVIEW_ACTION_SCROLL);
        }
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.myTopicButton:
			activity.startActivity(new Intent(activity, MyTopic.class));
			break;
		case R.id.pubTopicButton:
			activity.startActivity(new Intent(activity, CreateTopic.class).putExtra("fun", new FunsEntity()));
			break;
		default:
			break;
		}
	}
}
