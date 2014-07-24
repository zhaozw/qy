package ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import bean.CodeEntity;
import bean.Entity;
import bean.Result;

import bean.UserEntity;
import com.crashlytics.android.Crashlytics;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.vikaa.mycontact.R;

import config.AppClient;
import config.AppClient.ClientCallback;
import config.CommonValue;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import tools.AppException;
import tools.AppManager;
import tools.Logger;
import tools.StringUtils;
import tools.UIHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginCode1 extends AppActivity{
	private EditText mobileET;
//	private CountDown cd;
	boolean canVertify ;
	int leftSeconds;
	private ProgressDialog loadingPd;
	private TextView barTitle;
	private TextView regTV;
	private TextView textView1;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case CommonValue.LoginRequest.LoginMobile:
			AppManager.getAppManager().finishActivity(this);
			break;
		case CommonValue.LoginRequest.LoginWechat:
		case CommonValue.LoginRequest.Register:
			AppManager.getAppManager().finishActivity(this);
			break;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_code1);
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommonValue.ACTION_WECHAT_CODE);
        registerReceiver(receiver, filter);
		initUI();
		initData();
	}

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void initUI() {
		textView1 = (TextView) findViewById(R.id.textView1);
		barTitle = (TextView) findViewById(R.id.barTitle);
		regTV = (TextView) findViewById(R.id.textView2);
		regTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		Button rightBarButton = (Button) findViewById(R.id.rightBarButton);
		accretionArea(rightBarButton);
		mobileET = (EditText) findViewById(R.id.editTextPhone);
	}
	
	private void initData() {
		canVertify = true;
//		cd = new CountDown(60*1000, 1000);
	}
	
	public void ButtonClick(View v) {
		switch (v.getId()) {
		case R.id.rightBarButton:
			this.getVertifyCode();
			break;

		case R.id.textView1:
			this.wechat();
			break;
		case R.id.textView2:
			register();
			break;
		}
	}
	
	private void getVertifyCode(final String mobile)  {
		loadingPd = UIHelper.showProgress(this, null, null, true);
//		cd.cancel();
//		cd.start();
		AppClient.getVertifyCode(appContext, mobile, new ClientCallback() {
			@Override
			public void onSuccess(Entity data) {
				UIHelper.dismissProgress(loadingPd);
				CodeEntity code = (CodeEntity)data;
				switch (code.getError_code()) {
				case Result.RESULT_OK:
//					cd.cancel();
					canVertify = true;
					step2(code.info);
					break;
				default:
//					cd.cancel();
					canVertify = true;
					UIHelper.ToastMessage(getApplicationContext(), code.getMessage(), Toast.LENGTH_SHORT);
					break;
				}
			}
			
			@Override
			public void onFailure(String message) {
//				cd.cancel();
				canVertify = true;
				UIHelper.dismissProgress(loadingPd);
				UIHelper.ToastMessage(getApplicationContext(), message, Toast.LENGTH_SHORT);
			}
			
			@Override
			public void onError(Exception e) {
//				cd.cancel();
				canVertify = true;
				UIHelper.dismissProgress(loadingPd);
				Logger.i(e);
			}
		});
	}
	
	private void getVertifyCode() {
		if (StringUtils.isMobileNO(mobileET.getText().toString())) {
			if (canVertify) {
				getVertifyCode(mobileET.getText().toString());
			}
			else {
				UIHelper.ToastMessage(getApplicationContext(), String.format("还需要%d秒获取验证码", leftSeconds), Toast.LENGTH_SHORT);
			}
		}
		else {
			WarningDialog("请输入正确的手机号码");
		}
	}
	
	private void step2(String content) {
		Intent intent = new Intent(LoginCode1.this, LoginCode2.class);
		intent.putExtra("content", content);
		intent.putExtra("mobile", mobileET.getText().toString());
		startActivityForResult(intent, CommonValue.LoginRequest.LoginMobile);
	}
	
	private void wechat() {
//		Intent intent = new Intent(LoginCode1.this, LoginWechat.class);
//		startActivityForResult(intent, CommonValue.LoginRequest.LoginWechat);
        if (!api.isWXAppInstalled()){
            WarningDialog("本机没有安装微信");
            return;
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        api.sendReq(req);
	}
	
	private void register() {
		if (regTV.getText().toString().equals(getResources().getString(R.string.register))) {
			regTV.setText(getResources().getString(R.string.mobile_login));
			barTitle.setText("手机号码注册");
			textView1.setVisibility(View.INVISIBLE);
		}
		else {
			regTV.setText(getResources().getString(R.string.register));
			barTitle.setText("手机号码登录");
			textView1.setVisibility(View.VISIBLE);
		}
	}
	
//	class CountDown extends CountDownTimer {
//
//		public CountDown(long millisInFuture, long countDownInterval) {
//			super(millisInFuture, countDownInterval);
//		}
//
//		@Override
//		public void onFinish() {
//			canVertify = true;
//		}
//
//		@Override
//		public void onTick(long millisUntilFinished) {
//			canVertify = false;
//			leftSeconds = (int) (millisUntilFinished/1000);
//		}
//	}

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (CommonValue.ACTION_WECHAT_CODE.equals(intent.getAction())) {
                String code = intent.getStringExtra("code");
                AppClient.getAccessToken(code, CommonValue.APP_ID, CommonValue.SECRET, new AppClient.FileCallback() {
                    @Override
                    public void onSuccess(String filePath) {
                        try {
                            JSONObject json = new JSONObject(filePath);
                            String openid = json.getString("openid");
                            String accessToken = json.getString("access_token");
                            loginByWechat(openid, accessToken);
                        }
                        catch (Exception e) {
                            Crashlytics.logException(e);
                        }
                    }

                    @Override
                    public void onFailure(String message) {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        }
    };

    private void loginByWechat(String openid, String accessToken) {
        loadingPd = UIHelper.showProgress(this, null, null, true);
        AppClient.loginByWechat(appContext, openid, accessToken, new ClientCallback() {
            @Override
            public void onSuccess(Entity data) {
                UIHelper.dismissProgress(loadingPd);
                UserEntity user = (UserEntity) data;
                switch (user.getError_code()) {
                    case Result.RESULT_OK:
                        appContext.saveLoginInfo(user);
                        enterIndex(user);
                        break;
                    default:
                        UIHelper.ToastMessage(LoginCode1.this, user.getMessage(), Toast.LENGTH_SHORT);
                        break;
                }
            }

            @Override
            public void onFailure(String message) {
                UIHelper.dismissProgress(loadingPd);
                UIHelper.ToastMessage(LoginCode1.this, message, Toast.LENGTH_SHORT);
            }

            @Override
            public void onError(Exception e) {
                UIHelper.dismissProgress(loadingPd);
                Logger.i(e);
            }
        });
    }

    private void enterIndex(UserEntity user) {
        String reg = "手机用户.*";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(user.nickname);
        if (m.matches()) {
            Intent intent = new Intent(this, Register.class);
            intent.putExtra("mobile", user.username);
            intent.putExtra("jump", true);
            startActivity(intent);
            setResult(RESULT_OK);
            AppManager.getAppManager().finishActivity(this);
        }
        else {
            Intent intent = new Intent(this, Tabbar.class);
            startActivity(intent);
            setResult(RESULT_OK);
            AppManager.getAppManager().finishActivity(this);
        }
    }
}
