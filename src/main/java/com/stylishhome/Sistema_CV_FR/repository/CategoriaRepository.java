package com.stylishhome.Sistema_CV_FR.repository;

import com.stylishhome.Sistema_CV_FR.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de categorías de productos
 * Proporciona métodos para operaciones CRUD y consultas personalizadas
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    
    /**
     * Busca una categoría por su nombre (búsqueda exacta)
     * @param nombre Nombre de la categoría a buscar
     * @return Optional con la categoría encontrada
     */
    Optional<Categoria> findByNombre(String nombre);
    
    /**
     * Busca categorías por nombre (búsqueda parcial case-insensitive)
     * @param nombre Fragmento del nombre a buscar
     * @return Lista de categorías que coinciden con el criterio
     */
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Verifica si existe una categoría con el nombre especificado
     * @param nombre Nombre de la categoría a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Obtiene todas las categorías ordenadas por nombre de forma ascendente
     * @return Lista de categorías ordenadas alfabéticamente
     */
    List<Categoria> findAllByOrderByNombreAsc();
    
    /**
     * Busca categorías que contengan la descripción especificada
     * @param descripcion Fragmento de la descripción a buscar
     * @return Lista de categorías que coinciden con la descripción
     */
    List<Categoria> findByDescripcionContainingIgnoreCase(String descripcion);
    
    /**
     * Cuenta el número de productos asociados a una categoría
     * @param categoriaId ID de la categoría
     * @return Número de productos en la categoría
     */
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.categoria.id = :categoriaId")
    Long countProductosByCategoriaId(@Param("categoriaId") Integer categoriaId);
    
    /**
     * Obtiene categorías que tienen productos en stock
     * @return Lista de categorías con productos disponibles
     */
    @Query("SELECT DISTINCT c FROM Categoria c JOIN c.productos p WHERE p.stock > 0")
    List<Categoria> findCategoriasConProductosEnStock();
    
    /**
     * Obtiene categorías que no tienen productos asociados
     * @return Lista de categorías sin productos
     */
    @Query("SELECT c FROM Categoria c WHERE c.productos IS EMPTY")
    List<Categoria> findCategoriasSinProductos();
    
    /**
     * Obtiene el número total de categorías activas
     * @return Conteo de categorías
     */
    @Query("SELECT COUNT(c) FROM Categoria c")
    Long countTotalCategorias();
    
    /**
     * Busca categorías por múltiples criterios
     * @param nombre Nombre de la categoría (opcional)
     * @param descripcion Descripción de la categoría (opcional)
     * @return Lista de categorías que coinciden con los criterios
     */
    @Query("SELECT c FROM Categoria c WHERE " +
           "(:nombre IS NULL OR c.nombre LIKE %:nombre%) AND " +
           "(:descripcion IS NULL OR c.descripcion LIKE %:descripcion%)")
    List<Categoria> buscarCategorias(@Param("nombre") String nombre, 
                                   @Param("descripcion") String descripcion);
    
    /**
     * Obtiene categorías ordenadas por fecha de creación (más recientes primero)
     * @return Lista de categorías ordenadas por fecha descendente
     */
    List<Categoria> findAllByOrderByFechaCreacionDesc();
    
    /**
     * Obtiene las categorías más populares basadas en el número de productos
     * @param limit Número máximo de categorías a retornar
     * @return Lista de categorías ordenadas por cantidad de productos
     */
    @Query("SELECT c, COUNT(p) as productCount FROM Categoria c LEFT JOIN c.productos p " +
           "GROUP BY c.id, c.nombre, c.descripcion, c.fechaCreacion " +
           "ORDER BY productCount DESC")
    List<Object[]> findCategoriasMasPopulares(@Param("limit") int limit);
}