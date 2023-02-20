package com.ravingarinc.api.gui.api;

import com.ravingarinc.api.gui.BaseGui;

//Represents an item component which must be shutdown
public interface Active {
    void shutdown(BaseGui gui);
}
