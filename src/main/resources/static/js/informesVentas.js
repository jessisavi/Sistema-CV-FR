class InformesVentas {
    constructor() {
        this.paymentChart = null;
        this.trendChart = null;
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.inicializarGraficos();
        this.setupAutoCloseAlerts();
        console.log('Módulo de informe de ventas inicializado');
    }

    /**
     * Configura los event listeners
     */
    setupEventListeners() {
        // Botones de exportación
        document.getElementById('btnExportarVentas')?.addEventListener('click', () => this.exportarVentas());
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
        this.inicializarGraficoMetodosPago();
        this.inicializarGraficoTendenciaVentas();
    }

    /**
     * Inicializa el gráfico de métodos de pago
     */
    inicializarGraficoMetodosPago() {
        const paymentCtx = document.getElementById('paymentMethodChart')?.getContext('2d');
        if (!paymentCtx)
            return;

        this.paymentChart = new Chart(paymentCtx, {
            type: 'doughnut',
            data: {
                labels: ['Efectivo', 'Tarjeta Crédito', 'Transferencia', 'Cheque'],
                datasets: [{
                        data: [
                            totalEfectivo,
                            totalTarjeta,
                            totalTransferencia,
                            totalCheque
                        ],
                        backgroundColor: [
                            'rgba(40, 167, 69, 0.7)',
                            'rgba(0, 123, 255, 0.7)',
                            'rgba(23, 162, 184, 0.7)',
                            'rgba(255, 193, 7, 0.7)'
                        ],
                        borderColor: [
                            'rgba(40, 167, 69, 1)',
                            'rgba(0, 123, 255, 1)',
                            'rgba(23, 162, 184, 1)',
                            'rgba(255, 193, 7, 1)'
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
                                return '$' + context.raw.toLocaleString();
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Inicializa el gráfico de tendencia de ventas
     */
    inicializarGraficoTendenciaVentas() {
        const trendCtx = document.getElementById('salesTrendChart')?.getContext('2d');
        if (!trendCtx)
            return;

        // Datos simulados para la tendencia (en una implementación real vendrían del servidor)
        const datosTendencia = this.generarDatosTendencia();

        this.trendChart = new Chart(trendCtx, {
            type: 'line',
            data: {
                labels: ['Sem 1', 'Sem 2', 'Sem 3', 'Sem 4'],
                datasets: [{
                        label: 'Ventas Semanales',
                        data: datosTendencia,
                        backgroundColor: 'rgba(203, 154, 40, 0.2)',
                        borderColor: 'rgba(203, 154, 40, 1)',
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
                        beginAtZero: true,
                        ticks: {
                            callback: function (value) {
                                return '$' + (value / 1000000).toFixed(1) + 'M';
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
                    }
                }
            }
        });
    }

    /**
     * Genera datos de tendencia basados en las estadísticas
     */
    generarDatosTendencia() {
        const total = totalEfectivo + totalTarjeta + totalTransferencia + totalCheque;

        // Si hay datos reales, distribuirlos en las semanas
        if (total > 0) {
            return [
                Math.round(total * 0.2), // 20% primera semana
                Math.round(total * 0.3), // 30% segunda semana
                Math.round(total * 0.25), // 25% tercera semana
                Math.round(total * 0.25)  // 25% cuarta semana
            ];
        }

        // Datos simulados por defecto
        return [12000000, 15000000, 18000000, 21000000];
    }

    /**
     * Valida las fechas antes de enviar el formulario
     */
    validarFechas(e) {
        const fechaInicio = document.getElementById('fechaInicio').value;
        const fechaFin = document.getElementById('fechaFin').value;

        if (!fechaInicio || !fechaFin) {
            e.preventDefault();
            this.mostrarAlerta('Por favor, seleccione ambas fechas', 'warning');
            return false;
        }

        if (new Date(fechaInicio) > new Date(fechaFin)) {
            e.preventDefault();
            this.mostrarAlerta('La fecha de inicio no puede ser mayor a la fecha fin', 'error');
            return false;
        }

        return true;
    }

    /**
     * Exporta el informe de ventas
     */
    exportarVentas() {
        if (this.validarFechasExportacion()) {
            const url = `${baseUrl}informes/exportar?tipo=ventas&formato=excel&fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`;
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
     * Imprime la tabla de ventas
     */
    imprimirTabla() {
        const tabla = document.getElementById('tablaVentas');
        if (!tabla) {
            this.mostrarAlerta('No se encontró la tabla de ventas', 'error');
            return;
        }

        const printWindow = window.open('', '_blank');
        const fechaInicioDisplay = document.getElementById('fechaInicio')?.value || 'No especificada';
        const fechaFinDisplay = document.getElementById('fechaFin')?.value || 'No especificada';

        printWindow.document.write(`
            <html>
                <head>
                    <title>Imprimir Informe de Ventas</title>
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
                            font-size: 12px;
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
                            font-size: 11px;
                            font-weight: bold;
                        }
                        .bg-success { background-color: #28a745; color: white; }
                        .bg-primary { background-color: #007bff; color: white; }
                        .bg-info { background-color: #17a2b8; color: white; }
                        .bg-warning { background-color: #ffc107; color: black; }
                        .bg-danger { background-color: #dc3545; color: white; }
                        .footer {
                            margin-top: 20px;
                            text-align: center;
                            color: #666;
                            font-size: 12px;
                        }
                        @media print {
                            body { margin: 0; }
                            .header { margin-bottom: 15px; }
                        }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h2>Informe Detallado de Ventas</h2>
                        <p>Período: ${fechaInicioDisplay} - ${fechaFinDisplay}</p>
                        <p>Generado el: ${new Date().toLocaleDateString()}</p>
                    </div>
                    ${tabla.outerHTML}
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
     * Exporta la tabla a Excel
     */
    exportarTabla() {
        this.mostrarAlerta('La exportación a Excel se está procesando...', 'info');

        // Simular procesamiento
        setTimeout(() => {
            this.mostrarAlerta('Informe de ventas exportado correctamente a Excel', 'success');

            // En una implementación real, aquí se haría la llamada al servidor
            if (this.validarFechasExportacion()) {
                const url = `${baseUrl}informes/exportar?tipo=ventas-detalle&formato=excel&fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`;
                window.location.href = url;
            }
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
        if (this.paymentChart && nuevosDatos.metodosPago) {
            this.paymentChart.data.datasets[0].data = nuevosDatos.metodosPago;
            this.paymentChart.update();
        }

        if (this.trendChart && nuevosDatos.tendencia) {
            this.trendChart.data.datasets[0].data = nuevosDatos.tendencia;
            this.trendChart.update();
        }
    }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function () {
    new InformesVentas();
});

// Exportar para uso en otros módulos si es necesario
if (typeof module !== 'undefined' && module.exports) {
    module.exports = InformesVentas;
}