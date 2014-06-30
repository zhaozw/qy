package bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.AppException;
import tools.Logger;
import config.CommonValue;

public class TopicOptionListEntity extends Entity{
	public List<TopicOptionEntity> options = new ArrayList<TopicOptionEntity>();
	
	public static TopicOptionListEntity parse(String res) throws IOException, AppException {
		TopicOptionListEntity data = new TopicOptionListEntity();
		try {
			JSONObject js = new JSONObject(res);
			if(js.getInt("status") == 1) {
				data.error_code = Result.RESULT_OK;
				JSONObject info = js.getJSONObject("info");
				JSONArray ownedArr = info.getJSONArray("category");
				for (int i=0;i<ownedArr.length();i++) {
					TopicOptionEntity entity = new TopicOptionEntity();
					entity.category_id = ownedArr.getJSONObject(i).getString("category_id");
					entity.title = ownedArr.getJSONObject(i).getString("title");
					entity.sort = ownedArr.getJSONObject(i).getString("sort");
					entity.thumb = ownedArr.getJSONObject(i).getString("thumb");
                    entity.subTitle = ownedArr.getJSONObject(i).getString("news_title");
					data.options.add(entity);
				}
			}
			else {
				if (!js.isNull("error_code")) {
					data.error_code = js.getInt("error_code");
				}
				data.message = js.getString("info");
			}
		} catch (JSONException e) {
			Logger.i(e);
			throw AppException.json(e);
		}
		return data;
	}
}
