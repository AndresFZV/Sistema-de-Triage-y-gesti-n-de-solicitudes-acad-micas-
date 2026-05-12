package co.edu.uniquindio.proyecto.infrastructure.ia;

import co.edu.uniquindio.proyecto.domain.service.AsistenteIAService;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoSolicitud;
import co.edu.uniquindio.proyecto.infrastructure.ia.dto.GroqMessage;
import co.edu.uniquindio.proyecto.infrastructure.ia.dto.GroqRequest;
import co.edu.uniquindio.proyecto.infrastructure.ia.dto.GroqResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class GroqAsistenteIAService implements AsistenteIAService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public TipoSolicitud sugerirTipo(String descripcion) {
        String prompt = """
                Eres un asistente académico de la Universidad del Quindío.
                Analiza la siguiente descripción de una solicitud académica y responde ÚNICAMENTE con uno de estos valores exactos:
                HOMOLOGACION, CANCELACION, SOLICITUD_CUPO, OTRO
                
                No agregues explicaciones, solo el valor.
                
                Descripción: %s
                """.formatted(descripcion);

        try {
            String respuesta = llamarGroq(prompt).trim().toUpperCase();
            return TipoSolicitud.valueOf(respuesta);
        } catch (Exception e) {
            log.warn("[IA] No se pudo sugerir tipo para la solicitud. Usando OTRO por defecto.");
            return TipoSolicitud.OTRO;
        }
    }

    @Override
    public String resumirSolicitud(String descripcion) {
        String prompt = """
                Eres un asistente académico de la Universidad del Quindío.
                Resume la siguiente descripción de una solicitud en máximo 2 oraciones claras y concisas.
                Responde solo con el resumen, sin encabezados ni explicaciones adicionales.
                
                Descripción: %s
                """.formatted(descripcion);

        try {
            return llamarGroq(prompt).trim();
        } catch (Exception e) {
            log.warn("[IA] No se pudo resumir la solicitud. Retornando descripción original.");
            return descripcion;
        }
    }

    @Override
    public String validarDescripcion(String descripcion) {
        String prompt = """
                Eres un asistente académico de la Universidad del Quindío.
                Analiza si la siguiente descripción de una solicitud académica es clara y tiene suficiente información.
                Si es clara, responde: "OK"
                Si no es clara, responde con una sugerencia breve de mejora en máximo 1 oración.
                No agregues explicaciones adicionales.
                
                Descripción: %s
                """.formatted(descripcion);

        try {
            return llamarGroq(prompt).trim();
        } catch (Exception e) {
            log.warn("[IA] No se pudo validar la descripción.");
            return "OK";
        }
    }

    private String llamarGroq(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        GroqRequest request = new GroqRequest(
                model,
                List.of(new GroqMessage("user", prompt)),
                200,
                0.3f
        );

        HttpEntity<GroqRequest> entity = new HttpEntity<>(request, headers);

        GroqResponse response = restTemplate.postForObject(apiUrl, entity, GroqResponse.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new RuntimeException("Respuesta vacía de Groq");
        }

        return response.choices().get(0).message().content();
    }
}