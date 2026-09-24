package com.spotreport.spotreport;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

@Service
public class ReportService {
    private final Map<String, Report> reports = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public List<Report> findAll() {
        return reports.values().stream()
                .sorted((first, second) -> first.timestamp().compareTo(second.timestamp()))
                .toList();
    }

    public Report create(CreateReportRequest request) {
        String id = "SR-%03d".formatted(nextId.getAndIncrement());
        Report report = new Report(id, request.location().trim(), request.issue().trim(),
                request.description().trim(), "Reported", Instant.now());
        reports.put(id, report);
        return report;
    }

    public Report updateStatus(String id, String status) {
        return reports.computeIfPresent(id, (key, report) -> new Report(
                report.id(), report.location(), report.issue(), report.description(), status,
                report.timestamp()));
    }

    public record CreateReportRequest(String location, String issue, String description) {
    }
}
