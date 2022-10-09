package me.wpkg.cli.commands;

import android.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import me.wpkg.cli.android.ClientManagerActivity;
import me.wpkg.cli.commands.error.ErrorHandler;
import me.wpkg.cli.utils.Utils;

import java.io.IOException;

public class SendMessage extends Command
{
    public SendMessage(LinearLayout layout, ClientManagerActivity parent)
    {
        super("msg", "Send message", layout, parent);
    }

    @Override
    public void execute(View view, ClientManagerActivity parent, ErrorHandler errorHandler) throws IOException
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
                    errorHandler.check(sendCommand("msg " + message));

                    switch (errorHandler.get())
                    {
                        case OK: successDialog(view,"Message sended!"); break;
                        case ERROR: failDialog(view,"WPKG send message error: " + errorHandler.msg()); break;
                    }

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
