package com.wpkg.cli.android;

import androidx.constraintlayout.widget.ConstraintLayout;
import com.wpkg.cli.json.JsonMaps;

public interface ClientSelectedListener
{
    void clientSelected(JsonMaps.ClientObject client);

    void clientMenuInvoked(JsonMaps.ClientObject client, ConstraintLayout layout);
}
