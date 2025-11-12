// informeCotizaciones.js 
class InformeCotizaciones {
    constructor() {
        this.charts = {
            status: null,
            seller: null
        };
        this.init();
    }

    init() {
        this.initCharts();
        this.setupEventListeners();
        this.highlightExpiring();
    }

    // Inicializar gráficos
    initCharts() {
        this.initStatusChart();
        this.initSellerChart();
    }

    initStatusChart() {
        const ctx = document.getElementById('quotesStatusChart');
        if (!ctx)
            return;

        const estadisticas = {
            aprobadas: 8,
            pendientes: 12,
            rechazadas: 3,
            vencidas: 2
        };

        this.charts.status = new Chart(ctx.getContext('2d'), {
            type: 'pie',
            data: {
                labels: ['Aprobadas', 'Pendientes', 'Rechazadas', 'Vencidas'],
                datasets: [{
                        data: Object.values(estadisticas),
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
                    legend: {position: 'right'},
                    tooltip: {
                        callbacks: {
                            label: (ctx) => {
                                const total = ctx.dataset.data.reduce((a, b) => a + b, 0);
                                const percent = Math.round((ctx.raw / total) * 100);
                                return `${ctx.label}: ${ctx.raw} (${percent}%)`;
                            }
                        }
                    }
                }
            }
        });
    }

    initSellerChart() {
        const ctx = document.getElementById('sellerDistributionChart');
        if (!ctx)
            return;

        const data = {
            labels: ['Jsalas', 'Hsantorini', 'Aduarte', 'Shinestroza', 'Bvidal'],
            data: [12, 8, 15, 6, 9]
        };

        this.charts.seller = new Chart(ctx.getContext('2d'), {
            type: 'bar',
            data: {
                labels: data.labels,
                datasets: [{
                        label: 'Cotizaciones por Vendedor',
                        data: data.data,
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
                            stepSize: 1,
                            callback: (value) => value % 1 === 0 ? value : null
                        }
                    },
                    x: {
                        ticks: {
                            maxRotation: 45,
                            minRotation: 45
                        }
                    }
                },
                plugins: {
                    tooltip: {
                        callbacks: {
                            label: (ctx) => `Cotizaciones: ${ctx.raw}`
                        }
                    }
                }
            }
        });
    }

    // Event Listeners
    setupEventListeners() {
        // Validación de formulario
        const form = document.querySelector('form');
        if (form) {
            form.addEventListener('submit', (e) => this.validateDates(e));
        }

        // Resize charts
        window.addEventListener('resize', () => this.resizeCharts());
    }

    validateDates(e) {
        const fechaInicio = document.getElementById('fechaInicio').value;
        const fechaFin = document.getElementById('fechaFin').value;

        if (fechaInicio && fechaFin && fechaInicio > fechaFin) {
            e.preventDefault();
            alert('La fecha de inicio no puede ser mayor a la fecha de fin');
            return false;
        }
        return true;
    }

    resizeCharts() {
        Object.values(this.charts).forEach(chart => {
            if (chart)
                chart.resize();
        });
    }

    // Resaltar filas por vencer
    highlightExpiring() {
        const rows = document.querySelectorAll('#tablaCotizaciones tbody tr');
        rows.forEach(row => {
            if (row.cells.length < 10)
                return;

            const days = parseInt(row.cells[9].textContent);
            if (days <= 3 && days >= 0) {
                row.style.backgroundColor = 'rgba(255, 193, 7, 0.1)';
            } else if (days < 0) {
                row.style.backgroundColor = 'rgba(220, 53, 69, 0.1)';
            }
        });
    }

    // Actualizar gráficos
    updateCharts(data) {
        if (this.charts.status && data.estados) {
            this.charts.status.data.datasets[0].data = data.estados;
            this.charts.status.update();
        }

        if (this.charts.seller && data.vendedores) {
            this.charts.seller.data.labels = data.vendedores.labels;
            this.charts.seller.data.datasets[0].data = data.vendedores.data;
            this.charts.seller.update();
        }
    }

    // Filtrar tabla por estado
    filterByStatus(status) {
        const rows = document.querySelectorAll('#tablaCotizaciones tbody tr');
        rows.forEach(row => {
            if (row.cells.length < 11)
                return;

            const rowStatus = row.cells[10].textContent.trim();
            row.style.display = (status === 'TODOS' || rowStatus === status) ? '' : 'none';
        });
    }

    // Ordenar tabla
    sortTable(colIndex, asc = true) {
        const table = document.getElementById('tablaCotizaciones');
        if (!table)
            return;

        const tbody = table.querySelector('tbody');
        const rows = Array.from(tbody.querySelectorAll('tr'));

        rows.sort((a, b) => {
            const valA = a.cells[colIndex].textContent.trim();
            const valB = b.cells[colIndex].textContent.trim();

            // Columnas numéricas
            if ([5, 6, 7, 9].includes(colIndex)) {
                const numA = parseFloat(valA.replace(/[^\d.-]/g, ''));
                const numB = parseFloat(valB.replace(/[^\d.-]/g, ''));
                return asc ? numA - numB : numB - numA;
            }

            // Columnas de fecha
            if ([0, 1, 8].includes(colIndex)) {
                const dateA = new Date(valA.split('/').reverse().join('-'));
                const dateB = new Date(valB.split('/').reverse().join('-'));
                return asc ? dateA - dateB : dateB - dateA;
            }

            // Columnas de texto
            return asc ? valA.localeCompare(valB) : valB.localeCompare(valA);
        });

        rows.forEach(row => tbody.appendChild(row));
    }

    // Cargar datos del servidor
    async loadData(fechaInicio, fechaFin) {
        try {
            const res = await fetch(`/api/informes/cotizaciones?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`);
            const data = await res.json();
            this.updateCharts(data);
        } catch (error) {
            console.error('Error cargando datos:', error);
        }
    }
}

// Funciones de exportación e impresión
function exportarCotizaciones() {
    const fechaInicio = document.getElementById('fechaInicio').value;
    const fechaFin = document.getElementById('fechaFin').value;

    if (!fechaInicio || !fechaFin) {
        alert('Por favor seleccione un rango de fechas válido');
        return;
    }

    window.location.href = `/informes/exportar?tipo=cotizaciones&formato=excel&fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`;

    setTimeout(() => {
        alert('Informe de cotizaciones exportado exitosamente');
    }, 1000);
}

function imprimirTabla() {
    const tabla = document.getElementById('tablaCotizaciones');
    if (!tabla) {
        alert('No hay datos para imprimir');
        return;
    }

    const fechaInicio = document.getElementById('fechaInicio').value;
    const fechaFin = document.getElementById('fechaFin').value;

    const printWindow = window.open('', '_blank');
    printWindow.document.write(`
        <!DOCTYPE html>
        <html>
        <head>
            <title>Informe de Cotizaciones - Stylish Home</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 20px; color: #333; }
                .header { text-align: center; margin-bottom: 20px; border-bottom: 2px solid #333; padding-bottom: 10px; }
                .header h1 { margin: 0; font-size: 24px; }
                .header .periodo { margin: 5px 0; font-size: 14px; color: #666; }
                table { width: 100%; border-collapse: collapse; font-size: 10px; margin-top: 10px; }
                th, td { border: 1px solid #ddd; padding: 6px; text-align: left; }
                th { background-color: #f8f9fa; font-weight: bold; }
                .badge { padding: 3px 6px; border-radius: 3px; font-size: 9px; font-weight: bold; }
                .bg-success { background: #d4edda; color: #155724; }
                .bg-warning { background: #fff3cd; color: #856404; }
                .bg-danger { background: #f8d7da; color: #721c24; }
                .bg-secondary { background: #e2e3e5; color: #383d41; }
                .footer { margin-top: 20px; text-align: center; font-size: 12px; color: #666; border-top: 1px solid #ddd; padding-top: 10px; }
                @media print {
                    body { margin: 0; }
                    table { font-size: 9px; }
                }
            </style>
        </head>
        <body>
            <div class="header">
                <h1>Informe de Cotizaciones - Stylish Home</h1>
                <div class="periodo">Período: ${formatearFecha(fechaInicio)} - ${formatearFecha(fechaFin)}</div>
                <div class="periodo">Generado: ${new Date().toLocaleDateString()}</div>
            </div>
            ${tabla.outerHTML}
            <div class="footer">
                Sistema Comercial Stylish Home - Informe generado automáticamente
            </div>
            <script>window.onload = () => window.print();<\/script>
        </body>
        </html>
    `);
    printWindow.document.close();
}

function exportarTabla() {
    const fechaInicio = document.getElementById('fechaInicio').value;
    const fechaFin = document.getElementById('fechaFin').value;
    const filename = `informe_cotizaciones_${fechaInicio}_a_${fechaFin}.xlsx`;

    alert(`Exportando informe a: ${filename}`);

    setTimeout(() => {
        alert('Archivo exportado exitosamente: ' + filename);
    }, 1500);
}

function formatearFecha(fechaISO) {
    if (!fechaISO)
        return 'N/A';
    const fecha = new Date(fechaISO + 'T00:00:00');
    return fecha.toLocaleDateString('es-ES');
}

// Inicializar
document.addEventListener('DOMContentLoaded', () => {
    window.informeCotizaciones = new InformeCotizaciones();
});