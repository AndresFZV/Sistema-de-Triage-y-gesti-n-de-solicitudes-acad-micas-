package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios", indexes = {
        @Index(name = "idx_usuario_email", columnList = "email", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_externo", unique = true, nullable = false, length = 50)
    private String codigoExterno;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    /** Contraseña encriptada con BCrypt. Nunca se almacena en texto plano. */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false, length = 20)
    private TipoUsuarioEnum tipoUsuario;

    /** Rol de seguridad usado por Spring Security. */
    @Enumerated(EnumType.STRING)
    @Column(name = "rol_seguridad", nullable = false, length = 10)
    private RolSeguridadEnum rolSeguridad;
}