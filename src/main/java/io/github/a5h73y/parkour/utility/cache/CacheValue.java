package io.github.a5h73y.parkour.utility.cache;

import java.time.LocalDateTime;

public interface CacheValue<V> {
    V getValue();

    LocalDateTime getCreatedAt();
}