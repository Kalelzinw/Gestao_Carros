// Em src/main/java/com/example/gestao/carros/repositories/VehicleRepository.java
package com.example.gestao.carros.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // <-- Importe isto
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.gestao.carros.entities.Vehicle;

@Repository
// Adicione a herança aqui
public interface VehicleRepository extends JpaRepository<Vehicle, Integer>, JpaSpecificationExecutor<Vehicle> {
    
    List<Vehicle> findByUserId(Integer userId);
    
    @Modifying
    @Query(value = "DELETE FROM users_vehicles_favorites WHERE vehicle_id = :vehicleId", nativeQuery = true)
    void deleteFavoriteAssociations(@Param("vehicleId") Integer vehicleId);
}