package bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.AppException;

public class ActivityCreateEntity extends Entity {

	public String activity_id;
	public String openid;
	public String code;
	public String link;
	
	public static ActivityCreateEntity parse(String res) throws AppException {
		ActivityCreateEntity data = new ActivityCreateEntity();
		try {
			JSONObject js = new JSONObject(res);
			if (js.getInt("status") == 1) {
				data.error_code = Result.RESULT_OK;
				JSONObject info = js.getJSONObject("info");
				data.activity_id = info.getString("activity_id");
				data.openid = info.getString("openid");
				data.code = info.getString("code");
				data.link = info.getString("link");
			}
			else {
				data.error_code = 11;
				data.message = js.getString("info");
			}
			
		} catch (JSONException e) {
			throw AppException.json(e);
		}
		return data;
	}
	
}
