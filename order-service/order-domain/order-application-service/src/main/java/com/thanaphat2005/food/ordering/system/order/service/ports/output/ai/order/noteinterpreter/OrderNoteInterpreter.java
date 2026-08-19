package com.thanaphat2005.food.ordering.system.order.service.ports.output.ai.order.noteinterpreter;

import com.thanaphat2005.food.ordering.system.domain.valueobject.OrderPreferences;

public interface OrderNoteInterpreter {

    OrderPreferences interpret(String orderNotes);
}
