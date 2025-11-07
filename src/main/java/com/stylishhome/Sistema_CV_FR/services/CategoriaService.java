package com.stylishhome.Sistema_CV_FR.service;

import com.stylishhome.Sistema_CV_FR.model.Categoria;
import com.stylishhome.Sistema_CV_FR.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de categorías
 */
@Service
public class CategoriaService {
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    //Obtiene todas las categorías ordenadas por nombre
    public List<Categoria> obtenerTodasLasCategorias() {
        return categoriaRepository.findAllByOrderByNombreAsc();
    }
    
    //Busca una categoría por su ID

    public Optional<Categoria> obtenerCategoriaPorId(Integer id) {
        return categoriaRepository.findById(id);
    }
    
    //Busca una categoría por su nombre

    public Optional<Categoria> obtenerCategoriaPorNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre);
    }
    
    //Guarda una categoría (crear o actualizar)

    public Categoria guardarCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }
    
    //Elimina una categoría por su ID

    public void eliminarCategoria(Integer id) {
        categoriaRepository.deleteById(id);
    }
    
    //Verifica si existe una categoría con el nombre especificado
   
    public boolean existeCategoria(String nombre) {
        return categoriaRepository.existsByNombre(nombre);
    }
    
    //Busca categorías por nombre (búsqueda parcial)

    public List<Categoria> buscarCategoriasPorNombre(String nombre) {
        return categoriaRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    //Obtiene categorías con productos en stock
  
    public List<Categoria> obtenerCategoriasConProductosEnStock() {
        return categoriaRepository.findCategoriasConProductosEnStock();
    }
    
    //Obtiene el número de productos en una categoría
 
    public Long contarProductosPorCategoria(Integer categoriaId) {
        return categoriaRepository.countProductosByCategoriaId(categoriaId);
    }
    
    //Obtiene las categorías más populares

    public List<Object[]> obtenerCategoriasMasPopulares(int limite) {
        return categoriaRepository.findCategoriasMasPopulares(limite);
    }
}