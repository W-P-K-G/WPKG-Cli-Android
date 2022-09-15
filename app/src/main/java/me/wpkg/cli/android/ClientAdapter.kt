package me.wpkg.cli.android

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import me.wpkg.cli.json.JsonMaps.ClientObject

class ClientAdapter(var clients: ArrayList<ClientObject>, private val listener: ClientSelectedListener) :
    RecyclerView.Adapter<ClientAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(listItem)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val client = clients[position]
        holder.txtListClientName.text = client.name
        holder.txtListID.text = client.id.toString()

        holder.chbListJoined.isChecked = client.joined
        holder.txtListVersion.text = "v" + client.version

        holder.constraintLayout.setOnClickListener {
            listener.clientSelected(client)
        }

        holder.constraintLayout.setOnLongClickListener {
            listener.clientMenuInvoked(client, holder.constraintLayout)
            false
        }
    }

    override fun getItemCount(): Int
    {
        return clients.size
    }

    fun addItem(item: ClientObject)
    {
        clients.add(item)
        notifyItemInserted(clients.size - 1)
    }

    fun clear()
    {
        val size = clients.size
        clients.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun removeItem(position: Int)
    {
        clients.removeAt(position)
        notifyItemRemoved(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var txtListClientName: TextView
        var txtListID: TextView
        var txtListVersion: TextView
        var chbListJoined: CheckBox
        var constraintLayout: ConstraintLayout

        init
        {
            txtListClientName = itemView.findViewById(R.id.txtListNameLabel)
            txtListID = itemView.findViewById(R.id.txtListID)
            chbListJoined = itemView.findViewById(R.id.chbListJoined)
            txtListVersion = itemView.findViewById(R.id.txtListVersion)
            constraintLayout = itemView.findViewById(R.id.constraintLayout)
        }
    }
}