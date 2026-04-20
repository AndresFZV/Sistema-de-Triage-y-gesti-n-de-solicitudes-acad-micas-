package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa;

import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link UsuarioEntity}.
 *
 * <p>Define las operaciones de acceso a datos usando inferencia de métodos.
 * No es accedido directamente desde la capa de aplicación — actúa como
 * colaborador interno de {@link UsuarioJpaRepository}.</p>
 */
public interface UsuarioJpaDataRepository extends JpaRepository<UsuarioEntity, Long> {

    /** Busca un usuario por su código externo de negocio. */
    Optional<UsuarioEntity> findByCodigoExterno(String codigoExterno);

    /** Busca un usuario por su dirección de email. Usado por Spring Security durante el login. */
    Optional<UsuarioEntity> findByEmail(String email);

    /** Busca usuarios cuyo nombre contenga el texto indicado (insensible a mayúsculas). */
    List<UsuarioEntity> findByNombreContainingIgnoreCase(String nombre);
}