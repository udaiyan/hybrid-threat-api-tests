package io.github.udaiyan.threatapi.app.web;

import io.github.udaiyan.threatapi.app.service.ThreatService;
import io.github.udaiyan.threatapi.model.Threat;
import io.github.udaiyan.threatapi.model.ThreatListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ThreatController {

    private final ThreatService service;

    public ThreatController(ThreatService service) {
        this.service = service;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @GetMapping("/threats")
    public ThreatListResponse getThreats(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity) {
        return service.search(page, size, status, severity);
    }

    @GetMapping("/threats/{threatId}")
    public ResponseEntity<Threat> getThreat(@PathVariable String threatId) {
        return service.findById(threatId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/threats")
    public ResponseEntity<Threat> createThreat(@RequestBody Threat threat) {
        Threat created = service.create(threat);
        return ResponseEntity
                .created(URI.create("/api/v1/threats/" + created.id()))
                .body(created);
    }
}
