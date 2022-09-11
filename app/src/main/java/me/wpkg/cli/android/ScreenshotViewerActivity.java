package me.wpkg.cli.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MenuItem;
import android.widget.ImageView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import me.wpkg.cli.utils.Utils;

import java.io.InputStream;
import java.net.URL;

public class ScreenshotViewerActivity extends AppCompatActivity
{
    ImageView imgScreenshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot_viewer);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Screenshot");
        }

        imgScreenshot = findViewById(R.id.imgScreenshot);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        Bundle bundle = getIntent().getExtras();

        new Thread(() -> {
            try
            {
                InputStream in = new URL(bundle.getString("url")).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);

                runOnUiThread(() -> imgScreenshot.setImageBitmap(bitmap));
            }
            catch (Exception e)
            {
                Utils.errorSnakeBar(getWindow().getDecorView().getRootView(),e);
            }
        }).start();
    }
}