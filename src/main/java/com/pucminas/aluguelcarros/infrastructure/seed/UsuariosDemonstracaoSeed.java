package com.pucminas.aluguelcarros.infrastructure.seed;

import com.pucminas.aluguelcarros.domain.entity.Cliente;
import com.pucminas.aluguelcarros.domain.entity.Usuario;
import com.pucminas.aluguelcarros.domain.enums.Perfil;
import com.pucminas.aluguelcarros.infrastructure.repository.ClienteRepository;
import com.pucminas.aluguelcarros.infrastructure.repository.UsuarioRepository;
import com.pucminas.aluguelcarros.infrastructure.security.PasswordEncoderService;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Garante usuários de demonstração (ADMIN, AGENTE, CLIENTE) e cadastro de cliente
 * com o mesmo CPF para exibição do nome na interface. Idempotente: pode rodar a cada subida.
 */
@Singleton
@Requires(property = "seed.usuarios-demo.enabled", notEquals = "false")
public class UsuariosDemonstracaoSeed {

    private static final String SENHA_PADRAO = "123456";

    private record DemoUsuario(String cpf, Perfil perfil, String nome, String rg) {}

    private static final List<DemoUsuario> DEMOS = List.of(
            new DemoUsuario("33333333333", Perfil.ADMIN, "Dominic A", "RG-SEED-ADM-333"),
            new DemoUsuario("22222222222", Perfil.AGENTE, "Han", "RG-SEED-AGT-222"),
            new DemoUsuario("11111111111", Perfil.CLIENTE, "Braian", "RG-SEED-CLI-111")
    );

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoderService passwordEncoderService;

    public UsuariosDemonstracaoSeed(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            PasswordEncoderService passwordEncoderService) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoderService = passwordEncoderService;
    }

    @EventListener
    @Transactional
    public void aoIniciar(StartupEvent event) {
        for (DemoUsuario demo : DEMOS) {
            upsertUsuario(demo);
            upsertCliente(demo);
        }
    }

    private void upsertUsuario(DemoUsuario demo) {
        Optional<Usuario> opt = usuarioRepository.findByCpf(demo.cpf());
        if (opt.isEmpty()) {
            Usuario u = new Usuario();
            u.setCpf(demo.cpf());
            u.setSenha(passwordEncoderService.encode(SENHA_PADRAO));
            u.setPerfil(demo.perfil());
            usuarioRepository.save(u);
            return;
        }
        Usuario u = opt.get();
        u.setPerfil(demo.perfil());
        usuarioRepository.update(u);
    }

    private void upsertCliente(DemoUsuario demo) {
        Optional<Cliente> opt = buscarClientePorCpfDemo(demo.cpf());
        if (opt.isEmpty()) {
            Cliente c = new Cliente();
            c.setRg(demo.rg());
            c.setCpf(demo.cpf());
            c.setNome(demo.nome());
            c.setEndereco("Não informado");
            c.setProfissao("Não informado");
            c.setRendimentos(BigDecimal.ZERO);
            c.setEntidadesEmpregadoras(new ArrayList<>());
            clienteRepository.save(c);
            return;
        }
        Cliente c = opt.get();
        c.setNome(demo.nome());
        if (!demo.cpf().equals(c.getCpf()) && clienteRepository.findByCpf(demo.cpf()).isEmpty()) {
            c.setCpf(demo.cpf());
        }
        clienteRepository.update(c);
    }

    /**
     * Inclui variantes (12 dígitos, máscara) para encontrar o registro certo e alinhar ao CPF do {@code usuarios}.
     */
    private Optional<Cliente> buscarClientePorCpfDemo(String cpfCanonico) {
        for (String variante : variantesCpfClienteDemo(cpfCanonico)) {
            Optional<Cliente> o = clienteRepository.findByCpf(variante);
            if (o.isPresent()) {
                return o;
            }
        }
        return Optional.empty();
    }

    private static List<String> variantesCpfClienteDemo(String cpfCanonico) {
        if (!"11111111111".equals(cpfCanonico)) {
            return List.of(cpfCanonico);
        }
        return List.of(
                "11111111111",
                "111111111111",
                "111.111.111-11"
        );
    }
}
