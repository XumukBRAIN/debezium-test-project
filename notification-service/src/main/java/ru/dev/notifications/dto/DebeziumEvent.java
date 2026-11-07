package ru.dev.notifications.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumEvent {

    private Payload payload;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload {

        private After after;

        /**
         *  c - create
         *  u - update
         *  d - delete
         */
        private String op;

        @Setter
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class After {

            private Long id;

            private String number;

            @JsonProperty("total_price")
            private BigDecimal totalPrice;

            @JsonProperty("creation_date")
            private Long creationDate;

            public LocalDate getCreationDate() {
                return creationDate != null ? LocalDate.ofEpochDay(creationDate) : null;
            }
        }
    }
}

