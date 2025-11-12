class CotizacionLista {
    constructor() {
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setupFilters();
        this.setupExport();
        console.log('Lista de cotizaciones inicializada');
    }

    /**
     * Configura los event listeners
     */
    setupEventListeners() {
        // Botones de aprobación
        const btnAprobar = document.querySelectorAll('.btn-aprobar');
        btnAprobar.forEach(btn => {
            btn.addEventListener('click', (e) => {
                const codigo = btn.getAttribute('data-cotizacion-codigo');
                if (!confirm(`¿Está seguro de aprobar la cotización ${codigo}?`)) {
                    e.preventDefault();
                }
            });
        });

        // Botones de generar pedido
        const btnGenerarPedido = document.querySelectorAll('.btn-outline-success[title="Generar pedido"]');
        btnGenerarPedido.forEach(btn => {
            btn.addEventListener('click', (e) => {
                const cotizacionId = btn.getAttribute('data-cotizacion-id');
                this.generarPedido(cotizacionId);
            });
        });

        // Botón de exportar
        const btnExportar = document.getElementById('btnExportar');
        if (btnExportar) {
            btnExportar.addEventListener('click', () => {
                this.exportarCotizaciones();
            });
        }

        // Auto-cerrar alerts después de 5 segundos
        this.setupAutoCloseAlerts();
    }

    /**
     * Configura los filtros automáticos
     */
    setupFilters() {
        const selectEstado = document.getElementById('selectEstado');
        if (selectEstado) {
            selectEstado.addEventListener('change', () => {
                document.getElementById('filterForm').submit();
            });
        }

        // Filtro de búsqueda con debounce
        const inputBusqueda = document.getElementById('inputBusqueda');
        if (inputBusqueda) {
            let timeoutId;
            inputBusqueda.addEventListener('input', (e) => {
                clearTimeout(timeoutId);
                timeoutId = setTimeout(() => {
                    if (e.target.value.length === 0 || e.target.value.length >= 3) {
                        document.getElementById('searchForm').submit();
                    }
                }, 500);
            });
        }
    }

    /**
     * Configura la funcionalidad de exportación
     */
    setupExport() {
        // Puedes agregar aquí la lógica para configurar formatos de exportación
        console.log('Funcionalidad de exportación configurada');
    }

    /**
     * Genera un pedido a partir de una cotización
     */
    generarPedido(cotizacionId) {
        if (confirm('¿Desea generar un pedido a partir de esta cotización?')) {
            // Mostrar estado de carga
            this.mostrarCargando('Generando pedido...');

            // Aquí iría la llamada AJAX o redirección para generar el pedido
            console.log(`Generando pedido para cotización ID: ${cotizacionId}`);

            // Simular procesamiento
            setTimeout(() => {
                this.mostrarAlerta('Pedido generado correctamente', 'success');
                // Redireccionar o actualizar la página según sea necesario
            }, 2000);
        }
    }

    /**
     * Exporta las cotizaciones
     */
    exportarCotizaciones() {
        const formatos = ['PDF', 'Excel', 'CSV'];
        const formato = prompt(`Seleccione formato de exportación:\n${formatos.join(', ')}`, 'PDF');

        if (formato && formatos.map(f => f.toLowerCase()).includes(formato.toLowerCase())) {
            this.mostrarCargando(`Exportando a ${formato}...`);

            // Simular exportación
            setTimeout(() => {
                this.mostrarAlerta(`Cotizaciones exportadas en formato ${formato} correctamente`, 'success');

                // En una implementación real, aquí se descargaría el archivo
                console.log(`Exportando cotizaciones en formato: ${formato}`);
            }, 1500);
        } else if (formato) {
            this.mostrarAlerta(`Formato ${formato} no soportado`, 'error');
        }
    }

    /**
     * Muestra estado de carga
     */
    mostrarCargando(mensaje = 'Procesando...') {
        // Podrías integrar con un sistema de loading de tu aplicación
        console.log(`⏳ ${mensaje}`);

        // Ejemplo simple con alerta
        const alerta = document.createElement('div');
        alerta.className = 'alert alert-info alert-dismissible fade show';
        alerta.innerHTML = `
            <i class="fas fa-spinner fa-spin me-2"></i>${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        const container = document.querySelector('.container-fluid');
        container.insertBefore(alerta, container.firstChild);

        // Auto-eliminar después de 3 segundos
        setTimeout(() => {
            if (alerta.parentNode) {
                alerta.remove();
            }
        }, 3000);
    }

    /**
     * Muestra alertas al usuario
     */
    mostrarAlerta(mensaje, tipo = 'info') {
        const tipos = {
            'success': 'alert-success',
            'error': 'alert-danger',
            'warning': 'alert-warning',
            'info': 'alert-info'
        };

        const alerta = document.createElement('div');
        alerta.className = `alert ${tipos[tipo] || 'alert-info'} alert-dismissible fade show`;
        alerta.innerHTML = `
            <i class="fas ${this.getIconoAlerta(tipo)} me-2"></i>${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        const container = document.querySelector('.container-fluid');
        container.insertBefore(alerta, container.firstChild);

        // Auto-eliminar después de 5 segundos
        setTimeout(() => {
            if (alerta.parentNode) {
                const bsAlert = new bootstrap.Alert(alerta);
                bsAlert.close();
            }
        }, 5000);
    }

    /**
     * Obtiene el ícono correspondiente para el tipo de alerta
     */
    getIconoAlerta(tipo) {
        const iconos = {
            'success': 'fa-check-circle',
            'error': 'fa-exclamation-circle',
            'warning': 'fa-exclamation-triangle',
            'info': 'fa-info-circle'
        };
        return iconos[tipo] || 'fa-info-circle';
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
     * Filtra la tabla localmente (para implementaciones futuras)
     */
    filtrarTablaLocalmente(termino, estado) {
        const tabla = document.getElementById('tablaCotizaciones');
        const filas = tabla.querySelectorAll('tbody tr');
        let visibleCount = 0;

        filas.forEach(fila => {
            if (fila.cells.length === 1)
                return; // Saltar fila de "no hay datos"

            const textoFila = fila.textContent.toLowerCase();
            const estadoFila = fila.cells[5].textContent.trim();

            const coincideTexto = !termino || textoFila.includes(termino.toLowerCase());
            const coincideEstado = estado === 'Todas' || estadoFila === estado;

            if (coincideTexto && coincideEstado) {
                fila.style.display = '';
                visibleCount++;
            } else {
                fila.style.display = 'none';
            }
        });

        // Mostrar mensaje si no hay resultados
        this.mostrarResultadosFiltro(visibleCount);
    }

    /**
     * Muestra el resultado del filtrado local
     */
    mostrarResultadosFiltro(visibleCount) {
        // Implementar lógica para mostrar mensaje de resultados
        console.log(`Mostrando ${visibleCount} cotizaciones`);
    }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function () {
    new CotizacionLista();
});

// Exportar para uso en otros módulos si es necesario
if (typeof module !== 'undefined' && module.exports) {
    module.exports = CotizacionLista;
}

