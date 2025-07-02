package com.cbio.app.web.controller.v1;

import com.cbio.app.service.DockerManagementService;
import com.cbio.app.web.SecuredRestController;
import com.cbio.core.v1.dto.ContainerInfo;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/app-manager")
public class AppManagerController implements SecuredRestController {

    private final DockerManagementService dockerService;

    @GetMapping
    public ResponseEntity<List<ContainerInfo>> listAllContainers() {
        return ResponseEntity.ok(dockerService.listAllContainers());
    }

    @GetMapping("/{containerId}")
    public ResponseEntity<ContainerInfo> getContainer(@PathVariable String containerId) {
        return ResponseEntity.ok(dockerService.getContainerStatus(containerId));
    }

    @PostMapping("/{containerId}/restart")
    public ResponseEntity<Void> restartContainer(@PathVariable String containerId) {
        dockerService.restartContainer(containerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{containerName}/rebuild")
    public ResponseEntity<Void> rebuildContainer(
            @PathVariable String containerName,
            @RequestParam String imageName,
            @RequestParam String externalPort) throws IOException {

        dockerService.rebuildAndRestartContainer(containerName, imageName, externalPort);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{containerId}/logs/full")
    public ResponseEntity<String> getFullContainerLogs(
            @PathVariable String containerId,
            @RequestParam(defaultValue = "all") String since) {

        try {
            // Usando StringBuffer para coletar os logs
            StringBuilder logsBuilder = new StringBuilder();

            dockerService.getClient().logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withTailAll()
                    .exec(new LogContainerResultCallback() {
                        @Override
                        public void onNext(Frame frame) {
                            logsBuilder.append(new String(frame.getPayload()));
                        }
                    })
                    .awaitCompletion();

            String logs = logsBuilder.toString();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=logs_" + containerId + ".txt")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(logs);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao obter logs: " + e.getMessage());
        }
    }
}
