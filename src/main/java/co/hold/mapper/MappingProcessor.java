package co.hold.mapper;

import co.hold.domain.Event;

public interface MappingProcessor<T extends Event> {
    T process(T e);
    boolean processable(String metric);
}
