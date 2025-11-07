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
     * Autentica un usuario con nombre de usuario y contraseña
     * @param usuario Nombre de usuario
     * @param contraseña Contraseña
     * @return Usuario autenticado o empty si las credenciales son inválidas
     */
    public Optional<Usuario> autenticarUsuario(String usuario, String contraseña) {
        return usuarioRepository.findByUsuarioAndContraseña(usuario, contraseña);
    }
    
    /**
     * Obtiene todos los usuarios activos
     * @return Lista de usuarios activos
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
    
    /**
     * Busca un usuario por su ID
     * @param id ID del usuario
     * @return Usuario encontrado o empty si no existe
     */
    public Optional<Usuario> obtenerUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id);
    }
    
    /**
     * Guarda un usuario (crear o actualizar)
     * @param usuario Usuario a guardar
     * @return Usuario guardado
     */
    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Elimina un usuario por su ID
     * @param id ID del usuario a eliminar
     */
    public void eliminarUsuario(Integer id) {
        usuarioRepository.deleteById(id);
    }
    
    /**
     * Verifica si existe un usuario con el nombre de usuario especificado
     * @param usuario Nombre de usuario
     * @return true si existe, false en caso contrario
     */
    public boolean existeUsuario(String usuario) {
        return usuarioRepository.existsByUsuario(usuario);
    }
}