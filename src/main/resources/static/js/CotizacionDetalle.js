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
    
    // Inicializar tooltips de Bootstrap si existen
    initTooltips();
});

/**
 * Configura la funcionalidad de impresión
 */
function setupPrintFunctionality() {
    const printButton = document.querySelector('.btn-outline-custom[onclick*="print"]');
    
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
 * Inicializa los tooltips de Bootstrap
 */
function initTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

/**
 * Función para exportar la cotización a PDF (placeholder)
 */
function exportToPDF() {
    console.log('Función de exportación a PDF - por implementar');
    
    // Mostrar mensaje temporal
    showTemporaryMessage('La función de exportación a PDF estará disponible próximamente', 'info');
}

/**
 * Función para enviar la cotización por email (placeholder)
 */
function sendByEmail() {
    console.log('Función de envío por email - por implementar');
    
    // Mostrar mensaje temporal
    showTemporaryMessage('La función de envío por email estará disponible próximamente', 'info');
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
 * Calcula días restantes para vencimiento
 */
function getDaysUntilExpiry(validUntilDate) {
    const today = new Date();
    const expiryDate = new Date(validUntilDate);
    const diffTime = expiryDate - today;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
}

/**
 * Actualiza el badge de estado si la cotización está por vencer
 */
function updateExpiryStatus() {
    const validUntilElement = document.querySelector('[th\\:text*="validoHasta"]');
    const statusBadge = document.querySelector('.badge.fs-6');
    
    if (validUntilElement && statusBadge) {
        const validUntilText = validUntilElement.textContent.trim();
        const daysUntilExpiry = getDaysUntilExpiry(validUntilText);
        
        if (daysUntilExpiry <= 3 && daysUntilExpiry >= 0) {
            const warningIcon = document.createElement('i');
            warningIcon.className = 'fas fa-exclamation-triangle me-1';
            statusBadge.prepend(warningIcon);
            
            // Agregar tooltip
            statusBadge.setAttribute('data-bs-toggle', 'tooltip');
            statusBadge.setAttribute('title', `Vence en ${daysUntilExpiry} día(s)`);
            initTooltips();
        }
    }
}

// Ejecutar cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', updateExpiryStatus);
} else {
    updateExpiryStatus();
}