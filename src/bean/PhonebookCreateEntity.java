package bean;

import org.json.JSONException;
import org.json.JSONObject;
import tools.AppException;

/**
 * Created by donal on 14-6-28.
 */
public class PhonebookCreateEntity extends Entity{

    public String qun_id;
    public String openid;
    public String code;
    public String link;

    public static PhonebookCreateEntity parse(String res) throws AppException {
        PhonebookCreateEntity data = new PhonebookCreateEntity();
        try {
            JSONObject js = new JSONObject(res);
            if (js.getInt("status") == 1) {
                data.error_code = Result.RESULT_OK;
                JSONObject info = js.getJSONObject("info");
                data.qun_id = info.getString("qun_id");
                data.openid = info.getString("wechat_id");
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
