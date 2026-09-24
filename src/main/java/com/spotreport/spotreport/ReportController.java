package com.spotreport.spotreport;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private static final Set<String> STATUSES = Set.of(
            "Reported", "Under Review", "In Progress", "Resolved");

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public List<Report> getReports() {
        return reportService.findAll();
    }

    @PostMapping
    public Report createReport(@RequestBody ReportService.CreateReportRequest request) {
        if (request == null || blank(request.location()) || blank(request.issue())
                || blank(request.description())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Location, issue, and description are required.");
        }
        return reportService.create(request);
    }

    @PutMapping("/{id}/status")
    public Report updateStatus(@PathVariable String id, @RequestBody StatusRequest request) {
        if (request == null || !STATUSES.contains(request.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid report status.");
        }
        Report updated = reportService.updateStatus(id, request.status());
        if (updated == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found.");
        }
        return updated;
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    public record StatusRequest(String status) {
    }
}
