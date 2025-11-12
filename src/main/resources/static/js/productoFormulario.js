document.addEventListener('DOMContentLoaded', function () {
    inicializarFormularioProducto();
});

function inicializarFormularioProducto() {
    // Vista previa de imagen
    const imagenInput = document.getElementById('imagen');
    const imagenPreview = document.getElementById('imagenPreview');
    const imagenPreviewContainer = document.getElementById('imagenPreviewContainer');

    if (imagenInput && imagenPreview) {
        imagenInput.addEventListener('change', function (e) {
            const file = e.target.files[0];
            if (file) {
                // Validar tipo de archivo
                const tiposPermitidos = ['image/jpeg', 'image/png', 'image/gif'];
                if (!tiposPermitidos.includes(file.type)) {
                    mostrarError('Solo se permiten archivos de imagen (JPG, PNG, GIF)');
                    imagenInput.value = '';
                    return;
                }

                // Validar tamaño (5MB máximo)
                const tamanoMaximo = 5 * 1024 * 1024; // 5MB en bytes
                if (file.size > tamanoMaximo) {
                    mostrarError('La imagen no puede ser mayor a 5MB');
                    imagenInput.value = '';
                    return;
                }

                const reader = new FileReader();
                reader.onload = function (e) {
                    imagenPreview.src = e.target.result;
                    imagenPreviewContainer.style.display = 'block';
                };
                reader.readAsDataURL(file);
            } else {
                imagenPreviewContainer.style.display = 'none';
            }
        });
    }

    // Validación del formulario
    const formulario = document.getElementById('productoForm');
    if (formulario) {
        formulario.addEventListener('submit', function (e) {
            if (!validarFormulario()) {
                e.preventDefault();
            }
        });
    }

    // Generar código automático si está vacío
    const codigoInput = document.getElementById('codigo');
    const nombreInput = document.getElementById('nombre');

    if (codigoInput && nombreInput && !codigoInput.value) {
        nombreInput.addEventListener('blur', function () {
            if (!codigoInput.value && nombreInput.value) {
                generarCodigoAutomatico();
            }
        });
    }

    // Manejo del campo rectificado
    const rectificadoCheckbox = document.getElementById('rectificado');
    if (rectificadoCheckbox) {
        rectificadoCheckbox.addEventListener('change', function () {
            console.log('Rectificado:', this.checked);
        });
    }
}

function validarFormulario() {
    let esValido = true;
    const mensajesError = [];

    // Validar código
    const codigo = document.getElementById('codigo').value.trim();
    if (!codigo) {
        mensajesError.push('El código del producto es obligatorio');
        esValido = false;
    }

    // Validar nombre
    const nombre = document.getElementById('nombre').value.trim();
    if (!nombre) {
        mensajesError.push('El nombre del producto es obligatorio');
        esValido = false;
    }

    // Validar categoría
    const categoria = document.getElementById('categoria.idCategoria').value;
    if (!categoria) {
        mensajesError.push('Debe seleccionar una categoría');
        esValido = false;
    }

    // Validar precio
    const precio = document.getElementById('precio').value;
    if (!precio || parseFloat(precio) <= 0) {
        mensajesError.push('El precio debe ser mayor a cero');
        esValido = false;
    }

    // Validar stock
    const stock = document.getElementById('stock').value;
    if (stock === '' || parseInt(stock) < 0) {
        mensajesError.push('El stock no puede ser negativo');
        esValido = false;
    }

    // Mostrar errores si los hay
    if (mensajesError.length > 0) {
        mostrarErrores(mensajesError);
    }

    return esValido;
}

function generarCodigoAutomatico() {
    const nombre = document.getElementById('nombre').value.trim();
    const categoriaSelect = document.getElementById('categoria.idCategoria');
    const categoriaTexto = categoriaSelect.options[categoriaSelect.selectedIndex].text;

    if (nombre && categoriaTexto) {
        // Generar código basado en las primeras letras del nombre y categoría
        const codigo = generarCodigoUnico(nombre, categoriaTexto);
        document.getElementById('codigo').value = codigo;
    }
}

function generarCodigoUnico(nombre, categoria) {
    // Tomar primeras 3 letras del nombre (en mayúsculas)
    const inicialesNombre = nombre.substring(0, 3).toUpperCase();

    // Tomar primeras 3 letras de la categoría (en mayúsculas)
    const inicialesCategoria = categoria.substring(0, 3).toUpperCase();

    // Añadir timestamp para hacerlo único
    const timestamp = Date.now().toString().substring(8);

    return `${inicialesNombre}-${inicialesCategoria}-${timestamp}`;
}

function mostrarError(mensaje) {
    // Puedes implementar un sistema de notificaciones más sofisticado
    alert('Error: ' + mensaje);
}

function mostrarErrores(errores) {
    const mensaje = errores.join('\n');
    alert('Por favor corrija los siguientes errores:\n\n' + mensaje);
}

// Función para limpiar el formulario
function limpiarFormulario() {
    const formulario = document.getElementById('productoForm');
    if (formulario) {
        formulario.reset();
        const imagenPreviewContainer = document.getElementById('imagenPreviewContainer');
        if (imagenPreviewContainer) {
            imagenPreviewContainer.style.display = 'none';
        }
    }
}

// Función para cargar datos de producto (útil para modo edición)
function cargarDatosProducto(datos) {
    console.log('Cargando datos del producto:', datos);
}

// Exportar funciones para uso global
window.productoFormulario = {
    inicializar: inicializarFormularioProducto,
    validar: validarFormulario,
    limpiar: limpiarFormulario,
    cargarDatos: cargarDatosProducto
};