package com.cbio.app.entities;

import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.UsuarioDTO;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document("contact")
@Getter
@Setter
@Builder
public class ContactEntity {
    
    @Id
    private String id;

    private String name;

    private String path;

    private String email;

    private String phone;

    private String location;

    private String obs;

    private CompanyDTO company;

    @Getter(AccessLevel.NONE)
    private List<String> sessions;

    public List<String> getSessions() {
        if(sessions == null){
            sessions = new ArrayList<>();
        }
        return sessions;
    }
}
