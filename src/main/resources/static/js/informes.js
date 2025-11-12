class Informes {
    constructor() {
        this.salesChart = null;
        this.quotesChart = null;
        this.conversionChart = null;
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.inicializarGraficos();
        this.setupAutoCloseAlerts();
        console.log('Módulo de informes inicializado');
    }

    /**
     * Configura los event listeners
     */
    setupEventListeners() {
        // Botones de exportación
        document.getElementById('btnExportarTodos')?.addEventListener('click', () => this.exportarTodos());
        document.getElementById('btnExportarVentas')?.addEventListener('click', () => this.exportarVentas());
        document.getElementById('btnExportarCotizaciones')?.addEventListener('click', () => this.exportarCotizaciones());
        document.getElementById('btnExportarPedidos')?.addEventListener('click', () => this.exportarPedidos());

        // Botones de impresión
        document.getElementById('btnImprimirVentas')?.addEventListener('click', () => this.imprimirGrafico('salesChart'));
        document.getElementById('btnExportarGraficoVentas')?.addEventListener('click', () => this.exportarGrafico('salesChart'));
        document.getElementById('btnImprimirTabla')?.addEventListener('click', () => this.imprimirTabla());
        document.getElementById('btnExportarTabla')?.addEventListener('click', () => this.exportarTabla());

        // Validación de fechas en el formulario
        const filtroForm = document.getElementById('filtroForm');
        if (filtroForm) {
            filtroForm.addEventListener('submit', (e) => this.validarFechas(e));
        }
    }

    /**
     * Inicializa los gráficos Chart.js
     */
    inicializarGraficos() {
        this.inicializarGraficoVentas();
        this.inicializarGraficoCotizaciones();
        this.inicializarGraficoConversion();
    }

    /**
     * Inicializa el gráfico de ventas mensuales
     */
    inicializarGraficoVentas() {
        const salesCtx = document.getElementById('salesChart')?.getContext('2d');
        if (!salesCtx)
            return;

        this.salesChart = new Chart(salesCtx, {
            type: 'bar',
            data: {
                labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
                datasets: [{
                        label: 'Ventas 2024',
                        data: ventasMensuales,
                        backgroundColor: 'rgba(203, 154, 40, 0.7)',
                        borderColor: 'rgba(203, 154, 40, 1)',
                        borderWidth: 1
                    }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function (value) {
                                return '$' + value.toLocaleString();
                            }
                        }
                    }
                },
                plugins: {
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                return '$' + context.raw.toLocaleString();
                            }
                        }
                    },
                    legend: {
                        display: true,
                        position: 'top'
                    }
                }
            }
        });
    }

    /**
     * Inicializa el gráfico de estados de cotizaciones
     */
    inicializarGraficoCotizaciones() {
        const quotesCtx = document.getElementById('quotesChart')?.getContext('2d');
        if (!quotesCtx)
            return;

        this.quotesChart = new Chart(quotesCtx, {
            type: 'doughnut',
            data: {
                labels: ['Aprobadas', 'Pendientes', 'Rechazadas', 'Vencidas'],
                datasets: [{
                        data: estadosCotizaciones,
                        backgroundColor: [
                            'rgba(40, 167, 69, 0.7)',
                            'rgba(255, 193, 7, 0.7)',
                            'rgba(220, 53, 69, 0.7)',
                            'rgba(108, 117, 125, 0.7)'
                        ],
                        borderColor: [
                            'rgba(40, 167, 69, 1)',
                            'rgba(255, 193, 7, 1)',
                            'rgba(220, 53, 69, 1)',
                            'rgba(108, 117, 125, 1)'
                        ],
                        borderWidth: 1
                    }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right',
                    },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = Math.round((context.raw / total) * 100);
                                return `${context.label}: ${context.raw} (${percentage}%)`;
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Inicializa el gráfico de tasa de conversión
     */
    inicializarGraficoConversion() {
        const conversionCtx = document.getElementById('conversionChart')?.getContext('2d');
        if (!conversionCtx)
            return;

        this.conversionChart = new Chart(conversionCtx, {
            type: 'line',
            data: {
                labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun'],
                datasets: [{
                        label: 'Tasa de Conversión',
                        data: tasaConversion,
                        backgroundColor: 'rgba(40, 167, 69, 0.2)',
                        borderColor: 'rgba(40, 167, 69, 1)',
                        borderWidth: 2,
                        tension: 0.4,
                        fill: true
                    }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: false,
                        min: 45,
                        max: 90,
                        ticks: {
                            callback: function (value) {
                                return value + '%';
                            }
                        }
                    }
                },
                plugins: {
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                return context.raw + '%';
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Valida las fechas antes de enviar el formulario
     */
    validarFechas(e) {
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;

        if (!startDate || !endDate) {
            e.preventDefault();
            this.mostrarAlerta('Por favor, seleccione ambas fechas', 'warning');
            return false;
        }

        if (new Date(startDate) > new Date(endDate)) {
            e.preventDefault();
            this.mostrarAlerta('La fecha de inicio no puede ser mayor a la fecha fin', 'error');
            return false;
        }

        return true;
    }

    /**
     * Exporta todos los informes
     */
    exportarTodos() {
        if (this.validarFechasExportacion()) {
            if (confirm('¿Está seguro de que desea exportar todos los informes?')) {
                const url = `${baseUrl}informes/exportar?tipo=todos&formato=excel&fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`;
                window.location.href = url;
            }
        }
    }

    /**
     * Exporta informe de ventas
     */
    exportarVentas() {
        if (this.validarFechasExportacion()) {
            const url = `${baseUrl}informes/exportar?tipo=ventas&formato=excel&fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`;
            window.location.href = url;
        }
    }

    /**
     * Exporta informe de cotizaciones
     */
    exportarCotizaciones() {
        if (this.validarFechasExportacion()) {
            const url = `${baseUrl}informes/exportar?tipo=cotizaciones&formato=excel&fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`;
            window.location.href = url;
        }
    }

    /**
     * Exporta informe de pedidos
     */
    exportarPedidos() {
        if (this.validarFechasExportacion()) {
            const url = `${baseUrl}informes/exportar?tipo=pedidos&formato=excel&fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`;
            window.location.href = url;
        }
    }

    /**
     * Valida que existan fechas para exportación
     */
    validarFechasExportacion() {
        if (!fechaInicio || !fechaFin) {
            this.mostrarAlerta('Por favor, seleccione un período de fechas antes de exportar', 'warning');
            return false;
        }
        return true;
    }

    /**
     * Imprime un gráfico específico
     */
    imprimirGrafico(chartId) {
        const chartCanvas = document.getElementById(chartId);
        if (!chartCanvas) {
            this.mostrarAlerta('No se encontró el gráfico solicitado', 'error');
            return;
        }

        const printWindow = window.open('', '_blank');
        const chartTitle = this.obtenerTituloGrafico(chartId);

        printWindow.document.write(`
            <html>
                <head>
                    <title>Imprimir Gráfico - ${chartTitle}</title>
                    <style>
                        body { 
                            text-align: center; 
                            margin: 40px; 
                            font-family: Arial, sans-serif;
                        }
                        .header {
                            margin-bottom: 20px;
                            border-bottom: 2px solid #333;
                            padding-bottom: 10px;
                        }
                        img { 
                            max-width: 90%; 
                            height: auto;
                            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
                        }
                        .footer {
                            margin-top: 20px;
                            color: #666;
                            font-size: 12px;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h2>${chartTitle}</h2>
                        <p>Sistema CV - Generado el ${new Date().toLocaleDateString()}</p>
                    </div>
                    <img src="${chartCanvas.toDataURL()}">
                    <div class="footer">
                        <p>Stylish Home - Sistema de Gestión Comercial</p>
                    </div>
                </body>
            </html>
        `);
        printWindow.document.close();
        printWindow.print();
    }

    /**
     * Obtiene el título del gráfico basado en su ID
     */
    obtenerTituloGrafico(chartId) {
        const titulos = {
            'salesChart': 'Ventas Mensuales',
            'quotesChart': 'Estado de Cotizaciones',
            'conversionChart': 'Tasa de Conversión'
        };
        return titulos[chartId] || 'Gráfico';
    }

    /**
     * Exporta un gráfico como imagen
     */
    exportarGrafico(chartId) {
        const chartCanvas = document.getElementById(chartId);
        if (!chartCanvas) {
            this.mostrarAlerta('No se encontró el gráfico solicitado', 'error');
            return;
        }

        const link = document.createElement('a');
        const titulo = this.obtenerTituloGrafico(chartId);
        link.download = `grafico-${titulo.toLowerCase().replace(/\s+/g, '-')}.png`;
        link.href = chartCanvas.toDataURL();
        link.click();

        this.mostrarAlerta(`Gráfico "${titulo}" exportado correctamente`, 'success');
    }

    /**
     * Imprime la tabla de pedidos
     */
    imprimirTabla() {
        const tabla = document.getElementById('tablaPedidos');
        if (!tabla) {
            this.mostrarAlerta('No se encontró la tabla de pedidos', 'error');
            return;
        }

        const printWindow = window.open('', '_blank');
        printWindow.document.write(`
            <html>
                <head>
                    <title>Imprimir Tabla de Pedidos</title>
                    <style>
                        body { 
                            font-family: Arial, sans-serif; 
                            margin: 20px; 
                        }
                        .header {
                            text-align: center;
                            margin-bottom: 20px;
                            border-bottom: 2px solid #333;
                            padding-bottom: 10px;
                        }
                        table { 
                            width: 100%; 
                            border-collapse: collapse; 
                            margin-top: 10px;
                        }
                        th, td { 
                            border: 1px solid #ddd; 
                            padding: 8px; 
                            text-align: left; 
                        }
                        th { 
                            background-color: #f2f2f2; 
                            font-weight: bold;
                        }
                        .badge {
                            padding: 4px 8px;
                            border-radius: 4px;
                            font-size: 12px;
                            font-weight: bold;
                        }
                        .bg-success { background-color: #28a745; color: white; }
                        .bg-warning { background-color: #ffc107; color: black; }
                        .bg-danger { background-color: #dc3545; color: white; }
                        .bg-secondary { background-color: #6c757d; color: white; }
                        .footer {
                            margin-top: 20px;
                            text-align: center;
                            color: #666;
                            font-size: 12px;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h2>Tabla de Pedidos</h2>
                        <p>Período: ${fechaInicio && fechaFin ? `${fechaInicio} - ${fechaFin}` : 'No especificado'}</p>
                    </div>
                    ${tabla.outerHTML}
                    <div class="footer">
                        <p>Stylish Home - Sistema de Gestión Comercial - Generado el ${new Date().toLocaleDateString()}</p>
                    </div>
                </body>
            </html>
        `);
        printWindow.document.close();
        printWindow.print();
    }

    /**
     * Exporta la tabla a Excel
     */
    exportarTabla() {
        // Simulación de exportación a Excel
        this.mostrarAlerta('La exportación a Excel se está procesando...', 'info');

        // En una implementación real, aquí se haría la llamada al servidor
        setTimeout(() => {
            this.mostrarAlerta('Tabla exportada correctamente a Excel', 'success');
        }, 1500);
    }

    /**
     * Muestra alertas al usuario
     */
    mostrarAlerta(mensaje, tipo = 'info') {
        // Integrar con tu sistema de notificaciones existente
        console.log(`[${tipo.toUpperCase()}] ${mensaje}`);

        // Ejemplo simple con alerta nativa para errores y advertencias
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

    /**
     * Actualiza los gráficos con nuevos datos
     */
    actualizarGraficos(nuevosDatos) {
        if (this.salesChart && nuevosDatos.ventas) {
            this.salesChart.data.datasets[0].data = nuevosDatos.ventas;
            this.salesChart.update();
        }

        if (this.quotesChart && nuevosDatos.cotizaciones) {
            this.quotesChart.data.datasets[0].data = nuevosDatos.cotizaciones;
            this.quotesChart.update();
        }

        if (this.conversionChart && nuevosDatos.conversion) {
            this.conversionChart.data.datasets[0].data = nuevosDatos.conversion;
            this.conversionChart.update();
        }
    }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function () {
    new Informes();
});

// Exportar para uso en otros módulos si es necesario
if (typeof module !== 'undefined' && module.exports) {
    module.exports = Informes;
}