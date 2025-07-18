package com.example.gestao.carros.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.gestao.carros.dto.UserResponseDTO;
import com.example.gestao.carros.dto.VehicleSummaryDTO;
import com.example.gestao.carros.entities.User;
import com.example.gestao.carros.infra.security.UserSpringDetails;
import com.example.gestao.carros.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService service;

	/**
	 * Endpoint para obter os dados do perfil do usuário autenticado.
	 * URL: GET /user/profile
	 */
	@GetMapping("/profile")
	public ResponseEntity<UserResponseDTO> getUserProfile() {
		User user = service.getLoggedUserProfile();
		UserResponseDTO responseDTO = new UserResponseDTO(
				user.getId(),
				user.getFotoUrl(),
				user.getName(),
				user.getEmail(),
				user.getTel(),
				user.getDateBorn()
		);
		return ResponseEntity.ok(responseDTO);
	}

	/**
	 * Endpoint unificado para ATUALIZAR o perfil do usuário logado.
	 * Pode receber dados de texto, um arquivo de foto, ou ambos.
	 * URL: PATCH /user/update
	 */
	@PatchMapping("/update")
	public ResponseEntity<UserResponseDTO> updateUser(
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "tel", required = false) String tel,
			@RequestParam(value = "password", required = false) String password,
			@RequestPart(value = "foto", required = false) MultipartFile fotoFile
	) {
		// 1. Pega o ID do usuário logado a partir do token de segurança.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserSpringDetails loggedUser = (UserSpringDetails) authentication.getPrincipal();
		Integer userId = loggedUser.getId();

		// 2. Chama o serviço atualizado, passando todos os possíveis dados.
		User updatedUser = service.updateLoggedUser(userId, email, tel, password, fotoFile);

		// 3. Cria e retorna um DTO de resposta com os dados atualizados.
		UserResponseDTO responseDTO = new UserResponseDTO(
				updatedUser.getId(),
				updatedUser.getFotoUrl(),
				updatedUser.getName(),
				updatedUser.getEmail(),
				updatedUser.getTel(),
				updatedUser.getDateBorn()
		);

		return ResponseEntity.ok(responseDTO);
	}

	/**
	 * Endpoint para DELETAR a conta do usuário logado.
	 * URL: DELETE /user/delete
	 */
	@DeleteMapping("/delete")
	public ResponseEntity<Void> deleteUser() {
	    // 1. Pega o ID do usuário logado a partir do token de segurança
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    UserSpringDetails loggedUser = (UserSpringDetails) authentication.getPrincipal();
	    Integer userId = loggedUser.getId();

	    // 2. Chama o serviço passando o ID do usuário
	    service.deleteLoggedUser(userId);
	    
	    // 3. Retorna a resposta de sucesso
	    return ResponseEntity.noContent().build();
	}
	
	
	@PostMapping("/favorites/{vehicleId}") 
	public ResponseEntity<Void> addFavorite (@PathVariable Integer vehicleId){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserSpringDetails loggedUser = (UserSpringDetails) authentication.getPrincipal();
		Integer userId = loggedUser.getId();
		
		service.addVehicleToFavorites(userId, vehicleId);
		
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/favorites")
    public ResponseEntity<List<VehicleSummaryDTO>> getFavorites() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserSpringDetails loggedUser = (UserSpringDetails) authentication.getPrincipal();
        Integer userId = loggedUser.getId();

        List<VehicleSummaryDTO> favorites = service.getUserFavorites(userId);
        
        return ResponseEntity.ok(favorites);
    }
	
	@DeleteMapping("/favorites/{vehicleId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Integer vehicleId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserSpringDetails loggedUser = (UserSpringDetails) authentication.getPrincipal();
        Integer userId = loggedUser.getId();

        service.removeVehicleFromFavorites(userId, vehicleId);

        // Retorna 204 No Content, padrão para deleção bem-sucedida
        return ResponseEntity.noContent().build();
    }
}
