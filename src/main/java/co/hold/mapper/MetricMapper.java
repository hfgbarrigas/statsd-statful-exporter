package co.hold.mapper;

import co.hold.domain.Event;

import java.util.Collection;

public interface MetricMapper<T extends Event> {
    Iterable<T> map(Iterable<String> metricLines);
}
