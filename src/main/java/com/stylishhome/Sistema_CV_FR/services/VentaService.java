package com.stylishhome.Sistema_CV_FR.service;

import com.stylishhome.Sistema_CV_FR.model.Venta;
import com.stylishhome.Sistema_CV_FR.model.VentaDetalle;
import com.stylishhome.Sistema_CV_FR.model.Producto;
import com.stylishhome.Sistema_CV_FR.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de ventas
 */
@Service
@Transactional
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtiene todas las ventas
     */
    public List<Venta> obtenerTodasLasVentas() {
        return ventaRepository.findAll();
    }

    /**
     * Obtiene las últimas ventas para el Dashboard
     */
    public List<Venta> obtenerUltimasVentas(int limite) {
        return ventaRepository.findTopNByOrderByFechaDesc(limite);
    }

    /**
     * Busca una venta por su ID
     */
    public Optional<Venta> obtenerVentaPorId(Integer id) {
        return ventaRepository.findById(id);
    }

    /**
     * Busca una venta por número de factura
     */
    public Optional<Venta> obtenerVentaPorNumeroFactura(String numeroFactura) {
        return ventaRepository.findByNumeroFactura(numeroFactura);
    }

    /**
     * Obtiene el total de ventas general 
     */
    public Double obtenerVentasTotales() {
        return ventaRepository.obtenerVentasTotales();
    }

    /**
     * Obtiene el total de ventas de hoy 
     */
    public Double calcularVentasHoy() {
        LocalDate hoy = LocalDate.now();
        return ventaRepository.calcularTotalVentasHoy(hoy);
    }

    /**
     * Cuenta las ventas de hoy 
     */
    public Long contarVentasHoy() {
        LocalDate hoy = LocalDate.now();
        return ventaRepository.countByFecha(hoy);
    }

    /**
     * Crea una nueva venta
     */
    public Venta crearVenta(Venta venta) {
        validarVenta(venta);

        // Establecer fecha actual si no está definida
        if (venta.getFecha() == null) {
            venta.setFecha(LocalDate.now());
        }

        // Verificar stock y actualizar inventario
        for (VentaDetalle detalle : venta.getDetalles()) {
            Producto producto = productoService.obtenerProductoPorId(detalle.getProducto().getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + detalle.getProducto().getIdProducto()));

            if (!productoService.verificarStock(producto.getIdProducto(), detalle.getCantidad())) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            // Establecer precio unitario desde el producto si no está definido
            if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario().compareTo(java.math.BigDecimal.ZERO) == 0) {
                detalle.setPrecioUnitario(producto.getPrecio());
            }

            detalle.setProducto(producto);
        }

        // Los totales se calculan automáticamente con @PrePersist y @PreUpdate
        Venta ventaGuardada = ventaRepository.save(venta);

        // Actualizar stock después de guardar la venta
        if (ventaGuardada.getEstado() == Venta.EstadoVenta.COMPLETADA) {
            actualizarStockVenta(ventaGuardada);
        }

        return ventaGuardada;
    }

    /**
     * Actualiza una venta existente
     */
    public Venta actualizarVenta(Integer id, Venta venta) {
        if (!ventaRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrada con ID: " + id);
        }

        venta.setIdVenta(id);
        validarVenta(venta);
        // Los totales se calculan automáticamente con @PreUpdate

        return ventaRepository.save(venta);
    }

    /**
     * Elimina una venta por su ID
     */
    public void eliminarVenta(Integer id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));

        // Si la venta estaba completada, revertir el stock
        if (venta.getEstado() == Venta.EstadoVenta.COMPLETADA) {
            revertirStockVenta(venta);
        }

        ventaRepository.deleteById(id);
    }

    /**
     * Cambia el estado de una venta
     */
    public Venta cambiarEstadoVenta(Integer id, Venta.EstadoVenta nuevoEstado) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));

        Venta.EstadoVenta estadoAnterior = venta.getEstado();
        venta.setEstado(nuevoEstado);

        Venta ventaActualizada = ventaRepository.save(venta);

        // Actualizar stock si el estado cambió a COMPLETADA
        if (estadoAnterior != Venta.EstadoVenta.COMPLETADA && nuevoEstado == Venta.EstadoVenta.COMPLETADA) {
            actualizarStockVenta(ventaActualizada);
        } // Revertir stock si se cancela una venta completada
        else if (estadoAnterior == Venta.EstadoVenta.COMPLETADA && nuevoEstado == Venta.EstadoVenta.CANCELADA) {
            revertirStockVenta(ventaActualizada);
        }

        return ventaActualizada;
    }

    /**
     * Completa una venta
     */
    public Venta completarVenta(Integer id) {
        return cambiarEstadoVenta(id, Venta.EstadoVenta.COMPLETADA);
    }

    /**
     * Cancela una venta
     */
    public Venta cancelarVenta(Integer id) {
        return cambiarEstadoVenta(id, Venta.EstadoVenta.CANCELADA);
    }

    /**
     * Busca ventas por cliente
     */
    public List<Venta> obtenerVentasPorCliente(Integer clienteId) {
        return ventaRepository.findByClienteIdCliente(clienteId);
    }

    /**
     * Busca ventas por empleado/vendedor
     */
    public List<Venta> obtenerVentasPorEmpleado(Integer empleadoId) {
        return ventaRepository.findByEmpleadoIdEmpleado(empleadoId);
    }

    /**
     * Busca ventas por estado
     */
    public List<Venta> obtenerVentasPorEstado(Venta.EstadoVenta estado) {
        return ventaRepository.findByEstado(estado);
    }

    /**
     * Busca ventas por rango de fechas
     */
    public List<Venta> obtenerVentasPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return ventaRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    /**
     * Obtiene el total de ventas en un período
     */
    public Double calcularTotalVentasPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        return ventaRepository.calcularTotalVentasPeriodo(fechaInicio, fechaFin);
    }

    /**
     * Obtiene estadísticas de ventas 
     */
    public java.util.Map<String, Object> obtenerEstadisticasVentas() {
        java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();

        List<Object[]> ventasPorMetodoPago = ventaRepository.countVentasPorMetodoPago();
        for (Object[] resultado : ventasPorMetodoPago) {
            Venta.MetodoPago metodo = (Venta.MetodoPago) resultado[0];
            Long conteo = (Long) resultado[1];
            estadisticas.put("ventas" + metodo.name(), conteo);
        }

        estadisticas.put("totalVentas", ventaRepository.count());
        estadisticas.put("ventasCompletadas", ventaRepository.countByEstado(Venta.EstadoVenta.COMPLETADA));
        estadisticas.put("ventasPendientes", ventaRepository.countByEstado(Venta.EstadoVenta.PENDIENTE));
        estadisticas.put("ventasTotales", obtenerVentasTotales()); 
        estadisticas.put("ventasHoy", calcularVentasHoy()); 

        // Total de ventas del mes actual
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        estadisticas.put("ventasEsteMes", calcularTotalVentasPeriodo(inicioMes, finMes));

        return estadisticas;
    }

    /**
     * Actualiza el stock después de una venta completada
     */
    private void actualizarStockVenta(Venta venta) {
        for (VentaDetalle detalle : venta.getDetalles()) {
            productoService.actualizarStock(detalle.getProducto().getIdProducto(), -detalle.getCantidad());
        }
    }

    /**
     * Revierte el stock de una venta cancelada
     */
    private void revertirStockVenta(Venta venta) {
        for (VentaDetalle detalle : venta.getDetalles()) {
            productoService.actualizarStock(detalle.getProducto().getIdProducto(), detalle.getCantidad());
        }
    }

    /**
     * Valida los datos de una venta
     */
    private void validarVenta(Venta venta) {
        if (venta.getCliente() == null || venta.getCliente().getIdCliente() == null) {
            throw new RuntimeException("El cliente es obligatorio");
        }

        if (venta.getEmpleado() == null || venta.getEmpleado().getIdEmpleado() == null) {
            throw new RuntimeException("El empleado/vendedor es obligatorio");
        }

        if (venta.getFecha() == null) {
            throw new RuntimeException("La fecha de la venta es obligatoria");
        }

        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new RuntimeException("La venta debe tener al menos un producto");
        }

        // Verificar que el cliente existe
        if (!clienteService.obtenerClientePorId(venta.getCliente().getIdCliente()).isPresent()) {
            throw new RuntimeException("El cliente especificado no existe");
        }

        // Verificar que el empleado existe
        if (!usuarioService.obtenerUsuarioPorId(venta.getEmpleado().getIdEmpleado()).isPresent()) {
            throw new RuntimeException("El empleado/vendedor especificado no existe");
        }
    }
}
