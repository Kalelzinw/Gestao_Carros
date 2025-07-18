package com.example.gestao.carros.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gestao.carros.entities.Relatory;

public interface RelatoryRepository extends JpaRepository<Relatory, Integer> {
	 List<Relatory> findByVehicleId(Integer vehicleId);
}
