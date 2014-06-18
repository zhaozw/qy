package db.manager;

import java.util.List;

import tools.Logger;
import bean.CardIntroEntity;
import bean.RelationshipEntity;
import android.content.Context;
import android.database.Cursor;
import config.MyApplication;
import config.CommonValue.LianXiRenType;
import db.DBManager;
import db.SQLiteTemplate;
import db.SQLiteTemplate.RowMapper;

public class RelationshipManager {

	private static RelationshipManager relationManager = null;
	private static DBManager manager = null;

	private RelationshipManager(Context context) {
		manager = DBManager.getInstance(context, MyApplication.getInstance().getLoginUid());
	}

	public static RelationshipManager getInstance(Context context) {
		if (relationManager == null) {
			relationManager = new RelationshipManager(context);
		}

		return relationManager;
	}
	
	public static void destroy() {
		relationManager = null;
		manager = null;
	}
	
	public void saveRelationships(List<RelationshipEntity> list, String reOpenid) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		for (RelationshipEntity model : list) {
			String sql = String.format("insert or ignore into wcb_relationship(openid, re_openid, code, title, realname, wechat, phone, type, link, avatar) "
					+ "values('%s', '%s','%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", 
					model.openid, reOpenid, model.code, model.title, model.realname, model.wechat, model.phone, model.type, model.link, model.avatar);
			st.execSQL(sql);
		}
	}
	
	public void deleteRelationships(String reOpenid) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		st.deleteByCondition("wcb_relationship", "re_openid=?", new String[]{reOpenid});
	}
	
	public List<RelationshipEntity> getRelationships(String reOpenid) {
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		List<RelationshipEntity> list = st.queryForList(new RowMapper<RelationshipEntity>() {

			@Override
			public RelationshipEntity mapRow(Cursor cursor, int index) {
				RelationshipEntity data = new RelationshipEntity();
				data.openid = cursor.getString(0);
				data.code = cursor.getString(1);
				data.title = cursor.getString(2);
				data.realname = cursor.getString(3);
				data.wechat = cursor.getString(4);
				data.phone = cursor.getString(5);
				data.type = cursor.getString(6);
				data.link = cursor.getString(7);
				data.avatar = cursor.getString(8);
				return data;
			}
		}, "wcb_relationship", 
		new String[]{"openid", "code", "title", "realname", "wechat", "phone", "type", "link", "avatar"}, 
		null, 
		null, 
		null, 
		null, 
		null,
		null);
		return list;
	}
}
