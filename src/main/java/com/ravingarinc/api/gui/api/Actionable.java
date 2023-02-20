package com.ravingarinc.api.gui.api;

import com.ravingarinc.api.gui.BaseGui;
import com.ravingarinc.api.gui.component.action.Action;

public interface Actionable {
    void addAction(Action action);

    void performAllActions(BaseGui gui);

    String getParent();
}
