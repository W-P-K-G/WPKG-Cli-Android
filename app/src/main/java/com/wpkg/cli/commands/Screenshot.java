package com.wpkg.cli.commands;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import com.google.android.material.snackbar.Snackbar;
import com.wpkg.cli.android.ClientManagerActivity;
import com.wpkg.cli.android.MainActivity;
import com.wpkg.cli.android.ScreenshotViewerActivity;
import com.wpkg.cli.utils.Utils;

import java.io.IOException;

public class Screenshot extends Command{
    public Screenshot(LinearLayout layout, ClientManagerActivity parent) {
        super("screenshot", "Make screenshot", layout, parent);
    }

    @Override
    public void execute(View view,ClientManagerActivity parent) throws IOException
    {
        ProgressDialog dialog; dialog = ProgressDialog.show(parent, "",
            "Waiting for screenshot...", true);

        new Thread(() -> {
            try
            {
                String url = sendCommand("screenshot");

                parent.runOnUiThread(() -> {
                    dialog.dismiss();

                    Intent intent = new Intent(parent, ScreenshotViewerActivity.class);
                    intent.putExtra("url", url);
                    parent.startActivity(intent);
                });
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
