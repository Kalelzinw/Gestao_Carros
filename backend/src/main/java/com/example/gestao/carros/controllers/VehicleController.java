package com.example.gestao.carros.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.gestao.carros.dto.VehicleCreateDTO;
import com.example.gestao.carros.dto.VehicleResponseDTO;
import com.example.gestao.carros.dto.VehicleSummaryDTO;
import com.example.gestao.carros.dto.VehicleUpdateDTO;
import com.example.gestao.carros.entities.enums.StatusVeiculo;
import com.example.gestao.carros.infra.security.UserSpringDetails;
import com.example.gestao.carros.services.VehicleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/create")
    public ResponseEntity<VehicleResponseDTO> create(
            @RequestParam("marca") String marca,
            @RequestParam("modelo") String modelo,
            @RequestParam("ano") Integer ano,
            @RequestParam("cor") String cor,
            @RequestParam("quilometragem") Integer quilometragem,
            @RequestParam("localidade") String localidade,
            @RequestParam("valorVenda") Float valorVenda,
            @RequestParam("valorCompra") Float valorCompra,
            
            // MUDANÇA: O tipo do parâmetro 'status' agora é String
            @RequestParam("status") String statusStr,
            
            @RequestParam("visivel") Boolean visivel,
            @RequestParam("placa") String placa,
            @RequestParam("chassi") String chassi,
            @RequestParam("userId") Integer userId,
            @RequestPart("documentoPdf") MultipartFile documentoPdf,
            @RequestPart("vehicleImage") MultipartFile vehicleImage) {
    	 System.out.println("--- Valor recebido para 'statusStr': [" + statusStr + "]");
        // 1. Converte a String recebida para o Enum
    	 StatusVeiculo status = StatusVeiculo.valueOf(statusStr.trim().toUpperCase());

        // 2. Monta o DTO de criação
        VehicleCreateDTO createDTO = new VehicleCreateDTO();
        createDTO.setMarca(marca);
        createDTO.setModelo(modelo);
        createDTO.setAno(ano);
        createDTO.setCor(cor);
        createDTO.setQuilometragem(quilometragem);
        createDTO.setLocalidade(localidade);
        createDTO.setValorVenda(valorVenda);
        createDTO.setValorCompra(valorCompra);
        createDTO.setStatus(status); // <-- Usa o Enum convertido
        createDTO.setVisivel(visivel);
        createDTO.setPlaca(placa);
        createDTO.setChassi(chassi);
        createDTO.setUserId(userId);
        
        // O resto do método continua igual
        VehicleResponseDTO newVehicle = vehicleService.createVehicleWithFiles(createDTO, documentoPdf, vehicleImage);

        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/vehicles/{id}").buildAndExpand(newVehicle.getId()).toUri();
        
        return ResponseEntity.created(uri).body(newVehicle);
    }

    @GetMapping("/list")
    public ResponseEntity<List<VehicleSummaryDTO>> listAll(@RequestParam(required = false) String marca) { 
        List<VehicleSummaryDTO> list = vehicleService.findAllWithFilters(null, marca);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/my-vehicles")
    public ResponseEntity<List<VehicleResponseDTO>> findMyVehicles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserSpringDetails loggedUser = (UserSpringDetails) authentication.getPrincipal();
        Integer userId = loggedUser.getId();
        List<VehicleResponseDTO> userVehicles = vehicleService.findVehiclesByUser(userId);
        return ResponseEntity.ok(userVehicles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> findById(@PathVariable Integer id) {
        VehicleResponseDTO dto = vehicleService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/update/{carId}")
    public ResponseEntity<VehicleResponseDTO> update(
            @PathVariable Integer carId,
            
            // --- PARÂMETROS CORRIGIDOS: AGORA INCLUindo TODOS OS CAMPOS ---
            @RequestParam(value = "marca", required = false) String marca,
            @RequestParam(value = "modelo", required = false) String modelo,
            @RequestParam(value = "placa", required = false) String placa,
            @RequestParam(value = "chassi", required = false) String chassi,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "cor", required = false) String cor,
            @RequestParam(value = "quilometragem", required = false) Integer quilometragem,
            @RequestParam(value = "localidade", required = false) String localidade,
            @RequestParam(value = "valorVenda", required = false) Float valorVenda,
            @RequestParam(value = "valorCompra", required = false) Float valorCompra,
            @RequestParam(value = "status", required = false) String statusStr,
            @RequestParam(value = "visivel", required = false) Boolean visivel,
            
            // Arquivos opcionais
            @RequestPart(value = "vehicleImage", required = false) MultipartFile vehicleImage,
            @RequestPart(value = "documentoPdf", required = false) MultipartFile documentoPdf
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserSpringDetails loggedUser = (UserSpringDetails) authentication.getPrincipal();
        Integer userId = loggedUser.getId();

        // Monta o DTO de atualização com todos os dados recebidos
        VehicleUpdateDTO updateDTO = new VehicleUpdateDTO();
        updateDTO.setMarca(marca);
        updateDTO.setModelo(modelo);
        updateDTO.setPlaca(placa);
        updateDTO.setChassi(chassi);
        updateDTO.setAno(ano);
        updateDTO.setCor(cor);
        updateDTO.setQuilometragem(quilometragem);
        updateDTO.setLocalidade(localidade);
        updateDTO.setValorVenda(valorVenda);
        updateDTO.setValorCompra(valorCompra);
        if (statusStr != null && !statusStr.isBlank()) {
            updateDTO.setStatus(StatusVeiculo.fromString(statusStr));
        }
        updateDTO.setVisivel(visivel);

        VehicleResponseDTO updatedDto = vehicleService.updateVehicleForUser(carId, userId, updateDTO, vehicleImage, documentoPdf);

        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        vehicleService.delete(id); // Chamando o método 'delete' atualizado
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<VehicleSummaryDTO>> searchVehicles(@RequestParam("query") String query) {
        List<VehicleSummaryDTO> vehicles = vehicleService.findAllWithFilters(query, null);
        return ResponseEntity.ok(vehicles);
    }
}