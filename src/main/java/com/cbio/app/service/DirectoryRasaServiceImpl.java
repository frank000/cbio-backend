package com.cbio.app.service;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

@Service
public class DirectoryRasaServiceImpl {

    private static final String SOURCE_DIR = "/home/frank/workspace/cbio-rasa-default";
    public static final String BASE_TARGET_DIR = "/home/frank/rasas";


    public void copyRasaProject(String newProjectName) throws IOException {
        Path sourcePath = Path.of(SOURCE_DIR);
        Path targetPath = Path.of(BASE_TARGET_DIR, newProjectName);

        // Verifica se o diretório fonte existe
        if (!Files.exists(sourcePath)) {
            throw new IOException("Diretório fonte não encontrado: " + SOURCE_DIR);
        }

        // Cria o diretório base de destino se não existir
        Files.createDirectories(Path.of(BASE_TARGET_DIR));

        // Verifica se o diretório de destino já existe
        if (Files.exists(targetPath)) {
            throw new IOException("Diretório de destino já existe: " + targetPath);
        }

        // Copia o diretório recursivamente
        Files.walkFileTree(sourcePath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                new FileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        Path targetDir = targetPath.resolve(sourcePath.relativize(dir));
                        Files.createDirectory(targetDir);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Path targetFile = targetPath.resolve(sourcePath.relativize(file));
                        Files.copy(file, targetFile, StandardCopyOption.COPY_ATTRIBUTES);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) {
                        System.err.printf("Erro ao acessar arquivo %s: %s%n", file, exc.getMessage());
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                        return FileVisitResult.CONTINUE;
                    }
                });
    }

    public void deleteRasaProject(String projectName) throws IOException {
        Path projectPath = Path.of(BASE_TARGET_DIR, projectName);

        if (!Files.exists(projectPath)) {
            throw new IOException("Diretório não encontrado: " + projectPath);
        }

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


    // Método para validar o nome do projeto
    public boolean isValidProjectName(String projectName) {
        // Verifica se o nome do projeto contém apenas caracteres válidos
        return projectName != null &&
                projectName.matches("^[a-zA-Z0-9_-]+$") &&
                !projectName.isEmpty();
    }
}
