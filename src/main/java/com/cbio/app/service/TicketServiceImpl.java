package com.cbio.app.service;

import com.cbio.app.entities.TicketEntity;
import com.cbio.app.repository.TicketRepository;
import com.cbio.app.service.enuns.StatusTicketsEnum;
import com.cbio.app.service.mapper.TicketMapper;
import com.cbio.app.service.minio.MinioService;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.TicketService;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.MediaDTO;
import com.cbio.core.v1.dto.TicketDTO;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TicketServiceImpl implements TicketService {

    private final AuthService authService;

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final MinioService minioService;


    @Override
    public TicketDTO save(TicketDTO dto, MultipartFile image) {

        if(authService.getClaimsUserLogged().get("companyId") != null){
            String companyId = authService.getClaimsUserLogged().get("companyId").toString();
            dto.setCompany(CompanyDTO.builder()
                    .id(companyId)
                    .build());
            TicketEntity entity = ticketMapper.toEntity(dto);
            entity = ticketRepository.save(entity);

            // Upload da imagem para o MinIO, se existir
            if (image != null && !image.isEmpty()) {
                try {
                    // Gere um ID único para o arquivo
                    String fileId = UUID.randomUUID().toString();

                    // Defina o caminho/nome do arquivo no MinIO
                    String repo = "tickets/" + entity.getId();

                    // Faça o upload para o MinIO
                    minioService.putFile(
                            image,
                            image.getOriginalFilename(),
                            repo
                    );
                    // Armazene a URL da imagem no DTO

                    dto.setImagem(
                            MediaDTO.builder()
                                    .id(image.getOriginalFilename())
                                    .mimeType(image.getContentType())
                                    .build()
                    );
                } catch (IOException e) {
                    throw new RuntimeException("Erro ao processar a imagem", e);
                } catch (ServerException | InsufficientDataException | ErrorResponseException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                } catch (InvalidResponseException e) {
                    throw new RuntimeException(e);
                } catch (XmlParserException e) {
                    throw new RuntimeException(e);
                } catch (InternalException e) {
                    throw new RuntimeException(e);
                }
            }



            dto.setUserId(authService.getClaimsUserLogged().get("preferred_username").toString());
            dto.setStatus(StatusTicketsEnum.NOVO);
            ticketMapper.fromDto(dto, entity);
            return ticketMapper.toDto(ticketRepository.save(entity));

        }else{

            throw new RuntimeException("Você não está logado");
        }
    }

    @Override
    public TicketDTO update(TicketDTO dto, MultipartFile image) {
        TicketEntity entity = ticketRepository.findById(dto.getId()).orElseThrow();
        dto.setUserId(authService.getClaimsUserLogged().get("preferred_username").toString());

        // Upload da imagem para o MinIO, se existir
        if (image != null && !image.isEmpty()) {
            try {
                // Gere um ID único para o arquivo
                String fileId = UUID.randomUUID().toString();

                // Defina o caminho/nome do arquivo no MinIO
                String repo = "tickets/" + entity.getId();

                // Faça o upload para o MinIO
                minioService.putFile(
                        image,
                        image.getOriginalFilename(),
                        repo
                );
                // Armazene a URL da imagem no DTO

                dto.setImagem(
                        MediaDTO.builder()
                                .id(image.getOriginalFilename())
                                .mimeType(image.getContentType())
                                .build()
                );
            } catch (IOException e) {
                throw new RuntimeException("Erro ao processar a imagem", e);
            } catch (ServerException | InsufficientDataException | ErrorResponseException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (InvalidResponseException e) {
                throw new RuntimeException(e);
            } catch (XmlParserException e) {
                throw new RuntimeException(e);
            } catch (InternalException e) {
                throw new RuntimeException(e);
            }
        }



        ticketMapper.fromDto(dto, entity);



        return ticketMapper.toDto(ticketRepository.save(entity));
    }

    @Override
    public TicketDTO getById(String id) {

        TicketEntity ticketEntity = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Frase não encontrada."));
        return ticketMapper.toDto(ticketEntity);
    }

    @Override
    public void delete(String id) {
        String repo = "tickets/" + id;
        try {
            minioService.removeDir(repo);
            ticketRepository.findById(id).ifPresent(ticketRepository::delete);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        }

    }


}
