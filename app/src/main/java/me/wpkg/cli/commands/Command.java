package me.wpkg.cli.commands;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import me.wpkg.cli.android.ClientManagerActivity;
import me.wpkg.cli.android.MainActivity;
import me.wpkg.cli.commands.error.ErrorHandler;
import me.wpkg.cli.net.Client;
import me.wpkg.cli.utils.Utils;

import java.io.IOException;

public abstract class Command
{
    public String command,name;

    ClientManagerActivity parent;

    ErrorHandler errorHandler = new ErrorHandler();

    public Command(String command, String name, LinearLayout layout, ClientManagerActivity parent)
    {
        this.command = command;
        this.name = name;

        this.parent = parent;

        Button button = new Button(layout.getContext());
        button.setText(name);

        errorHandler.setSessionExpiredEvent(() -> parent.runOnUiThread(() ->{
            Toast.makeText(parent.getWindow().getContext(),"Client disconnected. Session expired.",Toast.LENGTH_LONG).show();
            parent.finish();
        }));

        errorHandler.setNotAuthorizedEvent(() -> parent.runOnUiThread(() -> {
            Toast.makeText(parent.getWindow().getContext(),"Server password expired.",Toast.LENGTH_LONG).show();
            Intent i = new Intent(parent, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            parent.startActivity(i);
        }));

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
                execute(view,parent,errorHandler);
                errorHandler.clear();
            }
            catch (Exception e)
            {
                parent.runOnUiThread(() -> Utils.errorSnakeBar(view,e));
            }
        });

        layout.addView(button);
    }

    public abstract void execute(View view, ClientManagerActivity parent, ErrorHandler errorHandler) throws IOException;

    protected void end()
    {
        parent.commandWorks = false;
    }

    public void failDialog(View view,String message)
    {
        Toast.makeText(view.getContext(),message,Toast.LENGTH_LONG).show();
    }

    protected String sendCommand(String command) throws IOException
    {
        send(command);
        return receive();
    }

    protected String receive() throws IOException
    {
        return Client.receiveString();
    }

    protected byte[] receiveRawdata()
    {
        return Client.rawdata_receive();
    };

    protected void sendRawdata(byte[] b)
    {
        Client.rawdata_send(b);
    }

    protected void send(String command) throws IOException
    {
        Client.sendString(command);
    }
}