package com.cbio.app.service.serder;

import com.cbio.app.service.minio.MinioService;
import com.cbio.app.service.minio.ResultGetFileFromMinio;
import com.cbio.app.service.utils.ParametersUtils;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.core.v1.dto.RasaMessageDTO;
import com.whatsapp.api.WhatsappApiFactory;
import com.whatsapp.api.configuration.ApiVersion;
import com.whatsapp.api.domain.media.FileType;
import com.whatsapp.api.domain.media.UploadResponse;
import com.whatsapp.api.domain.messages.*;
import com.whatsapp.api.domain.messages.response.MessageResponse;
import com.whatsapp.api.domain.messages.type.ButtonType;
import com.whatsapp.api.domain.messages.type.HeaderType;
import com.whatsapp.api.domain.messages.type.InteractiveMessageType;
import com.whatsapp.api.domain.templates.type.LanguageType;
import com.whatsapp.api.exception.WhatsappApiException;
import com.whatsapp.api.impl.WhatsappBusinessCloudApi;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Arrays;
import java.util.List;

@Component("whatsappSenderService")
@RequiredArgsConstructor
public class WhatsappSenderService implements Sender {

    private static final int QNTD_MAX_BUTTONS = 2;
    private static final Logger log = LoggerFactory.getLogger(WhatsappSenderService.class);
    private final MinioService minioService;
    private final ParametersUtils parametersUtils;

    @Override
    public void envia(DialogoDTO dialogoDTO) {

        try {
            Message message = null;
            WhatsappApiFactory factory = WhatsappApiFactory.newInstance(dialogoDTO.getCanal().getApiKey());
            WhatsappBusinessCloudApi whatsappBusinessCloudApi = factory.newBusinessCloudApi(ApiVersion.V20_0);

            boolean isDocument = DialogoDTO.TypeMessageEnum.DOCUMENT.name().equals(dialogoDTO.getType());
            boolean isImage = DialogoDTO.TypeMessageEnum.IMAGE.name().equals(dialogoDTO.getType());
            boolean isModel = DialogoDTO.TypeMessageEnum.MODEL.name().equals(dialogoDTO.getType());

            if (isDocument || isImage) {

                message = getMessageToDocumentImage(dialogoDTO, whatsappBusinessCloudApi);

            } else if (isModel) {

                message = getMessageModel(dialogoDTO);

            } else {

                if (dialogoDTO.getButtons() != null && dialogoDTO.getButtons().size() > 10) {
                    enviaListaDividida(dialogoDTO, whatsappBusinessCloudApi);
                } else {
                    message = getMessageTextButtonOption(dialogoDTO, whatsappBusinessCloudApi);
                }

            }

            if(message != null){
                MessageResponse messageResponse = whatsappBusinessCloudApi.sendMessage(dialogoDTO.getCanal().getIdCanal(), message);
                log.info("WHATSAPP SENDER {}", messageResponse.toString());
            }

        }catch (WhatsappApiException e){
            e.printStackTrace();
            String msg = String.format("WHATSAPP PROBLEMA: %s", e.getMessage());
            throw new RuntimeException(msg);
            //TODO criar registro de erros por cliente
        } catch (Exception e) {
            e.printStackTrace();
            String msg = String.format("PRBLEMA GENERICO: %s", e.getMessage());
            throw new RuntimeException(msg);
        }

    }

    private Message getMessageModel(DialogoDTO dialogoDTO) {
        com.whatsapp.api.domain.messages.Component<BodyComponent> bodyComponent = new BodyComponent();
        Message message;
        parametersUtils.mountAndPopulateDynamicParameters(dialogoDTO.getModel().getBody().getParameters(), dialogoDTO.getVariables());

        dialogoDTO
                .getModel()
                .getBody()
                .getParameters()
                .forEach(parameter -> {
                    bodyComponent.addParameter(new TextParameter(parameter.getValue()));
                });

        message = Message.MessageBuilder.builder()//
                .setTo("+" + dialogoDTO.getOnlyIdentificadorRementente())//
                .buildTemplateMessage(//
                        new TemplateMessage()//
                                .setLanguage(new Language(LanguageType.PT_BR))//
                                .setName(dialogoDTO.getModel().getName())//
                                .addComponent(bodyComponent)//

                );
        return message;
    }

    private Message getMessageToDocumentImage(DialogoDTO dialogoDTO, WhatsappBusinessCloudApi whatsappBusinessCloudApi) throws Exception {
        Message message;
        ResultGetFileFromMinio file = minioService.getResultGetFileFromMinio(dialogoDTO);


        FileType fileType = Arrays.stream(FileType.values())
                .filter(fileType1 -> fileType1.getType().equals(dialogoDTO.getMedia().getMimeType()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Tipo do arquivo não encontrada."));
        UploadResponse name = whatsappBusinessCloudApi.uploadMedia(dialogoDTO.getCanal().getIdCanal(), file.metadata().get("name"), fileType, file.fileBytes());

        var documentMessage = new DocumentMessage()//
                .setId(name.id())// media id (uploaded before)
                .setCaption(file.metadata().get("name"))//
                .setFileName(file.metadata().get("name"));

        return Message.MessageBuilder.builder()//
                .setTo(dialogoDTO.getOnlyIdentificadorRementente())//
                .buildDocumentMessage(documentMessage);
    }




    private void enviaListaDividida(DialogoDTO dialogoDTO, WhatsappBusinessCloudApi whatsappBusinessCloudApi) {
        List<RasaMessageDTO.Button> itens = dialogoDTO.getButtons();
        int totalItens = itens.size();
        int limite = 10; // Limite de itens por mensagem

        for (int i = 0; i < totalItens; i += limite) {
            List<RasaMessageDTO.Button> subLista = itens.subList(i, Math.min(i + limite, totalItens));

            // Cria uma cópia do DTO com a sublista de botões
            DialogoDTO dialogoCopy =  DialogoDTO.builder().build();
            BeanUtils.copyProperties(dialogoDTO, dialogoCopy); // Copia as propriedades do DTO original
            dialogoCopy.setButtons(subLista);

            // Envia a mensagem com a sublista
            Message message = montaOptions(dialogoCopy);
            whatsappBusinessCloudApi.sendMessage(dialogoCopy.getCanal().getIdCanal(), message);
        }
    }

    private static Message getMessageTextButtonOption(DialogoDTO dialogoDTO, WhatsappBusinessCloudApi whatsappBusinessCloudApi) {
        Message message;
        if (CollectionUtils.isEmpty(dialogoDTO.getButtons())) {
            // Mensagem de texto simples
            message = Message.MessageBuilder.builder()
                    .setTo("+" + dialogoDTO.getOnlyIdentificadorRementente())
                    .buildTextMessage(new TextMessage()
                            .setBody(dialogoDTO.getMensagem())
                            .setPreviewUrl(false));

        } else if (dialogoDTO.getButtons().size() <= QNTD_MAX_BUTTONS) {
            // Mensagem com botões
            List<RasaMessageDTO.Button> itens = dialogoDTO.getButtons();
            Action actionButtons = new Action();
            itens.forEach(button -> {
                actionButtons.addButton(
                        new Button()
                                .setType(ButtonType.REPLY)
                                .setReply(new Reply()
                                        .setId(button.getPayload())
                                        .setTitle(button.getTitle())
                                )
                );
            });

            message = Message.MessageBuilder.builder()
                    .setTo("+" + dialogoDTO.getOnlyIdentificadorRementente())
                    .buildInteractiveMessage(
                            InteractiveMessage.build()
                                    .setAction(actionButtons)
                                    .setType(InteractiveMessageType.BUTTON)
                                    .setBody(new Body()
                                            .setText(dialogoDTO.getMensagem())
                                    )
                    );

        } else {
            message = montaOptions(dialogoDTO);
        }

        return message;
    }

    private static Message montaOptions(DialogoDTO dialogoDTO) {
        Message message;
        // Mensagem com lista de opções
        List<RasaMessageDTO.Button> itens = dialogoDTO.getButtons();
        Action actionButtons = new Action();
        actionButtons.setButtonText("Escolha uma opção"); // Texto do botão que abre a lista

        Section opcoes = new Section()
                .setTitle("Opções");
        itens.forEach(button -> {
            opcoes.addRow(new Row()
                    .setId(button.getPayload())
                    .setTitle(button.getTitle())
                    .setDescription(button.getPayload())
            );
        });

        actionButtons.addSection(opcoes);

        message = Message.MessageBuilder.builder()
                .setTo("+" + dialogoDTO.getOnlyIdentificadorRementente())
                .buildInteractiveMessage(
                        InteractiveMessage.build()
                                .setAction(actionButtons)
                                .setType(InteractiveMessageType.LIST)
                                .setHeader(new Header()
                                        .setType(HeaderType.TEXT)
                                        .setText(dialogoDTO.getMensagem()))
                                .setBody(new Body()
                                        .setText("Escolha uma opção da lista")) // Corpo da mensagem obrigatório
                );
        return message;
    }

}
