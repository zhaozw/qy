package ui;


import java.util.ArrayList;
import java.util.List;

import tools.StringUtils;
import ui.adapter.FindAdFragmentAdapter;
import ui.adapter.FunsGridViewAdapter;
import ui.adapter.QunGridViewAdapter;
import bean.AdsEntity;
import bean.AdsListEntity;
import bean.Entity;
import bean.FunsEntity;
import bean.FunsListEntity;
import bean.QunsEntity;
import bean.QunsListEntity;
import bean.TopicOptionListEntity;

import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.vikaa.mycontact.R;

import config.AppClient;
import config.CommonValue;
import config.AppClient.ClientCallback;
import config.CommonValue.CreateViewUrlAndRequest;
import config.CommonValue.FunsType;
import android.R.string;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class Find extends AppActivity implements OnItemClickListener{
	private List<AdsEntity> ads = new ArrayList<AdsEntity>();
	private FindAdFragmentAdapter adsAdapter;
	
	private GridView qunGridView;
	private List<QunsEntity> quns = new ArrayList<QunsEntity>();
	private QunGridViewAdapter qunAdapter;
	
	private GridView funsGridView;
	private List<FunsEntity> funs = new ArrayList<FunsEntity>();
	private FunsGridViewAdapter funsAdapter;
	
	private TextView tvMessage;
	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(CommonValue.Login_SUCCESS_ACTION);
		registerReceiver(receiver, filter);
		super.onResume();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find);
		initUI();
	}
	
	private void initUI() {
		ViewPager adsViewPager = (ViewPager) findViewById(R.id.viewPager);
		adsAdapter = new FindAdFragmentAdapter(getSupportFragmentManager(), ads);
		adsViewPager.setAdapter(adsAdapter);
		
		qunGridView = (GridView) findViewById(R.id.qunGridView);
		quns.add(new QunsEntity(R.drawable.qun_normal, "1", "群友微友"));
		quns.add(new QunsEntity(R.drawable.qun_school, "3", "同学校友"));
		quns.add(new QunsEntity(R.drawable.qun_business, "5", "行业联盟"));
		quns.add(new QunsEntity(R.drawable.qun_economy, "7", "商业协会"));
		quns.add(new QunsEntity(R.drawable.qun_meeting, "17", "活动会议"));
		quns.add(new QunsEntity(R.drawable.qun_collegue, "14", "公司同事"));
		qunAdapter = new QunGridViewAdapter(this, quns);
		qunGridView.setAdapter(qunAdapter);
		qunGridView.setOnItemClickListener(this);
		
		funsGridView = (GridView) findViewById(R.id.funsGridView);
		funs.addAll(FunsListEntity.parse(this).funs);
		funsAdapter = new FunsGridViewAdapter(this, funs);
		funsGridView.setAdapter(funsAdapter);
		funsGridView.setOnItemClickListener(this);
		getAdsFromCache();
		
		tvMessage = (TextView) findViewById(R.id.messageView);
		if (StringUtils.notEmpty(appContext.getNews())) {
			try {
				if (Integer.valueOf(appContext.getNews()) > 0) {
					tvMessage.setVisibility(View.VISIBLE);
					tvMessage.setText(Integer.valueOf(appContext.getNews())<99?appContext.getNews():"99+");
				}
			}
			catch (Exception e) {
				Crashlytics.logException(e);
			}
		}
	}
	
	public void ButtonClick(View v) {
		switch (v.getId()) {
		case R.id.leftBarButton:
			showNotification();
			tvMessage.setVisibility(View.INVISIBLE);
			break;

		default:
			break;
		}
	}
	
	public void showNotification() {
		EasyTracker easyTracker = EasyTracker.getInstance(this);
		easyTracker.send(MapBuilder
	      .createEvent("ui_action",     // Event category (required)
	                   "button_press",  // Event action (required)
	                   "查看通知："+String.format("%s/message/index", CommonValue.BASE_URL),   // Event label
	                   null)            // Event value
	      .build()
		);
		Intent intent = new Intent(this, QYWebView.class);
		intent.putExtra(CommonValue.IndexIntentKeyValue.CreateView, String.format("%s/message/index", CommonValue.BASE_URL));
		startActivity(intent);
		AppClient.setMessageRead(appContext);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
		if (parent.getAdapter() == qunAdapter) {
			startActivity(new Intent(this, QYWebView.class)
			.putExtra(CommonValue.IndexIntentKeyValue.CreateView, CreateViewUrlAndRequest.ContactCreateUrl+"/id/"+quns.get(position).id));
		}
		if (parent.getAdapter() == funsAdapter) {
			if (funs.get(position).id.equals("20")) {
				startActivity(new Intent(this, CreateTopic.class).putExtra("fun", funs.get(position)));
			}
			else if (funs.get(position).id.equals("19")) {
				startActivity(new Intent(this, QYWebView.class)
				.putExtra(CommonValue.IndexIntentKeyValue.CreateView, CreateViewUrlAndRequest.CardCreateUrl1));
			}
			else {
				startActivity(new Intent(this, CreateActivity.class).putExtra("fun", funs.get(position)));
			}
		}
	}
	
	private void getAdsFromCache() {
		ads.add(new AdsEntity(CommonValue.ADS_TITLE, CommonValue.AD_THUMB+R.drawable.ad_default, CommonValue.AD_LINK));
		adsAdapter.notifyDataSetChanged();
		String key = String.format("%s-%s", CommonValue.CacheKey.ADS, appContext.getLoginUid());
		AdsListEntity entity = (AdsListEntity) appContext.readObject(key);
		if(entity != null){
			ads.clear();
			ads.addAll(entity.ads);
			adsAdapter.notifyDataSetChanged();
		}
		getAds();
	}
	
	private void getAds() {
		AppClient.getSlideAds(appContext, new ClientCallback() {
			@Override
			public void onSuccess(Entity data) {
				AdsListEntity adsList = (AdsListEntity) data;
				ads.clear();
				ads.addAll(adsList.ads);
				adsAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void onFailure(String message) {
				getAds();
			}
			
			@Override
			public void onError(Exception e) {
				getAds();
			}
		});
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (CommonValue.Login_SUCCESS_ACTION.equals(action)) {
				webViewLogin();
			}
		}

	};
	
	private void webViewLogin() {
		WebView webview = (WebView) findViewById(R.id.webview);
		webview.loadUrl(CommonValue.BASE_URL + "/home/app" + "?_sign=" + appContext.getLoginSign())  ;
		webview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			};
		});
	}
}
