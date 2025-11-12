class CotizacionFormulario {
    constructor() {
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setupProductos();
        this.calcularTotales();
        console.log('Formulario de cotización inicializado');
    }

    /**
     * Configura los event listeners del formulario
     */
    setupEventListeners() {
        // Botones de calcular
        const btnCalcular = document.getElementById('btnCalcular');
        const btnCalcularFooter = document.getElementById('btnCalcularFooter');
        if (btnCalcular)
            btnCalcular.addEventListener('click', () => this.calcularTotales());
        if (btnCalcularFooter)
            btnCalcularFooter.addEventListener('click', () => this.calcularTotales());

        // Botón agregar producto
        const btnAgregarProducto = document.getElementById('btnAgregarProducto');
        if (btnAgregarProducto) {
            btnAgregarProducto.addEventListener('click', () => this.agregarProducto());
        }

        // Botones eliminar fila (delegación de eventos)
        document.addEventListener('click', (e) => {
            if (e.target.closest('.btn-eliminar-fila')) {
                this.eliminarFila(e.target.closest('.btn-eliminar-fila'));
            }
        });

        // Validación del formulario
        const form = document.getElementById('cotizacionForm');
        if (form) {
            form.addEventListener('submit', (e) => this.validarFormulario(e));
        }

        // Auto-cerrar alerts después de 5 segundos
        this.setupAutoCloseAlerts();
    }

    /**
     * Configura la funcionalidad de productos
     */
    setupProductos() {
        // Agregar event listeners a todas las filas existentes
        document.querySelectorAll('.fila-producto').forEach(fila => {
            this.agregarEventListenersFila(fila);
        });

        // Inicializar cálculos para filas existentes
        this.inicializarFilasExistentes();
    }

    /**
     * Agrega event listeners a una fila de producto
     */
    agregarEventListenersFila(fila) {
        const selectProducto = fila.querySelector('.producto-select');
        const inputCantidad = fila.querySelector('.cantidad');
        const inputPrecio = fila.querySelector('.precio');
        const inputDescuento = fila.querySelector('.descuento');

        const actualizarPrecioYTotal = () => {
            this.actualizarLineaProducto(fila);
        };

        selectProducto.addEventListener('change', actualizarPrecioYTotal);
        inputCantidad.addEventListener('input', actualizarPrecioYTotal);
        inputDescuento.addEventListener('input', actualizarPrecioYTotal);
    }

    /**
     * Inicializa las filas existentes con sus valores
     */
    inicializarFilasExistentes() {
        document.querySelectorAll('.fila-producto').forEach(fila => {
            const selectProducto = fila.querySelector('.producto-select');
            if (selectProducto && selectProducto.value) {
                this.actualizarLineaProducto(fila);
            }
        });
    }

    /**
     * Formatea un número como moneda colombiana
     */
    formatearMoneda(valor) {
        const numero = typeof valor === 'string' ? this.limpiarMoneda(valor) : Number(valor);
        return new Intl.NumberFormat('es-CO', {
            style: 'currency',
            currency: 'COP',
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        }).format(numero);
    }

    /**
     * Limpia el formato de moneda y devuelve un número puro
     */
    limpiarMoneda(texto) {
        if (!texto || texto === '$0' || texto === '$' || texto === '')
            return 0;

        let limpio = texto.toString().replace(/[$\s]/g, '');
        limpio = limpio.replace(',', '.');

        const partes = limpio.split('.');
        if (partes.length > 1) {
            limpio = partes.join('');
        }

        const resultado = parseFloat(limpio);
        return isNaN(resultado) ? 0 : resultado;
    }

    /**
     * Agrega una nueva fila de producto a la tabla
     */
    agregarProducto() {
        const tbody = document.getElementById('cuerpoTabla');

        // Obtener opciones de productos del primer select
        const selectOriginal = document.querySelector('.producto-select');
        let opcionesHTML = '<option value="">Seleccione producto</option>';

        if (selectOriginal) {
            for (let i = 1; i < selectOriginal.options.length; i++) {
                const option = selectOriginal.options[i];
                opcionesHTML += `<option value="${option.value}" data-precio="${option.getAttribute('data-precio')}">${option.text}</option>`;
            }
        }

        const nuevaFila = document.createElement('tr');
        nuevaFila.className = 'fila-producto';
        nuevaFila.innerHTML = `
            <td>
                <select class="form-select producto-select" name="productoId" required>
                    ${opcionesHTML}
                </select>
            </td>
            <td>
                <input type="number" class="form-control cantidad" name="cantidad" value="1" min="1" required>
            </td>
            <td>
                <input type="text" class="form-control precio" name="precioUnitario" value="" placeholder="$0" readonly>
            </td>
            <td>
                <input type="text" class="form-control descuento" name="descuento" placeholder="0% o $0">
            </td>
            <td>
                <input type="text" class="form-control total-linea" value="" placeholder="$0" readonly>
            </td>
            <td>
                <button type="button" class="btn btn-sm btn-outline-danger btn-eliminar-fila">
                    <i class="fas fa-times"></i>
                </button>
            </td>
        `;

        tbody.appendChild(nuevaFila);
        this.agregarEventListenersFila(nuevaFila);
    }

    /**
     * Elimina una fila de producto
     */
    eliminarFila(boton) {
        const fila = boton.closest('tr');
        if (document.querySelectorAll('.fila-producto').length > 1) {
            fila.remove();
            this.calcularTotales();
        } else {
            this.mostrarAlerta('Debe haber al menos un producto en la cotización', 'warning');
        }
    }

    /**
     * Actualiza los valores de una línea de producto
     */
    actualizarLineaProducto(fila) {
        const selectProducto = fila.querySelector('.producto-select');
        const inputCantidad = fila.querySelector('.cantidad');
        const inputPrecio = fila.querySelector('.precio');
        const inputDescuento = fila.querySelector('.descuento');
        const totalLinea = fila.querySelector('.total-linea');

        const selectedOption = selectProducto.options[selectProducto.selectedIndex];
        let precio = 0;

        if (selectedOption && selectedOption.value) {
            const precioData = selectedOption.getAttribute('data-precio');
            precio = parseFloat(precioData) || 0;
        }

        const cantidad = parseInt(inputCantidad.value) || 0;
        const descuentoTexto = inputDescuento.value.trim();

        // Calcular subtotal de la línea
        let subtotal = cantidad * precio;
        let descuentoValor = 0;

        // Procesar descuento
        if (descuentoTexto.includes('%')) {
            const porcentaje = parseFloat(descuentoTexto.replace('%', '')) || 0;
            descuentoValor = subtotal * (porcentaje / 100);
        } else if (descuentoTexto) {
            descuentoValor = this.limpiarMoneda(descuentoTexto);
        }

        const total = Math.max(0, subtotal - descuentoValor);

        // Actualizar campos con formato
        inputPrecio.value = this.formatearMoneda(precio);
        totalLinea.value = this.formatearMoneda(total);

        // Calcular totales generales
        this.calcularTotales();
    }

    /**
     * Calcula los totales generales de la cotización
     */
    calcularTotales() {
        const filas = document.querySelectorAll('.fila-producto');
        let subtotal = 0;
        let descuentoTotal = 0;

        filas.forEach(fila => {
            const cantidad = parseFloat(fila.querySelector('.cantidad').value) || 0;
            const precio = this.limpiarMoneda(fila.querySelector('.precio').value);
            const descuentoTexto = fila.querySelector('.descuento').value.trim();

            // Calcular subtotal de la línea
            const subtotalLinea = cantidad * precio;
            subtotal += subtotalLinea;

            // Calcular descuento de la línea
            if (descuentoTexto.includes('%')) {
                const porcentaje = parseFloat(descuentoTexto.replace('%', '')) || 0;
                descuentoTotal += subtotalLinea * (porcentaje / 100);
            } else if (descuentoTexto) {
                descuentoTotal += this.limpiarMoneda(descuentoTexto);
            }
        });

        // Calcular IVA sobre el subtotal menos descuento
        const baseImponible = Math.max(0, subtotal - descuentoTotal);
        const iva = baseImponible * 0.19;
        const total = baseImponible + iva;

        // Actualizar elementos del resumen
        this.actualizarResumen(subtotal, descuentoTotal, iva, total);
    }

    /**
     * Actualiza el resumen financiero
     */
    actualizarResumen(subtotal, descuento, iva, total) {
        const subtotalElement = document.getElementById('subtotalResumen');
        const descuentoElement = document.getElementById('descuentoResumen');
        const ivaElement = document.getElementById('ivaResumen');
        const totalElement = document.getElementById('totalResumen');

        if (subtotalElement)
            subtotalElement.innerHTML = '$' + this.formatearMoneda(subtotal).replace('$', '');
        if (descuentoElement)
            descuentoElement.innerHTML = '-$' + this.formatearMoneda(descuento).replace('$', '');
        if (ivaElement)
            ivaElement.innerHTML = '$' + this.formatearMoneda(iva).replace('$', '');
        if (totalElement)
            totalElement.innerHTML = '$' + this.formatearMoneda(total).replace('$', '');
    }

    /**
     * Valida el formulario antes de enviar
     */
    validarFormulario(e) {
        const filas = document.querySelectorAll('.fila-producto');
        let tieneProductosValidos = false;

        filas.forEach(fila => {
            const productoId = fila.querySelector('.producto-select').value;
            const cantidad = parseFloat(fila.querySelector('.cantidad').value) || 0;
            const precio = this.limpiarMoneda(fila.querySelector('.precio').value);

            if (productoId && cantidad > 0 && precio > 0) {
                tieneProductosValidos = true;
            }
        });

        if (!tieneProductosValidos) {
            e.preventDefault();
            this.mostrarAlerta('Debe agregar al menos un producto válido a la cotización', 'error');
            return false;
        }

        // Obtener el total limpio
        const totalElement = document.getElementById('totalResumen');
        const total = totalElement ? this.limpiarMoneda(totalElement.textContent) : 0;

        if (total <= 0) {
            e.preventDefault();
            this.mostrarAlerta('El total de la cotización debe ser mayor a cero', 'error');
            return false;
        }

        // Recalcular antes de enviar
        this.calcularTotales();
        this.mostrarCargando();
        return true;
    }

    /**
     * Muestra estado de carga
     */
    mostrarCargando() {
        const btnGuardar = document.getElementById('btnGuardar');
        if (btnGuardar) {
            const originalText = btnGuardar.innerHTML;
            btnGuardar.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Guardando...';
            btnGuardar.disabled = true;

            setTimeout(() => {
                btnGuardar.innerHTML = originalText;
                btnGuardar.disabled = false;
            }, 3000);
        }
    }

    /**
     * Muestra alertas al usuario
     */
    mostrarAlerta(mensaje, tipo = 'info') {
        // Integrar con tu sistema de notificaciones existente
        console.log(`[${tipo.toUpperCase()}] ${mensaje}`);

        if (tipo === 'error' || tipo === 'warning') {
            alert(`⚠️ ${mensaje}`);
    }
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
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function () {
    new CotizacionFormulario();
});

// Exportar para uso en otros módulos si es necesario
if (typeof module !== 'undefined' && module.exports) {
    module.exports = CotizacionFormulario;
}