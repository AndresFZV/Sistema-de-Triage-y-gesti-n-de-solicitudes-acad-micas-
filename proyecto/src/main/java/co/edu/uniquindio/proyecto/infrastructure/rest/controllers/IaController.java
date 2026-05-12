package co.edu.uniquindio.proyecto.infrastructure.rest.controllers;

import co.edu.uniquindio.proyecto.domain.service.AsistenteIAService;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoSolicitud;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
@Tag(name = "IA", description = "Servicios de inteligencia artificial")
public class IaController {

    private final AsistenteIAService asistenteIAService;

    @PostMapping("/sugerir-tipo")
    @Operation(summary = "Sugerir tipo de solicitud basado en la descripción")
    public ResponseEntity<Map<String, String>> sugerirTipo(
            @RequestBody Map<String, String> body) {
        String descripcion = body.get("descripcion");
        TipoSolicitud tipo = asistenteIAService.sugerirTipo(descripcion);
        return ResponseEntity.ok(Map.of("tipoSugerido", tipo.name()));
    }

    @PostMapping("/resumir")
    @Operation(summary = "Resumir la descripción de una solicitud")
    public ResponseEntity<Map<String, String>> resumir(
            @RequestBody Map<String, String> body) {
        String descripcion = body.get("descripcion");
        String resumen = asistenteIAService.resumirSolicitud(descripcion);
        return ResponseEntity.ok(Map.of("resumen", resumen));
    }

    @PostMapping("/validar-descripcion")
    @Operation(summary = "Validar si la descripción es clara y suficiente")
    public ResponseEntity<Map<String, String>> validar(
            @RequestBody Map<String, String> body) {
        String descripcion = body.get("descripcion");
        String resultado = asistenteIAService.validarDescripcion(descripcion);
        return ResponseEntity.ok(Map.of("resultado", resultado));
    }
}