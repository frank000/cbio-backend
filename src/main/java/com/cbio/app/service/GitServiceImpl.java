package com.cbio.app.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class GitServiceImpl {


    /**
     * Realiza o clone de um repositório Git
     * @param repositoryUrl URL do repositório Git
     * @param localPath Caminho local onde o repositório será clonado
     * @param branch Nome do branch (opcional)
     * @return File objeto representando o diretório do repositório clonado
     */
    public File cloneRepository(String repositoryUrl, String localPath, String branch) throws GitAPIException {
        try {
            // Configura o comando de clone
            Git.cloneRepository()
                    .setURI(repositoryUrl)
                    .setDirectory(new File(localPath))
                    .setBranch(branch)
                    .call();

            return new File(localPath);
        } catch (GitAPIException e) {
            throw new GitAPIException("Erro ao clonar repositório: " + e.getMessage(), e) {};
        }
    }

    /**
     * Versão do método para repositórios privados que necessitam autenticação
     */
    public File clonePrivateRepository(String repositoryUrl,
                                       String localPath,
                                       String branch,
                                       String username,
                                       String password) throws GitAPIException {
        try {
            // Configura as credenciais
            org.eclipse.jgit.transport.CredentialsProvider credentialsProvider =
                    new org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider(username, password);

            // Configura o comando de clone com autenticação
            Git.cloneRepository()
                    .setURI(repositoryUrl)
                    .setDirectory(new File(localPath))
                    .setBranch(branch)
                    .setCredentialsProvider(credentialsProvider)
                    .call();

            return new File(localPath);
        } catch (GitAPIException e) {
            throw new GitAPIException("Erro ao clonar repositório privado: " + e.getMessage(), e) {};
        }
    }
}
