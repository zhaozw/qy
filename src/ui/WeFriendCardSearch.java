package ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bean.CardIntroEntity;
import bean.Entity;
import bean.FriendCardListEntity;
import bean.Result;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.vikaa.wecontact.R;

import config.AppClient;
import config.CommonValue;
import config.CommonValue.LianXiRenType;
import config.QYRestClient;
import config.AppClient.ClientCallback;
import db.manager.WeFriendManager;
import db.manager.WeFriendManager.DBCallback;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView.OnEditorActionListener;
import tools.AppManager;
import tools.Logger;
import tools.StringUtils;
import tools.UIHelper;
import ui.adapter.FriendCardSearchAdapter;
import ui.adapter.MyCardAdapter;

public class WeFriendCardSearch  extends AppActivity implements OnScrollListener, OnEditorActionListener, OnRefreshListener{
	private int lvDataState;
	private int currentPage;
	private ProgressDialog loadingPd;
	private List<CardIntroEntity> tempMobiles = new ArrayList<CardIntroEntity>(); 
	private List<CardIntroEntity> mobiles = new ArrayList<CardIntroEntity>();
	private List<CardIntroEntity> bilaterals = new ArrayList<CardIntroEntity>();
	private List<CardIntroEntity> networks = new ArrayList<CardIntroEntity>();
	
	private EditText editText;
	private Button searchDeleteButton;
	
	private String keyword;
	
	private ExpandableListView xlistView;
	private List<List<CardIntroEntity>> contactors = new ArrayList<List<CardIntroEntity>>();
	private FriendCardSearchAdapter mBilateralAdapter;
	
	private MyAsyncQueryHandler asyncQuery;
	private Uri uri ;
	private List<String> contactids = new ArrayList<String>();
	
	private SwipeRefreshLayout swipeLayout;
	private TextView noResultTV;
	private int mobileNum;
	@Override
	public void onStop() {
		setResult(RESULT_OK);
	    super.onStop();
	}
	  
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchrenmai);
		mobileNum = getIntent().getIntExtra("mobileNum", 0);
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		initUI();
		currentPage = 1;
		keyword = "";
		
	}
	
	private void initUI() {
		noResultTV = (TextView) findViewById(R.id.noting_view);
		editText = (EditText) findViewById(R.id.searchEditView);
		try {
			editText.setHint("您共有"+(Integer.valueOf(appContext.getDeg2()) + mobileNum)+"位二度人脉可搜索");
		}
		catch (Exception e) {
			Logger.i(e);
		}
		editText.setOnEditorActionListener(this);
		editText.addTextChangedListener(TWPN);
		searchDeleteButton = (Button) findViewById(R.id.searchDeleteButton);

		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.xrefresh);
		swipeLayout.setOnRefreshListener(this);
	    swipeLayout.setColorScheme(android.R.color.holo_blue_bright, 
	            android.R.color.holo_green_light, 
	            android.R.color.holo_orange_light, 
	            android.R.color.holo_red_light);
		
		xlistView = (ExpandableListView)findViewById(R.id.xlistview);
        xlistView.setDividerHeight(0);
        xlistView.setGroupIndicator(null);
        xlistView.setOnScrollListener(this);
        contactors.add(mobiles);
        contactors.add(bilaterals);
        contactors.add(networks);
		mBilateralAdapter = new FriendCardSearchAdapter(this, contactors);
		xlistView.setAdapter(mBilateralAdapter);
		xlistView.expandGroup(0);
		xlistView.expandGroup(1);
		xlistView.expandGroup(2);
		xlistView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2,
					long arg3) {
				return true;
			}
		});
		
	}
	
	public void ButtonClick(View v) {
		switch (v.getId()) {
		case R.id.leftBarButton:
			closeInput();
			AppManager.getAppManager().finishActivity(this);
			break;
		case R.id.searchEditView:
			editText.setCursorVisible(true);
			break;
		case R.id.searchDeleteButton:
			editText.setText("");
			editText.setCursorVisible(false);
			searchDeleteButton.setVisibility(View.INVISIBLE);
			closeInput();
			break;

        case R.id.btnCancel:
            closeInput();
            AppManager.getAppManager().finishActivity(this);
            break;
		}
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
		/**
		 * 查询结束的回调函数
		 */
		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//			tempMobiles.clear();
//			mobiles.clear();
//			mBilateralAdapter.notifyDataSetChanged();
//			new MobileTask().execute(cursor);
			tempMobiles.clear();
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				contactids.clear();
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String mimetype = cursor.getString(MIMETYPE_INDEX);
					if (Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {
						CardIntroEntity ce = new CardIntroEntity();
						ce.realname = cursor.getString(NAME_INDEX);
						ce.phone = cursor.getString(NUMBER_INDEX);
						ce.code = ""+cursor.getInt(ID_INDEX);
						ce.pinyin = cursor.getString(SORT_INDEX);
						ce.cardSectionType = LianXiRenType.mobile;
						ce.avatar = cursor.getString(PHOTO_INDEX);
						ce.department = "来自手机通讯录";
						ce.position = "";
						ce.py = StringUtils.getAlpha(ce.pinyin);
						if (!contactids.contains(ce.code)) {
							tempMobiles.add(ce);
							contactids.add(ce.code);
						}
					}
				}
			}
			mobiles.clear();
			if (StringUtils.notEmpty(keyword)) {
				mobiles.addAll(tempMobiles);
			}
			mBilateralAdapter.notifyDataSetChanged();
			resultCheck();
		}
	}
	
	private void resultCheck() {
		if (mobiles.isEmpty() && bilaterals.isEmpty() && networks.isEmpty()) {
			noResultTV.setVisibility(View.VISIBLE);
		}
		else {
			noResultTV.setVisibility(View.GONE);
		}
	}
	
//	private class MobileTask extends AsyncTask<Cursor, Void, Void> {
//
//		@Override
//		protected Void doInBackground(Cursor... params) {
//			Cursor cursor = params[0];
//			tempMobiles.clear();
//			if (cursor != null && cursor.getCount() > 0) {
//				cursor.moveToFirst();
//				contactids.clear();
//				for (int i = 0; i < cursor.getCount(); i++) {
//					cursor.moveToPosition(i);
//					String mimetype = cursor.getString(MIMETYPE_INDEX);
//					if (Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {
//						CardIntroEntity ce = new CardIntroEntity();
//						ce.realname = cursor.getString(NAME_INDEX);
//						ce.phone = cursor.getString(NUMBER_INDEX);
//						ce.code = ""+cursor.getInt(ID_INDEX);
//						ce.pinyin = cursor.getString(SORT_INDEX);
//						ce.cardSectionType = LianXiRenType.mobile;
//						ce.avatar = cursor.getString(PHOTO_INDEX);
//						ce.department = "来自手机通讯录";
//						ce.position = "";
//						ce.py = StringUtils.getAlpha(ce.pinyin);
//						if (!contactids.contains(ce.code)) {
//							tempMobiles.add(ce);
//							contactids.add(ce.code);
//						}
//					}
//				}
//			}
//			return null;
//		}
//		@Override
//		protected void onPostExecute(Void result) {
//			mobiles.clear();
//			if (StringUtils.notEmpty(keyword)) {
//				mobiles.addAll(tempMobiles);
//			}
//			mBilateralAdapter.notifyDataSetChanged();
//		}
//	}
	
	private void queryWeFriendsFromDB(final String key) {
		final Handler handler1 = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				bilaterals.clear();
				bilaterals.addAll((List<CardIntroEntity>)msg.obj);
				mBilateralAdapter.notifyDataSetChanged();
				swipeLayout.setRefreshing(false);
				resultCheck();
			}
		};
		swipeLayout.setRefreshing(true);
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		singleThreadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				WeFriendManager.getInstance(WeFriendCardSearch.this).searchWeFriendsByKeyword(keyword, new DBCallback() {
					@Override
					public void onQueryComplete(Object object, String key) {
						if (key.equals(keyword)) {
							Logger.i("a");
							Message msg = new Message();
							msg.obj = (List<CardIntroEntity>)object;
							handler1.sendMessage(msg);
						}
						else {
							Logger.i("b");
						}
					}
				});
			}
		});
	}
	
	private void searchFriendCard(int page, final String kw, String count, final int action) {
		if (!appContext.isNetworkConnected()) {
			return;
		}
		swipeLayout.setRefreshing(true);
		AppClient.searchFriendCard(this, appContext, page+"", kw, count, new ClientCallback() {
			@Override
			public void onSuccess(Entity data) {
				UIHelper.dismissProgress(loadingPd);
				FriendCardListEntity entity = (FriendCardListEntity)data;
				switch (entity.getError_code()) {
				case Result.RESULT_OK:
					if (kw.equals(keyword)) {
						handleSearchFriends(entity, action);
					}
					break;
				default:
					break;
				}
				swipeLayout.setRefreshing(false);
			}
			
			@Override
			public void onFailure(String message) {
				swipeLayout.setRefreshing(false);
			}
			
			@Override
			public void onError(Exception e) {
				swipeLayout.setRefreshing(false);
			}
		});
	}
	
	private synchronized void handleSearchFriends(FriendCardListEntity entity, int action) {
		List<CardIntroEntity> temp = new ArrayList<CardIntroEntity>();
		temp.addAll(entity.u);
		for (CardIntroEntity card : entity.u) {
			if (WeFriendManager.getInstance(this).isOpenidExist(card.openid)) {
				temp.remove(card);
			}
		}
		switch (action) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
			networks.clear();
			networks.addAll(temp);
			break;
		case UIHelper.LISTVIEW_ACTION_SCROLL:
			networks.addAll(temp);
			break;
		}
		if(entity.ne >= 1){					
			lvDataState = UIHelper.LISTVIEW_DATA_MORE;
		}
		else if (entity.ne == -1) {
			lvDataState = UIHelper.LISTVIEW_DATA_FULL;
		}
		if(networks.isEmpty() || StringUtils.empty(keyword)){
			networks.clear();
			lvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
		}
		mBilateralAdapter.notifyDataSetChanged();
		resultCheck();
	}

	@Override
	public boolean onEditorAction(TextView v, int actionID, KeyEvent arg2) {
		switch(actionID){  
        case EditorInfo.IME_ACTION_SEARCH:  
        	
    		editText.setCursorVisible(false);
            currentPage = 1;
            keyword = v.getText().toString();
            if (StringUtils.empty(keyword)) {
				return false;
			}
//    		searchFriendCard(1, keyword, "", UIHelper.LISTVIEW_ACTION_INIT);
            break;  
        }  
		return true;
	}
	
	TextWatcher TWPN = new TextWatcher() {
//        private CharSequence temp;
//        private int editStart ;
//        private int editEnd ;
        public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                int arg3) {
//            temp = s;
        }
       
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
        	if (s.length() > 0) {
        		noResultTV.setVisibility(View.GONE);
        		keyword = s.toString();
            	searchDeleteButton.setVisibility(View.VISIBLE);
            	asyncQuery.cancelOperation(0);
        		String sql = "display_name like "+"'%" + s.toString() + "%' "
        				+ "or " + Phone.NUMBER + " like " +"'%" + s.toString() + "%' "
        				+ "or sort_key like '" + s.toString().replace("", "%") + "'";
        		mobiles.clear();
        		bilaterals.clear();
        		networks.clear();
        		mBilateralAdapter.notifyDataSetChanged();
        		asyncQuery.startQuery(0, null, uri, projection, sql, null, "sort_key COLLATE LOCALIZED asc");
        		queryWeFriendsFromDB(keyword);
    			searchFriendCard(1, keyword, "", UIHelper.LISTVIEW_ACTION_INIT);
        	}
            else {
//            	QYRestClient.getIntance().cancelAllRequests(true);
            	keyword = "";
            	mobiles.clear();
            	bilaterals.clear();
            	networks.clear();
            	mBilateralAdapter.notifyDataSetChanged();
            	searchDeleteButton.setVisibility(View.INVISIBLE);
				
            }
        }
       
		public void afterTextChanged(Editable s) {
            
		}
    };

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		closeInput();
	}

	public void showCardView(CardIntroEntity entity) {
		EasyTracker easyTracker = EasyTracker.getInstance(this);
		easyTracker.send(MapBuilder
	      .createEvent("ui_action",     // Event category (required)
	                   "button_press",  // Event action (required)
	                   "查看名片："+entity.link,   // Event label
	                   null)            // Event value
	      .build()
		);
		Intent intent = new Intent(context, CardView.class);
		intent.putExtra(CommonValue.CardViewIntentKeyValue.CardView, entity);
		startActivityForResult(intent, CommonValue.CardViewUrlRequest.editCard);
	}
	
	public void showMobileView(CardIntroEntity entity) {
		EasyTracker easyTracker = EasyTracker.getInstance(this);
		easyTracker.send(MapBuilder
	      .createEvent("ui_action",     // Event category (required)
	                   "button_press",  // Event action (required)
	                   "查看手机名片",   // Event label
	                   null)            // Event value
	      .build()
		);
		Intent intent = new Intent(context, MobileVIew.class);
		intent.putExtra(CommonValue.CardViewIntentKeyValue.CardView, entity);
		startActivity(intent);
	}

	@Override
	public void onRefresh() {
		new Handler().postDelayed(new Runnable() {
	        @Override public void run() {
	            swipeLayout.setRefreshing(false);
	        }
	    }, 1000);
	}
}
