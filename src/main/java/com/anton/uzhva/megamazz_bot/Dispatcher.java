package com.anton.uzhva.megamazz_bot;

import com.anton.uzhva.megamazz_bot.handler.UserCallBackRequestHandler;
import com.anton.uzhva.megamazz_bot.handler.UserRequestHandler;
import com.anton.uzhva.megamazz_bot.model.UserRequest;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class Dispatcher {
    private final List<UserRequestHandler> handlers;
    private final List<UserCallBackRequestHandler> callBackHandlers;

    /**
     * Pay attention at this constructor
     * Since we have some global handlers,
     * like command /start which can interrupt any conversation flow.
     * These global handlers should go first in the list
     */
    public Dispatcher(List<UserRequestHandler> handlers, List<UserCallBackRequestHandler> callBackHandlers) {
        this.handlers = handlers
                .stream()
                .sorted(Comparator
                        .comparing(UserRequestHandler::isGlobal)
                        .reversed())
                .collect(Collectors.toList());
        this.callBackHandlers = callBackHandlers;
    }

    public boolean dispatch(UserRequest userRequest) {
        for (UserRequestHandler userRequestHandler : handlers) {
            if (userRequestHandler.isApplicable(userRequest)) {
                userRequestHandler.handle(userRequest);
                return true;
            }
        }
        return false;
    }

    public boolean dispatchCallBack(UserRequest userRequest) {
        for (UserCallBackRequestHandler callBackHandler : callBackHandlers) {
            if (callBackHandler.isCallbackApplicable(userRequest)) {
                callBackHandler.handleCallBack();
                return true;
            }
        }
        return false;
    }

}
