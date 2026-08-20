package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Modelos.Cita;
import org.esfe.HavenGlam.Modelos.Cliente;
import org.esfe.HavenGlam.Modelos.Empleado;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Servicios.Interfaces.ICitaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CitaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ICitaService citaService;

    @InjectMocks
    private CitaController citaController;

    private Cita cita;
    private Estado estado;
    private Cliente cliente;
    private Empleado empleado;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(citaController).build();
        estado = new Estado(1, "Activo", "General");
        cliente = new Cliente(1, null, estado);
        empleado = new Empleado(1, null, estado);
        cita = new Cita(1, cliente, empleado, LocalDate.of(2026, 8, 25),
                LocalTime.of(10, 0), LocalTime.of(10, 30), estado,
                "Cliente frecuente", LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /citas - Debe retornar la vista list con citas")
    void listar_RetornaVistaList() throws Exception {
        when(citaService.listar()).thenReturn(Arrays.asList(cita));

        mockMvc.perform(get("/citas"))
                .andExpect(status().isOk())
                .andExpect(view().name("citas/list"))
                .andExpect(model().attributeExists("citas"));

        verify(citaService, times(1)).listar();
    }

    @Test
    @DisplayName("GET /citas/crear - Debe retornar la vista form con una nueva Cita")
    void mostrarFormularioCrear_RetornaVistaForm() throws Exception {
        mockMvc.perform(get("/citas/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("citas/form"))
                .andExpect(model().attributeExists("cita"));
    }

    @Test
    @DisplayName("GET /citas/editar/{id} - Debe retornar la vista form con la Cita encontrada")
    void mostrarFormularioEditar_CuandoExiste_RetornaVistaForm() throws Exception {
        when(citaService.buscarPorId(1)).thenReturn(Optional.of(cita));

        mockMvc.perform(get("/citas/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("citas/form"))
                .andExpect(model().attributeExists("cita"));

        verify(citaService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("POST /citas/guardar - Con datos válidos debe guardar y redireccionar")
    void guardar_ConDatosValidos_Redirecciona() throws Exception {
        when(citaService.guardar(any(Cita.class))).thenReturn(cita);

        mockMvc.perform(post("/citas/guardar")
                        .param("cliente.idCliente", "1")
                        .param("empleado.idEmpleado", "1")
                        .param("fecha", "2026-08-25")
                        .param("hora", "10:00")
                        .param("horaFin", "10:30")
                        .param("estado.idEstado", "1")
                        .param("observaciones", "Cliente frecuente"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/citas"));

        verify(citaService, times(1)).guardar(any(Cita.class));
    }

    @Test
    @DisplayName("GET /citas/eliminar/{id} - Debe eliminar y redireccionar")
    void eliminar_Redirecciona() throws Exception {
        doNothing().when(citaService).eliminar(1);

        mockMvc.perform(get("/citas/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/citas"));

        verify(citaService, times(1)).eliminar(1);
    }
}