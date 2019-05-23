package io.hfbarrigas.mapper;

import io.hfbarrigas.domain.Event;

public interface MetricMapper<T extends Event> {
    Iterable<T> map(Iterable<String> metricLines);
}
