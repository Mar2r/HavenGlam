package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Servicios.Interfaces.ICitaService;
import org.esfe.HavenGlam.Servicios.Interfaces.ICitaServicioService;
import org.esfe.HavenGlam.Servicios.Interfaces.IClienteService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEmpleadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IServicioService;
import org.esfe.HavenGlam.Servicios.Interfaces.IUsuarioService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CitaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ICitaService citaService;

    @Mock
    private ICitaServicioService citaServicioService;

    @Mock
    private IServicioService servicioService;

    @Mock
    private IEmpleadoService empleadoService;

    @Mock
    private IClienteService clienteService;

    @Mock
    private IUsuarioService usuarioService;

    @Mock
    private IEstadoService estadoService;

    @InjectMocks
    private CitaController citaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(citaController).build();
    }

    @Test
    @DisplayName("GET /citas - Debe retornar la vista index del flujo de reservas")
    void index_RetornaVistaIndex() throws Exception {
        mockMvc.perform(get("/citas"))
                .andExpect(status().isOk())
                .andExpect(view().name("citas/index"));
    }

    @Test
    @DisplayName("GET /citas/crear - Debe retornar la vista index del flujo de reservas")
    void crear_RetornaVistaIndex() throws Exception {
        mockMvc.perform(get("/citas/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("citas/index"));
    }

    @Test
    @DisplayName("GET /citas/reservar - Debe retornar la vista index del flujo de reservas")
    void reservar_RetornaVistaIndex() throws Exception {
        mockMvc.perform(get("/citas/reservar"))
                .andExpect(status().isOk())
                .andExpect(view().name("citas/index"));
    }

    @Test
    @DisplayName("GET /citas/api/servicios - Debe retornar 200 con la lista de servicios")
    void apiServicios_RetornaLista() throws Exception {
        when(servicioService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/citas/api/servicios"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /citas/api/empleados - Debe retornar 200 con la lista de empleados")
    void apiEmpleados_RetornaLista() throws Exception {
        when(empleadoService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/citas/api/empleados"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /citas/api/disponibilidad - Debe retornar 200 con los turnos ocupados")
    void apiDisponibilidad_RetornaTurnosOcupados() throws Exception {
        when(citaService.listarActivasPorFecha(any(LocalDate.class))).thenReturn(List.of());

        mockMvc.perform(get("/citas/api/disponibilidad").param("fecha", "2026-09-06"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}