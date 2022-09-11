package me.wpkg.cli.android;

import androidx.constraintlayout.widget.ConstraintLayout;
import me.wpkg.cli.json.JsonMaps;

public interface ClientSelectedListener
{
    void clientSelected(JsonMaps.ClientObject client);

    void clientMenuInvoked(JsonMaps.ClientObject client, ConstraintLayout layout);
}
