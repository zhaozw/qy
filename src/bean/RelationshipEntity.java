package bean;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import tools.AppException;
import tools.Logger;

public class RelationshipEntity extends Entity {

	public String type;
	public String code;
	public String title;
	public String openid;
	public String link;
	public String avatar;
	public String realname;
	public String wechat;
	public String phone;
	
	public static RelationshipEntity parseSearch(JSONObject info) throws IOException, AppException {
		RelationshipEntity data = new RelationshipEntity();
		try {
			if (!info.isNull("t")) {
				data.type  = info.getString("t");
			}
			if (!info.isNull("co")) {
				data.code = info.getString("co");
			}
			if (!info.isNull("o")) {
				data.openid = info.getString("o");
			}
			if (!info.isNull("r")) {
				data.realname = info.getString("r");
			}
			if (!info.isNull("ph")) {
				data.phone = info.getString("ph");
			}
			if (!info.isNull("we")) {
				data.wechat= info.getString("we");
			}
			if (!info.isNull("l")) {
				data.link = info.getString("l");
			}
			if (!info.isNull("av")) {
				data.avatar = info.getString("av");
			}
			if (!info.isNull("ti")) {
				data.title  = info.getString("ti");
			}
			
		} catch (JSONException e) {
			Logger.i(info.toString());
			throw AppException.json(e);
		}
		return data;
	}
	
	public static RelationshipEntity parseCardInfo(JSONObject info) throws IOException, AppException {
		RelationshipEntity data = new RelationshipEntity();
		try {
			if (!info.isNull("type")) {
				data.type  = info.getString("type");
			}
			if (!info.isNull("code")) {
				data.code = info.getString("code");
			}
			if (!info.isNull("openid")) {
				data.openid = info.getString("openid");
			}
			if (!info.isNull("realname")) {
				data.realname = info.getString("realname");
			}
			if (!info.isNull("phone")) {
				data.phone = info.getString("phone");
			}
			if (!info.isNull("wechat")) {
				data.wechat= info.getString("wechat");
			}
			if (!info.isNull("link")) {
				data.link = info.getString("link");
			}
			if (!info.isNull("avatar")) {
				data.avatar = info.getString("avatar");
			}
			if (!info.isNull("title")) {
				data.title  = info.getString("title");
			}
		} catch (JSONException e) {
			Logger.i(info.toString());
			throw AppException.json(e);
		}
		return data;
	}
}
