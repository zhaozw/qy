package ui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.CookieStore;

import bean.CardIntroEntity;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.crashlytics.android.Crashlytics;
import com.loopj.android.http.PersistentCookieStore;
import com.nostra13.universalimageloader.core.assist.DiscCacheUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.vikaa.wecontact.R;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import config.AppClient;
import config.MyApplication;
import config.AppClient.FileCallback;
import service.IPolemoService;
import tools.AppContext;
import tools.AppManager;
import tools.BaseActivity;
import tools.ImageUtils;
import tools.Logger;
import tools.MD5Util;
import tools.StringUtils;
import tools.UIHelper;

public class AppActivity extends BaseActivity {
	protected MyApplication appContext;
	protected Context context = null;
	protected ProgressDialog loadingPd;
	protected IWXAPI api;
	protected int screeWidth;
	protected int screeHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext =  (MyApplication)getApplication();
		context = this;
		api = WXAPIFactory.createWXAPI(this, "wx8b5b960fc0311f3e", false);
		screeWidth = ImageUtils.getDisplayWidth(context);
		screeHeight = ImageUtils.getDisplayHeighth(context);
	}
	
	public boolean isServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("service.IPolemoService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public void forceLogout() {
		if (appContext.getPolemoClient()!=null) {
			appContext.getPolemoClient().disconnect();
		}
		if (isServiceRunning()) {
			Intent intent1 = new Intent(this, IPolemoService.class);
			stopService(intent1);
		}
		UIHelper.ToastMessage(this, "用户未登录,1秒后重新进入登录界面", Toast.LENGTH_SHORT);
		Handler jumpHandler = new Handler();
        jumpHandler.postDelayed(new Runnable() {
			public void run() {
				AppClient.Logout(appContext);
				CookieStore cookieStore = new PersistentCookieStore(AppActivity.this);  
				cookieStore.clear();
				AppManager.getAppManager().finishAllActivity();
				appContext.setUserLogout();
				Intent intent = new Intent(AppActivity.this, LoginCode1.class);
				startActivity(intent);
			}
		}, 1000);
	}
	
	public void closeInput() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null && this.getCurrentFocus() != null) {
			inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	
	//获取当前客户端版本信息
	public String  getCurrentVersionName(){
		String versionName = null;
        try { 
        	PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
        	versionName = info.versionName;
        } catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
        return versionName;
	}
	
	//warndialog
	public void WarningDialog(String message) {
		try {
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage(message);
			builder.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		   builder.create().show();
		}
		catch (Exception e) {
			Crashlytics.logException(e);
		}
	}
	
	public void WarningDialog(String message, String positive, String negative, final DialogClickListener listener) {
		try {
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage(message);
			builder.setPositiveButton(positive, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					listener.ok();
				}
			});
			if (StringUtils.notEmpty(negative)) {
				builder.setNegativeButton(negative, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
						listener.cancel();
					}
				});
			}
			builder.create().show();
		}
		catch (Exception e) {
			Crashlytics.logException(e);
		}
	}
	
	//wechat open
	public void WarningDialogAndOpenWechat(String value, String message) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", value);
            clipboard.setPrimaryClip(clip);
        } else {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboard.setText(value);
        }
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(message);
		builder.setPositiveButton("打开", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				api.openWXApp();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	   builder.create().show();
	}
	
	//card share
	public void oks(String title, String text, String link, String filePath) {
		try {
			final OnekeyShare oks = new OnekeyShare();
			oks.setNotification(R.drawable.ic_launcher, getResources().getString(R.string.app_name));
			oks.setTitle(title);
			if (StringUtils.notEmpty(filePath)) {
				oks.setImagePath(filePath);
			}
			else {
				String cachePath = cn.sharesdk.framework.utils.R.getCachePath(this, null);
				oks.setImagePath(cachePath + "logo.png");
			}
			oks.setText(text + "\n" + link);
			oks.setUrl(link);
			oks.setSiteUrl(link);
			oks.setSite(link);
			oks.setTitleUrl(link);
			oks.setLatitude(23.056081f);
			oks.setLongitude(113.385708f);
			oks.setSilent(false);
			oks.show(this);
		} catch (Exception e) {
			Logger.i(e);
		}
	}
	public void cardShare(boolean silent, String platform, CardIntroEntity card, String filePath) {
		try {
			String text = (StringUtils.notEmpty(card.intro)?card.intro:String.format("您好，我叫%s，这是我的名片，请多多指教。",card.realname));
			oks(card.realname, text, card.link, filePath);
		} catch (Exception e) {
			Logger.i(e);
		}
	}
	public void cardSharePre(final boolean silent, final String platform, final CardIntroEntity card) {
		if (StringUtils.empty(card.avatar)) {
			cardShare(silent, platform, card, "");
			return;
		}
		File file1 = DiscCacheUtil.findInCache(card.avatar, imageLoader.getDiscCache());
		if (file1 != null) {
			cardShare(silent, platform, card, file1.getAbsolutePath());
			return;
		}
		String storageState = Environment.getExternalStorageState();	
		if(storageState.equals(Environment.MEDIA_MOUNTED)){
			String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/qy/" + MD5Util.getMD5String(appContext.getLoginInfo().headimgurl) + ".png";
			File file = new File(savePath);
			if (file.exists()) {
				cardShare(silent, platform, card, savePath);
			}
			else {
				loadingPd = UIHelper.showProgress(this, null, null, true);
				AppClient.downFile(this, appContext, card.avatar, ".png", new FileCallback() {
					@Override
					public void onSuccess(String filePath) {
						UIHelper.dismissProgress(loadingPd);
						cardShare(silent, platform, card, filePath);
					}

					@Override
					public void onFailure(String message) {
						UIHelper.dismissProgress(loadingPd);
						cardShare(silent, platform, card, "");
					}

					@Override
					public void onError(Exception e) {
						UIHelper.dismissProgress(loadingPd);
						cardShare(silent, platform, card, "");
					}
				});
			}
		}
	}
	
	//call and send text message
	public void callMobile(String moblie) {
		Uri uri = null;
		uri = Uri.parse("tel:" + moblie);
		Intent it = new Intent(Intent.ACTION_CALL, uri);
		startActivity(it);
	}
	
	public void sendSMS(String moblie, String text) {
		Intent sendIntent = new Intent(Intent.ACTION_SENDTO);  
	    sendIntent.setData(Uri.parse("smsto:" + moblie));  
	    sendIntent.putExtra("sms_body", text);  
	    context.startActivity(sendIntent); 
	}
	
	public interface DialogClickListener
	{
		public void ok();
		public void cancel();
	}
}
