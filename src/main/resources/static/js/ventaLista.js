function imprimirFactura(numeroFactura) {
    const url = `/ventas/${numeroFactura}/imprimir`;
    window.open(url, '_blank');
}

// Función para verificar stock antes de agregar producto
function verificarStock(productoId, cantidad, callback) {
    $.ajax({
        url: '/ventas/api/verificar-stock',
        type: 'GET',
        data: {
            productoId: productoId,
            cantidad: cantidad
        },
        success: function (response) {
            callback(true, response);
        },
        error: function (xhr, status, error) {
            callback(false, 'Error al verificar stock: ' + error);
        }
    });
}

// Función para confirmar cambios de estado
function confirmarCambioEstado(accion, ventaId) {
    const mensajes = {
        'completar': '¿Está seguro de marcar esta venta como completada?',
        'cancelar': '¿Está seguro de cancelar esta venta?',
        'eliminar': '¿Está seguro de eliminar esta venta? Esta acción no se puede deshacer.'
    };

    return confirm(mensajes[accion] || '¿Está seguro de realizar esta acción?');
}

// Función para actualizar estadísticas en tiempo real
function actualizarEstadisticas() {
    $.ajax({
        url: '/ventas/api/estadisticas',
        type: 'GET',
        success: function (data) {
            // Actualizar los elementos del DOM con las nuevas estadísticas
            $('#ventasHoy').text('$' + data.ventasHoy.toLocaleString());
            $('#ventasMensuales').text('$' + data.ventasMensuales.toLocaleString());
            $('#ventasAnuales').text('$' + data.ventasAnuales.toLocaleString());
            $('#metaMensual').text('$' + data.metaMensual.toLocaleString());
            $('#porcentajeMeta').text(data.porcentajeMeta + '%');
            $('.progress-bar').css('width', data.porcentajeMeta + '%');
        },
        error: function (xhr, status, error) {
            console.error('Error al actualizar estadísticas:', error);
        }
    });
}

// Función para filtrar ventas por estado
function filtrarVentas(estado) {
    window.location.href = estado === 'Todos' ? '/ventas' : `/ventas/estado/${estado}`;
}

// Función para buscar ventas
function buscarVentas() {
    const criterio = $('#busquedaInput').val();
    if (criterio.trim() !== '') {
        window.location.href = `/ventas/buscar?numeroFactura=${encodeURIComponent(criterio)}`;
    }
}

// Inicialización cuando el documento está listo
$(document).ready(function () {
    $('#busquedaInput').on('keypress', function (e) {
        if (e.which === 13) {
            buscarVentas();
        }
    });

    // Actualizar estadísticas cada 5 minutos
    setInterval(actualizarEstadisticas, 300000);

    // Inicializar tooltips de Bootstrap
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Configurar eventos para los botones de acción
    $('.btn-completar').on('click', function (e) {
        const ventaId = $(this).data('venta-id');
        if (!confirmarCambioEstado('completar', ventaId)) {
            e.preventDefault();
        }
    });

    $('.btn-cancelar').on('click', function (e) {
        const ventaId = $(this).data('venta-id');
        if (!confirmarCambioEstado('cancelar', ventaId)) {
            e.preventDefault();
        }
    });

    $('.btn-eliminar').on('click', function (e) {
        const ventaId = $(this).data('venta-id');
        if (!confirmarCambioEstado('eliminar', ventaId)) {
            e.preventDefault();
        }
    });
});

// Función para exportar datos de ventas
function exportarVentas(formato) {
    const fechaInicio = $('#fechaInicio').val();
    const fechaFin = $('#fechaFin').val();
    const estado = $('#estadoFiltro').val();

    let url = `/ventas/exportar?formato=${formato}`;

    if (fechaInicio)
        url += `&fechaInicio=${fechaInicio}`;
    if (fechaFin)
        url += `&fechaFin=${fechaFin}`;
    if (estado && estado !== 'Todos')
        url += `&estado=${estado}`;

    window.location.href = url;
}

// Función para mostrar detalles rápidos de venta
function mostrarDetallesRapidos(ventaId) {
    $.ajax({
        url: `/ventas/${ventaId}/detalles-rapidos`,
        type: 'GET',
        success: function (data) {
            // Mostrar modal con detalles rápidos
            $('#modalDetallesVenta .modal-body').html(data);
            $('#modalDetallesVenta').modal('show');
        },
        error: function (xhr, status, error) {
            alert('Error al cargar detalles de la venta: ' + error);
        }
    });
}


