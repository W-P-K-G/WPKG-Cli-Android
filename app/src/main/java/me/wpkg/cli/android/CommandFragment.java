package me.wpkg.cli.android;

import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.wpkg.cli.commands.Command;
import me.wpkg.cli.commands.RunProcess;
import me.wpkg.cli.commands.Screenshot;
import me.wpkg.cli.commands.SendMessage;

import java.util.ArrayList;

public class CommandFragment extends Fragment
{
    public ClientManagerActivity parent;

    ArrayList<Command> commands = new ArrayList<>();

    public CommandFragment() {
        parent = ((ClientManagerActivity)getActivity());
    }

    public static CommandFragment newInstance()
    {
        CommandFragment fragment = new CommandFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_command, container, false);

        LinearLayout layout = view.findViewById(R.id.layoutClientCommands);

        ClientManagerActivity p = (ClientManagerActivity)getActivity();
        commands.add(new SendMessage(layout,p));
        commands.add(new RunProcess(layout,p));
        commands.add(new Screenshot(layout,p));

        return view;
    }
}