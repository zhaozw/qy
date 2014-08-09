package config;


import tools.AppManager;
import tools.ImageUtils;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;


/**
 * @author Donal Tong
 * @project FansCam
 * @created 2013-9-17
 * Copyright (c) 2013年 Donal Tong. All rights reserved.
 */
public class BaseActivity extends FragmentActivity{
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(null);
		AppManager.getAppManager().addActivity(this);
	}

	protected void onDestroy() {
		super.onDestroy();
	}
	
	public void accretionArea(final Button mButton) {  
        View parent = (View) mButton.getParent();  
        // <p>This method can be invoked from outside of the UI thread only when  
        // this View is attached to a window.</p>  
        parent.post(new Runnable() {  
            public void run() {  
                Rect outRect = new Rect();  
                mButton.getHitRect(outRect);  
                outRect.left -= ImageUtils.dip2px(BaseActivity.this, 15);  
                outRect.top -= ImageUtils.dip2px(BaseActivity.this, 15);  
                outRect.right += ImageUtils.dip2px(BaseActivity.this, 15);  
                outRect.bottom += ImageUtils.dip2px(BaseActivity.this, 15);  
                TouchDelegate deldgate = new TouchDelegate(outRect, mButton);  
                if (View.class.isInstance(mButton.getParent())) {  
                    ((View) mButton.getParent()).setTouchDelegate(deldgate);  
                }  
            }  
        });  
    }
	
//	public abstract void ButtonClick(View v);
}
