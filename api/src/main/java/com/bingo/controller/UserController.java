package com.bingo.controller;

import com.bingo.dto.RoleUpdateRequest;
import com.bingo.dto.SuspendRequest;
import com.bingo.dto.XpUpdateRequest;
import com.bingo.model.User;
import com.bingo.service.PresenceService;
import com.bingo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PresenceService presenceService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PatchMapping("/{id}/xp")
    public ResponseEntity<User> updateXp(@PathVariable String id, @RequestBody XpUpdateRequest req) {
        return ResponseEntity.ok(userService.updateXp(id, req));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<User> updateRole(@PathVariable String id, @RequestBody RoleUpdateRequest req) {
        return ResponseEntity.ok(userService.updateRole(id, req));
    }

    @PatchMapping("/{id}/suspend")
    public ResponseEntity<User> toggleSuspension(@PathVariable String id, @RequestBody SuspendRequest req) {
        return ResponseEntity.ok(userService.toggleSuspension(id, req));
    }

    @PostMapping("/{id}/troll")
    public ResponseEntity<User> applyTrollTheme(@PathVariable String id) {
        return ResponseEntity.ok(userService.applyTrollTheme(id));
    }

    @DeleteMapping("/{id}/troll")
    public ResponseEntity<User> removeTrollTheme(@PathVariable String id) {
        return ResponseEntity.ok(userService.removeTrollTheme(id));
    }

    @GetMapping("/online")
    public ResponseEntity<Set<String>> getOnlineUsers() {
        return ResponseEntity.ok(presenceService.getOnlineUsers());
    }
}