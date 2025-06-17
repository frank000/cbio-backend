package com.cbio.app.web.controller.v1;


import com.cbio.app.service.minio.MinioService;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.DialogoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/media")
@RequiredArgsConstructor
@Slf4j
public class MediaController {

    private final DialogoService dialogoService;
    private final MinioService minioService;
    private final AuthService authService;

    @GetMapping("/images/ticket/{ticketId}/{imageName}")
    public ResponseEntity<InputStreamResource> downloadImage(
            @PathVariable String imageName,
            @PathVariable String ticketId) {
        try {
            // Verifique se o arquivo é realmente uma imagem (opcional)
            if (!imageName.matches(".*\\.(jpg|jpeg|png|gif)$")) {
                return ResponseEntity.badRequest().build();
            }

            String repositorio = "tickets/" + ticketId;
            return minioService.getResponseEntityFile(imageName, repositorio);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/media-by-dialog/{dialogId}")
    public ResponseEntity<InputStreamResource> getMedia(@PathVariable String dialogId) {
        DialogoDTO dialogoDTO = dialogoService.getById(dialogId);
        try {

            ResponseEntity<InputStreamResource> file = minioService.getResponseEntityFile(dialogoDTO.getMedia().getId(), dialogoDTO.getChannelUuid());
            // Retorna a imagem com o MIME type correto
            return file;
        } catch (Exception e) {
            log.error("Erro ao baixar a mídia", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download-media-by-dialog/{dialogId}")
    public ResponseEntity<InputStreamResource> dowload(@PathVariable String dialogId) {
        DialogoDTO dialogoDTO = dialogoService.getById(dialogId);
        try {

            ResponseEntity<InputStreamResource> file = minioService.getResponseEntityFile(dialogoDTO.getMedia().getId(), dialogoDTO.getChannelUuid());

//            file.getHeaders().setContentType(MediaTypec.parseMediaType(dialogoDTO.getMedia().getMimeType()));
     //       file.getHeaders().set("Content-Disposition", "attachment; filename=\"" + file.getBody().getFilename() + "\"");
            // Retorna a imagem com o MIME type correto
            return file;
        } catch (Exception e) {
            log.error("Erro ao baixar a mídia", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
