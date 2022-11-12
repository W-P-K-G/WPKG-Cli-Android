package me.wpkg.cli.utils;

import android.content.Intent;
import android.view.View;
import com.google.android.material.snackbar.Snackbar;
import me.wpkg.cli.android.MainActivity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Utils
{
    public static double roundTo2DecimalPlace(double value)
    {
        return Math.round(value * 100.0) / 100.0;
    }

    public static void sleep(long millis)
    {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void errorSnakeBar(View view, Exception exception)
    {
        String message = exception instanceof SocketTimeoutException ? "Request timed out" : exception.getMessage();
        Snackbar.make(view, "Error: " + message, Snackbar.LENGTH_LONG).show();

        if (exception.getMessage().equals("Connection closed"))
        {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            view.getContext().startActivity(intent);
        }
    }

    public static String readStringFromURL(String requestURL) throws IOException {
        try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
