package com.cbio.chat.dto;

public class NotificationDTO {
  private String type;

  private String contents;

  private String fromUserId;

  public NotificationDTO() {}

  public NotificationDTO(String type, String contents, String fromUserId) {
    this.type = type;
    this.contents = contents;
    this.fromUserId = fromUserId;
  }

  public String getType() {
    return this.type;
  }

  public String getContent() {
    return this.contents;
  }

  public String getfromUserId() {
    return this.fromUserId;
  }
}