package co.edu.uniquindio.proyecto.application.dto.response;

import java.util.Map;

public record DashboardResponse(
        long totalSolicitudes,
        long pendientes,
        long enProceso,
        long atendidas,
        long rechazadas,
        long cerradas,
        long canceladas,
        long sinResponsable,
        Map<String, Long> porTipo
) {}