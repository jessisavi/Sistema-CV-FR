document.addEventListener('DOMContentLoaded', function () {
    inicializarEventos();
    inicializarFiltros();
    autoCerrarAlertas();
});

/**
 * Inicializa los eventos de la página
 */
function inicializarEventos() {
    // Evento para el filtro de categorías
    const categoriaSelect = document.getElementById('categoriaSelect');
    if (categoriaSelect) {
        categoriaSelect.addEventListener('change', function () {
            document.getElementById('filtroForm').submit();
        });
    }

    // Evento para tooltips
    inicializarTooltips();
}

/**
 * Inicializa los filtros de búsqueda
 */
function inicializarFiltros() {
    // Limpiar filtros si hay parámetros en la URL
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.toString() === '') {
        // Si no hay parámetros, asegurarse de que los campos estén limpios
        const busquedaInput = document.querySelector('input[name="busqueda"]');
        const categoriaSelect = document.getElementById('categoriaSelect');

        if (busquedaInput)
            busquedaInput.value = '';
        if (categoriaSelect)
            categoriaSelect.value = '';
    }
}

/**
 * Inicializa los tooltips de Bootstrap
 */
function inicializarTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[title]'));
    const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

/**
 * Auto-cierra las alertas después de 5 segundos
 */
function autoCerrarAlertas() {
    setTimeout(function () {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            if (alert.classList.contains('show')) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }
        });
    }, 5000);
}

/**
 * Exporta la lista de productos
 */
function exportarProductos() {
    // Mostrar mensaje de funcionalidad en desarrollo
    mostrarMensajeTemporal('Funcionalidad de exportación en desarrollo', 'info');
}

/**
 * Muestra un mensaje temporal al usuario
 * @param {string} mensaje - El mensaje a mostrar
 * @param {string} tipo - Tipo de mensaje: 'success', 'error', 'info', 'warning'
 */
function mostrarMensajeTemporal(mensaje, tipo = 'info') {
    const alertClass = {
        'success': 'alert-success',
        'error': 'alert-danger',
        'info': 'alert-info',
        'warning': 'alert-warning'
    }[tipo] || 'alert-info';

    const iconClass = {
        'success': 'fa-check-circle',
        'error': 'fa-exclamation-circle',
        'info': 'fa-info-circle',
        'warning': 'fa-exclamation-triangle'
    }[tipo] || 'fa-info-circle';

    const alertHtml = `
        <div class="alert ${alertClass} alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3" 
             style="z-index: 1060; min-width: 300px;" role="alert">
            <i class="fas ${iconClass} me-2"></i>
            ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', alertHtml);

    // Auto-cerrar después de 3 segundos
    setTimeout(() => {
        const alert = document.querySelector('.alert.position-fixed');
        if (alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }
    }, 3000);
}

/**
 * Filtra la tabla de productos en el cliente (si es necesario)
 * @param {string} busqueda - Texto de búsqueda
 */
function filtrarTabla(busqueda) {
    const tabla = document.querySelector('table');
    const filas = tabla.querySelectorAll('tbody tr');
    const busquedaLower = busqueda.toLowerCase();

    filas.forEach(fila => {
        const textoFila = fila.textContent.toLowerCase();
        if (textoFila.includes(busquedaLower)) {
            fila.style.display = '';
        } else {
            fila.style.display = 'none';
        }
    });
}

/**
 * Confirma la eliminación de un producto
 * @param {number} idProducto - ID del producto a eliminar
 * @param {string} nombreProducto - Nombre del producto
 */
function confirmarEliminacion(idProducto, nombreProducto) {
    if (confirm(`¿Está seguro de que desea eliminar el producto "${nombreProducto}"?`)) {
        // En una implementación real, aquí iría la llamada AJAX o redirección
        window.location.href = `/productos/eliminar/${idProducto}`;
    }
}

/**
 * Actualiza el stock de un producto
 * @param {number} idProducto - ID del producto
 * @param {number} nuevoStock - Nuevo valor de stock
 */
function actualizarStock(idProducto, nuevoStock) {
    if (nuevoStock < 0) {
        mostrarMensajeTemporal('El stock no puede ser negativo', 'error');
        return;
    }

    fetch(`/api/productos/${idProducto}/stock`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({stock: nuevoStock})
    })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    mostrarMensajeTemporal('Stock actualizado correctamente', 'success');
                    // Actualizar la interfaz
                    const stockElement = document.querySelector(`[data-producto-id="${idProducto}"] .stock-value`);
                    if (stockElement) {
                        stockElement.textContent = `${nuevoStock} und`;
                        actualizarEstadoStock(stockElement, nuevoStock);
                    }
                } else {
                    mostrarMensajeTemporal('Error al actualizar el stock', 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                mostrarMensajeTemporal('Error al actualizar el stock', 'error');
            });
}

/**
 * Actualiza el estado visual del stock
 * @param {HTMLElement} element - Elemento del stock
 * @param {number} stock - Cantidad de stock
 */
function actualizarEstadoStock(element, stock) {
    element.classList.remove('stock-low', 'stock-medium', 'stock-high');

    if (stock === 0) {
        element.classList.add('stock-low');
    } else if (stock <= 10) {
        element.classList.add('stock-low');
    } else if (stock <= 20) {
        element.classList.add('stock-medium');
    } else {
        element.classList.add('stock-high');
    }
}

// Exportar funciones para uso global
window.filtrarTabla = filtrarTabla;
window.confirmarEliminacion = confirmarEliminacion;
window.actualizarStock = actualizarStock;
window.exportarProductos = exportarProductos;