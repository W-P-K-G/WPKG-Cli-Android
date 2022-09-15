package me.wpkg.cli.android

import androidx.constraintlayout.widget.ConstraintLayout

import me.wpkg.cli.json.JsonMaps.ClientObject

interface ClientSelectedListener
{
    fun clientSelected(client: ClientObject)
    fun clientMenuInvoked(client: ClientObject, layout: ConstraintLayout)
}