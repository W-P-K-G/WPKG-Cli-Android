package me.wpkg.cli.android

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView

class ProgressDialog(val parent: Context?, title: String, message: String) : Dialog(parent!!)
{
    private val textView: TextView
    private val progressBar: ProgressBar
    init
    {
        setContentView(R.layout.progress_dialog)
        textView = findViewById(R.id.progressDialogText);
        progressBar = findViewById(R.id.progressDialogProgressBar)
        setTitle(title);
        setText(message);
        setCancelable(false)
    }

    fun setText(message: String)
    {
        textView.text = message;
    }

    public fun setProgress(progress: Int)
    {
        progressBar.progress = progress;
    }

}