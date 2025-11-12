package com.stylishhome.Sistema_CV_FR.service;

import com.stylishhome.Sistema_CV_FR.model.Cliente;
import com.stylishhome.Sistema_CV_FR.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de clientes
 */
@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Obtiene todos los clientes registrados en el sistema
     *
     * @return Lista de todos los clientes
     */
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }

    /**
     * Busca un cliente por su ID
     *
     * @param id ID del cliente a buscar
     * @return Optional con el cliente encontrado o empty si no existe
     */
    public Optional<Cliente> obtenerClientePorId(Integer id) {
        return clienteRepository.findById(id);
    }

    /**
     * Busca clientes por nombre o apellido (búsqueda parcial)
     *
     * @param criterio Texto a buscar en nombre o apellido
     * @return Lista de clientes que coinciden con el criterio
     */
    public List<Cliente> buscarClientesPorNombreOApellido(String criterio) {
        return clienteRepository.findByNombreOrApellidoContaining(criterio);
    }

    /**
     * Busca clientes por tipo de cliente
     *
     * @param tipoCliente Tipo de cliente a filtrar (Premium, Regular, etc.)
     * @return Lista de clientes del tipo especificado
     */
    public List<Cliente> obtenerClientesPorTipo(String tipoCliente) {
        return clienteRepository.findByTipoCliente(tipoCliente);
    }

    /**
     * Busca clientes por estado
     *
     * @param estado Estado a filtrar (Activo, Inactivo, etc.)
     * @return Lista de clientes del estado especificado
     */
    public List<Cliente> obtenerClientesPorEstado(String estado) {
        return clienteRepository.findByEstado(estado);
    }

    /**
     * Busca clientes por ciudad
     *
     * @param ciudad Ciudad a filtrar
     * @return Lista de clientes en la ciudad especificada
     */
    public List<Cliente> obtenerClientesPorCiudad(String ciudad) {
        return clienteRepository.findByCiudad(ciudad);
    }

    /**
     * Guarda un cliente en la base de datos (crear o actualizar)
     *
     * @param cliente Cliente a guardar
     * @return Cliente guardado con ID generado
     * @throws RuntimeException Si el número de documento ya existe
     */
    public Cliente guardarCliente(Cliente cliente) {
        // Validar datos del cliente antes de guardar
        validarCliente(cliente);

        // Validar que el número de documento no exista (excepto para actualizaciones)
        if (cliente.getIdCliente() == null) {
            // Es un nuevo cliente, verificar que el documento no exista
            if (cliente.getNumeroDocumento() != null
                    && !cliente.getNumeroDocumento().trim().isEmpty()
                    && clienteRepository.existsByNumeroDocumento(cliente.getNumeroDocumento())) {
                throw new RuntimeException("Ya existe un cliente con el número de documento: " + cliente.getNumeroDocumento());
            }
            // Establecer fecha de registro si no está definida
            if (cliente.getFechaRegistro() == null) {
                cliente.setFechaRegistro(LocalDate.now());
            }
            // Generar código automático si no está definido
            if (cliente.getCodigo() == null || cliente.getCodigo().trim().isEmpty()) {
                cliente.setCodigo("CLI-" + System.currentTimeMillis());
            }
        } else {
            // Es una actualización, verificar que el documento no esté siendo usado por otro cliente
            Optional<Cliente> clienteExistente = clienteRepository.findById(cliente.getIdCliente());
            if (clienteExistente.isPresent()
                    && cliente.getNumeroDocumento() != null
                    && !clienteExistente.get().getNumeroDocumento().equals(cliente.getNumeroDocumento())
                    && clienteRepository.existsByNumeroDocumento(cliente.getNumeroDocumento())) {
                throw new RuntimeException("Ya existe otro cliente con el número de documento: " + cliente.getNumeroDocumento());
            }
        }

        return clienteRepository.save(cliente);
    }

    /**
     * Elimina un cliente por su ID
     *
     * @param id ID del cliente a eliminar
     * @throws RuntimeException Si el cliente no existe o tiene relaciones
     * activas
     */
    public void eliminarCliente(Integer id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("No se encontró el cliente con ID: " + id);
        }

        // Aquí podrías agregar validaciones adicionales antes de eliminar
        clienteRepository.deleteById(id);
    }

    /**
     * Verifica si existe un cliente con el número de documento especificado
     *
     * @param numeroDocumento Número de documento a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeClientePorDocumento(String numeroDocumento) {
        return clienteRepository.existsByNumeroDocumento(numeroDocumento);
    }

    /**
     * Obtiene el número total de clientes registrados
     *
     * @return Total de clientes
     */
    public Long contarTotalClientes() {
        return clienteRepository.count();
    }

    /**
     * Obtiene el número de clientes activos - NUEVO MÉTODO (para Dashboard)
     *
     * @return Número de clientes activos
     */
    public Long contarClientesActivos() {
        return clienteRepository.countClientesActivos();
    }

    /**
     * Obtiene el número de clientes por tipo
     *
     * @param tipoCliente Tipo de cliente a contar
     * @return Número de clientes del tipo especificado
     */
    public Long contarClientesPorTipo(String tipoCliente) {
        return clienteRepository.countByTipoCliente(tipoCliente);
    }

    /**
     * Obtiene el número de clientes por estado
     *
     * @param estado Estado a contar
     * @return Número de clientes del estado especificado
     */
    public Long contarClientesPorEstado(String estado) {
        return clienteRepository.countByEstado(estado);
    }

    /**
     * Obtiene clientes registrados en un rango de fechas
     *
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de clientes registrados en el período
     */
    public List<Cliente> obtenerClientesPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return clienteRepository.findByFechaRegistroBetween(fechaInicio, fechaFin);
    }

    /**
     * Obtiene los clientes más recientes
     *
     * @param limite Número máximo de clientes a retornar
     * @return Lista de clientes más recientes
     */
    public List<Cliente> obtenerClientesRecientes(int limite) {
        return clienteRepository.findTopNByOrderByFechaRegistroDesc(limite);
    }

    /**
     * Obtiene el número de nuevos clientes hoy - NUEVO MÉTODO (para Dashboard)
     *
     * @return Número de clientes registrados hoy
     */
    public Long contarNuevosClientesHoy() {
        LocalDate hoy = LocalDate.now();
        return clienteRepository.countByFechaRegistro(hoy);
    }

    /**
     * Busca clientes por múltiples criterios
     *
     * @param search Término de búsqueda (nombre, apellido, email, documento)
     * @param tipoCliente Tipo de cliente (opcional)
     * @param estado Estado del cliente (opcional)
     * @return Lista de clientes que coinciden con los criterios
     */
    public List<Cliente> buscarClientes(String search, String tipoCliente, String estado) {
        return clienteRepository.buscarClientesConFiltros(search, tipoCliente, estado);
    }

    /**
     * Busca clientes con todos los filtros aplicables
     *
     * @param search Término de búsqueda general
     * @param tipo Tipo de cliente
     * @param estado Estado del cliente
     * @param ciudad Ciudad del cliente
     * @return Lista de clientes filtrados
     */
    public List<Cliente> buscarClientesConFiltrosCompletos(String search, String tipo, String estado, String ciudad) {
        return clienteRepository.buscarClientesCompletos(search, tipo, estado, ciudad);
    }

    /**
     * Actualiza el tipo de cliente
     *
     * @param idCliente ID del cliente a actualizar
     * @param nuevoTipo Nuevo tipo de cliente
     * @return Cliente actualizado
     */
    public Cliente actualizarTipoCliente(Integer idCliente, String nuevoTipo) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + idCliente));

        cliente.setTipoCliente(nuevoTipo);
        return clienteRepository.save(cliente);
    }

    /**
     * Actualiza el estado de un cliente
     *
     * @param idCliente ID del cliente a actualizar
     * @param nuevoEstado Nuevo estado del cliente
     * @return Cliente actualizado
     */
    public Cliente actualizarEstadoCliente(Integer idCliente, String nuevoEstado) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + idCliente));

        cliente.setEstado(nuevoEstado);
        return clienteRepository.save(cliente);
    }

    /**
     * Obtiene estadísticas básicas de clientes
     *
     * @return Mapa con estadísticas de clientes
     */
    public java.util.Map<String, Object> obtenerEstadisticasClientes() {
        java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();

        estadisticas.put("totalClientes", contarTotalClientes());
        estadisticas.put("clientesActivos", contarClientesActivos()); // CORREGIDO
        estadisticas.put("clientesInactivos", contarClientesPorEstado("Inactivo"));
        estadisticas.put("clientesPremium", contarClientesPorTipo("Premium"));
        estadisticas.put("clientesRegular", contarClientesPorTipo("Regular"));
        estadisticas.put("clientesRecientes", obtenerClientesRecientes(5).size());
        estadisticas.put("nuevosClientesHoy", contarNuevosClientesHoy()); // NUEVO

        return estadisticas;
    }

    /**
     * Obtiene estadísticas de clientes por ciudad
     *
     * @return Lista de estadísticas por ciudad
     */
    public List<Object[]> obtenerEstadisticasPorCiudad() {
        return clienteRepository.findEstadisticasPorCiudad();
    }

    /**
     * Obtiene clientes asignados a un empleado específico
     *
     * @param idEmpleado ID del empleado
     * @return Lista de clientes asignados al empleado
     */
    public List<Cliente> obtenerClientesPorEmpleado(Integer idEmpleado) {
        return clienteRepository.findByEmpleadoAsignadoIdEmpleado(idEmpleado);
    }

    /**
     * Obtiene clientes sin empleado asignado
     *
     * @return Lista de clientes sin asignar
     */
    public List<Cliente> obtenerClientesSinAsignar() {
        return clienteRepository.findByEmpleadoAsignadoIsNull();
    }

    /**
     * Asigna un empleado a un cliente
     *
     * @param idCliente ID del cliente
     * @param idEmpleado ID del empleado
     * @return Cliente actualizado
     */
    public Cliente asignarEmpleadoACliente(Integer idCliente, Integer idEmpleado) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + idCliente));
        return clienteRepository.save(cliente);
    }

    /**
     * Valida los datos de un cliente antes de guardar
     *
     * @param cliente Cliente a validar
     * @throws RuntimeException Si los datos no son válidos
     */
    private void validarCliente(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del cliente es obligatorio");
        }

        if (cliente.getApellido() == null || cliente.getApellido().trim().isEmpty()) {
            throw new RuntimeException("El apellido del cliente es obligatorio");
        }

        if (cliente.getNumeroDocumento() == null || cliente.getNumeroDocumento().trim().isEmpty()) {
            throw new RuntimeException("El número de documento es obligatorio");
        }

        // Validar formato de email si está presente
        if (cliente.getEmail() != null && !cliente.getEmail().isEmpty()) {
            if (!cliente.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new RuntimeException("El formato del correo electrónico no es válido");
            }
        }

        // Validar formato de correo electrónico si está presente
        if (cliente.getCorreoElectronico() != null && !cliente.getCorreoElectronico().isEmpty()) {
            if (!cliente.getCorreoElectronico().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new RuntimeException("El formato del correo electrónico no es válido");
            }
        }

        // Validar que el estado sea válido
        if (cliente.getEstado() != null
                && !cliente.getEstado().equals("Activo")
                && !cliente.getEstado().equals("Inactivo")) {
            throw new RuntimeException("El estado del cliente debe ser 'Activo' o 'Inactivo'");
        }

        // Validar que el tipo de cliente sea válido
        if (cliente.getTipoCliente() != null
                && !cliente.getTipoCliente().equals("Premium")
                && !cliente.getTipoCliente().equals("Regular")) {
            throw new RuntimeException("El tipo de cliente debe ser 'Premium' o 'Regular'");
        }
    }

    /**
     * Obtiene el conteo de clientes por rango de fechas
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Número de clientes registrados en el período
     */
    public Long contarClientesPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return clienteRepository.countByFechaRegistroBetween(fechaInicio, fechaFin);
    }

    /**
     * Obtiene un cliente por su código único
     *
     * @param codigo Código del cliente
     * @return Optional con el cliente encontrado
     */
    public Optional<Cliente> obtenerClientePorCodigo(String codigo) {
        return clienteRepository.findByCodigo(codigo);
    }

    /**
     * Verifica si existe un cliente con el código especificado
     *
     * @param codigo Código a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existeClientePorCodigo(String codigo) {
        return clienteRepository.existsByCodigo(codigo);
    }
}
