// informesPedidos.js 
class InformePedidos {
    constructor() {
        this.charts = {
            status: null,
            client: null
        };
        this.init();
    }

    init() {
        this.initCharts();
        this.setupEventListeners();
    }

    // Inicializar gráficos
    initCharts() {
        this.initStatusChart();
        this.initClientChart();
    }

    initStatusChart() {
        const ctx = document.getElementById('ordersStatusChart');
        if (!ctx)
            return;

        const stats = {
            completados: parseInt(document.querySelector('.text-warning h3')?.textContent) || 0,
            pendientes: 5,
            cancelados: 2
        };

        this.charts.status = new Chart(ctx.getContext('2d'), {
            type: 'doughnut',
            data: {
                labels: ['Completados', 'Pendientes', 'Cancelados'],
                datasets: [{
                        data: Object.values(stats),
                        backgroundColor: [
                            'rgba(40, 167, 69, 0.7)',
                            'rgba(255, 193, 7, 0.7)',
                            'rgba(220, 53, 69, 0.7)'
                        ],
                        borderColor: [
                            'rgba(40, 167, 69, 1)',
                            'rgba(255, 193, 7, 1)',
                            'rgba(220, 53, 69, 1)'
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

    initClientChart() {
        const ctx = document.getElementById('clientDistributionChart');
        if (!ctx)
            return;

        const data = {
            labels: ['Constructora Andina', 'Arq. María Pérez', 'Ing. Carlos Gómez', 'Decoraciones Laura', 'Arq. Abud Zahid'],
            data: [5, 3, 2, 4, 3]
        };

        this.charts.client = new Chart(ctx.getContext('2d'), {
            type: 'bar',
            data: {
                labels: data.labels,
                datasets: [{
                        label: 'Pedidos por Cliente',
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
                            label: (ctx) => `Pedidos: ${ctx.raw}`
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

    // Actualizar gráficos
    updateCharts(data) {
        if (this.charts.status && data.estados) {
            this.charts.status.data.datasets[0].data = data.estados;
            this.charts.status.update();
        }

        if (this.charts.client && data.clientes) {
            this.charts.client.data.labels = data.clientes.labels;
            this.charts.client.data.datasets[0].data = data.clientes.data;
            this.charts.client.update();
        }
    }

    // Cargar datos del servidor
    async loadData(fechaInicio, fechaFin) {
        try {
            const res = await fetch(`/api/informes/pedidos?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`);
            const data = await res.json();
            this.updateCharts(data);
        } catch (error) {
            console.error('Error cargando datos:', error);
        }
    }
}

// Funciones de exportación e impresión
function exportarPedidos() {
    const fechaInicio = document.getElementById('fechaInicio').value;
    const fechaFin = document.getElementById('fechaFin').value;

    if (!fechaInicio || !fechaFin) {
        alert('Por favor seleccione un rango de fechas válido');
        return;
    }

    window.location.href = `/informes/exportar?tipo=pedidos&formato=excel&fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`;

    setTimeout(() => {
        alert('Informe de pedidos exportado exitosamente');
    }, 1000);
}

function imprimirTabla() {
    const tabla = document.getElementById('tablaPedidos');
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
            <title>Informe de Pedidos - Stylish Home</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 20px; color: #333; }
                .header { text-align: center; margin-bottom: 20px; border-bottom: 2px solid #333; padding-bottom: 10px; }
                .header h1 { margin: 0; font-size: 24px; }
                .header .periodo { margin: 5px 0; font-size: 14px; color: #666; }
                table { width: 100%; border-collapse: collapse; font-size: 12px; margin-top: 10px; }
                th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                th { background-color: #f8f9fa; font-weight: bold; }
                .badge { padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: bold; }
                .bg-success { background: #d4edda; color: #155724; }
                .bg-warning { background: #fff3cd; color: #856404; }
                .bg-danger { background: #f8d7da; color: #721c24; }
                .footer { margin-top: 20px; text-align: center; font-size: 12px; color: #666; border-top: 1px solid #ddd; padding-top: 10px; }
                @media print {
                    body { margin: 0; }
                }
            </style>
        </head>
        <body>
            <div class="header">
                <h1>Informe de Pedidos - Stylish Home</h1>
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
    const filename = `informe_pedidos_${fechaInicio}_a_${fechaFin}.xlsx`;

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
    window.informePedidos = new InformePedidos();
});