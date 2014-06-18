package widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ResizeLinearLayout extends LinearLayout {
	
	private OnSizeChangedListener onSizeChangedListener;
	
	public ResizeLinearLayout(Context context) {
		super(context);
	}
	
	public ResizeLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ResizeLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
		this.onSizeChangedListener = onSizeChangedListener;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (onSizeChangedListener != null) {
			onSizeChangedListener.onSizeChanged(w, h, oldw, oldh);
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	public interface OnSizeChangedListener
	{
		public void onSizeChanged(int w, int h, int oldw, int oldh);
	}
}
