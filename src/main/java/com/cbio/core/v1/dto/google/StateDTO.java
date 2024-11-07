package com.cbio.core.v1.dto.google;

import lombok.*;

import java.io.*;
import java.util.Base64;

@Getter
@Setter
@Builder
@NoArgsConstructor // Necessário para a desserialização
@AllArgsConstructor // Para ter um construtor com todos os parâmetros
public class StateDTO implements Serializable {

    private String companyMail;
    private String companyId;
    private String urlRefered;
    public static String encodeToBase64(StateDTO state) throws Exception {
        // Serializa o objeto em um array de bytes
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(state);
        objectOutputStream.close();

        // Obtém os bytes do objeto serializado
        byte[] serializedBytes = byteArrayOutputStream.toByteArray();

        // Codifica os bytes em Base64
        return Base64.getEncoder().encodeToString(serializedBytes);
    }

    // Método para decodificar de Base64 e desserializar o objeto
    public static StateDTO decodeFromBase64(String base64String) throws Exception {
        // Decodifica a string Base64 de volta para bytes
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);

        // Converte os bytes de volta para um objeto Java desserializando
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decodedBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        StateDTO state = (StateDTO) objectInputStream.readObject();
        objectInputStream.close();

        return state;
    }
}
