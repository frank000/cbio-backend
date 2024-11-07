package com.cbio.core.v1.dto;

import lombok.*;

import java.io.Serializable;


@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ModelGridDTO implements Serializable {

    private String id;
    private String name;



}
