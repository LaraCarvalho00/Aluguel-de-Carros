package com.pucminas.aluguelcarros.unit;

import com.pucminas.aluguelcarros.application.dto.request.AutomovelRequestDTO;
import com.pucminas.aluguelcarros.application.dto.response.AutomovelResponseDTO;
import com.pucminas.aluguelcarros.application.mapper.AutomovelMapper;
import com.pucminas.aluguelcarros.application.service.impl.AutomovelServiceImpl;
import com.pucminas.aluguelcarros.domain.entity.Automovel;
import com.pucminas.aluguelcarros.domain.enums.TipoPropriedade;
import com.pucminas.aluguelcarros.domain.exception.BusinessException;
import com.pucminas.aluguelcarros.domain.exception.ResourceNotFoundException;
import com.pucminas.aluguelcarros.infrastructure.repository.AutomovelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AutomovelServiceImplTest {

    @Mock
    private AutomovelRepository automovelRepository;

    @Mock
    private AutomovelMapper automovelMapper;

    @InjectMocks
    private AutomovelServiceImpl automovelService;

    private AutomovelRequestDTO requestDTO;
    private Automovel automovel;
    private AutomovelResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestDTO = new AutomovelRequestDTO(
                "MAT-001",
                2024,
                "Toyota",
                "Corolla",
                "ABC-1234",
                "EMPRESA"
        );

        automovel = new Automovel();
        automovel.setId(1L);
        automovel.setMatricula("MAT-001");
        automovel.setAno(2024);
        automovel.setMarca("Toyota");
        automovel.setModelo("Corolla");
        automovel.setPlaca("ABC-1234");
        automovel.setDisponivel(true);
        automovel.setProprietario(TipoPropriedade.EMPRESA);

        responseDTO = new AutomovelResponseDTO(
                1L, "MAT-001", 2024, "Toyota", "Corolla", "ABC-1234", true, "EMPRESA"
        );
    }

    @Test
    @DisplayName("Deve criar automovel com sucesso")
    void deveCriarAutomovelComSucesso() {
        when(automovelRepository.findByPlaca(anyString())).thenReturn(Optional.empty());
        when(automovelRepository.findByMatricula(anyString())).thenReturn(Optional.empty());
        when(automovelMapper.toEntity(requestDTO)).thenReturn(automovel);
        when(automovelRepository.save(any(Automovel.class))).thenReturn(automovel);
        when(automovelMapper.toResponseDTO(automovel)).thenReturn(responseDTO);

        AutomovelResponseDTO resultado = automovelService.criar(requestDTO);

        assertNotNull(resultado);
        assertEquals("Toyota", resultado.marca());
        assertEquals("Corolla", resultado.modelo());
        assertEquals("ABC-1234", resultado.placa());
        verify(automovelRepository).save(any(Automovel.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar automovel com placa duplicada")
    void deveLancarExcecaoQuandoPlacaDuplicada() {
        when(automovelRepository.findByPlaca("ABC-1234")).thenReturn(Optional.of(automovel));

        assertThrows(BusinessException.class, () -> automovelService.criar(requestDTO));
        verify(automovelRepository, never()).save(any(Automovel.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao criar automovel com matricula duplicada")
    void deveLancarExcecaoQuandoMatriculaDuplicada() {
        when(automovelRepository.findByPlaca(anyString())).thenReturn(Optional.empty());
        when(automovelRepository.findByMatricula("MAT-001")).thenReturn(Optional.of(automovel));

        assertThrows(BusinessException.class, () -> automovelService.criar(requestDTO));
        verify(automovelRepository, never()).save(any(Automovel.class));
    }

    @Test
    @DisplayName("Deve buscar automovel por ID com sucesso")
    void deveBuscarAutomovelPorIdComSucesso() {
        when(automovelRepository.findById(1L)).thenReturn(Optional.of(automovel));
        when(automovelMapper.toResponseDTO(automovel)).thenReturn(responseDTO);

        AutomovelResponseDTO resultado = automovelService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Toyota", resultado.marca());
    }

    @Test
    @DisplayName("Deve lancar excecao quando automovel nao encontrado por ID")
    void deveLancarExcecaoQuandoAutomovelNaoEncontradoPorId() {
        when(automovelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> automovelService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve listar todos os automoveis")
    void deveListarTodosOsAutomoveis() {
        Automovel automovel2 = new Automovel();
        automovel2.setId(2L);
        automovel2.setMarca("Honda");

        AutomovelResponseDTO response2 = new AutomovelResponseDTO(
                2L, "MAT-002", 2023, "Honda", "Civic", "DEF-5678", true, "EMPRESA"
        );

        when(automovelRepository.findAll()).thenReturn(Arrays.asList(automovel, automovel2));
        when(automovelMapper.toResponseDTO(automovel)).thenReturn(responseDTO);
        when(automovelMapper.toResponseDTO(automovel2)).thenReturn(response2);

        List<AutomovelResponseDTO> resultado = automovelService.listarTodos();

        assertEquals(2, resultado.size());
        verify(automovelRepository).findAll();
    }

    @Test
    @DisplayName("Deve listar apenas automoveis disponiveis")
    void deveListarApenasAutomoveisDisponiveis() {
        when(automovelRepository.findByDisponivelTrue()).thenReturn(List.of(automovel));
        when(automovelMapper.toResponseDTO(automovel)).thenReturn(responseDTO);

        List<AutomovelResponseDTO> resultado = automovelService.listarDisponiveis();

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).disponivel());
        verify(automovelRepository).findByDisponivelTrue();
    }

    @Test
    @DisplayName("Deve atualizar automovel com sucesso")
    void deveAtualizarAutomovelComSucesso() {
        AutomovelResponseDTO responseAtualizado = new AutomovelResponseDTO(
                1L, "MAT-001", 2025, "Toyota", "Corolla Cross", "ABC-1234", true, "EMPRESA"
        );

        when(automovelRepository.findById(1L)).thenReturn(Optional.of(automovel));
        when(automovelRepository.findByPlaca("ABC-1234")).thenReturn(Optional.of(automovel));
        when(automovelRepository.findByMatricula("MAT-001")).thenReturn(Optional.of(automovel));
        when(automovelRepository.update(any(Automovel.class))).thenReturn(automovel);
        when(automovelMapper.toResponseDTO(any(Automovel.class))).thenReturn(responseAtualizado);

        AutomovelResponseDTO resultado = automovelService.atualizar(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals("Corolla Cross", resultado.modelo());
        verify(automovelRepository).update(any(Automovel.class));
    }

    @Test
    @DisplayName("Deve lancar excecao ao atualizar automovel inexistente")
    void deveLancarExcecaoAoAtualizarAutomovelInexistente() {
        when(automovelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> automovelService.atualizar(99L, requestDTO));
        verify(automovelRepository, never()).update(any(Automovel.class));
    }

    @Test
    @DisplayName("Deve deletar automovel com sucesso")
    void deveDeletarAutomovelComSucesso() {
        when(automovelRepository.existsById(1L)).thenReturn(true);

        automovelService.deletar(1L);

        verify(automovelRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lancar excecao ao deletar automovel inexistente")
    void deveLancarExcecaoAoDeletarAutomovelInexistente() {
        when(automovelRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> automovelService.deletar(99L));
        verify(automovelRepository, never()).deleteById(anyLong());
    }
}
