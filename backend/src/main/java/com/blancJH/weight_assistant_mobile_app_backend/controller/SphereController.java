package com.blancJH.weight_assistant_mobile_app_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blancJH.weight_assistant_mobile_app_backend.dto.SphereDTO;
import com.blancJH.weight_assistant_mobile_app_backend.service.SphereService;

@RestController
@RequestMapping("/api/spheres")
public class SphereController {

    private final SphereService sphereService;

    public SphereController(SphereService sphereService) {
        this.sphereService = sphereService;
    }

    @GetMapping("/get-all")
    public List<SphereDTO> getSpheres() {
        return sphereService.getAllSpheres();
    }
}
