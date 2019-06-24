package com.auth.java.converters;


import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface GenericConverter<D,E> {

    D convert(final E from);

    default List<D> convertList(final Collection<E> entities) {
        return entities.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }
}
