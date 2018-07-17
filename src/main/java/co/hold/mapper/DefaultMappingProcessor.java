package co.hold.mapper;

import co.hold.config.Mapping;
import co.hold.domain.DefaultEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;

public class DefaultMappingProcessor implements MappingProcessor<DefaultEvent> {

    private List<Mapping> mappings;

    public DefaultMappingProcessor(List<Mapping> mappings) {
        this.mappings = mappings;
    }

    @Override
    public DefaultEvent process(DefaultEvent e) {
        return mappings
                .stream()
                .filter(m -> this.matches(e.getMetricName(), m.getMatch()))
                .findFirst()
                .map(m -> DefaultEvent.builder(m.getName())
                        .type(e.getType())
                        .sampleRate(e.getSampleRate())
                        .value(e.getValue())
                        .tags(tags(e.getMetricName(), m))
                        .build())
                .orElse(e);
    }

    @Override
    public boolean processable(String metric) {
        return mappings
                .stream()
                .filter(m -> this.matches(metric, m.getMatch()))
                .findFirst()
                .map(m -> m.getAction().equals(Mapping.Action.MATCH))
                .orElse(true);
    }

    /**
     * extract tags given a specific mapping and a corresponding metric.
     *
     * e.g:
     * mapping match -> test.dispatcher.*.*.*
     * mapping tags -> label:$1, label2: $2, label3: $3
     * metric -> test.dispatcher.FooProcessor.send.success
     *
     * resulting tags -> label:FooProcessor, label2:send, label3: success
     *
     * @param metric - metric name
     * @param m - mapping
     * @return tags extracted
     */
    private Map<String, String> tags(final String metric, final Mapping m) {
        Objects.requireNonNull(m);
        Objects.requireNonNull(m.getMatch());
        Objects.requireNonNull(metric);

        Map<String, String> tags = new HashMap<>();
        String[] globedData = m.getMatch().split("\\.");
        String[] metricData = metric.split("\\.");

        IntStream.range(0, globedData.length)
                .forEach(i -> {
                    if ("*".equals(globedData[i]) && i < metricData.length) {
                        tags.putAll(m.getTags()
                                .entrySet()
                                .stream()
                                .filter(es -> es.getValue().contains("$" + i) || es.getKey().contains("$" + i))
                                .collect(toMap(es -> es.getKey().replace("$" + i, metricData[i]),
                                        es -> es.getValue().replace("$" + i, metricData[i]))));
                    }
                });

        return tags;
    }


    private boolean matches(final String metric, final String globedMatch) {
        Objects.requireNonNull(metric);
        Objects.requireNonNull(globedMatch);

        return Pattern.compile(
                globedMatch
                        .replace(".", "\\.")
                        .replace("*", ".+")
        ).matcher(metric).matches();
    }
}
