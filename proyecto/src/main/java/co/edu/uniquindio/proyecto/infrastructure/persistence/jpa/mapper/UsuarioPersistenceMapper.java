package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.mapper;

import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.valueobject.Email;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoUsuario;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.TipoUsuarioEnum;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper de persistencia para la entidad {@code Usuario}.
 *
 * <p>Convierte entre el modelo de dominio ({@link Usuario}) y el modelo
 * de persistencia ({@link UsuarioEntity}). Está gestionado por MapStruct
 * con el modelo de componente Spring.</p>
 *
 * <p>Los campos {@code password} y {@code rolSeguridad} son ignorados en la
 * conversión de dominio a entidad ya que son responsabilidad exclusiva de
 * la capa de seguridad e infraestructura.</p>
 */
@Mapper(componentModel = "spring")
public interface UsuarioPersistenceMapper {

    // ── Domain → Entity ───────────────────────────────────────────────────

    /**
     * Convierte la entidad de dominio {@link Usuario} a su entidad de persistencia.
     *
     * <p>{@code password} y {@code rolSeguridad} se ignoran porque son
     * gestionados por {@link co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.UsuarioJpaRepository}
     * directamente al crear o actualizar usuarios.</p>
     *
     * @param usuario Entidad de dominio a convertir.
     * @return Entidad JPA lista para persistir.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codigoExterno", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "email", expression = "java(usuario.getEmail().valor())")
    @Mapping(target = "tipoUsuario", expression = "java(toTipoUsuarioEnum(usuario.getTipoUsuario()))")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "rolSeguridad", ignore = true)
    UsuarioEntity toEntity(Usuario usuario);

    // ── Entity → Domain ───────────────────────────────────────────────────

    /**
     * Reconstruye la entidad de dominio {@link Usuario} desde su entidad de persistencia.
     *
     * @param entity Entidad JPA leída desde la base de datos.
     * @return Entidad de dominio reconstruida.
     */
    default Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;
        return new Usuario(
                entity.getCodigoExterno(),
                entity.getNombre(),
                new Email(entity.getEmail()),
                toTipoUsuarioDomain(entity.getTipoUsuario())
        );
    }

    // ── Conversiones de enumeradores ──────────────────────────────────────

    /** Convierte {@link TipoUsuario} del dominio a su equivalente de persistencia. */
    default TipoUsuarioEnum toTipoUsuarioEnum(TipoUsuario tipo) {
        if (tipo == null) return null;
        return TipoUsuarioEnum.valueOf(tipo.name());
    }

    /** Convierte {@link TipoUsuarioEnum} de persistencia a su equivalente de dominio. */
    default TipoUsuario toTipoUsuarioDomain(TipoUsuarioEnum tipo) {
        if (tipo == null) return null;
        return TipoUsuario.valueOf(tipo.name());
    }
}