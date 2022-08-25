package com.wpkg.cli.commands;

import android.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import com.wpkg.cli.android.ClientManagerActivity;
import com.wpkg.cli.utils.Utils;

import java.io.IOException;

public class RunProcess extends Command
{
    public RunProcess(LinearLayout layout, ClientManagerActivity parent)
    {
        super("run", "Run process", layout, parent);
    }

    private TextView text(String text)
    {
        TextView t = new TextView(parent);
        t.setText(text);
        return t;
    }

    @Override
    public void execute(View view,ClientManagerActivity parent) throws IOException
    {
        LinearLayout layout = new LinearLayout(parent);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText txtCommand = new EditText(parent);
        final EditText txtArgs = new EditText(parent);
        layout.addView(text("Enter command:"));
        layout.addView(txtCommand);
        layout.addView(text("Enter arguments:"));
        layout.addView(txtArgs);

        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setTitle("Run process");
        builder.setView(layout);
        builder.setPositiveButton("Send", (dialog,which) -> {
            dialog.dismiss();
            new Thread(() -> {
                try
                {
                    String command = txtCommand.getText().toString();
                    String args = txtArgs.getText().toString();

                    sendCommand("run " + command + " " + args);

                    parent.runOnUiThread(() -> Toast.makeText(view.getContext(), "Process runned!", Toast.LENGTH_SHORT).show());
                }
                catch (IOException e)
                {
                    Utils.errorSnakeBar(view,e);
                }
                end();
            }).start();
        });
        builder.setNegativeButton("Cancel", (dialog,which) -> { dialog.dismiss(); end();});
        parent.runOnUiThread(builder::show);
    }
}
