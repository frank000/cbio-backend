package com.cbio.chat.interfaces;

import org.springframework.messaging.handler.annotation.DestinationVariable;

public interface INotificationController {
  String notifications(@DestinationVariable long userId, String message);
}