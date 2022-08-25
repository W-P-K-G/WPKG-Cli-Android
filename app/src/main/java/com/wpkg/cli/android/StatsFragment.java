package com.wpkg.cli.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.wpkg.cli.networking.UDPClient;
import com.wpkg.cli.utils.Utils;

import java.io.IOException;

import static com.wpkg.cli.utils.Utils.roundTo2DecimalPlace;

public class StatsFragment extends Fragment
{
    public ClientManagerActivity parent;

    ProgressBar proClientCpu,proClientRam,proClientSwap;

    TextView txtClientCpu,txtClientRam,txtClientSwap;

    public StatsFragment() {
        parent = ((ClientManagerActivity)getActivity());
    }

    public static StatsFragment newInstance()
    {
        StatsFragment fragment = new StatsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        parent = ((ClientManagerActivity)getActivity());

        refresh();

        statsThread();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        proClientCpu = view.findViewById(R.id.proClientCpu);
        proClientRam = view.findViewById(R.id.proClientRam);
        proClientSwap = view.findViewById(R.id.proClientSwap);

        txtClientCpu = view.findViewById(R.id.txtClientCpu);
        txtClientRam = view.findViewById(R.id.txtClientRam);
        txtClientSwap = view.findViewById(R.id.txtClientSwap);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.layoutSwipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Thread(() -> {
                refreshStats();
                parent.runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
            }).start();
        });

        return view;
    }


    public void refresh()
    {
        ProgressDialog dialog = ProgressDialog.show(parent, "",
                "Refreshing. Please wait...", true);

        new Thread(() -> {
            refreshStats();
            parent.runOnUiThread(dialog::dismiss);
        }).start();
    }

    public void statsThread()
    {
        new Thread(() -> {
            Utils.sleep(10000);
            while (true)
            {
                if (!parent.joined)
                    return;

                if (!parent.commandWorks && parent.tabLayout.getSelectedTabPosition() == 1)
                    refreshStats();

                Utils.sleep(10000);
            }
        }).start();
    }

    public void refreshStats()
    {
        try
        {
            parent.statsRefreshing = true;

            String[] mess = UDPClient.sendCommand("stat").split(" ");

            int cpu = (int)Math.floor(Float.parseFloat(mess[0]));

            double memfree = roundTo2DecimalPlace(Double.parseDouble(mess[1]) / 1024 / 1024 / 1024);
            double memtotal = roundTo2DecimalPlace(Double.parseDouble(mess[2]) / 1024 / 1024 / 1024);

            double swapfree = roundTo2DecimalPlace(Double.parseDouble(mess[3]) / 1024 / 1024 / 1024);
            double swaptotal = roundTo2DecimalPlace(Double.parseDouble(mess[4]) / 1024 / 1024 / 1024);

            parent.runOnUiThread(() -> {
                proClientCpu.setProgress(cpu);
                txtClientCpu.setText(cpu + "%");

                txtClientRam.setText(memfree + "GB/" + memtotal + "GB");
                proClientRam.setProgress((int)(memfree * 100 / memtotal));

                txtClientSwap.setText(swapfree + "GB/" + swaptotal + "GB");
                proClientSwap.setProgress((int)(swapfree * 100 / swaptotal));
            });
            parent.statsRefreshing = false;
        }
        catch (IOException e)
        {
            Utils.errorSnakeBar(getActivity().findViewById(android.R.id.content),e);
        }
    }
}