package com.cbio.app.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DockerServiceImpl {

    public static final String CONTAINER_INIT_NAME = "con-%s";
    public static final String IMAGE_INIT_NAME = "rasa-%s";
    private static final Logger log = LoggerFactory.getLogger(DockerServiceImpl.class);

    @Value("${app.rasa.targe-path}")
    public String BASE_TARGET_DIR;


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

    @Async
    public void buildDockerImageAndRunContainer(String companyId, String externalPort) throws IOException, InterruptedException {

        String baseTargetDir = BASE_TARGET_DIR.concat(File.separator).concat(companyId);
        String imageName = getImageName(companyId);
        DockerClient client = getClient();

        if(imageExists(imageName)) {
            String containerName = getContainerName(imageName);

            stopAndRemoveContainer(client, containerName);
            client.stopContainerCmd(containerName).exec().wait();
            client.removeImageCmd(imageName).exec();

            log.info("CONTAINER REMOVED: Container {}", containerName);
            log.info("IMAGEM REMOVED: Imagem {}", imageName);
        }

        log.info("IMAGEM DIRECTORY: TO {}", baseTargetDir);

        String imageId = client.buildImageCmd(new File(baseTargetDir))
                .withTags(Set.of(imageName)) // Nome e tag da imagem
                .exec(new BuildImageResultCallback())
                .awaitImageId();// Retorna o ID da imagem criada

        log.info("IMAGEM CREATED: Imagem {}", imageId);

        if (StringUtils.hasText(imageId)) {
            runDockerContainer(imageName, externalPort);
        } else {
            String formatted = String.format("Erro ao executar docker build. Código de saída: %s", imageName);
            log.error(formatted);
            throw new IOException(formatted);
        }
    }

    @NotNull
    public static String getImageName(String companyId) {
        return String.format(IMAGE_INIT_NAME, companyId);
    }

    public void runDockerContainer(String dockerImage, String externalPort) throws IOException, InterruptedException {
        runDockerContainer(dockerImage, externalPort, getClient());
    }

    @Async
    public void runDockerContainer(String dockerImage, String externalPort, DockerClient client) throws IOException, InterruptedException {

        String containerName = getContainerName(dockerImage);
        Optional<Container> container = findContainer(containerName, client);
        try{
            if(container.isEmpty()){
                geraContainerERoda(dockerImage, externalPort, client, containerName);

            }else{
                stopAndRemoveContainer(containerName);
                geraContainerERoda(dockerImage, externalPort, client, containerName);
            }
        } catch (Exception e) {
            String msg = String.format("PROBLEMA: Geração ou restart de conteiner: %s", e.getMessage());
            log.error(msg, e);
        }

    }

    private static void geraContainerERoda(String dockerImage, String externalPort, DockerClient client, String containerName) throws IOException {
        String containerId = client.createContainerCmd(dockerImage)
                .withName(containerName) // Nome do container
                .withCmd("run")
                .withExposedPorts(ExposedPort.tcp(Integer.parseInt(externalPort))) // Expor a porta (se necessário)
                .withHostConfig(HostConfig.newHostConfig()
                        .withPortBindings(new PortBinding(Ports.Binding.bindPort(Integer.parseInt(externalPort)), ExposedPort.tcp(5005))) // Mapear portas
                )
                .exec()
                .getId();

        client.startContainerCmd(containerId).exec();

        log.info("CONTAINER STARTED: Container {}", containerId);

        if (!StringUtils.hasText(containerId)) {
            throw new IOException(String.format("Erro ao executar docker run. Código de saída: %s", containerName));
        } else {
            log.info("Docker run cointainer {} completed successfully, id {}", containerName, containerId);
        }
    }

    public boolean imageExists(String imageName) {
        try {
            DockerClient client = getClient();
            List<Image> images = client.listImagesCmd()
                    .withShowAll(true)
                    .exec();

            return images.stream()
                    .anyMatch(image -> image.getRepoTags() != null &&
                            Arrays.asList(image.getRepoTags()).contains(imageName));

        } catch (Exception e) {
            log.error("Erro ao verificar existência da imagem: " + imageName, e);
            throw new RuntimeException("Erro ao verificar existência da imagem: " + e.getMessage(), e);
        }
    }

    @NotNull
    public static String getContainerName(String dockerImage) {
        return String.format(CONTAINER_INIT_NAME, dockerImage);
    }
    @Async
    public void executeCompleteDockerFlow(String companyId, String externalPort) throws IOException, InterruptedException {
        executeCompleteDockerFlow(companyId, externalPort, getClient());
    }


    public void executeCompleteDockerFlow(String companyId, String externalPort, DockerClient client) throws IOException, InterruptedException {

        String imageName = getImageName(companyId);
        String containerName = getContainerName(imageName);

        if (imageExists(imageName)) {
            stopAndRemoveContainer(client, containerName);

            runDockerContainer(imageName, externalPort, client);

        } else {

            buildDockerImageAndRunContainer(companyId, externalPort);
        }
    }

    public void stopAndRemoveContainer(String containerName) {
        stopAndRemoveContainer(getClient(),containerName);
    }

    public Optional<Container> findContainer(String containerName, DockerClient client) {
        try {
            List<Container> containers = client.listContainersCmd()
                    .withShowAll(true)
                    .exec();

            return containers.stream()
                    .filter(container -> Arrays.asList(container.getNames())
                            .stream()
                            .anyMatch(name -> name.contains(containerName)))
                    .findFirst();

        } catch (Exception e) {
            log.error("Erro ao buscar container: " + containerName, e);
            throw new RuntimeException("Erro ao buscar container: " + e.getMessage(), e);
        }
    }

    public void restartContainer(String containerName, DockerClient client) throws IOException, InterruptedException {
        try {
            client.restartContainerCmd(containerName).exec();
            log.info("Container restartado: {}", containerName);
        } catch (Exception e) {
            log.warn("O container não estava rodando ou não existe: {}", containerName);
        }
    }
    public void stopAndRemoveContainer(DockerClient client, String containerName) {
        try {
            client.stopContainerCmd(containerName).exec();
            log.info("Container parado: {}", containerName);
        } catch (Exception e) {
            log.warn("O container não estava rodando ou não existe: {}", containerName);
        }

        try {
            client.removeContainerCmd(containerName).exec();
            log.info("Container removido: {}", containerName);
        } catch (Exception e) {
            log.warn("O container não existia:{}", containerName);
        }
    }

    public boolean isContainerRunning(String containerName) {
        return isContainerRunning(containerName, getClient());
    }

    public boolean isContainerRunning(String containerName, DockerClient client) {
        try {


            List<Container> containers = client.listContainersCmd().withShowAll(false) // Mostrar todos os containers (em execução ou não)
                    .exec();

            return containers.stream().anyMatch(container -> {
                return String.join(", ", container.getNames()).contains(containerName);
            });

        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar status do container: " + e.getMessage(), e);
        }
    }
}
