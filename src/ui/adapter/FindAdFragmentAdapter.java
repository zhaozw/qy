package ui.adapter;

import java.util.List;

import bean.AdsEntity;
import widget.AdImageFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FindAdFragmentAdapter extends FragmentPagerAdapter{
	
	private List<AdsEntity> ads;

	public FindAdFragmentAdapter(FragmentManager fm, List<AdsEntity> ads) {
		super(fm);
		this.ads = ads;
	}

	@Override
	public Fragment getItem(int position) {
		return AdImageFragment.newInstance(ads.get(position));
	}

	@Override
	public int getCount() {
		return ads.size();
	}

}
