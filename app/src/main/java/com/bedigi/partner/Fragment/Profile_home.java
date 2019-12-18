package com.bedigi.partner.Fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bedigi.partner.Adapter.TabPagerAdapter_home;
import com.bedigi.partner.Adapter.TabPagerAdapter_profile;
import com.bedigi.partner.Preferences.AppPreferences;
import com.bedigi.partner.R;
import com.google.android.material.tabs.TabLayout;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile_home extends Fragment {

    TextView name, id;
    CircleImageView profile_image;
    AppPreferences appPreferences;
    Context context;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public Profile_home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile_home, container, false);

        appPreferences = new AppPreferences(getActivity());
        context = getActivity();

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);

        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        TabPagerAdapter_profile adapter = new TabPagerAdapter_profile(getChildFragmentManager());
        adapter.addFragment(new Profile());
        adapter.addFragment(new FollowersList());
        viewPager.setAdapter(adapter);
    }

}
