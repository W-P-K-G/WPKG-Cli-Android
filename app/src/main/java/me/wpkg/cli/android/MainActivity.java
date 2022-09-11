package me.wpkg.cli.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import me.wpkg.cli.networking.UDPClient;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnConnect = findViewById(R.id.btnConnect);
        EditText txtAuthtoken = findViewById(R.id.txtAuthtoken);
        EditText txtIP = findViewById(R.id.txtIP);

        btnConnect.setOnClickListener((view) -> {
            if (TextUtils.isEmpty(txtIP.getText().toString()))
            {
                Snackbar.make(view, "Please enter ip", Snackbar.LENGTH_LONG).show();
                return;
            }
            else
            {
                ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                        "Connecting. Please wait...", true);

                new Thread(() -> {
                    try
                    {

                        String[] address = txtIP.getText().toString().split(":");

                        UDPClient.connect(address[0],Integer.parseInt(address[1]));
                        UDPClient.sendRegisterPing();

                        dialog.dismiss();

                        runOnUiThread(() -> startActivity(new Intent(this, WPKGManagerActivity.class)));
                    }
                    catch (IOException e)
                    {
                        runOnUiThread(() ->{
                           dialog.dismiss();
                           Snackbar.make(view, "Connection failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                       });
                    }
                }).start();

            }
        });
    }
}