package com.example.gestao.carros.repositories;

import com.example.gestao.carros.entities.Document;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
    // Encontra todos os documentos associados a um ID de veículo
    List<Document> findByVehicleId(Integer vehicleId);
}