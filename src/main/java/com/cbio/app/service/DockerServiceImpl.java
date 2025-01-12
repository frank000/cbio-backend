package com.cbio.app.service;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class DockerServiceImpl {

    public static final String CONTAINER_INIT_NAME = "con-%s";
    public static final String IMAGE_INIT_NAME = "rasa-%s";
    private static final Logger log = LoggerFactory.getLogger(DockerServiceImpl.class);

    @Value("${app.rasa.targe-path}")
    public String BASE_TARGET_DIR;

    @Async
    public void buildDockerImageAndRunContainer(String companyId, String externalPort) throws IOException, InterruptedException {

        String baseTargetDir = BASE_TARGET_DIR.concat(File.separator).concat(companyId);
        String imageName = getImageName(companyId);

        String command = "docker build -t " + imageName + " " + baseTargetDir;

        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("Erro ao executar docker build. Código de saída: " + exitCode);
        } else {
            runDockerContainer(imageName, externalPort);
        }
    }

    @NotNull
    public static String getImageName(String companyId) {
        return String.format(IMAGE_INIT_NAME, companyId);
    }

    // Executa o docker run
    public void runDockerContainer(String dockerImage, String externalPort) throws IOException, InterruptedException {

        String containerName = getContainerName(dockerImage);
        String command = String.format("docker run -d -p %s:5005 --name %s %s run", externalPort, containerName, dockerImage);

        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.inheritIO();  // Para ver a saída no console
        Process process = processBuilder.start();

        int exitCode = process.waitFor();  // Espera o processo terminar

        if (exitCode != 0) {
            throw new IOException("Erro ao executar docker run. Código de saída: " + exitCode);
        }else{
            log.info("Docker run cointainer {} completed successfully", containerName);
        }
    }

    @NotNull
    public static String getContainerName(String dockerImage) {
        return String.format(CONTAINER_INIT_NAME, dockerImage);
    }


    @Async
    public void executeCompleteDockerFlow(String companyId, String externalPort) throws IOException, InterruptedException {

        String imageName = getImageName(companyId);
        String containerName = getContainerName(imageName);
        String baseTargetDir = BASE_TARGET_DIR.concat(File.separator).concat(companyId);

        String scriptPath = "./dockerFlow.sh";
        StringBuilder stringBuilder = new StringBuilder();
        String space = " ";

        stringBuilder.append(scriptPath)
                .append(space)
                .append(imageName)
                .append(space)
                .append(containerName)
                .append(space)
                .append(baseTargetDir)
                .append(space)
                .append(externalPort);

        log.info(String.format("CONTAINER INIT: %s", stringBuilder.toString()));

        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", stringBuilder.toString());

        try {
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Script executado com sucesso.");
            } else {
                System.out.println("Erro ao executar o script. Código de saída: " + exitCode);
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isContainerRunning(String containerName) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("docker", "ps", "--filter", "name=" + containerName, "--format", "{{.Names}}");
            Process process = processBuilder.start();

            String output = new String(process.getInputStream().readAllBytes()).trim();
            return output.contains(containerName);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao verificar status do container: " + e.getMessage(), e);
        }
    }
}
