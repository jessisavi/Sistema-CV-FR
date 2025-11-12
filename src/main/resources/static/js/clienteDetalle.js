document.addEventListener('DOMContentLoaded', function() {
    // Auto-cerrar alertas después de 5 segundos
    setTimeout(function() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // Configuración para impresión
    setupPrintFunctionality();
    
    // Inicializar filtros de actividad
    initFiltrosActividad();
    
    // Inicializar tooltips de Bootstrap si existen
    initTooltips();
    
    // Configurar selector de período
    setupSelectorPeriodo();
});

/**
 * Configura la funcionalidad de impresión
 */
function setupPrintFunctionality() {
    const printButton = document.querySelector('.btn-cliente-outline[onclick*="print"]');
    
    if (printButton) {
        printButton.addEventListener('click', function() {
            // Agregar clase para estilos de impresión
            document.body.classList.add('printing');
            
            // Esperar un momento antes de imprimir para que los estilos se apliquen
            setTimeout(function() {
                window.print();
                
                // Remover clase después de imprimir
                setTimeout(function() {
                    document.body.classList.remove('printing');
                }, 500);
            }, 100);
        });
    }
}

/**
 * Inicializa los filtros de actividad
 */
function initFiltrosActividad() {
    const filtroButtons = document.querySelectorAll('.btn-cliente-filtro');
    
    filtroButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Remover clase active de todos los botones
            filtroButtons.forEach(btn => btn.classList.remove('active'));
            
            // Agregar clase active al botón clickeado
            this.classList.add('active');
            
            // Obtener el tipo de filtro
            const filtro = this.getAttribute('data-filtro');
            
            // Aplicar filtro (aquí se implementaría la lógica de filtrado real)
            aplicarFiltroActividad(filtro);
        });
    });
}

/**
 * Aplica el filtro de actividad
 */
function aplicarFiltroActividad(filtro) {
    console.log('Aplicando filtro:', filtro);
    showTemporaryMessage(`Filtro aplicado: ${filtro.charAt(0).toUpperCase() + filtro.slice(1)}`, 'info');
}

/**
 * Configura el selector de período
 */
function setupSelectorPeriodo() {
    const selectorPeriodo = document.querySelector('.selector-periodo');
    
    if (selectorPeriodo) {
        selectorPeriodo.addEventListener('change', function() {
            const periodo = this.value;
            console.log('Período seleccionado:', periodo)
            showTemporaryMessage(`Período cambiado a: ${periodo}`, 'info');
        });
    }
}

/**
 * Inicializa los tooltips de Bootstrap
 */
function initTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

/**
 * Función para contactar al cliente por email
 */
function contactarCliente() {
    const email = document.querySelector('.info-item-cliente a[href^="mailto:"]');
    if (email) {
        email.click();
    }
}

/**
 * Función para contactar al cliente por teléfono
 */
function llamarCliente() {
    const telefono = document.querySelector('.info-item-cliente a[href^="tel:"]');
    if (telefono) {
        telefono.click();
    }
}

/**
 * Función para exportar información del cliente a PDF
 */
function exportarAPDF() {
    // Esta función sería implementada con una librería como jsPDF
    console.log('Función de exportación a PDF - por implementar');
    
    // Mostrar mensaje temporal
    showTemporaryMessage('La función de exportación a PDF estará disponible próximamente', 'info');
}

/**
 * Función para generar reporte del cliente
 */
function generarReporte() {
    console.log('Función de generación de reporte - por implementar');
    
    // Mostrar mensaje temporal
    showTemporaryMessage('La función de generación de reportes estará disponible próximamente', 'info');
}

/**
 * Muestra un mensaje temporal en la interfaz
 */
function showTemporaryMessage(message, type = 'info') {
    const alertClass = type === 'info' ? 'alert-info' : 
                      type === 'success' ? 'alert-success' : 
                      type === 'warning' ? 'alert-warning' : 'alert-danger';
    
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert ${alertClass} alert-dismissible fade show position-fixed`;
    alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 1050; min-width: 300px;';
    alertDiv.innerHTML = `
        <i class="fas fa-info-circle me-2"></i>
        <span>${message}</span>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    document.body.appendChild(alertDiv);
    
    // Auto-eliminar después de 3 segundos
    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.parentNode.removeChild(alertDiv);
        }
    }, 3000);
}

/**
 * Calcula días desde el registro del cliente
 */
function getDiasDesdeRegistro(fechaRegistro) {
    const hoy = new Date();
    const registro = new Date(fechaRegistro);
    const diffTime = hoy - registro;
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
}

/**
 * Actualiza información dinámica del cliente
 */
function actualizarInfoDinamica() {
    const fechaRegistroElement = document.querySelector('.info-item-cliente .fa-calendar').parentElement;
    
    if (fechaRegistroElement) {
        const fechaRegistroText = fechaRegistroElement.textContent.trim();
        const diasDesdeRegistro = getDiasDesdeRegistro(fechaRegistroText);
        
        // Agregar información de antigüedad si es relevante
        if (diasDesdeRegistro > 0) {
            const antiguedadElement = document.createElement('small');
            antiguedadElement.className = 'text-muted d-block mt-1';
            antiguedadElement.textContent = `Cliente desde hace ${diasDesdeRegistro} días`;
            
            // Verificar si ya existe el elemento para no duplicar
            const existingAntiguedad = fechaRegistroElement.parentElement.querySelector('.antiguedad-info');
            if (!existingAntiguedad) {
                antiguedadElement.classList.add('antiguedad-info');
                fechaRegistroElement.parentElement.appendChild(antiguedadElement);
            }
        }
    }
}

// Ejecutar cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', function() {
        actualizarInfoDinamica();
        
        const clienteId = obtenerClienteIdDePagina();
        if (clienteId) {
            cargarDatosAdicionales(clienteId);
        }
    });
} else {
    actualizarInfoDinamica();
    
    const clienteId = obtenerClienteIdDePagina();
    if (clienteId) {
        cargarDatosAdicionales(clienteId);
    }
}

/**
 * Obtiene el ID del cliente de la página actual
 */
function obtenerClienteIdDePagina() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get('id') || null;
}

/**
 * Función para cambiar el estado del cliente
 */
function cambiarEstadoCliente(nuevoEstado) {
    const clienteId = obtenerClienteIdDePagina();
    if (!clienteId) return;
    
    // Confirmar cambio de estado
    const confirmacion = confirm(`¿Está seguro de que desea cambiar el estado del cliente a "${nuevoEstado}"?`);
    
    if (confirmacion) {
        console.log(`Cambiando estado del cliente ${clienteId} a: ${nuevoEstado}`);
        
        showTemporaryMessage(`Estado cambiado a ${nuevoEstado}`, 'success');
        
        // Recargar la página después de un momento para ver los cambios
        setTimeout(() => {
            window.location.reload();
        }, 1500);
    }
}

