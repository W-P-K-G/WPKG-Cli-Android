package com.wpkg.cli.android;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.wpkg.cli.json.JsonMaps;

import java.util.ArrayList;


public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ViewHolder>
{
    private ClientSelectedListener listener;
    ArrayList<JsonMaps.ClientObject> clients;

    public ClientAdapter(ArrayList<JsonMaps.ClientObject> clients,ClientSelectedListener listener)
    {
        this.clients = clients;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        final JsonMaps.ClientObject client = clients.get(position);
        holder.txtListClientName.setText(client.name);
        holder.txtListID.setText(String.valueOf(client.id));
        holder.chbListJoined.setChecked(client.joined);
        holder.txtListVersion.setText("v" + client.version);
        holder.constraintLayout.setOnClickListener((view) -> {
            listener.clientSelected(client);
        });
        holder.constraintLayout.setOnLongClickListener((view) -> {
            listener.clientMenuInvoked(client,holder.constraintLayout);
            return false;
        });
    }

    @Override
    public int getItemCount()
    {
        return clients.size();
    }

    public void addItem(JsonMaps.ClientObject item)
    {
        clients.add(item);
        notifyItemInserted(clients.size() - 1);
    }

    public void clear()
    {
        int size = clients.size();
        clients.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void removeItem(int position)
    {
        clients.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView txtListClientName, txtListID,txtListVersion;

        public CheckBox chbListJoined;
        public ConstraintLayout constraintLayout;
        public ViewHolder(View itemView)
        {
            super(itemView);
            this.txtListClientName = itemView.findViewById(R.id.txtListNameLabel);
            this.txtListID = itemView.findViewById(R.id.txtListID);
            this.chbListJoined = itemView.findViewById(R.id.chbListJoined);
            this.txtListVersion = itemView.findViewById(R.id.txtListVersion);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }
}