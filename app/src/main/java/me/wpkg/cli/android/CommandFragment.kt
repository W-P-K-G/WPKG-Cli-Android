package me.wpkg.cli.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

import me.wpkg.cli.commands.Command
import me.wpkg.cli.commands.RunProcess
import me.wpkg.cli.commands.Screenshot
import me.wpkg.cli.commands.SendMessage

class CommandFragment : Fragment()
{
    var parent = activity as ClientManagerActivity?
    var commands = ArrayList<Command>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_command, container, false)
        val layout = view.findViewById<LinearLayout>(R.id.layoutClientCommands)

        val p = activity as ClientManagerActivity?

        commands.add(SendMessage(layout, p))
        commands.add(RunProcess(layout, p))
        commands.add(Screenshot(layout, p))
        return view
    }

    companion object
    {
        @JvmStatic
        fun newInstance(): CommandFragment
        {
            return CommandFragment()
        }
    }
}