package com.example.gestao.carros.services;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.gestao.carros.dto.VehicleSummaryDTO;
import com.example.gestao.carros.entities.User;
import com.example.gestao.carros.entities.Vehicle;
import com.example.gestao.carros.infra.security.UserSpringDetails;
import com.example.gestao.carros.repositories.UserRepository;
import com.example.gestao.carros.repositories.VehicleRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // @RequiredArgsConstructor já cuida do construtor para campos 'final'
public class UserService {

	private final UserRepository repository;
	private final VehicleRepository vehiclerepository;
	private final PasswordEncoder passwordEncoder;
	private final FileStorageService fileStorageService;

	/**
	 * Obtém a entidade User completa do usuário atualmente autenticado.
	 */
	public User getLoggedUserProfile() {
		// 1. Pega a informação de autenticação do contexto de segurança.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// 2. Garante que o objeto de autenticação é do tipo que esperamos.
		if (!(authentication.getPrincipal() instanceof UserSpringDetails)) {
			// Isso pode acontecer se o token for inválido ou se a rota for acessada sem
			// token
			throw new IllegalStateException("Usuário não autenticado ou tipo de autenticação inesperado.");
		}

		// 3. Pega os detalhes do usuário que o Spring Security guardou.
		UserSpringDetails loggedUserDetails = (UserSpringDetails) authentication.getPrincipal();

		// 4. Retorna a entidade 'User' completa que está dentro do 'UserSpringDetails'.
		return loggedUserDetails.getUser();
	}

	/**
	 * MUDANÇA: Método unificado para atualizar o perfil do usuário logado. Aceita
	 * dados parciais (só o que o usuário quer mudar).
	 */
	@Transactional
	public User updateLoggedUser(Integer userId, String email, String tel, String password, MultipartFile fotoFile) {

		User userToUpdate = repository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("Usuário com ID " + userId + " não encontrado."));

		// Atualiza os campos de texto, SE eles foram enviados na requisição
		if (email != null && !email.isBlank()) {
			userToUpdate.setEmail(email);
		}
		if (tel != null && !tel.isBlank()) {
			userToUpdate.setTel(tel);
		}
		if (password != null && !password.isBlank()) {
			userToUpdate.setPassword(passwordEncoder.encode(password));
		}

		// Atualiza a foto, SE um novo arquivo de imagem foi enviado
		if (fotoFile != null && !fotoFile.isEmpty()) {
			// Deleta a foto de perfil antiga do disco, se houver uma
			if (userToUpdate.getFotoUrl() != null && !userToUpdate.getFotoUrl().isEmpty()) {
				fileStorageService.deleteFile(userToUpdate.getFotoUrl());
			}
			// Salva a nova foto e atualiza a URL no banco
			String newFotoUrl = fileStorageService.storeFile(fotoFile);
			userToUpdate.setFotoUrl(newFotoUrl);
		}

		return repository.save(userToUpdate);
	}

	/**
	 * MUDANÇA: Método para deletar a conta do usuário logado. Agora também apaga a
	 * foto de perfil do disco.
	 */
	@Transactional
	public void deleteLoggedUser(Integer userId) {
		User userToDelete = repository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("Usuário com ID " + userId + " não encontrado."));

		// 1. Apaga a foto de perfil do disco, se existir.
		if (userToDelete.getFotoUrl() != null && !userToDelete.getFotoUrl().isEmpty()) {
			fileStorageService.deleteFile(userToDelete.getFotoUrl());
		}

		// 2. Apaga o usuário do banco.
		// AVISO: Se o usuário tiver veículos, isso causará um erro de integridade.
		// A lógica para deletar os veículos associados precisaria ser adicionada aqui.
		repository.delete(userToDelete);
	}

	@Transactional
	public void addVehicleToFavorites(Integer userId, Integer vehicleId) {
		User user = repository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado"));
		Vehicle vehicle = vehiclerepository.findById(vehicleId)
				.orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado"));

		user.getFavorites().add(vehicle);

		repository.save(user);

	}
	
	
	 @Transactional
	    public void removeVehicleFromFavorites(Integer userId, Integer vehicleId) {
	        User user = repository.findById(userId)
	                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
	        Vehicle vehicle = vehiclerepository.findById(vehicleId)
	                .orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado."));

	        // Remove o veículo da lista de favoritos
	        user.getFavorites().remove(vehicle);
	        
	        repository.save(user);
	    }
	 
	 
	 @Transactional(readOnly = true)
	    public List<VehicleSummaryDTO> getUserFavorites(Integer userId) {
	        User user = repository.findById(userId)
	                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

	        // Pega a lista de entidades Vehicle e a transforma em uma lista de DTOs de resumo
	        return user.getFavorites().stream()
	                   .map(VehicleSummaryDTO::new)
	                   .toList();
	    }
}
