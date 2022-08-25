package com.wpkg.cli.utils;

import android.view.View;
import com.google.android.material.snackbar.Snackbar;

import java.net.SocketTimeoutException;

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
    }
}
