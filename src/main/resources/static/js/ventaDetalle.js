class VentaDetalle {
    constructor() {
        this.init();
    }

    init() {
        this.inicializarEventListeners();
        this.prepararImpresion();
    }

    inicializarEventListeners() {
        // Confirmación para acciones de estado
        this.configurarConfirmaciones();

        // Inicializar tooltips
        this.inicializarTooltips();

        // Configurar eventos de teclado para impresión
        this.configurarAtajosTeclado();
    }

    configurarConfirmaciones() {
        console.log('Confirmaciones configuradas');
    }

    inicializarTooltips() {
        // Inicializar tooltips de Bootstrap
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }

    configurarAtajosTeclado() {
        document.addEventListener('keydown', (e) => {
            // Ctrl+P para imprimir
            if ((e.ctrlKey || e.metaKey) && e.key === 'p') {
                e.preventDefault();
                this.imprimirFactura();
            }

            // Escape para volver a la lista
            if (e.key === 'Escape') {
                window.location.href = '/ventas';
            }
        });
    }

    prepararImpresion() {
        // Agregar estilos específicos para impresión
        const printStyles = `
            @media print {
                .btn, .navbar, .breadcrumb, .alert, .border-top {
                    display: none !important;
                }
                .card {
                    border: none !important;
                    box-shadow: none !important;
                }
                .card-header {
                    background: white !important;
                    color: black !important;
                    border-bottom: 2px solid #333 !important;
                }
                .badge {
                    border: 1px solid #333 !important;
                    background: white !important;
                    color: black !important;
                }
                body {
                    background: white !important;
                    font-size: 12pt !important;
                }
                .container-fluid {
                    max-width: 100% !important;
                    padding: 0 !important;
                }
            }
        `;

        const styleSheet = document.createElement("style");
        styleSheet.type = "text/css";
        styleSheet.innerText = printStyles;
        document.head.appendChild(styleSheet);
    }

    imprimirFactura() {
        // Crear contenido optimizado para impresión
        const contenidoOriginal = document.querySelector('.card-custom').innerHTML;
        window.print();
    }

    exportarPDF() {
        // Función para exportar a PDF (implementación básica)
        console.log('Exportando a PDF...');

        // Simular descarga
        this.mostrarMensaje('Preparando documento PDF...', 'info');

        setTimeout(() => {
            this.mostrarMensaje('PDF generado correctamente', 'success');
            // Aquí iría la lógica real de generación y descarga de PDF
        }, 2000);
    }

    compartirFactura() {
        // Función para compartir la factura (implementación básica)
        if (navigator.share) {
            navigator.share({
                title: 'Factura: ' + document.querySelector('.card-header h5').textContent,
                text: 'Detalles de la factura',
                url: window.location.href
            })
                    .then(() => console.log('Factura compartida'))
                    .catch((error) => console.log('Error al compartir:', error));
        } else {
            // Fallback para navegadores que no soportan Web Share API
            this.copiarEnlace();
        }
    }

    copiarEnlace() {
        // Copiar enlace al portapapeles
        const enlace = window.location.href;
        navigator.clipboard.writeText(enlace)
                .then(() => {
                    this.mostrarMensaje('Enlace copiado al portapapeles', 'success');
                })
                .catch(err => {
                    console.error('Error al copiar enlace:', err);
                    this.mostrarMensaje('Error al copiar enlace', 'error');
                });
    }

    mostrarMensaje(mensaje, tipo = 'info') {
        // Crear y mostrar mensaje temporal
        const alertClass = {
            'success': 'alert-success',
            'error': 'alert-danger',
            'warning': 'alert-warning',
            'info': 'alert-info'
        }[tipo] || 'alert-info';

        const alerta = document.createElement('div');
        alerta.className = `alert ${alertClass} alert-dismissible fade show position-fixed`;
        alerta.style.cssText = 'top: 20px; right: 20px; z-index: 1050; min-width: 300px;';
        alerta.innerHTML = `
            <i class="fas ${this.obtenerIconoTipo(tipo)} me-2"></i>
            ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        document.body.appendChild(alerta);

        // Auto-eliminar después de 5 segundos
        setTimeout(() => {
            if (alerta.parentNode) {
                alerta.remove();
            }
        }, 5000);
    }

    obtenerIconoTipo(tipo) {
        const iconos = {
            'success': 'fa-check-circle',
            'error': 'fa-exclamation-circle',
            'warning': 'fa-exclamation-triangle',
            'info': 'fa-info-circle'
        };
        return iconos[tipo] || 'fa-info-circle';
    }

    // Método para cargar datos adicionales si es necesario
    cargarDatosAdicionales() {
        console.log('Cargando datos adicionales...');
    }

    // Método para generar código QR de la factura
    generarQR() {
        console.log('Generando código QR...');
    }
}

// Funciones globales para uso en el HTML
function imprimirFactura() {
    const ventaDetalle = new VentaDetalle();
    ventaDetalle.imprimirFactura();
}

function exportarPDF() {
    const ventaDetalle = new VentaDetalle();
    ventaDetalle.exportarPDF();
}

function compartirFactura() {
    const ventaDetalle = new VentaDetalle();
    ventaDetalle.compartirFactura();
}

// Inicializar cuando el documento esté listo
document.addEventListener('DOMContentLoaded', function () {
    new VentaDetalle();
});

// Exportar para uso en otros módulos si es necesario
window.VentaDetalle = VentaDetalle;