package com.cbio.app.web.controller.v1;

import com.cbio.app.service.DockerManagementService;
import com.cbio.app.web.SecuredRestController;
import com.cbio.core.v1.dto.ContainerInfo;
import lombok.RequiredArgsConstructor;
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

}
