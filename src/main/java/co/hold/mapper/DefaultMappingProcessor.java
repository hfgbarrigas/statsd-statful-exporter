package co.hold.mapper;

import co.hold.config.Mapping;
import co.hold.domain.DefaultEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
     * <p>
     * e.g:
     * mapping match -> test.dispatcher.*.*.*
     * mapping tags -> label:$1, label2: $2, label3: $3
     * metric -> test.dispatcher.FooProcessor.send.success
     * <p>
     * resulting tags -> label:FooProcessor, label2:send, label3: success
     *
     * @param metric - metric name
     * @param m      - mapping
     * @return tags extracted
     */
    private Map<String, String> tags(final String metric, final Mapping m) {
        Objects.requireNonNull(m);
        Objects.requireNonNull(m.getMatch());
        Objects.requireNonNull(metric);
        // refactor :)
        Map<String, String> tags = new HashMap<>();

        String[] globedData = m.getMatch().split("\\.");
        String[] metricData = metric.split("\\.");

        List<Map.Entry<String, String>> list = Optional.ofNullable(m.getTags())
                .map(x -> m.getTags().entrySet()
                        .stream()
                        .filter(es -> es.getValue().contains("$") || es.getKey().contains("$"))
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());

        if (!list.isEmpty()) {

            for (int i = 0, j = 0; i < globedData.length; i++) {
                Matcher matcher = Pattern.compile(".*?\\*.*?").matcher(globedData[i]);
                if (matcher.matches() && i < metricData.length) {
                    Map.Entry<String, String> es = list.get(j);

                    //remove non-wildcard parts we're not interested in
                    for (String part : globedData[i].split("\\*")){
                        metricData[i] = metricData[i].replace(part, "");
                    }

                    tags.put(es.getKey().replaceFirst("\\$.", metricData[i]),
                            es.getValue().replaceFirst("\\$.", metricData[i]));
                    j++;
                }
            }

        }

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
