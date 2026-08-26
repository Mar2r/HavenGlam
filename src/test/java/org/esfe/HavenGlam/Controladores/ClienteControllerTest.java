package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Modelos.Cliente;
import org.esfe.HavenGlam.Modelos.Estado;
import org.esfe.HavenGlam.Modelos.Persona;
import org.esfe.HavenGlam.Modelos.RegistroClienteForm;
import org.esfe.HavenGlam.Servicios.Interfaces.IClienteService;
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
public class ClienteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IClienteService clienteService;

    @Mock
    private IRegistroService registroService;

    @Mock
    private IEstadoService estadoService;

    @InjectMocks
    private ClienteController clienteController;

    private Cliente cliente;
    private Estado estado;
    private Persona persona;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();
        estado = new Estado(1, "Activo", "General");
        persona = new Persona(1, "Juan", "Pérez", "70000001", "San Salvador", "12345678-9");
        cliente = new Cliente(1, persona, estado);
    }

    @Test
    @DisplayName("GET /clientes - Debe retornar la vista list con clientes")
    void listar_RetornaVistaList() throws Exception {
        when(clienteService.listar()).thenReturn(Arrays.asList(cliente));

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/list"))
                .andExpect(model().attributeExists("clientes"));

        verify(clienteService, times(1)).listar();
    }

    @Test
    @DisplayName("GET /clientes/crear - Debe retornar la vista form con un nuevo RegistroClienteForm")
    void mostrarFormularioCrear_RetornaVistaForm() throws Exception {
        mockMvc.perform(get("/clientes/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/form"))
                .andExpect(model().attributeExists("registroCliente"));
    }

    @Test
    @DisplayName("POST /clientes/registrar - Con datos válidos debe registrar y redireccionar")
    void registrar_ConDatosValidos_Redirecciona() throws Exception {
        when(registroService.registrarCliente(any(RegistroClienteForm.class))).thenReturn(cliente);

        mockMvc.perform(post("/clientes/registrar")
                        .param("nombre", "Juan")
                        .param("apellido", "Pérez")
                        .param("telefono", "70000001")
                        .param("direccion", "San Salvador")
                        .param("dui", "12345678-9")
                        .param("correo", "juan@haven.com")
                        .param("contra", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes"));

        verify(registroService, times(1)).registrarCliente(any(RegistroClienteForm.class));
    }

    @Test
    @DisplayName("POST /clientes/registrar - Cuando el correo ya está registrado debe retornar el form con error")
    void registrar_CuandoCorreoYaRegistrado_RetornaFormConError() throws Exception {
        when(registroService.registrarCliente(any(RegistroClienteForm.class)))
                .thenThrow(new IllegalStateException("Ese correo ya está registrado"));

        mockMvc.perform(post("/clientes/registrar")
                        .param("nombre", "Juan")
                        .param("apellido", "Pérez")
                        .param("telefono", "70000001")
                        .param("direccion", "San Salvador")
                        .param("dui", "12345678-9")
                        .param("correo", "juan@haven.com")
                        .param("contra", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/form"))
                .andExpect(model().attributeExists("errorRegistro"));

        verify(registroService, times(1)).registrarCliente(any(RegistroClienteForm.class));
    }

    @Test
    @DisplayName("GET /clientes/editar/{id} - Debe retornar la vista editar con el Cliente encontrado")
    void mostrarFormularioEditar_CuandoExiste_RetornaVistaEditar() throws Exception {
        when(clienteService.buscarPorId(1)).thenReturn(Optional.of(cliente));
        when(estadoService.listar()).thenReturn(Arrays.asList(estado));

        mockMvc.perform(get("/clientes/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/editar"))
                .andExpect(model().attributeExists("cliente"))
                .andExpect(model().attributeExists("estados"));

        verify(clienteService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("POST /clientes/guardar - Con datos válidos debe guardar y redireccionar")
    void guardar_ConDatosValidos_Redirecciona() throws Exception {
        when(clienteService.guardar(any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(post("/clientes/guardar")
                        .param("idCliente", "1")
                        .param("persona.idPersona", "1")
                        .param("estado.idEstado", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes"));

        verify(clienteService, times(1)).guardar(any(Cliente.class));
    }

    @Test
    @DisplayName("GET /clientes/eliminar/{id} - Debe eliminar y redireccionar")
    void eliminar_Redirecciona() throws Exception {
        doNothing().when(clienteService).eliminar(1);

        mockMvc.perform(get("/clientes/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes"));

        verify(clienteService, times(1)).eliminar(1);
    }
}