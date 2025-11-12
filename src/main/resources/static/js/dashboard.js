document.addEventListener('DOMContentLoaded', function () {
    console.log('Dashboard JS cargado');

    // Inicializar AOS
    if (typeof AOS !== 'undefined') {
        AOS.init({
            duration: 800,
            easing: 'ease-in-out',
            once: true
        });
    }

    // Inicializar gráfico de ventas
    inicializarGraficoVentas();

    // Configurar eventos del dropdown del gráfico
    configurarDropdownGrafico();
});

// Configuración del gráfico de ventas
function inicializarGraficoVentas() {
    const ctx = document.getElementById('salesChart');
    if (!ctx) {
        console.warn('Canvas salesChart no encontrado');
        return;
    }

    try {
        const salesCtx = ctx.getContext('2d');

        const months = ['May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic', 'Ene', 'Feb', 'Mar', 'Abr'];
        const currentYearSales = [45, 52, 60, 58, 65, 72, 80, 78, 85, 90, 95, 110];
        const targets = [50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105];

        function formatMillions(value) {
            return '$' + value.toFixed(1) + 'M';
        }

        const salesChart = new Chart(salesCtx, {
            type: 'bar',
            data: {
                labels: months,
                datasets: [
                    {
                        label: 'Ventas Totales',
                        data: currentYearSales,
                        backgroundColor: '#cb9a28',
                        borderColor: '#000000',
                        borderWidth: 1,
                        borderRadius: 4
                    },
                    {
                        label: 'Objetivo',
                        data: targets,
                        type: 'line',
                        borderColor: '#000000',
                        backgroundColor: 'rgba(0, 0, 0, 0.1)',
                        borderWidth: 2,
                        pointBackgroundColor: '#000000',
                        pointRadius: 4,
                        fill: true
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: formatMillions
                        },
                        grid: {
                            drawBorder: false
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                return context.dataset.label + ': ' + formatMillions(context.raw);
                            }
                        }
                    }
                }
            }
        });

        // Guardar referencia del gráfico para actualizaciones
        window.salesChart = salesChart;
        console.log('Gráfico inicializado correctamente');

    } catch (error) {
        console.error('Error al inicializar el gráfico:', error);
    }
}

// Configurar eventos del dropdown del gráfico
function configurarDropdownGrafico() {
    const dropdownItems = document.querySelectorAll('#chartDropdown + .dropdown-menu .dropdown-item');

    if (dropdownItems.length === 0) {
        console.warn('Dropdown items no encontrados');
        return;
    }

    dropdownItems.forEach(item => {
        item.addEventListener('click', function (e) {
            e.preventDefault();
            const period = this.getAttribute('data-period');
            actualizarPeriodoGrafico(period);

            // Actualizar texto del botón
            const dropdownButton = document.getElementById('chartDropdown');
            if (dropdownButton) {
                dropdownButton.textContent = this.textContent;
            }
        });
    });
}

// Función para actualizar el gráfico según el período seleccionado
function actualizarPeriodoGrafico(period) {
    console.log('Período seleccionado:', period);

    // Obtener nuevos datos según el período
    const nuevosDatos = obtenerDatosPorPeriodo(period);

    if (window.salesChart && nuevosDatos) {
        window.salesChart.data.labels = nuevosDatos.labels;
        window.salesChart.data.datasets[0].data = nuevosDatos.ventas;
        window.salesChart.data.datasets[1].data = nuevosDatos.objetivos;
        window.salesChart.update();
    }
}

// Función para obtener datos por período
function obtenerDatosPorPeriodo(period) {
    const datosEjemplo = {
        '6': {
            labels: ['Nov', 'Dic', 'Ene', 'Feb', 'Mar', 'Abr'],
            ventas: [80, 78, 85, 90, 95, 110],
            objetivos: [80, 85, 90, 95, 100, 105]
        },
        '12': {
            labels: ['May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic', 'Ene', 'Feb', 'Mar', 'Abr'],
            ventas: [45, 52, 60, 58, 65, 72, 80, 78, 85, 90, 95, 110],
            objetivos: [50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105]
        },
        'ytd': {
            labels: ['Ene', 'Feb', 'Mar', 'Abr'],
            ventas: [85, 90, 95, 110],
            objetivos: [90, 95, 100, 105]
        },
        'lastYear': {
            labels: ['May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic', 'Ene', 'Feb', 'Mar', 'Abr'],
            ventas: [40, 48, 55, 52, 58, 65, 72, 70, 75, 80, 85, 95],
            objetivos: [45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100]
        }
    };

    return datosEjemplo[period] || datosEjemplo['12'];
}

// Función para cargar datos reales del dashboard via AJAX (opcional)
function cargarDatosDashboard() {
    fetch('/api/dashboard/metricas')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error en la respuesta del servidor');
                }
                return response.json();
            })
            .then(data => {
                actualizarMetricasEnTiempoReal(data);
            })
            .catch(error => {
                console.error('Error cargando datos del dashboard:', error);
            });
}

// Función para actualizar las métricas en tiempo real
function actualizarMetricasEnTiempoReal(data) {
    console.log('Datos actualizados:', data);
}