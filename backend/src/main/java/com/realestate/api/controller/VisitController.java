package com.realestate.api.controller;

import com.realestate.api.dto.mapper.VisitMapper;
import com.realestate.api.dto.request.VisitRequest;
import com.realestate.api.dto.response.VisitResponse;
import com.realestate.api.model.User;
import com.realestate.api.security.SecurityUtils;
import com.realestate.api.service.UserService;
import com.realestate.api.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;
    private final VisitMapper visitMapper;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<VisitResponse> scheduleVisit(@Valid @RequestBody VisitRequest request) {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        User visitor = userService.getCurrentUser(userEmail);

        return ResponseEntity.ok(
                visitMapper.toResponse(
                        visitService.scheduleVisit(
                                visitor,
                                request.getPropertyId(),
                                request.getScheduledDate(),
                                request.getScheduledTime(),
                                request.getNotes()
                        )
                )
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<VisitResponse>> getMyVisits() {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        User user = userService.getCurrentUser(userEmail);
        return ResponseEntity.ok(visitMapper.toResponseList(visitService.getMyVisits(user.getId())));
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<VisitResponse>> getIncomingVisits() {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        User agent = userService.getCurrentUser(userEmail);
        return ResponseEntity.ok(visitMapper.toResponseList(visitService.getIncomingVisits(agent.getId())));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<VisitResponse> updateVisitStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(visitMapper.toResponse(visitService.updateVisitStatus(id, body.get("status"), userEmail)));
    }
}
