package com.bedigi.partner.Fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.bedigi.partner.API.RetrofitAPI;
import com.bedigi.partner.Activities.Appointments;
import com.bedigi.partner.Activities.EnterOTP;
import com.bedigi.partner.Activities.Login;
import com.bedigi.partner.Adapter.HistoryAdapter;
import com.bedigi.partner.Adapter.TabPagerAdapter_home;
import com.bedigi.partner.Model.HistoryModel;
import com.bedigi.partner.Preferences.AppPreferences;
import com.bedigi.partner.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    TextView name, id;
    CircleImageView profile_image;
    AppPreferences appPreferences;
    Context context;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        appPreferences = new AppPreferences(getActivity());
        context = getActivity();

        profile_image = (CircleImageView) view.findViewById(R.id.profile_image);
        name = (TextView) view.findViewById(R.id.username);

        name.setText("Welcome " + appPreferences.getName());

        if (!(appPreferences.getImage().matches(""))) {
            Picasso.with(context).load(appPreferences.getImage()).placeholder(R.drawable.user_image).into(profile_image);
        }else{
            profile_image.setImageResource(R.drawable.user_image);
        }


        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);

        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        TabPagerAdapter_home adapter = new TabPagerAdapter_home(getChildFragmentManager());
        adapter.addFragment(new TodayAppointments());
        adapter.addFragment(new UpcomingAppointments());
        viewPager.setAdapter(adapter);
    }

}
