package me.wpkg.cli.commands;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import me.wpkg.cli.android.ClientManagerActivity;
import me.wpkg.cli.android.ProgressDialog;
import me.wpkg.cli.android.ScreenshotViewerActivity;
import me.wpkg.cli.commands.error.ErrorHandler;
import me.wpkg.cli.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Screenshot extends Command
{
    private final File imageFolder;

    public Screenshot(LinearLayout layout, ClientManagerActivity parent)
    {
        super("screenshot", "Make screenshot", layout, parent);
        imageFolder = new File(layout.getContext().getCacheDir(), "wpkgScreenshots");

        if (!imageFolder.exists())
            imageFolder.mkdir();
        imageFolder.deleteOnExit();
    }

    @Override
    public void execute(View view, ClientManagerActivity parent, ErrorHandler errorHandler) throws IOException
    {
        ProgressDialog dialog = new ProgressDialog(view.getContext(), "",
            "Waiting for screenshot...");
        dialog.show();

        new Thread(() -> {
            try
            {
                int size = Integer.parseInt(errorHandler.check(sendCommand("screenshot")));

                switch (errorHandler.get())
                {
                    case OK:
                        double bandwidth = 0;
                        String bandwidthtext = "% (" + bandwidth + "KB/s)";

                        File imageFile = new File(imageFolder,"screenshot" + System.currentTimeMillis() + ".png");
                        imageFile.deleteOnExit();

                        long timeprev = 0;
                        int i = 0;

                        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                            send("OK");

                            while (i < size) {
                                int percent = (int) (((float)i / (float)size) * 100);

                                byte[] buffer = receiveRawdata();
                                long timenow = System.nanoTime();

                                bandwidth = Utils.roundTo2DecimalPlace((double)(buffer.length / 1024) / ((double)(timenow - timeprev) / 1000000000));

                                if (i % 1000 == 0)
                                    bandwidthtext = "(" + bandwidth + "KB/s)";

                                String finalBandwidthtext = bandwidthtext;
                                parent.runOnUiThread(() -> {
                                    dialog.setProgress(percent);
                                    dialog.setText("Receiving screenshot... \n" + percent + "% " + finalBandwidthtext);
                                });
                                fos.write(buffer);
                                i += buffer.length;

                                timeprev = timenow;
                            }
                        }
                        dialog.dismiss();
                        parent.runOnUiThread(() -> {
                            Intent intent = new Intent(parent, ScreenshotViewerActivity.class);
                            intent.putExtra("path", imageFile.getPath());
                            parent.startActivity(intent);
                        });
                        break;
                    case ERROR: failDialog(view,"WPKG screenshot error: " + errorHandler.msg()); break;
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
