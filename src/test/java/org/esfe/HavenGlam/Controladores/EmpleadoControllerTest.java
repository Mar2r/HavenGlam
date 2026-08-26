package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Modelos.Empleado;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Persona;
import org.esfe.HavenGlam.Modelos.RegistroEmpleadoForm;
import org.esfe.HavenGlam.Servicios.Interfaces.IEmpleadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IEstadoService;
import org.esfe.HavenGlam.Servicios.Interfaces.IRegistroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EmpleadoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IEmpleadoService empleadoService;

    @Mock
    private IRegistroService registroService;

    @Mock
    private IEstadoService estadoService;

    @InjectMocks
    private EmpleadoController empleadoController;

    private Empleado empleado;
    private Estado estado;
    private Persona persona;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(empleadoController).build();
        estado = new Estado(1, "Activo", "General");
        persona = new Persona(1, "María", "Gómez", "70000002", "San Salvador", "98765432-1");
        empleado = new Empleado(1, persona, estado);
    }

    @Test
    @DisplayName("GET /empleados - Debe retornar la vista list con empleados")
    void listar_RetornaVistaList() throws Exception {
        when(empleadoService.listar()).thenReturn(Arrays.asList(empleado));

        mockMvc.perform(get("/empleados"))
                .andExpect(status().isOk())
                .andExpect(view().name("empleados/list"))
                .andExpect(model().attributeExists("empleados"));

        verify(empleadoService, times(1)).listar();
    }

    @Test
    @DisplayName("GET /empleados/crear - Debe retornar la vista form con un nuevo RegistroEmpleadoForm")
    void mostrarFormularioCrear_RetornaVistaForm() throws Exception {
        mockMvc.perform(get("/empleados/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("empleados/form"))
                .andExpect(model().attributeExists("registroEmpleado"));
    }

    @Test
    @DisplayName("POST /empleados/registrar - Con datos válidos debe registrar y redireccionar")
    void registrar_ConDatosValidos_Redirecciona() throws Exception {
        when(registroService.registrarEmpleado(any(RegistroEmpleadoForm.class))).thenReturn(empleado);

        mockMvc.perform(post("/empleados/registrar")
                        .param("nombre", "María")
                        .param("apellido", "Gómez")
                        .param("telefono", "70000002")
                        .param("direccion", "San Salvador")
                        .param("dui", "98765432-1")
                        .param("correo", "maria@haven.com")
                        .param("contra", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/empleados"));

        verify(registroService, times(1)).registrarEmpleado(any(RegistroEmpleadoForm.class));
    }

    @Test
    @DisplayName("POST /empleados/registrar - Cuando el correo ya está registrado debe retornar el form con error")
    void registrar_CuandoCorreoYaRegistrado_RetornaFormConError() throws Exception {
        when(registroService.registrarEmpleado(any(RegistroEmpleadoForm.class)))
                .thenThrow(new IllegalStateException("Ese correo ya está registrado"));

        mockMvc.perform(post("/empleados/registrar")
                        .param("nombre", "María")
                        .param("apellido", "Gómez")
                        .param("telefono", "70000002")
                        .param("direccion", "San Salvador")
                        .param("dui", "98765432-1")
                        .param("correo", "maria@haven.com")
                        .param("contra", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("empleados/form"))
                .andExpect(model().attributeExists("errorRegistro"));

        verify(registroService, times(1)).registrarEmpleado(any(RegistroEmpleadoForm.class));
    }

    @Test
    @DisplayName("GET /empleados/editar/{id} - Debe retornar la vista editar con el Empleado encontrado")
    void mostrarFormularioEditar_CuandoExiste_RetornaVistaEditar() throws Exception {
        when(empleadoService.buscarPorId(1)).thenReturn(Optional.of(empleado));
        when(estadoService.listar()).thenReturn(Arrays.asList(estado));

        mockMvc.perform(get("/empleados/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("empleados/editar"))
                .andExpect(model().attributeExists("empleado"))
                .andExpect(model().attributeExists("estados"));

        verify(empleadoService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("POST /empleados/guardar - Con datos válidos debe guardar y redireccionar")
    void guardar_ConDatosValidos_Redirecciona() throws Exception {
        when(empleadoService.guardar(any(Empleado.class))).thenReturn(empleado);

        mockMvc.perform(post("/empleados/guardar")
                        .param("idEmpleado", "1")
                        .param("persona.idPersona", "1")
                        .param("estado.idEstado", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/empleados"));

        verify(empleadoService, times(1)).guardar(any(Empleado.class));
    }

    @Test
    @DisplayName("GET /empleados/eliminar/{id} - Debe eliminar y redireccionar")
    void eliminar_Redirecciona() throws Exception {
        doNothing().when(empleadoService).eliminar(1);

        mockMvc.perform(get("/empleados/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/empleados"));

        verify(empleadoService, times(1)).eliminar(1);
    }
}