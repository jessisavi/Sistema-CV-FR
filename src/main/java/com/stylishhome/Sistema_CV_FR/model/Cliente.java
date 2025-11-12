package com.stylishhome.Sistema_CV_FR.model;

import com.stylishhome.Sistema_CV_FR.repository.ClienteRepository;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "clientes")
@Data
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcliente")
    private Integer idCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idempleado")
    private Usuario empleadoAsignado;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "tipo_documento", length = 10)
    private String tipoDocumento;

    @Column(name = "numero_documento", length = 20)
    private String numeroDocumento;

    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(name = "celular", length = 20)
    private String celular;

    @Column(name = "correo_electronico", length = 100)
    private String correoElectronico;

    @Column(name = "tipo_cliente", length = 20)
    private String tipoCliente = "Regular";

    @Column(name = "codigo", length = 20, unique = true)
    private String codigo;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "estado", length = 20)
    private String estado = "Activo";

//Obtener nombre completo
    public String getNombreCompleto() {
        return (this.nombre != null ? this.nombre : "") + " " + (this.apellido != null ? this.apellido : "");
    }

//Generar código automático
    @PrePersist
    public void prePersist() {
        if (this.codigo == null || this.codigo.trim().isEmpty()) {
            this.codigo = "CLI-" + System.currentTimeMillis();
        }
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDate.now();
        }
        if (this.estado == null) {
            this.estado = "Activo";
        }
        if (this.tipoCliente == null) {
            this.tipoCliente = "Regular";
        }
    }

    public static class Estadisticas {

        public static Long contarClientesActivos(ClienteRepository repository) {
            return repository.countClientesActivos();
        }
    }
}
