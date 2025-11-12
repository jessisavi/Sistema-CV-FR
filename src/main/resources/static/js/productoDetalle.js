class ProductoDetalle {
    constructor() {
        this.productoId = null;
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setupAutoCloseAlerts();
        this.inicializarModalConfirmacion();
        console.log('Módulo de detalle de producto inicializado');
    }

    /**
     * Configura los event listeners
     */
    setupEventListeners() {
        // Botón de eliminar producto
        const btnEliminar = document.querySelector('button[data-bs-target="#confirmarEliminacionModal"]');
        if (btnEliminar) {
            btnEliminar.addEventListener('click', (e) => {
                this.prepararEliminacion();
            });
        }

        // Confirmación de eliminación en el modal
        const formEliminar = document.querySelector('form[th\\:action*="eliminar"]');
        if (formEliminar) {
            formEliminar.addEventListener('submit', (e) => {
                if (!this.confirmarEliminacion()) {
                    e.preventDefault();
                }
            });
        }

        // Manejo de errores en la imagen del producto
        this.configurarManejoImagen();
    }

    /**
     * Configura el manejo de errores en la imagen del producto
     */
    configurarManejoImagen() {
        const imagenProducto = document.querySelector('.product-detail-image');
        if (imagenProducto) {
            imagenProducto.addEventListener('error', () => {
                // Si falla la imagen específica, intentar con la imagen por defecto
                if (!imagenProducto.src.includes('default.jpg')) {
                    imagenProducto.src = '/images/productos/default.jpg';
                }
            });
        }
    }

    /**
     * Prepara la eliminación del producto
     */
    prepararEliminacion() {
        const nombreProducto = document.querySelector('.card-header-custom h4 span')?.textContent || 'el producto';
        console.log(`Preparando eliminación de: ${nombreProducto}`);
    }

    /**
     * Confirma la eliminación del producto
     */
    confirmarEliminacion() {
        const nombreProducto = document.querySelector('.card-header-custom h4 span')?.textContent || 'el producto';
        const confirmacion = confirm(`¿Está completamente seguro de que desea eliminar "${nombreProducto}"?`);
        
        if (confirmacion) {
            this.mostrarCargando('Eliminando producto...');
            return true;
        }
        
        return false;
    }

    /**
     * Muestra estado de carga
     */
    mostrarCargando(mensaje) {
        console.log(mensaje);
    }

    /**
     * Configura el cierre automático de alertas
     */
    setupAutoCloseAlerts() {
        setTimeout(() => {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(alert => {
                if (!alert.classList.contains('alert-permanent')) {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                }
            });
        }, 5000);
    }

    /**
     * Inicializa el modal de confirmación
     */
    inicializarModalConfirmacion() {
        const modal = document.getElementById('confirmarEliminacionModal');
        if (modal) {
            modal.addEventListener('show.bs.modal', () => {
                this.prepararEliminacion();
            });
        }
    }

    /**
     * Actualiza el estado del stock visualmente
     */
    actualizarEstadoStock(stock) {
        const estadoElement = document.querySelector('.badge.fs-5.p-3');
        if (!estadoElement) return;

        // Remover clases anteriores
        estadoElement.classList.remove('bg-danger', 'bg-warning', 'bg-info', 'bg-success');
        
        // Aplicar nueva clase según el stock
        if (stock === 0) {
            estadoElement.classList.add('bg-danger');
            estadoElement.textContent = 'Sin Stock';
        } else if (stock <= 10) {
            estadoElement.classList.add('bg-warning');
            estadoElement.textContent = 'Stock Bajo';
        } else if (stock <= 20) {
            estadoElement.classList.add('bg-info');
            estadoElement.textContent = 'Stock Medio';
        } else {
            estadoElement.classList.add('bg-success');
            estadoElement.textContent = 'Stock Óptimo';
        }
    }

    /**
     * Exporta información del producto
     */
    exportarInformacion() {
        const producto = {
            nombre: document.querySelector('.card-header-custom h4 span')?.textContent,
            codigo: document.querySelector('.badge.bg-dark')?.textContent,
            precio: document.querySelector('.inventory-card .text-success')?.textContent,
            stock: document.querySelector('.inventory-card .text-success, .text-warning, .text-danger')?.textContent,
            ubicacion: document.querySelector('.inventory-card .text-primary')?.textContent
        };

        const contenido = `
Información del Producto - Stylish Home
=======================================

Nombre: ${producto.nombre}
Código: ${producto.codigo}
Precio: ${producto.precio}
Stock: ${producto.stock}
Ubicación: ${producto.ubicacion}

Generado el: ${new Date().toLocaleDateString()}
        `.trim();

        this.descargarArchivo(contenido, `producto-${producto.codigo}.txt`);
    }

    /**
     * Descarga un archivo con el contenido especificado
     */
    descargarArchivo(contenido, nombreArchivo) {
        const blob = new Blob([contenido], { type: 'text/plain' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        
        link.href = url;
        link.download = nombreArchivo;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
    }

    /**
     * Muestra alertas al usuario
     */
    mostrarAlerta(mensaje, tipo = 'info') {
        console.log(`[${tipo.toUpperCase()}] ${mensaje}`);

        // Ejemplo simple con alerta nativa
        if (tipo === 'error' || tipo === 'warning') {
            alert(`⚠️ ${mensaje}`);
        }
    }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    new ProductoDetalle();
});

// Exportar para uso en otros módulos si es necesario
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ProductoDetalle;
}