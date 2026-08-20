package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Modelos.Cita;
import org.esfe.HavenGlam.Modelos.CitaServicio;
import org.esfe.HavenGlam.Modelos.Servicio;
import org.esfe.HavenGlam.Servicios.Interfaces.ICitaService;
import org.esfe.HavenGlam.Servicios.Interfaces.ICitaServicioService;
import org.esfe.HavenGlam.Servicios.Interfaces.IServicioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CitaServicioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ICitaServicioService citaServicioService;

    @Mock
    private ICitaService citaService;

    @Mock
    private IServicioService servicioService;

    @InjectMocks
    private CitaServicioController citaServicioController;

    private CitaServicio citaServicio;
    private Cita cita;
    private Servicio servicio;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(citaServicioController).build();
        cita = new Cita();
        cita.setIdCita(1);
        servicio = new Servicio();
        servicio.setIdServicio(1);
        citaServicio = new CitaServicio(1, cita, servicio, new BigDecimal("25.00"));
    }

    @Test
    @DisplayName("GET /citaservicios - Debe retornar la vista list con citaServicios")
    void listar_RetornaVistaList() throws Exception {
        when(citaServicioService.listar()).thenReturn(Arrays.asList(citaServicio));

        mockMvc.perform(get("/citaservicios"))
                .andExpect(status().isOk())
                .andExpect(view().name("citaservicios/list"))
                .andExpect(model().attributeExists("citaServicios"));

        verify(citaServicioService, times(1)).listar();
    }

    @Test
    @DisplayName("GET /citaservicios/crear - Debe retornar la vista form con un nuevo CitaServicio")
    void mostrarFormularioCrear_RetornaVistaForm() throws Exception {
        when(citaService.listar()).thenReturn(Collections.singletonList(cita));
        when(servicioService.listar()).thenReturn(Collections.singletonList(servicio));

        mockMvc.perform(get("/citaservicios/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("citaservicios/form"))
                .andExpect(model().attributeExists("citaServicio"))
                .andExpect(model().attributeExists("citas"))
                .andExpect(model().attributeExists("servicios"));

        verify(citaService, times(1)).listar();
        verify(servicioService, times(1)).listar();
    }

    @Test
    @DisplayName("GET /citaservicios/editar/{id} - Debe retornar la vista form con el CitaServicio encontrado")
    void mostrarFormularioEditar_CuandoExiste_RetornaVistaForm() throws Exception {
        when(citaServicioService.buscarPorId(1)).thenReturn(Optional.of(citaServicio));
        when(citaService.listar()).thenReturn(Collections.singletonList(cita));
        when(servicioService.listar()).thenReturn(Collections.singletonList(servicio));

        mockMvc.perform(get("/citaservicios/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("citaservicios/form"))
                .andExpect(model().attributeExists("citaServicio"))
                .andExpect(model().attributeExists("citas"))
                .andExpect(model().attributeExists("servicios"));

        verify(citaServicioService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("POST /citaservicios/guardar - Con datos válidos debe guardar y redireccionar")
    void guardar_ConDatosValidos_Redirecciona() throws Exception {
        when(citaServicioService.guardar(any(CitaServicio.class))).thenReturn(citaServicio);

        mockMvc.perform(post("/citaservicios/guardar")
                        .param("cita.idCita", "1")
                        .param("servicio.idServicio", "1")
                        .param("precioAlMomento", "25.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/citaservicios"));

        verify(citaServicioService, times(1)).guardar(any(CitaServicio.class));
    }

    @Test
    @DisplayName("GET /citaservicios/eliminar/{id} - Debe eliminar y redireccionar")
    void eliminar_Redirecciona() throws Exception {
        doNothing().when(citaServicioService).eliminar(1);

        mockMvc.perform(get("/citaservicios/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/citaservicios"));

        verify(citaServicioService, times(1)).eliminar(1);
    }
}