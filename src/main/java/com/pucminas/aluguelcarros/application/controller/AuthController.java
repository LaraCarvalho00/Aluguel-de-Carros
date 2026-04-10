package com.pucminas.aluguelcarros.application.controller;

import com.pucminas.aluguelcarros.application.dto.request.RegistroRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.UsuarioResponseDTO;
import com.pucminas.aluguelcarros.domain.entity.Usuario;
import com.pucminas.aluguelcarros.domain.exception.BusinessException;
import com.pucminas.aluguelcarros.infrastructure.repository.UsuarioRepository;
import com.pucminas.aluguelcarros.infrastructure.security.PasswordEncoderService;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller("/api/v1/auth")
@Validated
@Tag(name = "Autenticação", description = "Registro e login de usuários")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderService passwordEncoderService;

    public AuthController(UsuarioRepository usuarioRepository,
                          PasswordEncoderService passwordEncoderService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoderService = passwordEncoderService;
    }

    @Post("/registro")
    @Status(HttpStatus.CREATED)
    @Secured(SecurityRule.IS_ANONYMOUS)
    @Transactional
    @Operation(summary = "Registrar usuário", description = "Cria um novo usuário com CPF, senha e perfil")
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
            content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "CPF já cadastrado ou dados inválidos")
    public UsuarioResponseDTO registro(@Body @Valid RegistroRequestDTO dto) {
        if (usuarioRepository.existsByCpf(dto.cpf())) {
            throw new BusinessException("CPF já cadastrado no sistema");
        }

        Usuario usuario = new Usuario();
        usuario.setCpf(dto.cpf());
        usuario.setSenha(passwordEncoderService.encode(dto.senha()));
        usuario.setPerfil(dto.perfil());

        Usuario salvo = usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(salvo.getId(), salvo.getCpf(), salvo.getPerfil());
    }
}
