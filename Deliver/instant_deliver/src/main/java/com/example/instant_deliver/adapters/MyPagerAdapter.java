package com.example.instant_deliver.adapters;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by King on 2017/5/11.
 */

public class MyPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;
    private List<String>titles;
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    //重载构造方法
    public MyPagerAdapter(FragmentManager fm, List<Fragment> fragments,List<String>titles) {
        super(fm);
        this.fragments=fragments;
       this.titles=titles;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    /**
     * 在fragment嵌套fragment的时候，重写此方法会导致页面不显示
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }*/

    //在PagerAdapter里覆盖destroyItem方法可阻止销毁Fragment
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
    }

}
