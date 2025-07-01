package com.cbio.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
public class DirectoryRasaServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(DirectoryRasaServiceImpl.class);
    private final RestTemplate restTemplate;
    @Value("${app.rasa.default-path}")
    private String SOURCE_DIR;

    @Value("${app.rasa.targe-path}")
    public String BASE_TARGET_DIR;

    @Value("${app.github.token}")
    public String tokenGitHub;

    public DirectoryRasaServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CompletableFuture<Void> copyRasaProjectAsync(String newProjectName) {
        return CompletableFuture.supplyAsync(() -> {
            return createGitHubRepository(newProjectName, false);
        }).thenCompose(repoUrl -> {
            // Garante que o clone seja completado antes de continuar
            return cloneRepositoryAsync(repoUrl, newProjectName);
        });
    }

    private CompletableFuture<Void> cloneRepositoryAsync(String repoUrl, String projectName) {
        return CompletableFuture.runAsync(() -> {
            Path targetPath = Path.of(BASE_TARGET_DIR, projectName);

            if (Files.exists(targetPath)) {
                throw new CompletionException(
                        new IOException("Diretório de destino já existe: " + targetPath));
            }

            try {
                // Verificação mais robusta do processo Git
                ProcessBuilder builder = new ProcessBuilder(
                        "git", "clone", repoUrl, targetPath.toString())
                        .directory(new File(BASE_TARGET_DIR))  // Define diretório de trabalho
                        .redirectErrorStream(true);

                Process process = builder.start();

                // Ler a saída do processo para debug
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("Git: {}", line);
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new IOException("Git clone falhou com código " + exitCode);
                }

                // Verificação adicional - confirma que os arquivos foram clonados
                if (!Files.exists(targetPath.resolve(".git"))) {
                    throw new IOException("Clone não foi completado corretamente");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CompletionException(new IOException("Clonagem interrompida", e));
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        });
    }
    public void copyRasaProject(String newProjectName) throws IOException {
        // 1. Criar o repositório no GitHub
        String repoUrl = createGitHubRepository(newProjectName, false);

        // 2. Clonar o repositório para o diretório local
        cloneRepository(repoUrl, newProjectName);
    }
    private String createGitHubRepository(String repoName, boolean isPrivate) {
        String apiUrl = "https://api.github.com/repos/frank000/cbio-rasa-default/generate";

        String requestBody = String.format("{\"name\":\"%s\", \"private\":%b}", repoName, isPrivate);

        String response = restTemplate.postForObject(apiUrl,
                new HttpEntity<>(requestBody, createHeadersWithAuth()),
                String.class);

        // Extrai a URL do repositório da resposta
        // (aqui você precisaria parsear o JSON da resposta)
        return "https://github.com/frank000/" + repoName + ".git";
    }

//TODO tirar o token
    private HttpHeaders createHeadersWithAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("token %s", tokenGitHub));
        headers.set("Accept", "application/vnd.github.v3+json");
        return headers;
    }



    private void cloneRepository(String repoUrl, String projectName) throws IOException {
        Path targetPath = Path.of(BASE_TARGET_DIR, projectName);

        if (Files.exists(targetPath)) {
            throw new IOException("Diretório de destino já existe: " + targetPath);
        }

        // Executa o comando git clone
        ProcessBuilder builder = new ProcessBuilder(
                "git", "clone", repoUrl, targetPath.toString());
        builder.redirectErrorStream(true);

        try {
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Falha ao clonar repositório");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Clonagem interrompida", e);
        }
    }
//    public void copyRasaProject(String newProjectName) throws IOException {
//        Path sourcePath = Path.of(SOURCE_DIR);
//        Path targetPath = Path.of(BASE_TARGET_DIR, newProjectName);
//
//        // Verifica se o diretório fonte existe
//        if (!Files.exists(sourcePath)) {
//            throw new IOException("Diretório fonte não encontrado: " + SOURCE_DIR);
//        }
//
//        // Cria o diretório base de destino se não existir
//        Files.createDirectories(Path.of(BASE_TARGET_DIR));
//
//        // Verifica se o diretório de destino já existe
//        if (Files.exists(targetPath)) {
//            throw new IOException("Diretório de destino já existe: " + targetPath);
//        }
//
//        // Copia o diretório recursivamente
//        Files.walkFileTree(sourcePath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
//                new FileVisitor<Path>() {
//                    @Override
//                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
//                        Path targetDir = targetPath.resolve(sourcePath.relativize(dir));
//                        Files.createDirectory(targetDir);
//                        return FileVisitResult.CONTINUE;
//                    }
//
//                    @Override
//                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                        Path targetFile = targetPath.resolve(sourcePath.relativize(file));
//                        Files.copy(file, targetFile, StandardCopyOption.COPY_ATTRIBUTES);
//                        return FileVisitResult.CONTINUE;
//                    }
//
//                    @Override
//                    public FileVisitResult visitFileFailed(Path file, IOException exc) {
//                        System.err.printf("Erro ao acessar arquivo %s: %s%n", file, exc.getMessage());
//                        return FileVisitResult.CONTINUE;
//                    }
//
//                    @Override
//                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
//                        return FileVisitResult.CONTINUE;
//                    }
//                });
//    }

    public void deleteRasaProject(String projectName) throws IOException {
        Path projectPath = Path.of(BASE_TARGET_DIR, projectName);

        if (!Files.exists(projectPath)) {
            IOException ioException = new IOException("Diretório não encontrado: " + projectPath);
            log.error(String.format("Diretório não encontrado: %s", projectName), ioException);
        }else{
            // Deleta recursivamente o diretório e seu conteúdo
            Files.walkFileTree(projectPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc != null) throw exc;
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }


    // Método para validar o nome do projeto
    public boolean isValidProjectName(String projectName) {
        // Verifica se o nome do projeto contém apenas caracteres válidos
        return projectName != null &&
                projectName.matches("^[a-zA-Z0-9_-]+$") &&
                !projectName.isEmpty();
    }
}
