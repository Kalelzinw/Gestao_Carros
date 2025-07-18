package com.example.gestao.carros.controllers;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.gestao.carros.dto.LoginRequestDTO;
import com.example.gestao.carros.dto.ResponseDTO;
import com.example.gestao.carros.entities.User;
import com.example.gestao.carros.infra.security.TokenService;
import com.example.gestao.carros.repositories.UserRepository;
import com.example.gestao.carros.services.FileStorageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;
	private final FileStorageService fileStorageService;

	@PostMapping("/login")
	public ResponseEntity login(@RequestBody LoginRequestDTO body) {
		User user = this.repository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("User not found"));
		if (passwordEncoder.matches(body.password(), user.getPassword())) {
			String token = this.tokenService.generateToken(user);
			return ResponseEntity.ok(new ResponseDTO(user.getName(), token));
		}
		return ResponseEntity.badRequest().build();
	}

	@PostMapping("/register")
	// MUDANÇA: A assinatura do método agora aceita campos e um arquivo
	public ResponseEntity register(@RequestParam("name") String name, @RequestParam("email") String email,
			@RequestParam("password") String password, @RequestParam("tel") String tel, @RequestParam("cpf") String cpf,
			
			// MUDANÇA: Adicione a anotação @DateTimeFormat aqui
            @RequestParam("dateBorn") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateBorn,
			
			@RequestPart("foto") MultipartFile fotoFile) {
		Optional<User> user = this.repository.findByEmail(email);

		if (user.isEmpty()) {
			// 1. Salva o arquivo da foto no disco e obtém o nome gerado.
			String fotoFileName = fileStorageService.storeFile(fotoFile);

			// 2. Cria a entidade User com os dados recebidos.
			User newUser = new User();
			newUser.setPassword(passwordEncoder.encode(password));
			newUser.setEmail(email);
			newUser.setName(name);
			newUser.setTel(tel);
			newUser.setDateBorn(dateBorn);
			newUser.setCpf(cpf);

			// 3. Seta o nome do arquivo da foto na entidade.
			newUser.setFotoUrl(fotoFileName);

			// 4. Salva o novo usuário no banco.
			this.repository.save(newUser);

			// 5. Gera o token (o TokenService recebe o objeto User completo, como antes).
			String token = this.tokenService.generateToken(newUser);
			return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token));
		}

		// Se o usuário já existir, retorna um erro.
		return ResponseEntity.badRequest().body("Usuário com este email já existe.");
	}
}