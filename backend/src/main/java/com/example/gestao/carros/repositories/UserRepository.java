package com.example.gestao.carros.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.gestao.carros.entities.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	   Optional<User> findByEmail(String email);

	   @Query(value = "SELECT COUNT(*) > 0 FROM users_vehicles_favorites WHERE user_id = :userId AND vehicle_id = :vehicleId", nativeQuery = true)
	    boolean isVehicleFavoritedByUser(@Param("userId") Integer userId, @Param("vehicleId") Integer vehicleId);
}
