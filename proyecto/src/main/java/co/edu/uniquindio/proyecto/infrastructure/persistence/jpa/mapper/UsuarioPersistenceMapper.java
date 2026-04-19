package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.mapper;

import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.valueobject.Email;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoUsuario;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.TipoUsuarioEnum;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioPersistenceMapper {

    // ---- Domain → Entity ----

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codigoExterno", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "email", expression = "java(usuario.getEmail().valor())")
    @Mapping(target = "tipoUsuario", expression = "java(toTipoUsuarioEnum(usuario.getTipoUsuario()))")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "rolSeguridad", ignore = true)
    UsuarioEntity toEntity(Usuario usuario);

    // ---- Entity → Domain ----

    default Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;
        return new Usuario(
                entity.getCodigoExterno(),
                entity.getNombre(),
                new Email(entity.getEmail()),
                toTipoUsuarioDomain(entity.getTipoUsuario())
        );
    }

    // ---- Enum conversions ----

    default TipoUsuarioEnum toTipoUsuarioEnum(TipoUsuario tipo) {
        if (tipo == null) return null;
        return TipoUsuarioEnum.valueOf(tipo.name());
    }

    default TipoUsuario toTipoUsuarioDomain(TipoUsuarioEnum tipo) {
        if (tipo == null) return null;
        return TipoUsuario.valueOf(tipo.name());
    }
}