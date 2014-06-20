package bean;

import org.json.JSONException;
import org.json.JSONObject;

import tools.AppException;
import tools.StringUtils;

public class AdsEntity extends Entity{
	
	public String title;
	public String thumb;
	public String link;

	public AdsEntity() {
		super();
	}
	
	public AdsEntity(String title, String thumb, String link) {
		super();
		this.title = title;
		this.thumb = thumb;
		this.link = link;
	}


	public static AdsEntity parse(JSONObject info) throws AppException {
		AdsEntity data = new AdsEntity();
		try {
			data.title = info.getString("title");
			data.thumb = info.getString("thumb");
			data.link = info.getString("link");
		} catch (JSONException e) {
			throw AppException.json(e);
		}
		return data;
	}
	
}
