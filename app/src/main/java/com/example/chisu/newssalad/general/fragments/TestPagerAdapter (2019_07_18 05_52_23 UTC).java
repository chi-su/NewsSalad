package com.example.chisu.newssalad.general.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TestPagerAdapter extends FragmentPagerAdapter {

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "     라이브     ";
            case 1:
                return "     스포츠     ";
            case 2:
                return "     과학     ";
            case 3:
                return "    엔터테인먼트    ";
            case 4:
                return "     비즈니스     ";
        }
        return super.getPageTitle(position);
    }

    //페이지 갯수
    private static int PAGE_NUMBER = 5;

    public TestPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return page_1.newInsance();
            case 1:
                return page_2.newInsance();
            case 2:
                return page_3.newInsance();
            case 3:
                return page_4.newInsance();
            case 4:
                return page_5.newInsance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_NUMBER;
    }
}
