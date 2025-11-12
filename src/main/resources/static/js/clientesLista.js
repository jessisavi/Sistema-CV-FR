class ClientesManager {
    constructor() {
        this.typeFilter = document.getElementById('typeFilter');
        this.statusFilter = document.getElementById('statusFilter');
        this.formSearch = document.getElementById('formSearch');
        this.clearSearch = document.getElementById('clearSearch');
        this.deleteButtons = document.querySelectorAll('.btn-eliminar');
        
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setupDeleteConfirmations();
        this.setupSearchClearButton();
    }

    setupEventListeners() {
        // Filtros
        if (this.typeFilter) {
            this.typeFilter.addEventListener('change', () => this.handleFilterChange('tipo', this.typeFilter.value));
        }
        
        if (this.statusFilter) {
            this.statusFilter.addEventListener('change', () => this.handleFilterChange('estado', this.statusFilter.value));
        }
    }

    setupDeleteConfirmations() {
        // Confirmación para eliminar clientes
        this.deleteButtons.forEach(button => {
            button.addEventListener('click', (e) => {
                const clienteNombre = button.getAttribute('data-cliente-nombre');
                if (!this.confirmDelete(clienteNombre)) {
                    e.preventDefault();
                }
            });
        });
    }

    setupSearchClearButton() {
        // Mostrar/ocultar botón de limpiar búsqueda
        if (this.formSearch && this.clearSearch) {
            this.formSearch.addEventListener('input', () => {
                this.toggleClearSearchButton();
            });
            
            // Estado inicial
            this.toggleClearSearchButton();
        }
    }

    handleFilterChange(filterType, filterValue) {
        const url = new URL(window.location.href);
        
        if (filterValue) {
            url.searchParams.set(filterType, filterValue);
        } else {
            url.searchParams.delete(filterType);
        }
        
        window.location.href = url.toString();
    }

    confirmDelete(clienteNombre) {
        return confirm(`¿Está seguro de eliminar al cliente ${clienteNombre}? Esta acción no se puede deshacer.`);
    }

    toggleClearSearchButton() {
        if (this.formSearch.value.trim() !== '') {
            this.clearSearch.classList.remove('d-none');
        } else {
            this.clearSearch.classList.add('d-none');
        }
    }

    // Método para búsqueda en tiempo real (si se implementa en el futuro)
    setupRealTimeSearch() {
        if (this.formSearch && !this.formSearch.value) {
            this.formSearch.addEventListener('input', (e) => {
                this.filterTable(e.target.value.toLowerCase());
            });
        }
    }

    filterTable(filterText) {
        const tableBody = document.getElementById('tableBody');
        const rows = tableBody.getElementsByTagName('tr');
        
        Array.from(rows).forEach(row => {
            if (row.querySelector('.text-center')) return; // Saltar fila de "no resultados"
            
            const text = row.textContent.toLowerCase();
            row.style.display = text.includes(filterText) ? '' : 'none';
        });
    }

    // Métodos utilitarios
    static formatPhone(phone) {
        if (!phone) return '';
        // Formatear número de teléfono: (XXX) XXX-XXXX
        const cleaned = phone.replace(/\D/g, '');
        if (cleaned.length === 10) {
            return cleaned.replace(/(\d{3})(\d{3})(\d{4})/, '($1) $2-$3');
        }
        return phone;
    }

    static formatDate(dateString) {
        if (!dateString) return '';
        try {
            const date = new Date(dateString);
            return date.toLocaleDateString('es-ES');
        } catch (error) {
            return dateString;
        }
    }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    new ClientesManager();
    
    // Aplicar formatos si es necesario
    ClientesManager.applyFormats();
});

// Utilidades estáticas
ClientesManager.applyFormats = function() {
    // Aplicar formatos a teléfonos y fechas si es necesario
    const phones = document.querySelectorAll('[data-phone]');
    const dates = document.querySelectorAll('[data-date]');
    
    phones.forEach(phone => {
        const original = phone.textContent;
        phone.textContent = ClientesManager.formatPhone(original);
    });
    
    dates.forEach(date => {
        const original = date.textContent;
        date.textContent = ClientesManager.formatDate(original);
    });
};

// Exportar para uso global (si es necesario)
window.ClientesManager = ClientesManager;