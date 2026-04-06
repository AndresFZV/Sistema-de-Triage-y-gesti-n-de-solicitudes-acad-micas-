package co.edu.uniquindio.proyecto.domain.repository;

import co.edu.uniquindio.proyecto.domain.entity.Usuario;

public interface UsuarioRepository {

    Usuario obtenerPorId(String id);
}