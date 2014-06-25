package bean;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.AppException;
import tools.Logger;
import tools.StringUtils;

public class TopicListEntity extends Entity{

	public List<TopicEntity> datas = new ArrayList<TopicEntity>();
	public int next;

	public static TopicListEntity parse(String res) throws IOException, AppException {
		TopicListEntity data = new TopicListEntity();
		try {
			JSONObject js = new JSONObject(res);
			if(js.getInt("status") == 1) {
				data.error_code = Result.RESULT_OK;
				JSONObject info = js.getJSONObject("info");
				JSONArray ownedArr = info.getJSONArray("list");
				for (int i=0;i<ownedArr.length();i++) {
					TopicEntity entity = new TopicEntity();
					entity.category_id = ownedArr.getJSONObject(i).getString("category_id");
					entity.title = ownedArr.getJSONObject(i).getString("title");
					entity.thumb = ownedArr.getJSONObject(i).getString("thumb");
					entity.news_id = ownedArr.getJSONObject(i).getString("news_id");
					entity.code = ownedArr.getJSONObject(i).getString("code");
					entity.pubdate = StringUtils.phpLongtoDate(ownedArr.getJSONObject(i).getString("pubdate"), new SimpleDateFormat("MM-dd HH:mm"));
					entity.link = ownedArr.getJSONObject(i).getString("link");
					entity.rss_id = ownedArr.getJSONObject(i).getString("rss_id");
					entity.url = ownedArr.getJSONObject(i).getString("url");
					entity.from = "";
					if (!ownedArr.getJSONObject(i).isNull("rss")) {
						if (StringUtils.notEmpty(ownedArr.getJSONObject(i).getString("rss"))) {
							JSONObject rss =  ownedArr.getJSONObject(i).getJSONObject("rss");
							if (rss.isNull("title")) {
								if (StringUtils.notEmpty(rss.getString("title"))) {
									entity.from = rss.getString("title");
								}
							}
						}
					}
					data.datas.add(entity);
				}
				data.next = info.getInt("next");
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
	
	public static TopicListEntity parseMyTopic(String res) throws IOException, AppException {
		TopicListEntity data = new TopicListEntity();
		try {
			JSONObject js = new JSONObject(res);
			if(js.getInt("status") == 1) {
				data.error_code = Result.RESULT_OK;
				JSONObject info = js.getJSONObject("info");
				JSONArray ownedArr = info.getJSONArray("list");
				for (int i=0;i<ownedArr.length();i++) {
					TopicEntity entity = new TopicEntity();
					entity.title = ownedArr.getJSONObject(i).getString("title");
					entity.thumb = ownedArr.getJSONObject(i).getString("thumb");
					entity.news_id = ownedArr.getJSONObject(i).getString("news_id");
					entity.code = ownedArr.getJSONObject(i).getString("code");
					entity.pubdate = StringUtils.phpLongtoDate(ownedArr.getJSONObject(i).getString("pubdate"), new SimpleDateFormat("MM-dd HH:mm"));
					entity.link = ownedArr.getJSONObject(i).getString("link");
					entity.url = ownedArr.getJSONObject(i).getString("url");
					entity.from = "";
					data.datas.add(entity);
				}
				data.next = info.getInt("next");
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
