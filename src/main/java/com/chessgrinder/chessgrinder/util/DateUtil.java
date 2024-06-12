package com.chessgrinder.chessgrinder.util;

import javax.annotation.Nullable;
import java.time.*;

public final class DateUtil {

    public static Instant nowInstantAtUtc() {
        return ZonedDateTime.now()
                .withZoneSameInstant(ZoneOffset.UTC).toInstant();
    }

    @Nullable
    public static LocalDateTime atStartOfDay(@Nullable LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay();
    }

    private DateUtil() {
    }
}
