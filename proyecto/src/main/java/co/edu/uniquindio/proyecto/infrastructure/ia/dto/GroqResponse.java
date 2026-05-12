package co.edu.uniquindio.proyecto.infrastructure.ia.dto;

import java.util.List;

public record GroqResponse(
        List<GroqChoice> choices
) {}