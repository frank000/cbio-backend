package com.cbio.app.service;

import com.cbio.core.v1.dto.ContainerInfo;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerPort;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DockerManagementService {


    public DockerClient getClient() {
        Path dockerSock = Paths.get("/var/run/docker.sock");

        if (!Files.exists(dockerSock)) {
            throw new RuntimeException("Docker socket não encontrado em: " + dockerSock);
        }

        if (!Files.isReadable(dockerSock)) {
            throw new RuntimeException("Docker socket não está acessível para leitura");
        }

        if (!Files.isWritable(dockerSock)) {
            throw new RuntimeException("Docker socket não está acessível para escrita");
        }
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .build();

        DockerHttpClient httpClient = new ZerodepDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }
    // Listar todos containers
    public List<ContainerInfo> listAllContainers() {
        DockerClient dockerClient = getClient();
        return dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec()
                .stream()
                .filter(container -> {
                    return Arrays.stream(container.getNames())
                            .findFirst()
                            .orElse("")
                            .replace("/", "").contains("con-rasa");
                })
                .map(this::convertToContainerInfo)
                .collect(Collectors.toList());
    }

    // Obter status de um container específico
    public ContainerInfo getContainerStatus(String containerId) {
        DockerClient dockerClient = getClient();
        List<Container> containers = dockerClient.listContainersCmd()
                .withIdFilter(Collections.singletonList(containerId))
                .exec();

        if (containers.isEmpty()) {
            throw new NotFoundException("Container não encontrado");
        }

        return convertToContainerInfo(containers.get(0));
    }

    // Reiniciar container
    public void restartContainer(String containerId) {
        DockerClient dockerClient = getClient();
        dockerClient.restartContainerCmd(containerId).exec();
    }

    // Rebuild e recriar container
    public void rebuildAndRestartContainer(String containerName, String imageName, String externalPort) throws IOException {
        // 1. Parar e remover container existente
        try {
            DockerClient dockerClient = getClient();

            dockerClient.stopContainerCmd(containerName).exec();
            dockerClient.removeContainerCmd(containerName).exec();
        } catch (NotFoundException e) {
            log.warn("Container não encontrado para remoção, continuando...");
        }

        // 2. Remover imagem antiga
        try {
            DockerClient dockerClient = getClient();
            dockerClient.removeImageCmd(imageName).exec();
        } catch (NotFoundException e) {
            log.warn("Imagem não encontrada para remoção, continuando...");
        }

        // 3. Build da nova imagem (assumindo que o Dockerfile está no contexto correto)
        BuildImageResultCallback callback = new BuildImageResultCallback() {
            @Override
            public void onNext(BuildResponseItem item) {
                log.info(item.getStream());
                super.onNext(item);
            }
        };
        DockerClient dockerClient = getClient();
        String imageId = dockerClient.buildImageCmd()
                .withDockerfile(new File("Dockerfile"))
                .withTags(Collections.singleton(imageName))
                .exec(callback)
                .awaitImageId();

        // 4. Criar e iniciar novo container
        geraContainerERoda(imageName, externalPort, dockerClient, containerName);
    }

    private ContainerInfo convertToContainerInfo(Container container) {
        ContainerInfo info = new ContainerInfo();
        info.setId(container.getId());
        info.setName(Arrays.stream(container.getNames())
                .findFirst()
                .orElse("")
                .replace("/", ""));
        info.setStatus(container.getStatus());
        info.setImage(container.getImage());
        info.setState(container.getState());

        // Mapeamento de portas
        ContainerPort[] ports = container.getPorts();
        info.setPorts(List.of(ports).stream()
                .map(p -> new ContainerInfo.PortMapping(
                        p.getPublicPort() != null ? p.getPublicPort().toString() : "",
                        p.getPrivatePort() != null ? p.getPrivatePort().toString() : ""))
                .collect(Collectors.toList()));

        return info;
    }

    private void geraContainerERoda(String dockerImage, String externalPort, DockerClient client, String containerName) throws IOException {
        // Sua implementação existente
    }
}