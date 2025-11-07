package com.stylishhome.Sistema_CV_FR.repository;

import com.stylishhome.Sistema_CV_FR.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    
    Optional<Producto> findByCodigo(String codigo);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
    List<Producto> findByCategoriaIdCategoria(Integer idCategoria);
    
    List<Producto> findByStockLessThan(Integer stockMinimo);
    List<Producto> findByPrecioBetween(Double precioMin, Double precioMax);
    boolean existsByCodigo(String codigo);
    
    @Query("SELECT p FROM Producto p WHERE p.stock < 10 ORDER BY p.stock ASC")
    List<Producto> findProductosStockCritico();
    
    @Query("SELECT p FROM Producto p WHERE " +
           "(:nombre IS NULL OR p.nombre LIKE %:nombre%) AND " +
           "(:categoriaId IS NULL OR p.categoria.idCategoria = :categoriaId) AND " +
           "(:proveedorId IS NULL OR p.proveedor.idProveedor = :proveedorId)")
    List<Producto> buscarProductos(@Param("nombre") String nombre,
                                 @Param("categoriaId") Integer categoriaId,
                                 @Param("proveedorId") Integer proveedorId);
    
    // NUEVOS MÉTODOS PARA INFORMES
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock < :stockMinimo")
    Long countByStockLessThan(@Param("stockMinimo") Integer stockMinimo);
    

    @Query("SELECT p, SUM(vd.cantidad) as totalVendido, SUM(vd.cantidad * vd.precioUnitario) as totalIngresos " +
           "FROM Producto p " +
           "LEFT JOIN VentaDetalle vd ON p.idProducto = vd.producto.idProducto " +
           "LEFT JOIN Venta v ON vd.venta.idVenta = v.idVenta " +
           "WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin AND v.estado = 'COMPLETADA' " +
           "GROUP BY p.idProducto, p.codigo, p.nombre, p.descripcion, p.precio " +
           "ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidosPeriodo(@Param("fechaInicio") LocalDate fechaInicio,
                                                 @Param("fechaFin") LocalDate fechaFin);
    

    @Query("SELECT cat.nombre, SUM(vd.cantidad) as totalVendido " +
           "FROM Categoria cat " +
           "JOIN cat.productos p " +
           "LEFT JOIN VentaDetalle vd ON p.idProducto = vd.producto.idProducto " +
           "LEFT JOIN Venta v ON vd.venta.idVenta = v.idVenta " +
           "WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin AND v.estado = 'COMPLETADA' " +
           "GROUP BY cat.idCategoria, cat.nombre " +
           "ORDER BY totalVendido DESC")
    List<Object[]> findCategoriasMasVendidasPeriodo(@Param("fechaInicio") LocalDate fechaInicio,
                                                  @Param("fechaFin") LocalDate fechaFin);
    

    @Query("SELECT p, SUM(vd.cantidad) as totalVendido " +
           "FROM Producto p " +
           "LEFT JOIN VentaDetalle vd ON p.idProducto = vd.producto.idProducto " +
           "LEFT JOIN Venta v ON vd.venta.idVenta = v.idVenta " +
           "WHERE v.estado = 'COMPLETADA' " +
           "GROUP BY p.idProducto, p.codigo, p.nombre, p.descripcion, p.precio " +
           "ORDER BY totalVendido DESC LIMIT 10")
    List<Object[]> findTop10ProductosMasVendidos();
}