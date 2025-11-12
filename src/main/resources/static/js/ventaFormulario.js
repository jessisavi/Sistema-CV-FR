class VentaFormulario {
    constructor() {
        this.contadorFilas = 0;
        this.init();
    }

    init() {
        this.inicializarEventListeners();
        this.inicializarValores();
        this.calcularTotales();
        this.actualizarContadorFilas();
    }

    inicializarEventListeners() {
        // Agregar producto
        document.getElementById('agregarProducto').addEventListener('click', () => this.agregarProducto());

        // Event delegation para elementos dinámicos
        document.getElementById('productosBody').addEventListener('change', (e) => {
            if (e.target.classList.contains('producto-select')) {
                this.actualizarPrecioYTotal(e.target);
            }
        });

        document.getElementById('productosBody').addEventListener('input', (e) => {
            if (e.target.classList.contains('cantidad')) {
                this.actualizarPrecioYTotal(e.target);
            }
        });

        document.getElementById('productosBody').addEventListener('click', (e) => {
            if (e.target.classList.contains('eliminar-fila') || 
                e.target.closest('.eliminar-fila')) {
                this.eliminarFila(e);
            }
        });

        // Validación del formulario
        document.getElementById('ventaForm').addEventListener('submit', (e) => this.validarFormulario(e));
    }

    inicializarValores() {
        // Inicializar valores de productos existentes
        document.querySelectorAll('#productosBody tr').forEach((row, index) => {
            const productoSelect = row.querySelector('.producto-select');
            if (productoSelect && productoSelect.value) {
                this.actualizarPrecioInput(row);
            }
        });
    }

    agregarProducto() {
        const tbody = document.getElementById('productosBody');
        const nuevaFilaIndex = this.contadorFilas;

        const newRow = document.createElement('tr');
        newRow.innerHTML = `
            <td>${this.contadorFilas + 1}</td>
            <td>
                <select class="form-select producto-select" name="detalles[${nuevaFilaIndex}].producto.idProducto" required>
                    <option value="">Seleccionar producto...</option>
                    ${this.generarOpcionesProductos()}
                </select>
            </td>
            <td>
                <input type="number" class="form-control cantidad" name="detalles[${nuevaFilaIndex}].cantidad" value="1" min="1" required>
            </td>
            <td>
                <input type="text" class="form-control precio" name="detalles[${nuevaFilaIndex}].precioUnitario" value="" placeholder="$0" readonly>
            </td>
            <td>
                <input type="text" class="form-control total" name="detalles[${nuevaFilaIndex}].total" value="" placeholder="$0" readonly>
            </td>
            <td>
                <button type="button" class="btn btn-sm btn-danger eliminar-fila">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        `;

        tbody.appendChild(newRow);
        this.contadorFilas++;
        this.renumerarFilas();
    }

    generarOpcionesProductos() {
        const primerSelect = document.querySelector('.producto-select');
        if (!primerSelect) return '';

        let opciones = '';
        for (let i = 1; i < primerSelect.options.length; i++) {
            const option = primerSelect.options[i];
            opciones += `<option value="${option.value}" data-precio="${option.getAttribute('data-precio')}">${option.text}</option>`;
        }
        return opciones;
    }

    actualizarPrecioYTotal(elemento) {
        const row = elemento.closest('tr');
        if (!row) return;

        const productoSelect = row.querySelector('.producto-select');
        const precioInput = row.querySelector('.precio');
        const totalInput = row.querySelector('.total');
        const cantidadInput = row.querySelector('.cantidad');

        let precio = 0;
        if (productoSelect && productoSelect.value) {
            const selectedOption = productoSelect.options[productoSelect.selectedIndex];
            precio = parseFloat(selectedOption.getAttribute('data-precio')) || 0;
        }

        const cantidad = parseInt(cantidadInput.value) || 0;
        const total = precio * cantidad;

        precioInput.value = this.formatearMoneda(precio);
        totalInput.value = this.formatearMoneda(total);

        this.calcularTotales();
    }

    actualizarPrecioInput(row) {
        const productoSelect = row.querySelector('.producto-select');
        const precioInput = row.querySelector('.precio');
        
        if (productoSelect && productoSelect.value && precioInput) {
            const selectedOption = productoSelect.options[productoSelect.selectedIndex];
            const precio = parseFloat(selectedOption.getAttribute('data-precio')) || 0;
            precioInput.value = this.formatearMoneda(precio);
        }
    }

    eliminarFila(event) {
        const button = event.target.classList.contains('eliminar-fila') ? event.target : event.target.closest('.eliminar-fila');
        const row = button.closest('tr');
        
        if (row && confirm('¿Está seguro de eliminar este producto?')) {
            row.remove();
            this.renumerarFilas();
            this.calcularTotales();
            this.actualizarContadorFilas();
        }
    }

    renumerarFilas() {
        const filas = document.querySelectorAll('#productosBody tr');
        filas.forEach((fila, index) => {
            // Actualizar número de fila
            fila.querySelector('td:first-child').textContent = index + 1;
            
            // Actualizar nombres de los campos
            this.actualizarNombresCampos(fila, index);
        });
    }

    actualizarNombresCampos(fila, index) {
        const inputs = fila.querySelectorAll('input, select');
        inputs.forEach(input => {
            const name = input.getAttribute('name');
            if (name) {
                const newName = name.replace(/detalles\[\d+\]/, `detalles[${index}]`);
                input.setAttribute('name', newName);
            }
        });
    }

    actualizarContadorFilas() {
        this.contadorFilas = document.querySelectorAll('#productosBody tr').length;
    }

    calcularTotales() {
        let subtotal = 0;

        document.querySelectorAll('#productosBody tr').forEach(row => {
            const totalInput = row.querySelector('.total');
            if (totalInput && totalInput.value) {
                const totalValue = this.limpiarMoneda(totalInput.value);
                subtotal += totalValue;
            }
        });

        const descuento = 0; // Se puede agregar un campo para descuento
        const iva = subtotal * 0.19;
        const total = subtotal + iva - descuento;

        // Actualizar resumen
        document.getElementById('subtotalResumen').textContent = this.formatearMoneda(subtotal);
        document.getElementById('descuentoResumen').textContent = this.formatearMoneda(descuento);
        document.getElementById('ivaResumen').textContent = this.formatearMoneda(iva);
        document.getElementById('totalResumen').textContent = this.formatearMoneda(total);
    }

    formatearMoneda(valor) {
        const numero = typeof valor === 'string' ? this.limpiarMoneda(valor) : Number(valor);
        return new Intl.NumberFormat('es-CO', {
            style: 'currency',
            currency: 'COP',
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        }).format(numero);
    }

    limpiarMoneda(texto) {
        if (!texto || texto === '$0' || texto === '$' || texto === '') return 0;

        let limpio = texto.toString().replace(/[$\s]/g, '');
        limpio = limpio.replace(',', '.');

        const partes = limpio.split('.');
        if (partes.length > 1) {
            limpio = partes.join('');
        }

        const resultado = parseFloat(limpio);
        return isNaN(resultado) ? 0 : resultado;
    }

    validarFormulario(event) {
        const productos = document.querySelectorAll('.producto-select');
        let tieneProductosValidos = false;

        productos.forEach(select => {
            if (select.value) {
                const cantidad = select.closest('tr').querySelector('.cantidad').value;
                if (cantidad && parseInt(cantidad) > 0) {
                    tieneProductosValidos = true;
                }
            }
        });

        if (!tieneProductosValidos) {
            event.preventDefault();
            this.mostrarError('Debe agregar al menos un producto a la venta.');
            return false;
        }

        const totalElement = document.getElementById('totalResumen');
        const total = totalElement ? this.limpiarMoneda(totalElement.textContent) : 0;

        if (total <= 0) {
            event.preventDefault();
            this.mostrarError('El total de la venta debe ser mayor a cero.');
            return false;
        }

        // Verificar stock antes de enviar
        if (!this.verificarStockDisponible()) {
            event.preventDefault();
            return false;
        }

        return true;
    }

    verificarStockDisponible() {
        let stockDisponible = true;
        const promesas = [];

        document.querySelectorAll('#productosBody tr').forEach(row => {
            const productoSelect = row.querySelector('.producto-select');
            const cantidadInput = row.querySelector('.cantidad');

            if (productoSelect.value && cantidadInput.value) {
                const productoId = productoSelect.value;
                const cantidad = parseInt(cantidadInput.value);

                const promesa = this.verificarStockProducto(productoId, cantidad)
                    .then(disponible => {
                        if (!disponible) {
                            stockDisponible = false;
                            const productoNombre = productoSelect.options[productoSelect.selectedIndex].text;
                            this.mostrarError(`Stock insuficiente para: ${productoNombre}`);
                        }
                    })
                    .catch(error => {
                        console.error('Error al verificar stock:', error);
                    });

                promesas.push(promesa);
            }
        });

        // En una implementación real, se debería esperar a que todas las promesas se resuelvan
        return stockDisponible;
    }

    verificarStockProducto(productoId, cantidad) {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                resolve(true);
            }, 100);
        });
    }

    mostrarError(mensaje) {
        // Crear alerta temporal
        const alerta = document.createElement('div');
        alerta.className = 'alert alert-danger alert-dismissible fade show';
        alerta.innerHTML = `
            <i class="fas fa-exclamation-circle me-2"></i>
            ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        const container = document.querySelector('.container-fluid');
        container.insertBefore(alerta, container.firstChild);

        // Auto-eliminar después de 5 segundos
        setTimeout(() => {
            if (alerta.parentNode) {
                alerta.remove();
            }
        }, 5000);
    }
}

// Inicializar cuando el documento esté listo
document.addEventListener('DOMContentLoaded', function() {
    new VentaFormulario();
});

// Funciones globales para uso externo
window.VentaFormulario = VentaFormulario;