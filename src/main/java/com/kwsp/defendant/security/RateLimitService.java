package com.kwsp.defendant.security;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private static final int MAXIMUM_ATTEMPTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    private final Map<String, AttemptWindow> attempts =
            new ConcurrentHashMap<>();

    public boolean allow(String clientIdentifier) {
        Instant now = Instant.now();

        AttemptWindow updatedWindow = attempts.compute(
                clientIdentifier,
                (key, existingWindow) -> {
                    if (
                            existingWindow == null ||
                            now.isAfter(
                                    existingWindow.startedAt().plus(WINDOW)
                            )
                    ) {
                        return new AttemptWindow(now, 1);
                    }

                    return new AttemptWindow(
                            existingWindow.startedAt(),
                            existingWindow.count() + 1
                    );
                }
        );

        return updatedWindow.count() <= MAXIMUM_ATTEMPTS;
    }

    private record AttemptWindow(
            Instant startedAt,
            int count
    ) {
    }
}