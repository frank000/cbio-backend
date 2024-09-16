package com.cbio.app.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

@FeignClient(name="telegramClient", url = "${telegram.url}")
public interface TelegramClient {

    @PostMapping("bot{token}/sendMessage")
    Optional<ApiResponse<Message>> sendMessage(@PathVariable String token, SendMessage sendMessage);
//
//    @PostMapping("{token}/sendMessage")
//    Optional<ApiResponse<Message>> sendMessage(@PathVariable String token, MessageTextDTO sendMessage);
//
//
//    @PostMapping("{token}/sendPhoto")
//    Optional<ApiResponse<Message>> sendPhoto(@PathVariable String token, MessageFotoDTO sendPhoto);
//
//    @PostMapping("{token}/sendVideo")
//    Optional<ApiResponse<Message>> sendVideo(@Path1Variable String token, MessageVideoDTO sendVideo);
//
//    @PostMapping("{token}/sendAudio")
//    Optional<ApiResponse<Message>> sendAudio(@PathVariable String token, MessageAudioDTO sendAudio);
//
//    @PostMapping("{token}/sendDocument")
//    Optional<ApiResponse<Message>> sendDocument(@PathVariable String token, MessageDocumentoDTO sendDocument);
//
//    @PostMapping(value = "{token}/sendDocument", consumes = "multipart/form-data", produces = "multipart/form-data")
//    Optional<ApiResponse<Message>> sendDocument(@PathVariable String token, @RequestPart("caption") String caption, @RequestPart("chat_id") String chatId, @RequestPart("document") MultipartFile file);
//
//    @PostMapping("{token}/sendVenue")
//    Optional<ApiResponse<Message>> sendVenue(@PathVariable String token, SendVenue sendVenue);
//
//    @PostMapping("{token}/sendContact")
//    Optional<ApiResponse<Message>> sendContact(@PathVariable String token, SendContact sendContact);
//
//    @GetMapping("{token}/getFile")
//    ApiResponse<File> getFileInfoById(@PathVariable String token, @RequestPart("file_id") String fileId);
}
