package com.stylishhome.Sistema_CV_FR.service;

import com.stylishhome.Sistema_CV_FR.model.Cotizacion;
import com.stylishhome.Sistema_CV_FR.model.CotizacionDetalle;
import com.stylishhome.Sistema_CV_FR.model.Producto;
import com.stylishhome.Sistema_CV_FR.repository.CotizacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de cotizaciones
 */
@Service
@Transactional
public class CotizacionService {

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtiene todas las cotizaciones
     */
    public List<Cotizacion> obtenerTodasLasCotizaciones() {
        return cotizacionRepository.findAll();
    }

    /**
     * Busca una cotización por su ID
     */
    public Optional<Cotizacion> obtenerCotizacionPorId(Integer id) {
        return cotizacionRepository.findById(id);
    }

    /**
     * Crea una nueva cotización
     */
    public Cotizacion crearCotizacion(Cotizacion cotizacion) {
        validarCotizacion(cotizacion);

        // Establecer fecha actual si no está definida
        if (cotizacion.getFecha() == null) {
            cotizacion.setFecha(LocalDate.now());
        }

        // Establecer fecha de validez por defecto (15 días)
        if (cotizacion.getValidoHasta() == null) {
            cotizacion.setValidoHasta(cotizacion.getFecha().plusDays(15));
        }

        // Los totales se calculan automáticamente con @PrePersist y @PreUpdate
        // No es necesario llamar calcularTotales() manualmente
        return cotizacionRepository.save(cotizacion);
    }

    /**
     * Actualiza una cotización existente
     */
    public Cotizacion actualizarCotizacion(Integer id, Cotizacion cotizacion) {
        if (!cotizacionRepository.existsById(id)) {
            throw new RuntimeException("Cotización no encontrada con ID: " + id);
        }

        cotizacion.setIdCotizacion(id);
        validarCotizacion(cotizacion);
        // Los totales se calculan automáticamente con @PreUpdate
        // No es necesario llamar calcularTotales() manualmente

        return cotizacionRepository.save(cotizacion);
    }

    /**
     * Elimina una cotización por su ID
     */
    public void eliminarCotizacion(Integer id) {
        if (!cotizacionRepository.existsById(id)) {
            throw new RuntimeException("Cotización no encontrada con ID: " + id);
        }
        cotizacionRepository.deleteById(id);
    }

    /**
     * Busca cotizaciones por cliente
     */
    public List<Cotizacion> obtenerCotizacionesPorCliente(Integer clienteId) {
        return cotizacionRepository.findByClienteIdCliente(clienteId);
    }

    /**
     * Busca cotizaciones por empleado/vendedor
     */
    public List<Cotizacion> obtenerCotizacionesPorEmpleado(Integer empleadoId) {
        return cotizacionRepository.findByEmpleadoIdEmpleado(empleadoId);
    }

    /**
     * Busca cotizaciones por estado
     */
    public List<Cotizacion> obtenerCotizacionesPorEstado(Cotizacion.EstadoCotizacion estado) {
        return cotizacionRepository.findByEstado(estado);
    }

    /**
     * Busca cotizaciones por rango de fechas
     */
    public List<Cotizacion> obtenerCotizacionesPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return cotizacionRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    /**
     * Obtiene cotizaciones que están por vencer
     */
    public List<Cotizacion> obtenerCotizacionesPorVencer() {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(7); // Próximos 7 días
        return cotizacionRepository.findCotizacionesPorVencer(hoy, limite);
    }

    /**
     * Cambia el estado de una cotización
     */
    public Cotizacion cambiarEstadoCotizacion(Integer id, Cotizacion.EstadoCotizacion nuevoEstado) {
        Cotizacion cotizacion = cotizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada con ID: " + id));

        cotizacion.setEstado(nuevoEstado);
        return cotizacionRepository.save(cotizacion);
    }

    /**
     * Aprobar una cotización
     */
    public Cotizacion aprobarCotizacion(Integer id) {
        return cambiarEstadoCotizacion(id, Cotizacion.EstadoCotizacion.APROBADA);
    }

    /**
     * Rechazar una cotización
     */
    public Cotizacion rechazarCotizacion(Integer id) {
        return cambiarEstadoCotizacion(id, Cotizacion.EstadoCotizacion.RECHAZADA);
    }

    /**
     * Agrega un detalle a una cotización
     */
    public Cotizacion agregarDetalleCotizacion(Integer cotizacionId, CotizacionDetalle detalle) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada con ID: " + cotizacionId));

        // Verificar que el producto existe
        Producto producto = productoService.obtenerProductoPorId(detalle.getProducto().getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Establecer precio unitario desde el producto si no está definido
        if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) == 0) {
            detalle.setPrecioUnitario(producto.getPrecio());
        }

        detalle.setProducto(producto);
        cotizacion.agregarDetalle(detalle);
        // Los totales se calculan automáticamente con @PreUpdate
        // No es necesario llamar calcularTotales() manualmente

        return cotizacionRepository.save(cotizacion);
    }

    /**
     * Remueve un detalle de una cotización
     */
    public Cotizacion removerDetalleCotizacion(Integer cotizacionId, Integer detalleId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada con ID: " + cotizacionId));

        CotizacionDetalle detalle = cotizacion.getDetalles().stream()
                .filter(d -> d.getId().equals(detalleId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado con ID: " + detalleId));

        cotizacion.removerDetalle(detalle);
        // Los totales se calculan automáticamente con @PreUpdate
        // No es necesario llamar calcularTotales() manualmente

        return cotizacionRepository.save(cotizacion);
    }

    /**
     * Obtiene estadísticas de cotizaciones
     */
    public java.util.Map<String, Object> obtenerEstadisticasCotizaciones() {
        java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();

        List<Object[]> conteoPorEstado = cotizacionRepository.countCotizacionesPorEstado();
        for (Object[] resultado : conteoPorEstado) {
            String estado = (String) resultado[0];
            Long conteo = (Long) resultado[1];
            estadisticas.put("cotizaciones" + estado, conteo);
        }

        estadisticas.put("totalCotizaciones", cotizacionRepository.count());
        estadisticas.put("cotizacionesPorVencer", obtenerCotizacionesPorVencer().size());

        return estadisticas;
    }

    /**
     * Valida los datos de una cotización
     */
    private void validarCotizacion(Cotizacion cotizacion) {
        if (cotizacion.getCliente() == null || cotizacion.getCliente().getIdCliente() == null) {
            throw new RuntimeException("El cliente es obligatorio");
        }

        if (cotizacion.getEmpleado() == null || cotizacion.getEmpleado().getIdEmpleado() == null) {
            throw new RuntimeException("El empleado/vendedor es obligatorio");
        }

        if (cotizacion.getFecha() == null) {
            throw new RuntimeException("La fecha de la cotización es obligatoria");
        }

        if (cotizacion.getValidoHasta() == null) {
            throw new RuntimeException("La fecha de validez es obligatoria");
        }

        if (cotizacion.getValidoHasta().isBefore(cotizacion.getFecha())) {
            throw new RuntimeException("La fecha de validez no puede ser anterior a la fecha de la cotización");
        }

        // Verificar que el cliente existe
        if (!clienteService.obtenerClientePorId(cotizacion.getCliente().getIdCliente()).isPresent()) {
            throw new RuntimeException("El cliente especificado no existe");
        }

        // Verificar que el empleado existe
        if (!usuarioService.obtenerUsuarioPorId(cotizacion.getEmpleado().getIdEmpleado()).isPresent()) {
            throw new RuntimeException("El empleado/vendedor especificado no existe");
        }
    }
}
