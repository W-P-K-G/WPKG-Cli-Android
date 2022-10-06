package me.wpkg.cli.commands;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import me.wpkg.cli.android.ClientManagerActivity;
import me.wpkg.cli.android.ScreenshotViewerActivity;
import me.wpkg.cli.commands.error.ErrorHandler;
import me.wpkg.cli.utils.Utils;

import java.io.IOException;

public class Screenshot extends Command{
    public Screenshot(LinearLayout layout, ClientManagerActivity parent) {
        super("screenshot", "Make screenshot", layout, parent);
    }

    @Override
    public void execute(View view, ClientManagerActivity parent, ErrorHandler errorHandler) throws IOException
    {
        ProgressDialog dialog; dialog = ProgressDialog.show(parent, "",
            "Waiting for screenshot...", true);

        new Thread(() -> {
            try
            {
                String url = errorHandler.check(sendCommand("screenshot"));

                dialog.dismiss();

                switch (errorHandler.get())
                {
                    case OK: {
                        parent.runOnUiThread(() -> {
                            Intent intent = new Intent(parent, ScreenshotViewerActivity.class);
                            intent.putExtra("url", url);
                            parent.startActivity(intent);
                        });
                        break;
                    }
                    case ERROR: {
                        parent.runOnUiThread(() -> failDialog(view,"Error creating screenshot by WPKG"));
                        break;
                    }
                }
            }
            catch (IOException e)
            {
                dialog.dismiss();
                Utils.errorSnakeBar(view,e);
            }
            end();
        }).start();
    }
}
