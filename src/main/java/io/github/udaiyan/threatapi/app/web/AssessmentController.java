package io.github.udaiyan.threatapi.app.web;

import io.github.udaiyan.threatapi.model.Assessment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AssessmentController {

    @GetMapping("/assessments")
    public List<Assessment> getAssessments() {
        return List.of(new Assessment(
                "99999999-9999-9999-9999-999999999999",
                "11111111-1111-1111-1111-111111111111",
                "Synthetic assessment for demonstration purposes",
                Instant.parse("2026-10-01T12:00:00Z")
        ));
    }
}
