package com.cbio.core.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContainerInfo {
    private String id;
    private String name;
    private String status;
    private String image;
    private List<PortMapping> ports;
    private String state;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PortMapping {
        private String hostPort;
        private String containerPort;

        // Getters e Setters
    }
}

