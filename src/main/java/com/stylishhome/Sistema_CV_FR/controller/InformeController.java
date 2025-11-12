package com.stylishhome.Sistema_CV_FR.controller;

import com.stylishhome.Sistema_CV_FR.model.InformeLog;
import com.stylishhome.Sistema_CV_FR.model.Usuario;
import com.stylishhome.Sistema_CV_FR.service.InformeService;
import com.stylishhome.Sistema_CV_FR.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador para la generación de informes y reportes
 */
@Controller
@RequestMapping("/informes")
public class InformeController {

    @Autowired
    private InformeService informeService;

    @Autowired
    private DashboardService dashboardService;

    /**
     * Muestra la página principal de informes
     */
    @GetMapping
    public String mostrarInformes(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        model.addAttribute("titulo", "Generación de Informes");

        return "informes/principal";
    }

    /**
     * Muestra el informe de pedidos (ventas)
     */
    @GetMapping("/pedidos")
    public String mostrarInformePedidos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            HttpSession session,
            Model model) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        // Establecer fechas por defecto si no se proporcionan
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().minusMonths(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        try {
            // Obtener datos del servicio
            Map<String, Object> informe = informeService.generarInformeVentas(fechaInicio, fechaFin, null);

            // Calcular estadísticas adicionales para el template
            List<Object[]> ventasDetalladas = (List<Object[]>) informe.get("ventasDetalladas");
            long totalPedidos = ventasDetalladas != null ? ventasDetalladas.size() : 0;
            long pedidosCompletados = ventasDetalladas != null
                    ? ventasDetalladas.stream().filter(v -> "COMPLETADA".equals(v[4])).count() : 0;
            long pedidosPendientes = ventasDetalladas != null
                    ? ventasDetalladas.stream().filter(v -> "PENDIENTE".equals(v[4])).count() : 0;
            long pedidosCancelados = ventasDetalladas != null
                    ? ventasDetalladas.stream().filter(v -> "CANCELADA".equals(v[4])).count() : 0;

            double totalValor = (Double) informe.get("totalPeriodo");
            double promedioPedido = totalPedidos > 0 ? totalValor / totalPedidos : 0;
            double tasaCompletacion = totalPedidos > 0 ? (pedidosCompletados * 100.0 / totalPedidos) : 0;

            model.addAttribute("pedidos", ventasDetalladas);
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("totalPedidos", totalPedidos);
            model.addAttribute("pedidosCompletados", pedidosCompletados);
            model.addAttribute("pedidosPendientes", pedidosPendientes);
            model.addAttribute("pedidosCancelados", pedidosCancelados);
            model.addAttribute("totalValor", totalValor);
            model.addAttribute("promedioPedido", promedioPedido);
            model.addAttribute("tasaCompletacion", String.format("%.1f", tasaCompletacion));

        } catch (Exception e) {
            model.addAttribute("error", "Error al generar el informe: " + e.getMessage());
        }

        return "informes/informesPedidos";
    }

    /**
     * Muestra el informe de cotizaciones
     */
    @GetMapping("/cotizaciones")
    public String mostrarInformeCotizaciones(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            HttpSession session,
            Model model) {

        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        // Establecer fechas por defecto si no se proporcionan
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().minusMonths(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        try {
            // Obtener datos del servicio
            Map<String, Object> informe = informeService.generarInformeCotizaciones(fechaInicio, fechaFin, null);

            // Calcular estadísticas adicionales para el template
            List<Object[]> cotizacionesDetalladas = (List<Object[]>) informe.get("cotizacionesDetalladas");
            long totalCotizaciones = cotizacionesDetalladas != null ? cotizacionesDetalladas.size() : 0;

            // Calcular estados
            long aprobadas = 0, pendientes = 0, rechazadas = 0, vencidas = 0;
            double totalValor = 0;

            if (cotizacionesDetalladas != null) {
                for (Object[] cotizacion : cotizacionesDetalladas) {
                    String estado = (String) cotizacion[4]; // Asumiendo que el estado está en la posición 4
                    Double total = (Double) cotizacion[3]; // Asumiendo que el total está en la posición 3

                    if (total != null) {
                        totalValor += total;
                    }

                    switch (estado) {
                        case "APROBADA":
                            aprobadas++;
                            break;
                        case "PENDIENTE":
                            pendientes++;
                            break;
                        case "RECHAZADA":
                            rechazadas++;
                            break;
                        case "VENCIDA":
                            vencidas++;
                            break;
                    }
                }
            }

            double promedioCotizacion = totalCotizaciones > 0 ? totalValor / totalCotizaciones : 0;
            double tasaAprobacion = totalCotizaciones > 0 ? (aprobadas * 100.0 / totalCotizaciones) : 0;

            Map<String, Object> estadisticas = Map.of(
                    "totalCotizaciones", totalCotizaciones,
                    "totalValor", totalValor,
                    "promedioCotizacion", promedioCotizacion,
                    "aprobadas", aprobadas,
                    "pendientes", pendientes,
                    "rechazadas", rechazadas,
                    "vencidas", vencidas,
                    "tasaAprobacion", String.format("%.1f", tasaAprobacion)
            );

            model.addAttribute("cotizaciones", cotizacionesDetalladas);
            model.addAttribute("estadisticas", estadisticas);
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);

        } catch (Exception e) {
            model.addAttribute("error", "Error al generar el informe: " + e.getMessage());
        }

        return "informes/informeCotizaciones";
    }

    /**
     * Genera y muestra el informe de resumen general
     */
    @PostMapping("/resumen-general")
    public String generarInformeResumenGeneral(@RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            @RequestParam(required = false) Integer empleadoId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        try {
            Map<String, Object> informe = informeService.generarInformeResumenGeneral(fechaInicio, fechaFin, empleadoId);

            // Registrar en el log
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            informeService.registrarGeneracionInforme(
                    InformeLog.Tipos.RESUMEN_GENERAL,
                    fechaInicio,
                    fechaFin,
                    usuario.getIdEmpleado(),
                    "Resumen general del sistema"
            );

            model.addAttribute("informe", informe);
            model.addAttribute("titulo", "Informe de Resumen General");
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("tipoInforme", "RESUMEN_GENERAL");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al generar el informe: " + e.getMessage());
            return "redirect:/informes";
        }

        return "informes/resumen-general";
    }

    /**
     * Genera y muestra el informe de ventas
     */
    @PostMapping("/ventas")
    public String generarInformeVentas(@RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            @RequestParam(required = false) Integer empleadoId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        try {
            Map<String, Object> informe = informeService.generarInformeVentas(fechaInicio, fechaFin, empleadoId);

            // Registrar en el log
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            informeService.registrarGeneracionInforme(
                    InformeLog.Tipos.INFORME_VENTAS,
                    fechaInicio,
                    fechaFin,
                    usuario.getIdEmpleado(),
                    "Informe detallado de ventas"
            );

            model.addAttribute("informe", informe);
            model.addAttribute("titulo", "Informe de Ventas");
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("tipoInforme", "INFORME_VENTAS");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al generar el informe: " + e.getMessage());
            return "redirect:/informes";
        }

        return "informes/ventas";
    }

    /**
     * Genera y muestra el informe de productos más vendidos
     */
    @PostMapping("/productos-mas-vendidos")
    public String generarInformeProductosMasVendidos(@RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        try {
            Map<String, Object> informe = informeService.generarInformeProductosMasVendidos(fechaInicio, fechaFin);

            // Registrar en el log
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            informeService.registrarGeneracionInforme(
                    InformeLog.Tipos.INFORME_PEDIDOS,
                    fechaInicio,
                    fechaFin,
                    usuario.getIdEmpleado(),
                    "Informe de productos más vendidos"
            );

            model.addAttribute("informe", informe);
            model.addAttribute("titulo", "Productos Más Vendidos");
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("tipoInforme", "INFORME_PEDIDOS");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al generar el informe: " + e.getMessage());
            return "redirect:/informes";
        }

        return "informes/productos-mas-vendidos";
    }

    /**
     * Muestra el historial de informes generados
     */
    @GetMapping("/historial")
    public String mostrarHistorialInformes(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        List<InformeLog> historial = informeService.obtenerHistorialInformes();
        model.addAttribute("historial", historial);
        model.addAttribute("titulo", "Historial de Informes");

        return "informes/historial";
    }

    /**
     * Muestra estadísticas de uso de informes
     */
    @GetMapping("/estadisticas")
    public String mostrarEstadisticasInformes(Model model, HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }

        Map<String, Object> estadisticas = informeService.obtenerEstadisticasInformes();
        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("titulo", "Estadísticas de Informes");

        return "informes/estadisticas";
    }

    /**
     * API para obtener datos del dashboard (AJAX)
     */
    @GetMapping("/api/dashboard")
    @ResponseBody
    public Map<String, Object> obtenerDatosDashboard(HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return Map.of("error", "No autenticado");
        }

        try {
            Map<String, Object> metricas = dashboardService.obtenerMetricasDashboard();
            Map<String, Object> graficos = dashboardService.obtenerDatosGraficos();

            return Map.of(
                    "metricas", metricas,
                    "graficos", graficos,
                    "success", true
            );
        } catch (Exception e) {
            return Map.of(
                    "error", "Error al obtener datos del dashboard: " + e.getMessage(),
                    "success", false
            );
        }
    }

    /**
     * API para generar informe rápido (AJAX)
     */
    @PostMapping("/api/generar-rapido")
    @ResponseBody
    public Map<String, Object> generarInformeRapido(@RequestParam String tipo,
            @RequestParam String periodo,
            HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return Map.of("success", false, "error", "No autenticado");
        }

        try {
            LocalDate fechaInicio;
            LocalDate fechaFin = LocalDate.now();

            switch (periodo.toUpperCase()) {
                case "HOY":
                    fechaInicio = LocalDate.now();
                    break;
                case "SEMANA":
                    fechaInicio = LocalDate.now().minusDays(7);
                    break;
                case "MES":
                    fechaInicio = LocalDate.now().withDayOfMonth(1);
                    break;
                case "TRIMESTRE":
                    fechaInicio = LocalDate.now().minusMonths(3);
                    break;
                default:
                    fechaInicio = LocalDate.now().withDayOfMonth(1);
                    fechaFin = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            }

            Usuario usuario = (Usuario) session.getAttribute("usuario");
            Map<String, Object> parametros = Map.of(
                    "fechaInicio", fechaInicio,
                    "fechaFin", fechaFin,
                    "empleadoId", usuario.getIdEmpleado()
            );

            Map<String, Object> informe = informeService.generarInformePersonalizado(tipo, parametros);

            return Map.of(
                    "success", true,
                    "informe", informe,
                    "fechaInicio", fechaInicio,
                    "fechaFin", fechaFin
            );

        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "error", "Error al generar informe: " + e.getMessage()
            );
        }
    }
}
