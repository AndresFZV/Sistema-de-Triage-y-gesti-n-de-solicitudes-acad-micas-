package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa un usuario del sistema en la base de datos.
 *
 * <p>Combina dos responsabilidades complementarias: los datos de dominio
 * del usuario (nombre, email, tipo) y las credenciales de seguridad
 * (password encriptado, rol de Spring Security).</p>
 *
 * <p>Es el espejo de persistencia de la entidad {@code Usuario} del dominio.
 * El mapeo entre ambos lo realiza {@code UsuarioPersistenceMapper}.</p>
 */
@Entity
@Table(name = "usuarios", indexes = {
        @Index(name = "idx_usuario_email", columnList = "email", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class UsuarioEntity {

    /** Identificador interno generado automáticamente por la base de datos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificador externo de negocio del usuario. Inmutable una vez creado. */
    @Column(name = "codigo_externo", unique = true, nullable = false, length = 50)
    private String codigoExterno;

    /** Nombre completo del usuario. */
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    /** Dirección de correo electrónico única del usuario. */
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    /** Contraseña encriptada con BCrypt. Nunca se almacena en texto plano. */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /** Rol del usuario dentro del dominio académico. */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false, length = 20)
    private TipoUsuarioEnum tipoUsuario;

    /** Rol de seguridad usado por Spring Security para autorización. */
    @Enumerated(EnumType.STRING)
    @Column(name = "rol_seguridad", nullable = false, length = 10)
    private RolSeguridadEnum rolSeguridad;
}