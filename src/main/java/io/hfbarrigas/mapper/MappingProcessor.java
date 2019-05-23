package io.hfbarrigas.mapper;

import io.hfbarrigas.domain.Event;

public interface MappingProcessor<T extends Event> {
    T process(T e);
    boolean processable(String metric);
}
