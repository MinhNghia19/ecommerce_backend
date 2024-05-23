package com.example.ecommerce_backend.controller;

import com.example.ecommerce_backend.models.Role;
import com.example.ecommerce_backend.responses.ResponseObject;
import com.example.ecommerce_backend.services.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/roles")
public class RoleContronller {
    private final RoleService roleService;
    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllrole(){
        List<Role> role = roleService.getAllRoles();
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get all role successfully")
                .status(HttpStatus.OK)
                .data(role)
                .build());
    }
}
