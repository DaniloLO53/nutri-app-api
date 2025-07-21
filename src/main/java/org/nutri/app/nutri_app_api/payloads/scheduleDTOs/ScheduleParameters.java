package org.nutri.app.nutri_app_api.payloads.scheduleDTOs;

import lombok.Getter;

import java.time.LocalDate;

// Ideally, use @lombok.@Builder (we made it manually for educational proposes)
@Getter
public class ScheduleParameters {
    private final LocalDate startDate;
    private final LocalDate endDate;

    private ScheduleParameters(Builder builder) {
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
    }

    public static class Builder {
        private LocalDate startDate = LocalDate.now();
        private LocalDate endDate = LocalDate.now().plusWeeks(1);

        public Builder withStartDate(LocalDate startDate) {
            if (startDate != null) this.startDate = startDate;
            return this;
        }

        public Builder withEndDate(LocalDate endDate) {
            if (endDate != null) this.endDate = endDate;
            return this;
        }

        public ScheduleParameters build() {
            return new ScheduleParameters(this);
        }
    }
}
