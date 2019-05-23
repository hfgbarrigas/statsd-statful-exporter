package io.hfbarrigas.senders;

import io.hfbarrigas.domain.Event;
import reactor.core.publisher.Mono;

public interface MetricsSender<T extends Event> {
    Mono<Void> send(Iterable<T> events);
}
