package com.cbio.core.v1.dto;


import lombok.*;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ContactDTO  implements Serializable {
    private String id;

    private String name;

    private String email;

    private String path;

    private String phone;

    private String location;

    private String obs;

    private CompanyDTO company;

    private List<String> sessions;

}
