package com.wpkg.cli.android;

import android.app.ProgressDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.wpkg.cli.networking.UDPClient;
import com.wpkg.cli.utils.Utils;

import java.io.IOException;

import static com.wpkg.cli.utils.Utils.roundTo2DecimalPlace;

public class ClientManagerActivity extends AppCompatActivity
{

    public boolean joined = false,statsRefreshing,commandWorks;

    TabAdapter adapter;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_manager);

        Bundle bundle = getIntent().getExtras();

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(bundle.getString("name"));
        }

        tabLayout = findViewById(R.id.tabLayoutClient);
        ViewPager2 viewPager = findViewById(R.id.viewPager2Client);

        adapter = new TabAdapter(this,getSupportFragmentManager(),getLifecycle());
        viewPager.setAdapter(adapter);

        String[] tabs = {"Commands","Stats"};

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) ->
                {
                    tab.setText(tabs[position]);
                }
        ).attach();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            disconnect();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        joined = true;
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        joined = false;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        disconnect();
    }

    public void disconnect()
    {
        joined = false;
        new Thread(() -> {
            try
            {
                UDPClient.sendCommand("/unjoin");
            }
            catch (IOException e)
            {
                Utils.errorSnakeBar(getWindow().getDecorView().getRootView(),e);
            }

            runOnUiThread(this::finish);
        }).start();
    }

}
class TabAdapter extends FragmentStateAdapter
{
    ClientManagerActivity parent;

    CommandFragment commandFragment;
    StatsFragment statsFragment;
    public TabAdapter(ClientManagerActivity parent,FragmentManager fragmentManager, Lifecycle lifecycle)
    {
        super(fragmentManager,lifecycle);
        this.parent = parent;

        commandFragment = CommandFragment.newInstance();
        statsFragment = StatsFragment.newInstance();
    }

    @Override
    public Fragment createFragment(int position)
    {
        switch (position)
        {
            case 0:
                return commandFragment;
            case 1:
                return statsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount()
    {
        return 2;
    }
}

