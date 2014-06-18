package db.manager;

import java.util.List;

import android.content.Context;
import bean.PhoneIntroEntity;
import bean.RelationshipEntity;
import config.MyApplication;
import db.DBManager;
import db.SQLiteTemplate;

public class MyPhonebookManager {
	
	private static MyPhonebookManager myPhonebookManager = null;
	private static DBManager manager = null;

	private MyPhonebookManager(Context context) {
		manager = DBManager.getInstance(context, MyApplication.getInstance().getLoginUid());
	}

	public static MyPhonebookManager getInstance(Context context) {
		if (myPhonebookManager == null) {
			myPhonebookManager = new MyPhonebookManager(context);
		}

		return myPhonebookManager;
	}
	
	public static void destroy() {
		myPhonebookManager = null;
		manager = null;
	}
	
	public void saveMyPhonebooks(List<PhoneIntroEntity> list, String reOpenid) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		for (PhoneIntroEntity model : list) {
			String sql = String.format("insert or ignore into wcb_relationship(code, title, logo) "
					+ "values('%s', '%s','%s')", 
					model.code, model.title, model.logo);
			st.execSQL(sql);
		}
	}
}
