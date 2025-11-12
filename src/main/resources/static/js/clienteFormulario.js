class ClienteFormulario {
    constructor() {
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setupFormValidation();
        this.setupAutoCodigo();
        this.setupTipoClienteHandler();
        console.log('Formulario de cliente inicializado');
    }

    /**
     * Configura los event listeners del formulario
     */
    setupEventListeners() {
        // Event listener para el botón de limpiar
        const btnLimpiar = document.getElementById('btnLimpiar');
        if (btnLimpiar) {
            btnLimpiar.addEventListener('click', (e) => {
                e.preventDefault();
                this.limpiarFormulario();
            });
        }

        // Event listener para prevenir envío con Enter en campos no submit
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && e.target.tagName !== 'TEXTAREA' && e.target.type !== 'submit') {
                e.preventDefault();
            }
        });

        // Event listener para mostrar confirmación al salir con cambios sin guardar
        window.addEventListener('beforeunload', (e) => {
            if (this.hasUnsavedChanges()) {
                e.preventDefault();
                e.returnValue = 'Tienes cambios sin guardar. ¿Estás seguro de que quieres salir?';
                return e.returnValue;
            }
        });
    }

    /**
     * Configura la validación del formulario
     */
    setupFormValidation() {
        const form = document.getElementById('clienteForm');
        if (!form)
            return;

        form.addEventListener('submit', (event) => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
                this.mostrarErroresValidacion();
            } else {
                this.mostrarCargando();
            }

            form.classList.add('was-validated');
        });

        // Validación en tiempo real para campos requeridos
        const requiredFields = form.querySelectorAll('[required]');
        requiredFields.forEach(field => {
            field.addEventListener('blur', () => {
                this.validarCampo(field);
            });

            field.addEventListener('input', () => {
                if (field.classList.contains('is-invalid')) {
                    this.validarCampo(field);
                }
            });
        });
    }

    /**
     * Configura la generación automática de código
     */
    setupAutoCodigo() {
        const nombreInput = document.getElementById('nombre');
        const apellidoInput = document.getElementById('apellido');

        if (nombreInput && apellidoInput) {
            // Solo generar código para nuevos clientes (cuando no hay ID)
            const clienteId = document.querySelector('input[name="id"]')?.value;
            if (!clienteId) {
                nombreInput.addEventListener('blur', () => this.generarCodigo());
                apellidoInput.addEventListener('blur', () => this.generarCodigo());
            }
        }
    }

    /**
     * Configura el manejo de cambios según el tipo de cliente
     */
    setupTipoClienteHandler() {
        const tipoClienteSelect = document.getElementById('tipo');
        if (tipoClienteSelect) {
            tipoClienteSelect.addEventListener('change', () => {
                this.actualizarCamposPremium();
            });
            // Ejecutar al cargar la página
            this.actualizarCamposPremium();
        }
    }

    /**
     * Genera un código automático basado en nombre y apellido
     */
    generarCodigo() {
        const nombre = document.getElementById('nombre')?.value.trim();
        const apellido = document.getElementById('apellido')?.value.trim();

        if (nombre && apellido) {
            const codigo = (nombre.substring(0, 3) + apellido.substring(0, 3)).toUpperCase();
            console.log('Código sugerido para el cliente:', codigo);

            // Aquí podrías asignar el código a un campo hidden si lo necesitas
            // document.getElementById('codigo').value = codigo;

            // Mostrar sugerencia al usuario
            this.mostrarSugerenciaCodigo(codigo);
        }
    }

    /**
     * Muestra sugerencia de código al usuario
     */
    mostrarSugerenciaCodigo(codigo) {
        // Podrías implementar un toast o notificación aquí
        console.info(`Sugerencia de código: ${codigo}`);
    }

    /**
     * Actualiza la visibilidad de campos según el tipo de cliente
     */
    actualizarCamposPremium() {
        const tipoClienteSelect = document.getElementById('tipo');
        const limiteCreditoGroup = document.getElementById('limiteCredito')?.closest('.mb-3');
        const descuentoGroup = document.getElementById('descuento')?.closest('.mb-3');

        if (!tipoClienteSelect || !limiteCreditoGroup || !descuentoGroup)
            return;

        const esPremium = tipoClienteSelect.value === 'Premium';

        // Siempre mostrar ambos campos, pero podrías agregar lógica específica aquí
        if (esPremium) {
            this.resaltarCamposPremium();
        } else {
            this.quitarResaltadoPremium();
        }
    }

    /**
     * Resalta campos específicos para clientes Premium
     */
    resaltarCamposPremium() {
        const camposPremium = document.querySelectorAll('#limiteCredito, #descuento');
        camposPremium.forEach(campo => {
            campo.closest('.mb-3').classList.add('campo-premium');
        });
    }

    /**
     * Quita el resaltado de campos Premium
     */
    quitarResaltadoPremium() {
        const camposPremium = document.querySelectorAll('.campo-premium');
        camposPremium.forEach(campo => {
            campo.classList.remove('campo-premium');
        });
    }

    /**
     * Valida un campo individual
     */
    validarCampo(field) {
        if (field.checkValidity()) {
            field.classList.remove('is-invalid');
            field.classList.add('is-valid');
        } else {
            field.classList.remove('is-valid');
            field.classList.add('is-invalid');
        }
    }

    /**
     * Muestra todos los errores de validación
     */
    mostrarErroresValidacion() {
        const invalidFields = document.querySelectorAll('.is-invalid');
        if (invalidFields.length > 0) {
            invalidFields[0].scrollIntoView({
                behavior: 'smooth',
                block: 'center'
            });

            // Mostrar alerta general
            this.mostrarAlerta('Por favor, complete todos los campos requeridos correctamente.', 'warning');
        }
    }

    /**
     * Limpia el formulario
     */
    limpiarFormulario() {
        if (confirm('¿Estás seguro de que quieres limpiar todos los campos del formulario?')) {
            const form = document.getElementById('clienteForm');
            form.reset();
            form.classList.remove('was-validated');

            // Remover clases de validación
            const fields = form.querySelectorAll('.is-valid, .is-invalid');
            fields.forEach(field => {
                field.classList.remove('is-valid', 'is-invalid');
            });

            this.mostrarAlerta('Formulario limpiado correctamente.', 'info');
        }
    }

    /**
     * Verifica si hay cambios sin guardar
     */
    hasUnsavedChanges() {
        const form = document.getElementById('clienteForm');
        if (!form)
            return false;

        const formData = new FormData(form);
        let hasChanges = false;

        for (let [key, value] of formData.entries()) {
            if (value && key !== 'id') {
                hasChanges = true;
                break;
            }
        }

        return hasChanges;
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

            // Restaurar después de 3 segundos (en caso de que algo falle)
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
        // Podrías integrar con tu sistema de notificaciones
        console.log(`[${tipo.toUpperCase()}] ${mensaje}`);

        // Ejemplo simple con alerta nativa
        if (tipo === 'warning') {
            alert(`⚠️ ${mensaje}`);
    }
    }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function () {
    new ClienteFormulario();
});

// Exportar para uso en otros módulos si es necesario
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ClienteFormulario;
}