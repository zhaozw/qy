package ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tools.AppManager;
import tools.Logger;
import tools.StringUtils;
import ui.adapter.FieldAdapter;
import ui.adapter.PrivacyAdapter;
import ui.adapter.QunTypeAdapter;
import widget.GridViewForScrollView;
import bean.FunsEntity;
import bean.KeyValue;
import bean.QunsEntity;
import bean.QunsListEntity;

import com.vikaa.mycontact.R;

import config.CommonValue;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class CreatePhonebook extends AppActivity implements OnItemClickListener{
	
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
	private List<QunsEntity> quns = new ArrayList<QunsEntity>();
	private QunTypeAdapter adapterType;
	private GridViewForScrollView gvQun;
	
	private QunsEntity qunOfChoosing ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_phonebook);
		initUI();
		changeType();
	}
	
	private void initUI() {
		gvQun = (GridViewForScrollView) findViewById(R.id.typeGridView);
		quns.addAll(QunsListEntity.parse(this).quns);
		qunOfChoosing = quns.get(0);
		quns.get(0).isSelected = true;
		adapterType = new QunTypeAdapter(this, quns);
		gvQun.setAdapter(adapterType);
		gvQun.setOnItemClickListener(this);
		
		moreButton = (Button) findViewById(R.id.more);
		moreLayout = (LinearLayout) findViewById(R.id.moreLayout);
		qunNameET = (EditText) findViewById(R.id.qunName);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View convertView, int position, long arg3) {
		for (QunsEntity qun : quns) {
			qun.isSelected = false;
		}
		qunOfChoosing = quns.get(position);
		quns.get(position).isSelected = true;
		adapterType.notifyDataSetChanged();
		changeType();
	}
	
	private void changeType() {
		qunNameET.setText(qunOfChoosing.label);
		richET.setText(qunOfChoosing.descriptions.get(0));
	}
}
