/**
 * JavaScript principal para el Sistema CV
 * Funciones comunes y utilidades
 */

// Configuración global
document.addEventListener('DOMContentLoaded', function() {
    inicializarFuncionesComunes();
});

//Inicializa las funciones comunes al cargar la página

function inicializarFuncionesComunes() {
    // Auto-cerrar alertas después de 5 segundos
    autoCerrarAlertas();
    
    // Inicializar tooltips de Bootstrap
    inicializarTooltips();
    
    // Inicializar animaciones de entrada
    inicializarAnimacionesEntrada();
    
    // Configurar máscaras de entrada
    configurarMascarasEntrada();
    
    // Configurar validaciones de formularios
    configurarValidacionesFormularios();
}

/**
 * Auto-cierra las alertas después de 5 segundos
 */
function autoCerrarAlertas() {
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(function(alert, index) {
        setTimeout(function() {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000 + (index * 500)); // Stagger the closing
    });
}

//Inicializa los tooltips de Bootstrap

function inicializarTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

//Inicializa animaciones de entrada para elementos

function inicializarAnimacionesEntrada() {
    const elementosAnimados = document.querySelectorAll('.card, .table, .form-section');
    
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

//Configura máscaras de entrada para formularios

function configurarMascarasEntrada() {
    // Máscara para números de teléfono
    const telefonoInputs = document.querySelectorAll('input[type="tel"]');
    telefonoInputs.forEach(input => {
        input.addEventListener('input', function(e) {
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
        input.addEventListener('input', function(e) {
            // Limitar a números y letras
            e.target.value = e.target.value.replace(/[^a-zA-Z0-9]/g, '');
        });
    });
}

//Configura validaciones de formularios

function configurarValidacionesFormularios() {
    const forms = document.querySelectorAll('form');
    
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!validarFormulario(form)) {
                e.preventDefault();
                mostrarMensaje('Por favor complete todos los campos obligatorios', 'error');
            }
        });
    });
}

/**
 * Valida un formulario
 * @param {HTMLFormElement} formulario - Formulario a validar
 * @returns {boolean} - True si es válido
 */
function validarFormulario(formulario) {
    const camposRequeridos = formulario.querySelectorAll('[required]');
    let esValido = true;
    
    camposRequeridos.forEach(campo => {
        if (!campo.value.trim()) {
            marcarCampoInvalido(campo);
            esValido = false;
        } else {
            marcarCampoValido(campo);
        }
    });
    
    return esValido;
}

/**
 * Marca un campo como inválido
 * @param {HTMLElement} campo - Campo a marcar
 */
function marcarCampoInvalido(campo) {
    campo.classList.add('is-invalid');
    campo.classList.remove('is-valid');
    
    // Agregar mensaje de error si no existe
    if (!campo.parentNode.querySelector('.invalid-feedback')) {
        const mensajeError = document.createElement('div');
        mensajeError.className = 'invalid-feedback';
        mensajeError.textContent = 'Este campo es obligatorio';
        campo.parentNode.appendChild(mensajeError);
    }
}

/**
 * Marca un campo como válido
 * @param {HTMLElement} campo - Campo a marcar
 */
function marcarCampoValido(campo) {
    campo.classList.add('is-valid');
    campo.classList.remove('is-invalid');
    
    // Remover mensaje de error si existe
    const mensajeError = campo.parentNode.querySelector('.invalid-feedback');
    if (mensajeError) {
        mensajeError.remove();
    }
}

/**
 * Muestra un mensaje temporal
 * @param {string} mensaje - Mensaje a mostrar
 * @param {string} tipo - Tipo de mensaje (success, error, warning, info)
 */
function mostrarMensaje(mensaje, tipo = 'info') {
    const tiposClases = {
        'success': 'alert-success',
        'error': 'alert-danger',
        'warning': 'alert-warning',
        'info': 'alert-info'
    };
    
    const tiposIconos = {
        'success': 'fas fa-check-circle',
        'error': 'fas fa-exclamation-circle',
        'warning': 'fas fa-exclamation-triangle',
        'info': 'fas fa-info-circle'
    };
    
    const alerta = document.createElement('div');
    alerta.className = `alert ${tiposClases[tipo]} alert-dismissible fade show position-fixed`;
    alerta.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    alerta.innerHTML = `
        <i class="${tiposIconos[tipo]} me-2"></i>
        ${mensaje}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    document.body.appendChild(alerta);
    
    // Auto-cerrar después de 3 segundos
    setTimeout(() => {
        const bsAlert = new bootstrap.Alert(alerta);
        bsAlert.close();
    }, 3000);
}

/**
 * Confirma una acción con el usuario
 * @param {string} mensaje - Mensaje a mostrar
 * @param {Function} callback - Función a ejecutar si confirma
 */
function confirmarAccion(mensaje, callback) {
    const modal = crearModalConfirmacion(mensaje);
    document.body.appendChild(modal);
    
    const bsModal = new bootstrap.Modal(modal);
    bsModal.show();
    
    modal.querySelector('.btn-confirmar').addEventListener('click', function() {
        callback();
        bsModal.hide();
        modal.remove();
    });
    
    modal.addEventListener('hidden.bs.modal', function() {
        modal.remove();
    });
}

/**
 * Crea un modal de confirmación
 * @param {string} mensaje - Mensaje a mostrar
 * @returns {HTMLElement} - Modal creado
 */
function crearModalConfirmacion(mensaje) {
    const modal = document.createElement('div');
    modal.className = 'modal fade';
    modal.innerHTML = `
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-warning">
                    <h5 class="modal-title">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        Confirmar Acción
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p>${mensaje}</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times me-2"></i>
                        Cancelar
                    </button>
                    <button type="button" class="btn btn-primary btn-confirmar">
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
function formatearMoneda(numero) {
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
function formatearFecha(fecha) {
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
function obtenerParametroURL(nombre) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(nombre);
}

/**
 * Copia texto al portapapeles
 * @param {string} texto - Texto a copiar
 */
function copiarAlPortapapeles(texto) {
    navigator.clipboard.writeText(texto).then(function() {
        mostrarMensaje('Texto copiado al portapapeles', 'success');
    }).catch(function() {
        mostrarMensaje('Error al copiar el texto', 'error');
    });
}

/**
 * Descarga un archivo
 * @param {string} contenido - Contenido del archivo
 * @param {string} nombreArchivo - Nombre del archivo
 * @param {string} tipoMime - Tipo MIME del archivo
 */
function descargarArchivo(contenido, nombreArchivo, tipoMime = 'text/plain') {
    const blob = new Blob([contenido], { type: tipoMime });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = nombreArchivo;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
}

//Muestra un loading overlay

function mostrarLoading() {
    const loading = document.createElement('div');
    loading.id = 'loading-overlay';
    loading.className = 'position-fixed top-0 start-0 w-100 h-100 bg-dark bg-opacity-50 d-flex justify-content-center align-items-center';
    loading.style.zIndex = '9999';
    loading.innerHTML = `
        <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Cargando...</span>
        </div>
        <span class="ms-3 text-white">Cargando...</span>
    `;
    
    document.body.appendChild(loading);
}

//Oculta el loading overlay

function ocultarLoading() {
    const loading = document.getElementById('loading-overlay');
    if (loading) {
        loading.remove();
    }
}

/**
 * Realiza una petición AJAX
 * @param {string} url - URL de la petición
 * @param {Object} opciones - Opciones de la petición
 * @returns {Promise} - Promesa con la respuesta
 */
async function peticionAjax(url, opciones = {}) {
    mostrarLoading();
    
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
        mostrarMensaje('Error al procesar la solicitud', 'error');
        throw error;
    } finally {
        ocultarLoading();
    }
}

// Utilidades de fecha
const DateUtils = {
    /**
     * Obtiene la fecha actual en formato ISO
     * @returns {string} - Fecha actual
     */
    obtenerFechaActual: function() {
        return new Date().toISOString().split('T')[0];
    },
    
    /**
     * Suma días a una fecha
     * @param {Date} fecha - Fecha base
     * @param {number} dias - Días a sumar
     * @returns {Date} - Nueva fecha
     */
    sumarDias: function(fecha, dias) {
        const nuevaFecha = new Date(fecha);
        nuevaFecha.setDate(nuevaFecha.getDate() + dias);
        return nuevaFecha;
    },
    
    /**
     * Calcula la diferencia en días entre dos fechas
     * @param {Date} fecha1 - Primera fecha
     * @param {Date} fecha2 - Segunda fecha
     * @returns {number} - Diferencia en días
     */
    diferenciaDias: function(fecha1, fecha2) {
        const unDia = 24 * 60 * 60 * 1000;
        return Math.round(Math.abs((fecha1 - fecha2) / unDia));
    }
};

// Utilidades de número
const NumberUtils = {
    /**
     * Formatea un número con separadores de miles
     * @param {number} numero - Número a formatear
     * @returns {string} - Número formateado
     */
    formatearNumero: function(numero) {
        return new Intl.NumberFormat('es-CO').format(numero);
    },
    
    /**
     * Genera un número aleatorio entre min y max
     * @param {number} min - Mínimo
     * @param {number} max - Máximo
     * @returns {number} - Número aleatorio
     */
    aleatorio: function(min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }
};

// Utilidades de string
const StringUtils = {
    /**
     * Capitaliza la primera letra de cada palabra
     * @param {string} texto - Texto a capitalizar
     * @returns {string} - Texto capitalizado
     */
    capitalizar: function(texto) {
        return texto.replace(/\w\S*/g, function(txt) {
            return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
        });
    },
    
    /**
     * Genera un slug a partir de un texto
     * @param {string} texto - Texto a convertir
     * @returns {string} - Slug generado
     */
    generarSlug: function(texto) {
        return texto.toString().toLowerCase()
            .replace(/\s+/g, '-')           // Replace spaces with -
            .replace(/[^\w\-]+/g, '')       // Remove all non-word chars
            .replace(/\-\-+/g, '-')         // Replace multiple - with single -
            .replace(/^-+/, '')             // Trim - from start of text
            .replace(/-+$/, '');            // Trim - from end of text
    }
};

// Exportar funciones para uso global
window.SistemaCV = {
    mostrarMensaje,
    confirmarAccion,
    formatearMoneda,
    formatearFecha,
    copiarAlPortapapeles,
    descargarArchivo,
    peticionAjax,
    DateUtils,
    NumberUtils,
    StringUtils
};

