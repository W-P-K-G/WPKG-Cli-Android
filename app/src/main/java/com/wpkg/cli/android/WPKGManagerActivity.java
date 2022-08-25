package com.wpkg.cli.android;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.snackbar.Snackbar;
import com.wpkg.cli.json.JsonMaps;
import com.wpkg.cli.networking.UDPClient;
import com.wpkg.cli.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

public class WPKGManagerActivity extends AppCompatActivity implements View.OnClickListener,ClientSelectedListener
{

    RecyclerView recyclerView;
    ClientAdapter adapter;
    Button btnLogoff,btnRefresh;

    TextView txtNoClients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wpkgmanager);

        btnLogoff = findViewById(R.id.btnDisconnect);
        btnLogoff.setOnClickListener(this);

        btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(this);

        txtNoClients = findViewById(R.id.txtNoClients);

        ArrayList<JsonMaps.ClientObject> clients = new ArrayList<>();

        adapter = new ClientAdapter(clients,this);

        recyclerView = findViewById(R.id.clientRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        refreshList();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        logOff();
    }

    @Override
    public void onClick(View source)
    {
        if (source == btnLogoff)
        {
           logOff();
        }
        else if (source == btnRefresh)
        {
            refreshList();
            Toast.makeText(source.getContext(), "Client list refreshed!", Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshList()
    {
        adapter.clear();
        txtNoClients.setVisibility(View.INVISIBLE);
        new Thread(() -> {
            try
            {
                JsonMaps.ClientMap map = getClientList(UDPClient.sendCommand("/rat-list"));

                if (map.clients.length == 0)
                {
                    runOnUiThread(() -> txtNoClients.setVisibility(View.VISIBLE));
                    return;
                }

                for (JsonMaps.ClientObject client : map.clients)
                    runOnUiThread(() -> adapter.addItem(client));
            }
            catch (IOException e)
            {
                Utils.errorSnakeBar(getWindow().getDecorView().getRootView(),e);
            }

        }).start();
    }

    public void logOff()
    {
        new Thread(() -> {
            UDPClient.logOff();

            runOnUiThread(this::finish);
        }).start();
    }

    public JsonMaps.ClientMap getClientList(String json)
    {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonMaps.ClientMap clientJSON = null;
        try
        {
            clientJSON = objectMapper.readValue(json, JsonMaps.ClientMap.class);
        }
        catch (JsonProcessingException e)
        {
            Snackbar.make(getWindow().getDecorView().getRootView(),"JSON parsing failed: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
        }
        return clientJSON;
    }

    @Override
    public void clientSelected(JsonMaps.ClientObject client)
    {
        if (client.joined)
            Snackbar.make(getWindow().getDecorView().getRootView(),"Client is already joined.", Snackbar.LENGTH_SHORT).show();
        else
        {
            new Thread(() -> {
                try
                {
                    UDPClient.sendCommand("/join " + client.id);

                    Intent intent = new Intent(this, ClientManagerActivity.class);
                    intent.putExtra("name", client.name);
                    runOnUiThread(() -> startActivity(intent));
                }
                catch (IOException e)
                {
                    Utils.errorSnakeBar(getWindow().getDecorView().getRootView(),e);
                }
            }).start();
        }
    }

    @Override
    public void clientMenuInvoked(JsonMaps.ClientObject client, ConstraintLayout layout)
    {
        PopupMenu popup = new PopupMenu(this, layout);
        popup.getMenuInflater().inflate(R.menu.client_popup_menu, popup.getMenu());

        popup.setOnMenuItemClickListener((menuItem) -> {
            if (menuItem.getItemId() == R.id.menuItemKill)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Kill");
                builder.setMessage("Are you want to kill " + client.name + "?");
                builder.setPositiveButton("OK", (dialog,which) -> {
                    dialog.dismiss();

                    new Thread(() -> {
                        try
                        {
                            UDPClient.sendCommand("/close " + client.id);

                            runOnUiThread(() -> {
                                Toast.makeText(this,"Client killed!",Toast.LENGTH_SHORT).show();
                                refreshList();
                            });
                        }
                        catch (IOException e)
                        {
                            Utils.errorSnakeBar(getWindow().getDecorView().getRootView(),e);
                        }
                    }).start();
                });
                builder.setNegativeButton("Cancel",(dialog,which) -> dialog.dismiss());
                AlertDialog alert = builder.create();
                alert.show();
            }
            return true;
        });

        popup.show();
    }
}