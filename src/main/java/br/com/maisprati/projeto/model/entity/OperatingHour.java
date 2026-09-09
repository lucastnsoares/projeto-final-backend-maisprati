package br.com.maisprati.projeto.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public class OperatingHour {
        @Enumerated(EnumType.STRING)
        @Column(name = "day_of_week", nullable = false)
        private DayOfWeek dayOfWeek;

        @Column(name = "opening_time", nullable = false)
        private LocalTime openingTime;

        @Column(name = "closing_time", nullable = false)
        private LocalTime closingTime;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof OperatingHour that)) return false;
            return dayOfWeek == that.dayOfWeek &&
                    Objects.equals(openingTime, that.openingTime) &&
                    Objects.equals(closingTime, that.closingTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dayOfWeek, openingTime, closingTime);
        }
}
