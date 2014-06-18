package ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tools.AppManager;
import ui.adapter.FieldAdapter;
import ui.adapter.PrivacyAdapter;
import widget.GridViewForScrollView;
import bean.FunsEntity;
import bean.KeyValue;

import com.vikaa.mycontact.R;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class CreatePhonebook extends AppActivity {
	
	private EditText richET;
	private EditText qunNameET;
	private LinearLayout moreLayout;
	private Button moreButton;

	private Spinner privacySP;
	private Spinner questionSP;
	private Spinner exchangeSP;
	
	private GridViewForScrollView fieldGridView;
	private FieldAdapter fieldAdapter;
	private List<String> fields = new ArrayList<String>();
	private FunsEntity fun;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_phonebook);
		initUI();
	}
	
	private void initUI() {
		moreButton = (Button) findViewById(R.id.more);
		moreLayout = (LinearLayout) findViewById(R.id.moreLayout);
		qunNameET = (EditText) findViewById(R.id.activityName);
		richET = (EditText) findViewById(R.id.richEditText);
		
		privacySP = (Spinner) findViewById(R.id.privacySP);
		List<KeyValue> privacys = new ArrayList<KeyValue>();
		privacys.add(new KeyValue("对群友可见","0"));
		privacys.add(new KeyValue("群友需申请可见","1"));
		privacys.add(new KeyValue("对任何人公开","2"));
		privacys.add(new KeyValue("申通通过后才能进群","3"));
		PrivacyAdapter pAdapter = new PrivacyAdapter(this, privacys);
		privacySP.setAdapter(pAdapter);
		
		questionSP = (Spinner) findViewById(R.id.questionSP);
		List<KeyValue> questions = new ArrayList<KeyValue>();
		questions.add(new KeyValue("公开显示回答的问题","0"));
		questions.add(new KeyValue("答案仅管理员可见","1"));
		PrivacyAdapter qAdapter = new PrivacyAdapter(this, questions);
		questionSP.setAdapter(qAdapter);
		
		exchangeSP = (Spinner) findViewById(R.id.exchangeSP);
		List<KeyValue> exchanges = new ArrayList<KeyValue>();
		exchanges.add(new KeyValue("成员自由交换名片","0"));
		exchanges.add(new KeyValue("群主引见","1"));
		PrivacyAdapter eAdapter = new PrivacyAdapter(this, exchanges);
		exchangeSP.setAdapter(eAdapter);
	}
	
	public void ButtonClick(View v) {
		switch (v.getId()) {
		case R.id.leftBarButton:
			closeInput();
			AppManager.getAppManager().finishActivity(this);
			break;

		case R.id.rightBarButton:
			break;
			
		case R.id.more:
			moreButton.setVisibility(View.GONE);
			moreLayout.setVisibility(View.VISIBLE);
			break;
		}
	}
}
