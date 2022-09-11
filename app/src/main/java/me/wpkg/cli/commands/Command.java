package me.wpkg.cli.commands;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import me.wpkg.cli.android.ClientManagerActivity;
import me.wpkg.cli.networking.UDPClient;
import me.wpkg.cli.utils.Utils;

import java.io.IOException;

public abstract class Command
{
    public String command,name;

    ClientManagerActivity parent;

    public Command(String command, String name, LinearLayout layout, ClientManagerActivity parent)
    {
        this.command = command;
        this.name = name;

        this.parent = parent;

        Button button = new Button(layout.getContext());
        button.setText(name);

        button.setOnClickListener((view) ->
        {
            if (parent.statsRefreshing)
            {
                Toast.makeText(view.getContext(), "Refresh is in progress...", Toast.LENGTH_SHORT).show();
                return;
            }

            if (parent.commandWorks)
                return;

            try
            {
                parent.commandWorks = true;
                execute(view,parent);
            }
            catch (Exception e)
            {
                parent.runOnUiThread(() -> Utils.errorSnakeBar(view,e));
            }
        });

        layout.addView(button);
    }

    public abstract void execute(View view,ClientManagerActivity parent) throws IOException;

    protected void end()
    {
        parent.commandWorks = false;
    }

    protected String sendCommand(String command) throws IOException
    {
        sendToServer(command);
        return receiveFromServer();
    }

    protected String receiveFromServer() throws IOException
    {
        return UDPClient.receiveString();
    }

    protected byte[] receiveRawdata()
    {
        return UDPClient.rawdata_receive();
    };

    protected void sendRawdata(byte[] b)
    {
        UDPClient.rawdata_send(b);
    }

    protected void sendToServer(String command) throws IOException
    {
        UDPClient.sendString(command);
    }
}