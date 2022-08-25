package com.wpkg.cli.commands;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import com.wpkg.cli.android.ClientManagerActivity;
import com.wpkg.cli.utils.Utils;

import java.io.IOException;

public class SendMessage extends Command
{
    public SendMessage(LinearLayout layout, ClientManagerActivity parent)
    {
        super("msg", "Send message", layout, parent);
    }

    @Override
    public void execute(View view,ClientManagerActivity parent) throws IOException
    {
        final EditText taskEditText = new EditText(parent);
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setTitle("Send message");
        builder.setMessage("Enter message to send:");
        builder.setView(taskEditText);
        builder.setPositiveButton("Send", (dialog,which) -> {
            dialog.dismiss();
            new Thread(() -> {
                try
                {
                    String message = taskEditText.getText().toString();
                    sendCommand("msg " + message);

                    parent.runOnUiThread(() -> Toast.makeText(view.getContext(), "Message sended!", Toast.LENGTH_SHORT).show());
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