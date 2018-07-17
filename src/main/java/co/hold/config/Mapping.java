package co.hold.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Map;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Mapping {

    public enum Action {
        DROP("drop"),
        MATCH("match");

        private String name;

        Action(String name) {
            this.name = name;
        }

        @JsonCreator
        public static Action fromValue(String value) {
            return Arrays.stream(values())
                    .filter(v -> v.name.equals(value))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }

        public String getName() {
            return this.name;
        }
    }

    @JsonProperty(required = true)
    private String match;
    @JsonProperty(required = true)
    private String name;
    @JsonProperty
    private Map<String, String> tags;
    @JsonProperty(defaultValue = "match", required = true)
    private Action action;

}
