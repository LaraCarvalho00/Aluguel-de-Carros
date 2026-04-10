package com.pucminas.aluguelcarros.infrastructure.security;

import com.pucminas.aluguelcarros.domain.entity.Usuario;
import com.pucminas.aluguelcarros.infrastructure.repository.UsuarioRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Singleton
public class AuthenticationProviderImpl implements AuthenticationProvider<HttpRequest<?>> {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderService passwordEncoderService;

    public AuthenticationProviderImpl(UsuarioRepository usuarioRepository,
                                      PasswordEncoderService passwordEncoderService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoderService = passwordEncoderService;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest,
                                                          AuthenticationRequest<?, ?> authenticationRequest) {
        return Mono.fromCallable(() -> {
            String cpf = authenticationRequest.getIdentity().toString();
            String senha = authenticationRequest.getSecret().toString();

            Optional<Usuario> usuarioOpt = usuarioRepository.findByCpf(cpf);

            if (usuarioOpt.isEmpty()) {
                return (AuthenticationResponse) AuthenticationResponse
                        .failure(AuthenticationFailureReason.USER_NOT_FOUND);
            }

            Usuario usuario = usuarioOpt.get();

            if (passwordEncoderService.matches(senha, usuario.getSenha())) {
                return AuthenticationResponse.success(cpf, List.of(usuario.getPerfil().name()));
            }

            return AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
        });
    }
}
