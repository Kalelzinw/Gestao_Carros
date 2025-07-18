package com.example.gestao.carros.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.gestao.carros.dto.VehicleCreateDTO;
import com.example.gestao.carros.dto.VehicleResponseDTO;
import com.example.gestao.carros.dto.VehicleSummaryDTO;
import com.example.gestao.carros.dto.VehicleUpdateDTO;
import com.example.gestao.carros.entities.Document;
import com.example.gestao.carros.entities.User;
import com.example.gestao.carros.entities.Vehicle;
import com.example.gestao.carros.exceptions.ResourceNotFoundException;
import com.example.gestao.carros.infra.security.UserSpringDetails;
import com.example.gestao.carros.repositories.DocumentRepository;
import com.example.gestao.carros.repositories.UserRepository;
import com.example.gestao.carros.repositories.VehicleRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleService {

	private final VehicleRepository vehicleRepository;
	private final UserRepository userRepository;
	private final DocumentRepository documentRepository;
	private final FileStorageService fileStorageService;

	@Transactional
	// MUDANÇA 1: Nome do método e assinatura atualizados para receber o arquivo de imagem.
	public VehicleResponseDTO createVehicleWithFiles(VehicleCreateDTO dto, MultipartFile pdfFile, MultipartFile imageFile) {

		// Busca o usuário que será o dono do veículo.
		User owner = userRepository.findById(dto.getUserId()).orElseThrow(
				() -> new EntityNotFoundException("Usuário com ID " + dto.getUserId() + " não encontrado."));

		// MUDANÇA 2: Salva a imagem no disco e obtém o nome único gerado.
		String imageFileName = fileStorageService.storeFile(imageFile);

		// Salva o PDF no disco e obtém o nome único gerado.
		String pdfFileName = fileStorageService.storeFile(pdfFile);

		// Cria a nova entidade Vehicle.
		Vehicle vehicleEntity = new Vehicle();

		// Mapeia os campos do DTO para a entidade.
		vehicleEntity.setMarca(dto.getMarca());
		vehicleEntity.setModelo(dto.getModelo());
		vehicleEntity.setAno(dto.getAno());
		vehicleEntity.setCor(dto.getCor());
		vehicleEntity.setQuilometragem(dto.getQuilometragem());
		vehicleEntity.setLocalidade(dto.getLocalidade());
		vehicleEntity.setValorVenda(dto.getValorVenda());
		vehicleEntity.setValorCompra(dto.getValorCompra());
		vehicleEntity.setStatus(dto.getStatus());
		vehicleEntity.setVisivel(dto.getVisivel());
		vehicleEntity.setPlaca(dto.getPlaca());
		vehicleEntity.setChassi(dto.getChassi());
		vehicleEntity.setUser(owner);

		// MUDANÇA 3: Define o nome do arquivo da imagem na entidade.
		vehicleEntity.setImageUrl(imageFileName);

		// Salva a entidade Vehicle no banco para que ela receba um ID.
		Vehicle savedVehicle = vehicleRepository.save(vehicleEntity);

		// Cria a entidade Document para representar o PDF salvo.
		Document documentEntity = new Document();
		documentEntity.setTipo("Documento Principal");
		documentEntity.setArquivo(pdfFileName);
		documentEntity.setVehicle(savedVehicle);

		// Salva a entidade Document.
		documentRepository.save(documentEntity);

		// Retorna a resposta completa.
		return new VehicleResponseDTO(savedVehicle);
	}

	
	@Transactional(readOnly = true)
	public List<VehicleResponseDTO> findVehiclesByUser(Integer userId) {
	   
	    if (!userRepository.existsById(userId)) {
	        throw new EntityNotFoundException("Usuário com ID " + userId + " não encontrado.");
	    }
	     // Usa o novo método do repositório
	    List<Vehicle> result = vehicleRepository.findByUserId(userId);
	    
	   
	    return result.stream().map(VehicleResponseDTO::new).toList();
	}
	

@Transactional
public VehicleResponseDTO updateVehicleForUser(Integer carId, Integer userId, VehicleUpdateDTO dto, MultipartFile imageFile, MultipartFile pdfFile) {

    // 1. Busca o veículo e verifica a posse (lógica que você já tinha, está perfeita)
    Vehicle vehicleToUpdate = vehicleRepository.findById(carId)
            .orElseThrow(() -> new EntityNotFoundException("Veículo com ID " + carId + " não encontrado."));

    if (!vehicleToUpdate.getUser().getId().equals(userId)) {
        throw new SecurityException("Acesso negado: você não tem permissão para atualizar este veículo.");
    }
    
    // 2. ATUALIZAÇÃO COMPLETA: Agora verifica todos os campos possíveis do DTO
    
    // --- Dados Principais ---
    if (dto.getMarca() != null && !dto.getMarca().isBlank()) {
        vehicleToUpdate.setMarca(dto.getMarca());
    }
    if (dto.getModelo() != null && !dto.getModelo().isBlank()) {
        vehicleToUpdate.setModelo(dto.getModelo());
    }
    if (dto.getAno() != null) {
        vehicleToUpdate.setAno(dto.getAno());
    }
    if (dto.getPlaca() != null && !dto.getPlaca().isBlank()) {
        vehicleToUpdate.setPlaca(dto.getPlaca());
    }
    if (dto.getChassi() != null && !dto.getChassi().isBlank()) {
        vehicleToUpdate.setChassi(dto.getChassi());
    }

    // --- Detalhes Adicionais ---
    if (dto.getCor() != null && !dto.getCor().isBlank()) {
        vehicleToUpdate.setCor(dto.getCor());
    }
    if (dto.getQuilometragem() != null) {
        vehicleToUpdate.setQuilometragem(dto.getQuilometragem());
    }
    if (dto.getLocalidade() != null && !dto.getLocalidade().isBlank()) {
        vehicleToUpdate.setLocalidade(dto.getLocalidade());
    }

    // --- Informações de Venda ---
    if (dto.getValorVenda() != null) {
        vehicleToUpdate.setValorVenda(dto.getValorVenda());
    }
    if (dto.getValorCompra() != null) {
        vehicleToUpdate.setValorCompra(dto.getValorCompra());
    }
    if (dto.getStatus() != null) {
        vehicleToUpdate.setStatus(dto.getStatus());
    }
    if (dto.getVisivel() != null) {
        vehicleToUpdate.setVisivel(dto.getVisivel());
    }

    // 3. Atualiza a imagem, se uma nova foi enviada
    if (imageFile != null && !imageFile.isEmpty()) {
        if (vehicleToUpdate.getImageUrl() != null && !vehicleToUpdate.getImageUrl().isBlank()) {
            fileStorageService.deleteFile(vehicleToUpdate.getImageUrl()); // Deleta a antiga
        }
        String newImageName = fileStorageService.storeFile(imageFile); // Salva a nova
        vehicleToUpdate.setImageUrl(newImageName);
    }
    
    // 4. (Lógica futura) Atualiza o documento, se um novo for enviado
    if (pdfFile != null && !pdfFile.isEmpty()) {
        // Implementar a lógica para deletar o documento antigo e salvar o novo
        // Ex: documentRepository.findByVehicleId(carId).ifPresent(doc -> fileStorageService.deleteFile(doc.getArquivo()));
        // ...e depois salvar o novo...
    }

    // 5. Salva e retorna a entidade atualizada
    Vehicle updatedVehicle = vehicleRepository.save(vehicleToUpdate);
    return new VehicleResponseDTO(updatedVehicle);
}
	@Transactional
	public void delete(Integer id) { // Renomeei para 'delete' para ficar mais claro
	    // 1. Garante que o veículo que se quer deletar existe.
	    if (!vehicleRepository.existsById(id)) {
	        throw new EntityNotFoundException("Veículo com ID " + id + " não encontrado.");
	    }

	    
	    List<Document> documents = documentRepository.findByVehicleId(id);

	 
	    for (Document doc : documents) {
	        fileStorageService.deleteFile(doc.getArquivo()); 
	    }

	 
	    documentRepository.deleteAll(documents);
	    
	    vehicleRepository.deleteFavoriteAssociations(id);

	   

	    
	    vehicleRepository.deleteById(id);
	}
	
	@Transactional(readOnly = true) 
	public VehicleResponseDTO findById(Integer vehicleId) { 
	    Vehicle vehicle = vehicleRepository.findById(vehicleId)
	        .orElseThrow(() -> new ResourceNotFoundException("Veículo com ID " + vehicleId + " não encontrado."));

	    VehicleResponseDTO dto = new VehicleResponseDTO(vehicle);

	    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    
	    if (principal instanceof UserSpringDetails) {
	        UserSpringDetails userDetails = (UserSpringDetails) principal;
	        Integer userId = userDetails.getId();
	        
	        boolean isFavorited = userRepository.isVehicleFavoritedByUser(userId, vehicleId);
	        
	        // --- LOG DE DEBUG NO BACKEND ---
	        System.out.println("=============================================");
	        System.out.println("[BACKEND] Verificando favorito para Veículo ID: " + vehicleId + " e Usuário ID: " + userId);
	        System.out.println("[BACKEND] Resultado da consulta isFavoritedByUser: " + isFavorited);
	        System.out.println("=============================================");
	        
	        dto.setFavorited(isFavorited);
	    }
	    
	    return dto;
	}
	
	 @Transactional(readOnly = true)
	    public List<VehicleSummaryDTO> findAllWithFilters(String query, String marca) {
	        Specification<Vehicle> spec = (root, criteriaQuery, criteriaBuilder) -> {
	            List<Predicate> predicates = new ArrayList<>();

	            if (marca != null && !marca.isBlank()) {
	                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("marca")), marca.toLowerCase()));
	            }

	            if (query != null && !query.isBlank()) {
	                Predicate marcaLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("marca")), "%" + query.toLowerCase() + "%");
	                Predicate modeloLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("modelo")), "%" + query.toLowerCase() + "%");
	                predicates.add(criteriaBuilder.or(marcaLike, modeloLike));
	            }
	            
	            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	        };

	        List<Vehicle> result = vehicleRepository.findAll(spec);

	        return result.stream()
	                     .map(VehicleSummaryDTO::new)
	                     .toList();
	    }
	
	
}