package org.esfe.HavenGlam.Controladores;
import org.esfe.HavenGlam.Modelos.Persona;
import org.esfe.HavenGlam.Servicios.Interfaces.IPersonaService;
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
public class PersonaControllerTest {
    private MockMvc mockMvc;

    @Mock
    private IPersonaService personaService;
    @InjectMocks
    private PersonaController personaController;

    private Persona persona;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(personaController).build();
        persona = new Persona(1, "Juan ", "Perez", "70000001", "San Salvador", "12345678-5");
    }
    @Test
    @DisplayName("GET /personas -Debe retornar la vista de list con perosnas")
    void listar_RetornaVistaList() throws Exception{
        when (personaService.listar()).thenReturn(Arrays.asList(persona));

        mockMvc.perform(get("/personas"))
                .andExpect(status().isOk())
                .andExpect(view().name("personas/list"))
                .andExpect(model().attributeExists("personas"));

        verify(personaService, times(1)).listar();
    }
   @Test
    @DisplayName("GET /personas/crear - Debe retornar la vista de form con una nueva Persona")
    void mostrarFormularioCrear_RetornarVistaForm() throws Exception{
        mockMvc.perform(get("/personas/crear"))
                .andExpect(status().isOk())
                .andExpect(view().name("personas/form"))
                .andExpect(model().attributeExists("persona"));
   }
   @Test
    @DisplayName("GET /persoonas/editar{id} - Debe retornar la vista de form con la persona encontrada ")
    void mostrarFormularioEditar_CuandoExiste_RetornarVistaForm()throws Exception{
        when(personaService.buscarPorId(1)).thenReturn(Optional.of(persona));

        mockMvc.perform(get("/personas/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("personas/form"))
                .andExpect(model().attributeExists("persona"));

        verify(personaService, times(1)).buscarPorId(1);

       }
    @Test
    @DisplayName("POST /personas/guardar -Con datos validos  debe guardar y redireccionar")
    void guardar_ConDatosValidos_Redirecciona()throws Exception{
        when(personaService.guardar(any(Persona.class))).thenReturn(persona);

       mockMvc.perform(post("/personas/guardar")
               .param("nombre", "Juan")
               .param("apellido", "Pérez")
               .param("telefono", "70000001")
               .param("direccion", "San Salvador")
               .param("dui", "12345678-9"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/personas"));

       verify(personaService, times(1)).guardar(any(Persona.class));
    }
    @Test
    @DisplayName("GET /personas/eliminar/{id} - Debe eliminar y redireccionar")
    void eliminar_Redirecciona() throws Exception{
        doNothing().when(personaService).eliminar(1);

        mockMvc.perform(get("/personas/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/personas"));

        verify(personaService, times(1)).eliminar(1);
    }
}
