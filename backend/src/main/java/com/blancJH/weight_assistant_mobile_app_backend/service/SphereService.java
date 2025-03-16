package com.blancJH.weight_assistant_mobile_app_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.blancJH.weight_assistant_mobile_app_backend.dto.SphereDTO;
import com.blancJH.weight_assistant_mobile_app_backend.model.Sphere;
import com.blancJH.weight_assistant_mobile_app_backend.repository.SphereRepository;

@Service
public class SphereService {

    private final SphereRepository sphereRepository;

    public SphereService(SphereRepository sphereRepository) {
        this.sphereRepository = sphereRepository;
    }

    public List<SphereDTO> getAllSpheres() {
        List<Sphere> spheres = sphereRepository.findAll();
        // Convert Sphere entity to DTO
        return spheres.stream().map(s -> {
            SphereDTO dto = new SphereDTO();
            dto.setId(s.getId());
            dto.setSphereName(s.getSphereName());
            dto.setSphereUrl(s.getSphereUrl());
            dto.setSphereRank(s.getSphereRank());
            return dto;
        }).collect(Collectors.toList());
    }
}
