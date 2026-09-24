package com.spotreport.spotreport;

import java.time.Instant;

public record Report(
        String id,
        String location,
        String issue,
        String description,
        String status,
        Instant timestamp) {
}
