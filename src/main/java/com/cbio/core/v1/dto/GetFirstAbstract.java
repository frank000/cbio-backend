package com.cbio.core.v1.dto;

import java.util.List;
import java.util.Optional;

public abstract class GetFirstAbstract<T>{
    // Método abstrato que receberá o tipo de retorno desejado
    public abstract List<T> getContents();

    // Método getFirst para obter o primeiro elemento da lista
    public Optional<T> getFirsts() {
        List<T> items = getContents();
        return items != null && !items.isEmpty() ? Optional.of(items.get(0)) : Optional.empty();
    }
}
