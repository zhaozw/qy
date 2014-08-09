package contact;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bean.Entity;

import tools.AppException;
import tools.Logger;

public class MobileSynListBean extends Entity{
	public List<MobileSynBean> data = new ArrayList<MobileSynBean>();
	
	public static MobileSynListBean parse(String res) throws AppException {
		MobileSynListBean list = new MobileSynListBean();
		try {
			JSONObject js = new JSONObject(res);
			if(js.getInt("status") == 1) {
				JSONObject info = js.getJSONObject("info");
				JSONArray d = info.getJSONArray("d");
				for (int i = 0; i < d.length(); i++) {
					JSONObject p = d.getJSONObject(i);
					MobileSynBean m = new MobileSynBean();
					m.prefix = p.getString("prefix");
					m.suffix = p.getString("suffix");
					m.firstname = p.getString("firstname");
					m.middlename = p.getString("middlename");
					m.lastname = p.getString("lastname");
					m.organization = p.getString("organization");
					m.department = p.getString("department");
					m.jobtitle = p.getString("jobtitle");
					JSONArray phoneArr = p.getJSONArray("phone");
					for (int j = 0; j < phoneArr.length(); j++) {
						if (phoneArr.get(j) instanceof JSONObject) {
							JSONObject phoneObj = phoneArr.getJSONObject(j);
							PhoneBean phoneBean = new PhoneBean();
							phoneBean.label = phoneObj.getString("label");
							phoneBean.phone = phoneObj.getString("phone");
							m.phone.add(phoneBean);
						}
					}
					JSONArray email = p.getJSONArray("email");
					for (int j = 0; j < email.length(); j++) {
						if (email.get(j) instanceof JSONObject) {
							JSONObject emailObj = email.getJSONObject(j);
							EmailBean emailBean = new EmailBean();
							emailBean.label = emailObj.getString("label");
							emailBean.email = emailObj.getString("email");
							m.email.add(emailBean);
						}
					}
					list.data.add(m);
				}
			}
			else {
				
			}
		} catch (JSONException e) {
			Logger.i(e);
			
			throw AppException.json(e);
		}
		return list;
	}
}
