package com.stylishhome.Sistema_CV_FR.service;

import com.stylishhome.Sistema_CV_FR.model.Usuario;
import com.stylishhome.Sistema_CV_FR.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de usuarios/empleados
 */
@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * Busca un usuario por nombre de usuario para Spring Security
     *
     * @param username Nombre de usuario
     * @return Usuario encontrado o null si no existe
     */
    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsuario(username)
                .orElse(null);
    }
    
    /**
     * Obtiene todos los usuarios activos
     *
     * @return Lista de usuarios activos
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
    
    /**
     * Busca un usuario por su ID
     *
     * @param id ID del usuario
     * @return Usuario encontrado o empty si no existe
     */
    public Optional<Usuario> obtenerUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id);
    }
    
    /**
     * Guarda un usuario (crear o actualizar)
     *
     * @param usuario Usuario a guardar
     * @return Usuario guardado
     */
    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Elimina un usuario por su ID
     *
     * @param id ID del usuario a eliminar
     */
    public void eliminarUsuario(Integer id) {
        usuarioRepository.deleteById(id);
    }
    
    /**
     * Verifica si existe un usuario con el nombre de usuario especificado
     *
     * @param usuario Nombre de usuario
     * @return true si existe, false en caso contrario
     */
    public boolean existeUsuario(String usuario) {
        return usuarioRepository.existsByUsuario(usuario);
    }
    
    /**
     * Autentica un usuario con nombre de usuario y contraseña
     */
    public Optional<Usuario> autenticarUsuario(String usuario, String contraseña) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsuario(usuario);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuarioEncontrado = usuarioOpt.get();
            // Comparación directa (sin encriptación para compatibilidad con tu BD existente)
            if (usuarioEncontrado.getContraseña().equals(contraseña)) {
                return usuarioOpt;
            }
        }
        
        return Optional.empty();
    }
}