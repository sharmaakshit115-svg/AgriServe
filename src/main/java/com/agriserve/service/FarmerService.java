package com.agriserve.service;

import com.agriserve.entity.Farmer;
import com.agriserve.repository.FarmerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FarmerService {

    private final FarmerRepository farmerRepository;

    public Farmer getFarmerById(Long id){
        return farmerRepository.findById(id).orElse(null);
    }
}
