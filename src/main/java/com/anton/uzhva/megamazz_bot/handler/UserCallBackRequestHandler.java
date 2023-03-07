package com.anton.uzhva.megamazz_bot.handler;

import com.anton.uzhva.megamazz_bot.model.UserRequest;

public interface UserCallBackRequestHandler {
    void  handleCallBack ();
   boolean isValidCallBack ();
   boolean isCallbackApplicable (UserRequest userRequest);
}
