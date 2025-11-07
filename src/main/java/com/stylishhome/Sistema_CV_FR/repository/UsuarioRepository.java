package com.stylishhome.Sistema_CV_FR.repository;

import com.stylishhome.Sistema_CV_FR.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la gestión de usuarios/empleados
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    /**
     * Busca un usuario por su nombre de usuario
     * @param usuario Nombre de usuario
     * @return Optional con el usuario encontrado
     */
    Optional<Usuario> findByUsuario(String usuario);
    
    /**
     * Verifica si existe un usuario con el nombre de usuario especificado
     * @param usuario Nombre de usuario
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsuario(String usuario);
    
    /**
     * Autentica un usuario por nombre de usuario y contraseña
     * @param usuario Nombre de usuario
     * @param contraseña Contraseña
     * @return Optional con el usuario autenticado
     */
    @Query("SELECT u FROM Usuario u WHERE u.usuario = :usuario AND u.contraseña = :contraseña AND u.activo = true")
    Optional<Usuario> findByUsuarioAndContraseña(@Param("usuario") String usuario, 
                                               @Param("contraseña") String contraseña);
}