package co.edu.uniquindio.proyecto.infrastructure.rest;

import co.edu.uniquindio.proyecto.application.usecase.*;
import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.valueobject.*;
import co.edu.uniquindio.proyecto.infrastructure.rest.controllers.SolicitudController;
import co.edu.uniquindio.proyecto.infrastructure.rest.mapper.SolicitudMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SolicitudController.class)
class SolicitudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CrearSolicitudUseCase crearSolicitudUseCase;

    @MockitoBean
    private ClasificarSolicitudUseCase clasificarSolicitudUseCase;

    @MockitoBean
    private EnRevisionUseCase enRevisionUseCase;

    @MockitoBean
    private AtenderSolicitudUseCase atenderSolicitudUseCase;

    @MockitoBean
    private RechazarSolicitudUseCase rechazarSolicitudUseCase;

    @MockitoBean
    private CerrarSolicitudUseCase cerrarSolicitudUseCase;

    @MockitoBean
    private CancelarSolicitudUseCase cancelarSolicitudUseCase;

    @MockitoBean
    private ConsultarSolicitudesPorEstadoUseCase consultarPorEstadoUseCase;

    @MockitoBean
    private SolicitudRepository solicitudRepository;

    @MockitoBean
    private SolicitudMapper mapper;

    private Solicitud solicitudMock;
    private Usuario estudianteMock;

    @BeforeEach
    void setUp() {
        estudianteMock = new Usuario("U-001", "Juan Pérez",
                new Email("juan@uniquindio.edu.co"), TipoUsuario.ESTUDIANTE);
        solicitudMock = new Solicitud("Solicitud de homologación de materia cursada", estudianteMock);
    }

    // ---- POST /api/solicitudes ----

    @Test
    void debeRegistrarSolicitudYRetornar201() throws Exception {
        when(crearSolicitudUseCase.ejecutar(anyString(), anyString()))
                .thenReturn(solicitudMock);
        when(mapper.toResponse(any())).thenReturn(
                new co.edu.uniquindio.proyecto.application.dto.response.SolicitudResponse(
                        solicitudMock.getCodigo().valor(),
                        solicitudMock.getDescripcion(),
                        solicitudMock.getEstado().name(),
                        null, null,
                        solicitudMock.getFechaCreacion(),
                        null, null
                )
        );

        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "descripcion": "Solicitud de homologación de materia cursada",
                                    "solicitanteId": "U-001"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.codigo").exists())
                .andExpect(jsonPath("$.estado").value("CLASIFICACION"));
    }

    @Test
    void debeRetornar400CuandoDescripcionEsMuyCorta() throws Exception {
        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "descripcion": "Muy corta",
                                    "solicitanteId": "U-001"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.descripcion").exists());
    }

    @Test
    void debeRetornar400CuandoDescripcionEsNula() throws Exception {
        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "solicitanteId": "U-001"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.descripcion").exists());
    }

    @Test
    void debeRetornar400CuandoSolicitanteIdEsNulo() throws Exception {
        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "descripcion": "Solicitud de homologación de materia cursada"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.solicitanteId").exists());
    }

    // ---- PATCH /api/solicitudes/{codigo}/clasificar ----

    @Test
    void debeClasificarSolicitudYRetornar200() throws Exception {
        when(solicitudRepository.obtenerPorCodigo(any())).thenReturn(solicitudMock);
        when(mapper.toResponse(any())).thenReturn(
                new co.edu.uniquindio.proyecto.application.dto.response.SolicitudResponse(
                        solicitudMock.getCodigo().valor(),
                        solicitudMock.getDescripcion(),
                        "PENDIENTE",
                        "HOMOLOGACION", "ALTA",
                        solicitudMock.getFechaCreacion(),
                        null, null
                )
        );

        mockMvc.perform(patch("/api/solicitudes/SOL-001/clasificar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "tipoSolicitud": "HOMOLOGACION",
                                    "adminId": "U-002"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.tipoSolicitud").value("HOMOLOGACION"));
    }

    @Test
    void debeRetornar400CuandoTipoSolicitudEsNulo() throws Exception {
        mockMvc.perform(patch("/api/solicitudes/SOL-001/clasificar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "adminId": "U-002"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.tipoSolicitud").exists());
    }

    // ---- GET /api/solicitudes/{codigo}/historial ----

    @Test
    void debeRetornarHistorialDeSolicitud() throws Exception {
        when(solicitudRepository.obtenerPorCodigo(any())).thenReturn(solicitudMock);
        when(mapper.toEventoResponseList(any())).thenReturn(java.util.List.of(
                new co.edu.uniquindio.proyecto.application.dto.response.EventoHistorialResponse(
                        "Solicitud registrada",
                        "CLASIFICACION",
                        "Juan Pérez",
                        solicitudMock.getFechaCreacion()
                )
        ));

        mockMvc.perform(get("/api/solicitudes/SOL-001/historial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descripcion").value("Solicitud registrada"))
                .andExpect(jsonPath("$[0].estadoResultante").value("CLASIFICACION"));
    }

    // ---- Manejo de excepciones ----

    @Test
    void debeRetornar422CuandoSeViolaReglaDeNegocio() throws Exception {
        when(crearSolicitudUseCase.ejecutar(anyString(), anyString()))
                .thenThrow(new co.edu.uniquindio.proyecto.domain.exception
                        .ReglaDominioException("La descripción no puede estar vacía"));

        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "descripcion": "Solicitud de homologación de materia cursada",
                                    "solicitanteId": "U-001"
                                }
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensaje").value("La descripción no puede estar vacía"));
    }
}