package bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tools.AppException;

public class AdsListEntity extends Entity {

	public List<AdsEntity> ads = new ArrayList<AdsEntity>();
	
	public static AdsListEntity parse(String decode) throws AppException {
		AdsListEntity data = new AdsListEntity();
		try {
			JSONObject info = new JSONObject(decode);
			if (info.getInt("status") == 1) {
				JSONArray infos = info.getJSONArray("info");
				for (int i = 0; i < infos.length(); i++) {
					data.ads.add(AdsEntity.parse(infos.getJSONObject(i)));
				}
			}
		} catch (JSONException e) {
			throw AppException.json(e);
		}
		return data;
	}
	
}
