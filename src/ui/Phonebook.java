package ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tools.Logger;
import tools.StringUtils;
import tools.UIHelper;
import ui.adapter.PhonebookAdapter;
import bean.CardIntroEntity;
import bean.Entity;
import bean.PhoneIntroEntity;
import bean.PhoneListEntity;
import bean.RecommendListEntity;
import bean.Result;

import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.vikaa.mycontact.R;

import config.AppClient;
import config.CommonValue;
import config.AppClient.ClientCallback;
import config.CommonValue.LianXiRenType;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;
import android.widget.ExpandableListView.OnGroupClickListener;

public class Phonebook extends AppActivity{
	
	private ExpandableListView xlistView;
	private List<PhoneIntroEntity> myQuns = new ArrayList<PhoneIntroEntity>();
	private List<PhoneIntroEntity> comQuns = new ArrayList<PhoneIntroEntity>();
	private List<List<PhoneIntroEntity>> quns = new ArrayList<List<PhoneIntroEntity>>();
	private PhonebookAdapter phoneAdapter;
	
	private MyAsyncQueryHandler asyncQuery;
	private Uri uri ;
	EditText editText;
	
	private int mobileNum = 0;
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phonebook);
		IntentFilter filter = new IntentFilter();
		filter.addAction(CommonValue.PHONEBOOK_CREATE_ACTION);
		filter.addAction(CommonValue.PHONEBOOK_DELETE_ACTION);
		registerReceiver(receiver, filter);
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		initUI();
		getPhoneListFromCache();
		getSquareListFromCache();
	}
	
	private void initUI() {
		editText = (EditText) findViewById(R.id.searchEditView);
		editText.setHint("您共有"+appContext.getDeg2()+"位二度人脉可搜索");
		xlistView = (ExpandableListView)findViewById(R.id.xlistview);
        xlistView.setDividerHeight(0);
        xlistView.setGroupIndicator(null);
        quns.add(myQuns);
        quns.add(comQuns);
		phoneAdapter = new PhonebookAdapter(this, quns);
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
					if (groupPosition == 0) {
						if (childPosition == 0) {
							showMobile();
						}
						else if (childPosition == 1) {
							showFamily();
						}
						else if (childPosition == 2) {
							showFriend();
						}
						else {
							showPhonebook(quns.get(groupPosition).get(childPosition));
						}
					}
					else {
						showPhonebook(quns.get(groupPosition).get(childPosition));
					}
				}
				catch (Exception e) {
					Crashlytics.logException(e);
				}
				return true;
			}
		});
	}
	
	private void showMobile() {
		EasyTracker easyTracker = EasyTracker.getInstance(this);
		easyTracker.send(MapBuilder
	      .createEvent("ui_action",     // Event category (required)
	                   "button_press",  // Event action (required)
	                   "查看手机通讯录",   // Event label
	                   null)            // Event value
	      .build()
		);
		Intent intent = new Intent(this, MobilePhone.class);
		startActivity(intent);
	}
	
	private void showFriend() {
		EasyTracker easyTracker = EasyTracker.getInstance(this);
		easyTracker.send(MapBuilder
	      .createEvent("ui_action",     // Event category (required)
	                   "button_press",  // Event action (required)
	                   "查看微友通讯录",   // Event label
	                   null)            // Event value
	      .build()
		);
		Intent intent = new Intent(this, WeFriendCard.class);
		startActivity(intent);
	}
	
	private void showFamily() {
		EasyTracker easyTracker = EasyTracker.getInstance(this);
		easyTracker.send(MapBuilder
	      .createEvent("ui_action",     // Event category (required)
	                   "button_press",  // Event action (required)
	                   "查看家族通讯录",   // Event label
	                   null)            // Event value
	      .build()
		);
		Intent intent = new Intent(this, FamilyPhonebook.class);
		startActivity(intent);
	}
	
	private void showPhonebook(PhoneIntroEntity entity) {
        Logger.i(entity.link);
        if (StringUtils.empty(entity.link)) {
            return;
        }
		EasyTracker easyTracker = EasyTracker.getInstance(this);
		easyTracker.send(MapBuilder
	      .createEvent("ui_action",     // Event category (required)
	                   "button_press",  // Event action (required)
	                   "查看群友通讯录："+entity.link,   // Event label
	                   null)            // Event value
	      .build()
		);
		Intent intent = new Intent(this, QYWebView.class);
		intent.putExtra(CommonValue.IndexIntentKeyValue.CreateView, entity.link);
	    startActivityForResult(intent, CommonValue.PhonebookViewUrlRequest.editPhoneview);
	}
	
	public void ButtonClick(View v) {
		switch (v.getId()) {
		case R.id.searchEditView:
		case R.id.navbar:
			Intent intent = new Intent(this, WeFriendCardSearch.class);
			intent.putExtra("mobileNum", mobileNum);
            startActivityForResult(intent, 12);
			break;
		}
	}
	
	private void addFixSection() {
		PhoneIntroEntity mobile = new PhoneIntroEntity();
		mobile.title = "手机通讯录";
		mobile.phoneSectionType = CommonValue.PhoneSectionType.MobileSectionType;
		mobile.subtitle ="";
		mobile.logo = "drawable://"+R.drawable.mobile_phone_icon;
		myQuns.add(mobile);
		PhoneIntroEntity family = new PhoneIntroEntity();
		family.title = "家族通讯录";
		family.subtitle ="亲情无间，家族宗亲按谱排序";
		family.logo = "drawable://" + R.drawable.family_phone_icon;
		myQuns.add(family);
		PhoneIntroEntity friend = new PhoneIntroEntity();
		friend.title = "微友通讯录";
		friend.subtitle ="微信、QQ、微博各个平台认识的好友";
		friend.logo = "drawable://" + R.drawable.wefriend_phone_icon;
		myQuns.add(friend);
		asyncQuery.startQuery(0, null, uri, projection, null, null, "sort_key COLLATE LOCALIZED asc");
	}
	
	private void getPhoneListFromCache() {
		String key = String.format("%s-%s", CommonValue.CacheKey.PhoneList, appContext.getLoginUid());
		PhoneListEntity entity = (PhoneListEntity) appContext.readObject(key);
		if(entity != null){
			handlerPhoneSection(entity);
		}
		getPhoneList();
	}
	
	private void getPhoneList() {
//		if (myQuns.isEmpty()) {
			loadingPd = UIHelper.showProgress(this, null, null, true);
//		}
		AppClient.getPhoneList(appContext, new ClientCallback() {
			@Override
			public void onSuccess(Entity data) {
				UIHelper.dismissProgress(loadingPd);
				PhoneListEntity entity = (PhoneListEntity)data;
				switch (entity.getError_code()) {
				case Result.RESULT_OK:
					handlerPhoneSection(entity);
					break;
				case CommonValue.USER_NOT_IN_ERROR:
					forceLogout();
					break;
				default:
//					UIHelper.ToastMessage(getApplicationContext(), entity.getMessage(), Toast.LENGTH_SHORT);
					break;
				}
			}
			
			@Override
			public void onFailure(String message) {
				UIHelper.dismissProgress(loadingPd);
				UIHelper.ToastMessage(getApplicationContext(), message, Toast.LENGTH_SHORT);
			}
			@Override
			public void onError(Exception e) {
				UIHelper.dismissProgress(loadingPd);
				Crashlytics.logException(e);
			}
		});
	}
	
	private void handlerPhoneSection(PhoneListEntity entity) {
		myQuns.clear();
		addFixSection();
		myQuns.addAll(entity.owned);
		myQuns.addAll(entity.joined);
		phoneAdapter.notifyDataSetChanged();
	}
	
	private void getSquareListFromCache() {
		String key = String.format("%s-%s", CommonValue.CacheKey.SquareList, appContext.getLoginUid());
		RecommendListEntity entity = (RecommendListEntity) appContext.readObject(key);
		if(entity != null){
			handlerSquare(entity, UIHelper.LISTVIEW_ACTION_INIT);
		}
		getSquareList(UIHelper.LISTVIEW_ACTION_INIT);
	}
	
	private void getSquareList(final int action) {
		AppClient.getPhoneSquareList(appContext, 1+"", "", new ClientCallback() {
			
			@Override
			public void onSuccess(Entity data) {
				RecommendListEntity entity = (RecommendListEntity)data;
				handlerSquare(entity, action);
			}
			
			@Override
			public void onFailure(String message) {
			}
			
			@Override
			public void onError(Exception e) {
			}
		});
	}
	
	private void handlerSquare(RecommendListEntity entity, int action) {
//		switch (action) {
//		case UIHelper.LISTVIEW_ACTION_INIT:
//		case UIHelper.LISTVIEW_ACTION_REFRESH:
			comQuns.clear();
			comQuns.addAll(entity.squares);
//			break;
//		case UIHelper.LISTVIEW_ACTION_SCROLL:
//			comQuns.addAll(entity.squares);
//			break;
//		}
		phoneAdapter.notifyDataSetChanged();
	}
	
	private String[] projection = {Data.MIMETYPE, Phone.NUMBER, "display_name", "contact_id", "sort_key", "photo_thumb_uri"};
	private final static int MIMETYPE_INDEX = 0;
	private final static int NUMBER_INDEX = 1;
	private final static int NAME_INDEX = 2;
	private final static int ID_INDEX = 3;
	private final static int SORT_INDEX = 4;
	private final static int PHOTO_INDEX = 5;
	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			try {
				myQuns.get(0).subtitle = "共"+cursor.getCount()+"位好友";
				mobileNum = cursor.getCount();
				editText.setHint("您共有"+(Integer.valueOf(appContext.getDeg2()) + cursor.getCount())+"位二度人脉可搜索");
			}
			catch (Exception e) {
				Crashlytics.logException(e);
			}
		}
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (CommonValue.PHONEBOOK_CREATE_ACTION.equals(action) 
					|| CommonValue.PHONEBOOK_DELETE_ACTION.equals(action)) {
				getPhoneList();
			}
		}

	};
}
