/**
 * JavaScript principal para el Sistema CV
 */

class SistemaCV {
    constructor() {
        this.colors = {
            primary: '#000000',
            secondary: '#d9d9d9',
            accent: '#cb9a28',
            success: '#28a745',
            danger: '#dc3545',
            warning: '#ffc107',
            info: '#17a2b8'
        };
        this.init();
    }

    init() {
        this.inicializarFuncionesComunes();
        this.setupGlobalEvents();
    }

    inicializarFuncionesComunes() {
        this.autoCerrarAlertas();
        this.inicializarTooltips();
        this.inicializarAnimacionesEntrada();
        this.configurarMascarasEntrada();
        this.configurarValidacionesFormularios();
    }

    setupGlobalEvents() {
        // Manejar errores globales
        window.addEventListener('error', (e) => {
            console.error('Error global:', e.error);
            this.mostrarMensaje('Ha ocurrido un error inesperado', 'danger');
        });

        window.addEventListener('unhandledrejection', (e) => {
            console.error('Promesa rechazada:', e.reason);
            this.mostrarMensaje('Error en la operación', 'danger');
        });

        // Mejorar accesibilidad de formularios
        this.mejorarAccesibilidadFormularios();
    }

    mejorarAccesibilidadFormularios() {
        // Agregar labels automáticos a campos sin label
        const inputsSinLabel = document.querySelectorAll('input:not([id*="hidden"]):not([type="hidden"])');
        inputsSinLabel.forEach(input => {
            if (!input.labels || input.labels.length === 0) {
                const label = document.createElement('label');
                label.textContent = input.getAttribute('placeholder') || 'Campo de entrada';
                label.className = 'form-label visually-hidden';
                input.parentNode.insertBefore(label, input);
            }
        });
    }

    /**
     * Auto-cierra las alertas después de 5 segundos
     */
    autoCerrarAlertas() {
        const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
        alerts.forEach((alert, index) => {
            setTimeout(() => {
                if (alert.parentNode) {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                }
            }, 5000 + (index * 500));
        });
    }

    /**
     * Inicializa los tooltips de Bootstrap
     */
    inicializarTooltips() {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map((tooltipTriggerEl) => {
            return new bootstrap.Tooltip(tooltipTriggerEl, {
                boundary: 'window'
            });
        });
    }

    /**
     * Inicializa animaciones de entrada para elementos
     */
    inicializarAnimacionesEntrada() {
        const elementosAnimados = document.querySelectorAll('[data-aos="fade-up"]');

        // Si no hay AOS, usar animaciones básicas
        if (typeof AOS === 'undefined') {
            elementosAnimados.forEach((elemento, index) => {
                elemento.style.opacity = '0';
                elemento.style.transform = 'translateY(20px)';
                elemento.style.transition = 'all 0.5s ease';

                setTimeout(() => {
                    elemento.style.opacity = '1';
                    elemento.style.transform = 'translateY(0)';
                }, index * 100);
            });
        }
    }

    /**
     * Configura máscaras de entrada para formularios
     */
    configurarMascarasEntrada() {
        // Máscara para números de teléfono
        const telefonoInputs = document.querySelectorAll('input[type="tel"]');
        telefonoInputs.forEach(input => {
            input.addEventListener('input', (e) => {
                let value = e.target.value.replace(/\D/g, '');
                if (value.length >= 10) {
                    value = value.replace(/(\d{3})(\d{3})(\d{4})/, '($1) $2-$3');
                }
                e.target.value = value;
            });
        });

        // Máscara para números de documento
        const documentoInputs = document.querySelectorAll('input[id*="documento"]');
        documentoInputs.forEach(input => {
            input.addEventListener('input', (e) => {
                e.target.value = e.target.value.replace(/[^a-zA-Z0-9]/g, '');
            });
        });

        // Máscara para moneda
        const monedaInputs = document.querySelectorAll('input[data-mask="currency"]');
        monedaInputs.forEach(input => {
            input.addEventListener('input', (e) => {
                let value = e.target.value.replace(/\D/g, '');
                if (value) {
                    value = this.formatearMoneda(parseInt(value));
                }
                e.target.value = value;
            });
        });
    }

    /**
     * Configura validaciones de formularios
     */
    configurarValidacionesFormularios() {
        const forms = document.querySelectorAll('form[data-validate="true"]');

        forms.forEach(form => {
            // Validación en tiempo real
            const inputs = form.querySelectorAll('input, select, textarea');
            inputs.forEach(input => {
                input.addEventListener('blur', () => {
                    this.validarCampo(input);
                });
            });

            // Validación al enviar
            form.addEventListener('submit', (e) => {
                if (!this.validarFormulario(form)) {
                    e.preventDefault();
                    this.mostrarMensaje('Por favor complete todos los campos obligatorios correctamente', 'warning');
                }
            });
        });
    }

    /**
     * Valida un campo individual
     * @param {HTMLElement} campo - Campo a validar
     */
    validarCampo(campo) {
        if (campo.hasAttribute('required') && !campo.value.trim()) {
            this.marcarCampoInvalido(campo, 'Este campo es obligatorio');
        } else if (campo.type === 'email' && campo.value) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(campo.value)) {
                this.marcarCampoInvalido(campo, 'Ingrese un email válido');
            } else {
                this.marcarCampoValido(campo);
            }
        } else {
            this.marcarCampoValido(campo);
        }
    }

    /**
     * Valida un formulario
     * @param {HTMLFormElement} formulario - Formulario a validar
     * @returns {boolean} - True si es válido
     */
    validarFormulario(formulario) {
        const camposRequeridos = formulario.querySelectorAll('[required]');
        let esValido = true;

        camposRequeridos.forEach(campo => {
            this.validarCampo(campo);
            if (campo.classList.contains('is-invalid')) {
                esValido = false;
            }
        });

        return esValido;
    }

    /**
     * Marca un campo como inválido
     * @param {HTMLElement} campo - Campo a marcar
     * @param {string} mensaje - Mensaje de error
     */
    marcarCampoInvalido(campo, mensaje = 'Este campo es obligatorio') {
        campo.classList.add('is-invalid');
        campo.classList.remove('is-valid');

        let mensajeError = campo.parentNode.querySelector('.invalid-feedback');
        if (!mensajeError) {
            mensajeError = document.createElement('div');
            mensajeError.className = 'invalid-feedback';
            campo.parentNode.appendChild(mensajeError);
        }
        mensajeError.textContent = mensaje;
    }

    /**
     * Marca un campo como válido
     * @param {HTMLElement} campo - Campo a marcar
     */
    marcarCampoValido(campo) {
        campo.classList.add('is-valid');
        campo.classList.remove('is-invalid');

        const mensajeError = campo.parentNode.querySelector('.invalid-feedback');
        if (mensajeError) {
            mensajeError.remove();
        }
    }

    /**
     * Muestra un mensaje temporal (toast)
     * @param {string} mensaje - Mensaje a mostrar
     * @param {string} tipo - Tipo de mensaje (primary, secondary, accent, success, danger, warning, info)
     */
    mostrarMensaje(mensaje, tipo = 'info') {
        const tiposIconos = {
            'primary': 'fas fa-info-circle',
            'secondary': 'fas fa-info-circle',
            'accent': 'fas fa-star',
            'success': 'fas fa-check-circle',
            'danger': 'fas fa-exclamation-circle',
            'warning': 'fas fa-exclamation-triangle',
            'info': 'fas fa-info-circle'
        };

        // Crear contenedor de toasts si no existe
        let toastContainer = document.getElementById('toast-container');
        if (!toastContainer) {
            toastContainer = document.createElement('div');
            toastContainer.id = 'toast-container';
            toastContainer.className = 'position-fixed top-0 end-0 p-3';
            toastContainer.style.zIndex = '9999';
            document.body.appendChild(toastContainer);
        }

        const toast = document.createElement('div');
        toast.className = `toast align-items-center text-white bg-${tipo} border-0`;
        toast.setAttribute('role', 'alert');
        toast.setAttribute('aria-live', 'assertive');
        toast.setAttribute('aria-atomic', 'true');

        toast.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">
                    <i class="${tiposIconos[tipo]} me-2"></i>
                    ${mensaje}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Cerrar"></button>
            </div>
        `;

        toastContainer.appendChild(toast);
        const bsToast = new bootstrap.Toast(toast, {delay: 3000});
        bsToast.show();

        // Remover del DOM después de ocultar
        toast.addEventListener('hidden.bs.toast', () => {
            toast.remove();
        });
    }

    /**
     * Confirma una acción con el usuario
     * @param {string} mensaje - Mensaje a mostrar
     * @param {Function} callback - Función a ejecutar si confirma
     * @param {string} tipo - Tipo de confirmación (warning, danger, info)
     */
    confirmarAccion(mensaje, callback, tipo = 'warning') {
        const modal = this.crearModalConfirmacion(mensaje, tipo);
        document.body.appendChild(modal);

        const bsModal = new bootstrap.Modal(modal);
        bsModal.show();

        modal.querySelector('.btn-confirmar').addEventListener('click', () => {
            callback();
            bsModal.hide();
        });

        modal.addEventListener('hidden.bs.modal', () => {
            modal.remove();
        });
    }

    /**
     * Crea un modal de confirmación
     * @param {string} mensaje - Mensaje a mostrar
     * @param {string} tipo - Tipo de confirmación
     * @returns {HTMLElement} - Modal creado
     */
    crearModalConfirmacion(mensaje, tipo = 'warning') {
        const tiposConfig = {
            'warning': {
                color: 'warning',
                icon: 'fa-exclamation-triangle',
                title: 'Confirmar Acción'
            },
            'danger': {
                color: 'danger',
                icon: 'fa-exclamation-circle',
                title: 'Acción Peligrosa'
            },
            'info': {
                color: 'info',
                icon: 'fa-info-circle',
                title: 'Confirmación'
            }
        };

        const config = tiposConfig[tipo] || tiposConfig.warning;

        const modal = document.createElement('div');
        modal.className = 'modal fade';
        modal.tabIndex = -1;
        modal.innerHTML = `
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-${config.color} text-dark">
                        <h5 class="modal-title">
                            <i class="fas ${config.icon} me-2"></i>
                            ${config.title}
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                    </div>
                    <div class="modal-body">
                        <p>${mensaje}</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times me-2"></i>
                            Cancelar
                        </button>
                        <button type="button" class="btn btn-${config.color} btn-confirmar">
                            <i class="fas fa-check me-2"></i>
                            Confirmar
                        </button>
                    </div>
                </div>
            </div>
        `;

        return modal;
    }

    /**
     * Formatea un número como moneda
     * @param {number} numero - Número a formatear
     * @returns {string} - Número formateado
     */
    formatearMoneda(numero) {
        return new Intl.NumberFormat('es-CO', {
            style: 'currency',
            currency: 'COP',
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        }).format(numero);
    }

    /**
     * Formatea una fecha
     * @param {Date|string} fecha - Fecha a formatear
     * @returns {string} - Fecha formateada
     */
    formatearFecha(fecha) {
        const fechaObj = typeof fecha === 'string' ? new Date(fecha) : fecha;
        return fechaObj.toLocaleDateString('es-CO', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }

    /**
     * Obtiene el parámetro de URL
     * @param {string} nombre - Nombre del parámetro
     * @returns {string|null} - Valor del parámetro o null
     */
    obtenerParametroURL(nombre) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(nombre);
    }

    /**
     * Copia texto al portapapeles
     * @param {string} texto - Texto a copiar
     */
    copiarAlPortapapeles(texto) {
        navigator.clipboard.writeText(texto).then(() => {
            this.mostrarMensaje('Texto copiado al portapapeles', 'success');
        }).catch(() => {
            this.mostrarMensaje('Error al copiar el texto', 'danger');
        });
    }

    /**
     * Descarga un archivo
     * @param {string} contenido - Contenido del archivo
     * @param {string} nombreArchivo - Nombre del archivo
     * @param {string} tipoMime - Tipo MIME del archivo
     */
    descargarArchivo(contenido, nombreArchivo, tipoMime = 'text/plain') {
        const blob = new Blob([contenido], {type: tipoMime});
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = nombreArchivo;
        a.style.display = 'none';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
    }

    /**
     * Muestra un loading overlay
     */
    mostrarLoading() {
        let loading = document.getElementById('loading-overlay');
        if (!loading) {
            loading = document.createElement('div');
            loading.id = 'loading-overlay';
            loading.className = 'position-fixed top-0 start-0 w-100 h-100 bg-dark bg-opacity-75 d-flex justify-content-center align-items-center';
            loading.style.zIndex = '9999';
            loading.innerHTML = `
                <div class="text-center">
                    <div class="spinner-border text-accent mb-3" role="status" style="width: 3rem; height: 3rem;">
                        <span class="visually-hidden">Cargando...</span>
                    </div>
                    <div class="text-white">Cargando...</div>
                </div>
            `;
            document.body.appendChild(loading);
        }
        loading.style.display = 'flex';
    }

    /**
     * Oculta el loading overlay
     */
    ocultarLoading() {
        const loading = document.getElementById('loading-overlay');
        if (loading) {
            loading.style.display = 'none';
        }
    }

    /**
     * Realiza una petición AJAX
     * @param {string} url - URL de la petición
     * @param {Object} opciones - Opciones de la petición
     * @returns {Promise} - Promesa con la respuesta
     */
    async peticionAjax(url, opciones = {}) {
        this.mostrarLoading();

        try {
            const config = {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                ...opciones
            };

            const respuesta = await fetch(url, config);

            if (!respuesta.ok) {
                throw new Error(`HTTP error! status: ${respuesta.status}`);
            }

            return await respuesta.json();
        } catch (error) {
            console.error('Error en la petición:', error);
            this.mostrarMensaje('Error al procesar la solicitud', 'danger');
            throw error;
        } finally {
            this.ocultarLoading();
    }
    }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    window.SistemaCV = new SistemaCV();
});