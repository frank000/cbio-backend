package com.cbio.app.service.serder;

import com.cbio.app.client.InstagramFeignClient;
import com.cbio.app.client.TelegramClient;
import com.cbio.app.service.minio.MinioService;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.core.service.SessaoService;
import com.cbio.core.v1.enuns.InstagramEnuns;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("instagramSenderService")
@RequiredArgsConstructor
public class InstagramSenderService implements Sender {

    private final TelegramClient telegramClient;
    private final SessaoService sessaoService;
    private final InstagramFeignClient instagramFeignClient;
    private final MinioService minioService;

    private String token = "Bearer %s";

    @Override
    public void envia(DialogoDTO dialogoDTO) {

        try {

            InstagramFeignClient.InstagramMessage.InstagramMessageBuilder igBuilder = InstagramFeignClient.InstagramMessage.builder();

            igBuilder.recipient(InstagramFeignClient.InstagramMessage.Recipient.builder()
                    .id(dialogoDTO.getIdentificadorRemetente().split("_")[0])
                    .build());

            InstagramFeignClient.InstagramMessage.Message.MessageBuilder messageBuilder = InstagramFeignClient.InstagramMessage.Message.builder();

            boolean isImage = DialogoDTO.TypeMessageEnum.IMAGE.name().equals(dialogoDTO.getType());
            if (isImage) {
                String fileUrl = minioService.getFileUrl(dialogoDTO.getMedia().getId(), dialogoDTO.getChannelUuid());
                dialogoDTO.getMedia().setUrl(fileUrl);

                messageBuilder.attachment(
                        InstagramFeignClient.InstagramMessage.Message.Attachment.builder()
                                .type(InstagramEnuns.AttachmentTypeEnum.IMAGE.getValue())
                                .payload(
                                        InstagramFeignClient.InstagramMessage.Message.Attachment.Payload.builder()
                                                .url(fileUrl)
                                                .build()
                                )
                                .build()
                );

            }else if (!dialogoDTO.getButtons().isEmpty()) {

                List<InstagramFeignClient.InstagramMessage.Buttons> buttonsList = dialogoDTO
                        .getButtons()
                        .stream().map(button ->
                                InstagramFeignClient.InstagramMessage.Buttons.builder()
                                        .title(button.getTitle())
                                        .payload(button.getPayload())
                                        .type(InstagramEnuns.ButtonTypeEnum.POSTBACK.getValue())
                                        .build()
                        )
                        .collect(Collectors.toList());


                messageBuilder.attachment(
                        InstagramFeignClient.InstagramMessage.Message.Attachment.builder()
                                .type(InstagramEnuns.AttachmentTypeEnum.TEMPLATE.getValue())
                                .payload(
                                        InstagramFeignClient.InstagramMessage.Message.Attachment.Payload.builder()
                                                .templateType(InstagramEnuns.TemplateTypeEnum.BUTTON.getValue())
                                                .text(dialogoDTO.getMensagem())
                                                .buttons(buttonsList)
                                                .build()
                                )
                                .build()
                );
            }else{
                messageBuilder.text(dialogoDTO.getMensagem());
            }

            InstagramFeignClient.InstagramMessage message = igBuilder
                    .message(messageBuilder.build())
                    .build();


            instagramFeignClient.sendMeMessage(
                    String.format(token, dialogoDTO.getCanal().getApiKey()),
                    message
            );
        } catch (Exception e) {
            String msg = String.format("Envio para o canal Instagram com problema: %s", e.getMessage());
            throw new RuntimeException(msg);
        }

    }

}
