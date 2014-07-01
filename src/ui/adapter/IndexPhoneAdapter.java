package ui.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.vikaa.wecontact.R;

import config.CommonValue;
import config.CommonValue.PhoneSectionType;
import bean.PhoneIntroEntity;
import tools.Logger;
import tools.StringUtils;
import ui.Index;
import ui.MyCard;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IndexPhoneAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<PhoneIntroEntity> phones;
	private DisplayImageOptions displayImageOptions;
	private DisplayImageOptions displayActivityOptions;
	
	public IndexPhoneAdapter(Context context, List<PhoneIntroEntity> phones) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.phones = phones;
		this.displayImageOptions = new DisplayImageOptions.Builder()
		.bitmapConfig(Bitmap.Config.RGB_565)
		.showImageOnLoading(R.drawable.content_image_loading)
		.showImageForEmptyUri(R.drawable.logo_120)
		.showImageOnFail(R.drawable.logo_120)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) 
		.displayer(new BitmapDisplayer() {
			@Override
			public void display(Bitmap bitmap, ImageAware imageAware,
					LoadedFrom loadedFrom) {
				imageAware.setImageBitmap(bitmap);
			}
		})
		.build();
		this.displayActivityOptions = new DisplayImageOptions.Builder()
		.bitmapConfig(Bitmap.Config.RGB_565)
		.showImageOnLoading(R.drawable.content_image_loading)
		.showImageForEmptyUri(R.drawable.activity_logo)
		.showImageOnFail(R.drawable.activity_logo)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) 
		.displayer(new BitmapDisplayer() {
			@Override
			public void display(Bitmap bitmap, ImageAware imageAware,
					LoadedFrom loadedFrom) {
				imageAware.setImageBitmap(bitmap);
			}
		})
		.build();
	}

	static class CellHolder {
		ImageView avatarView;
		TextView titleView;
		TextView desView;
		TextView creatorView;
	}

	@Override
	public int getCount() {
		return phones.size();
	}
	
	@Override
	public Object getItem(int arg0) {
		return phones.get(arg0);
	}
	
	@Override
	public long getItemId(int arg0) {
		return phones.get(arg0).getId();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		CellHolder cell = null;
		if (convertView == null) {
			cell = new CellHolder();
			convertView = inflater.inflate(R.layout.index_phone_cell, null);
			cell.titleView = (TextView) convertView.findViewById(R.id.title);
			cell.desView = (TextView) convertView.findViewById(R.id.des);
			cell.avatarView = (ImageView) convertView.findViewById(R.id.avatarImageView);
			cell.creatorView = (TextView) convertView.findViewById(R.id.creator);
			convertView.setTag(cell);
		}
		else {
			cell = (CellHolder) convertView.getTag();
		}
		final PhoneIntroEntity model = phones.get(position);
		cell.titleView.setText(model.title);
		String des = String.format("人数:%s 点击数:%s\n", model.member, model.hits);
		if (model.phoneSectionType.equals(PhoneSectionType.OwnedSectionType) 
				|| model.phoneSectionType.equals(PhoneSectionType.JoinedSectionType)) {
			ImageLoader.getInstance().displayImage(model.logo, cell.avatarView, displayImageOptions);
		}
		else {
			ImageLoader.getInstance().displayImage(model.logo, cell.avatarView, displayActivityOptions);
		}
		cell.creatorView.setText(Html.fromHtml("由<font color=\"#088ec1\">"+model.creator+"</font>发起"));
		cell.desView.setText(des);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (model.phoneSectionType.equals(PhoneSectionType.OwnedSectionType) 
						|| model.phoneSectionType.equals(PhoneSectionType.JoinedSectionType)) {
					((Index)context).showPhoneViewWeb(model);
				}
				else {
					((Index)context).showActivityViewWeb(model);
				}
			}
		});
		return convertView;
	}

}
