package org.esfe.HavenGlam.Controladores;

import org.esfe.HavenGlam.Modelos.Cita;
import org.esfe.HavenGlam.Modelos.CitaServicio;
import org.esfe.HavenGlam.Modelos.Empleado;
import org.esfe.HavenGlam.Repositorios.CitaRepository;
import org.esfe.HavenGlam.Repositorios.CitaServicioRepository;
import org.esfe.HavenGlam.Repositorios.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    // ---- Supuestos de negocio ajustables ----
    // Tu base de datos no guarda horarios/turnos, así que se asume una
    // capacidad fija por empleado para estimar ocupación y carga de trabajo.
    private static final int CITAS_POR_EMPLEADO_AL_DIA = 8;

    // Paleta para las porciones de la dona de "Mezcla de servicios"
    private static final String[] PALETA_DONA = {"#4D0E13", "#C8A49F", "#D8C4AC", "#8A2530", "#6B1A21"};

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private CitaServicioRepository citaServicioRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        LocalDate hoy = LocalDate.now();
        LocalDate mismoDiaSemanaPasada = hoy.minusWeeks(1);
        LocalDate inicioSemana = hoy.with(DayOfWeek.MONDAY);
        LocalDate inicioMes = hoy.withDayOfMonth(1);

        List<Cita> todasLasCitas = citaRepository.findAll();
        List<CitaServicio> todosCitaServicio = citaServicioRepository.findAll();

        List<Empleado> empleadosActivos = empleadoRepository.findAll().stream()
                .filter(e -> e.getEstado() != null && "Activo".equalsIgnoreCase(e.getEstado().getNombreEstado()))
                .collect(Collectors.toList());

        List<Cita> citasHoyList = todasLasCitas.stream()
                .filter(c -> hoy.equals(c.getFecha()))
                .sorted(Comparator.comparing(Cita::getHora))
                .collect(Collectors.toList());

        // ---- Ingresos de hoy vs. mismo día de la semana pasada ----
        BigDecimal ingresosHoy = sumaIngresos(todosCitaServicio, hoy);
        BigDecimal ingresosSemanaPasada = sumaIngresos(todosCitaServicio, mismoDiaSemanaPasada);
        Double variacionPorcentual = null;
        if (ingresosSemanaPasada.compareTo(BigDecimal.ZERO) > 0) {
            variacionPorcentual = ingresosHoy.subtract(ingresosSemanaPasada)
                    .divide(ingresosSemanaPasada, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        // ---- Clientas atendidas esta semana (distintas) ----
        long clientasEstaSemana = todasLasCitas.stream()
                .filter(c -> c.getFecha() != null && !c.getFecha().isBefore(inicioSemana) && !c.getFecha().isAfter(hoy))
                .map(c -> c.getCliente().getIdCliente())
                .distinct()
                .count();

        // ---- Ocupación estimada del salón ----
        int capacidadHoy = Math.max(empleadosActivos.size() * CITAS_POR_EMPLEADO_AL_DIA, 1);
        int ocupacionPorcentaje = (int) Math.min(100, Math.round((citasHoyList.size() * 100.0) / capacidadHoy));

        // ---- Agenda de hoy ----
        List<Map<String, Object>> agenda = new ArrayList<>();
        for (Cita c : citasHoyList) {
            String servicios = nombresServiciosDeCita(todosCitaServicio, c);
            String estadoNombre = c.getEstado() != null ? c.getEstado().getNombreEstado() : "";

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("hora", c.getHora());
            item.put("cliente", nombreCompleto(c.getCliente().getPersona().getNombre(), c.getCliente().getPersona().getApellido()));
            item.put("servicio", servicios);
            item.put("empleado", c.getEmpleado().getPersona().getNombre());
            item.put("estado", estadoNombre);
            item.put("tagClass", claseEtiquetaAgenda(estadoNombre));
            item.put("esAhora", "En curso".equalsIgnoreCase(estadoNombre));
            agenda.add(item);
        }

        // ---- Mezcla de servicios por categoría (mes actual) ----
        Map<String, Long> conteoPorCategoria = todosCitaServicio.stream()
                .filter(cs -> cs.getCita() != null && cs.getCita().getFecha() != null && !cs.getCita().getFecha().isBefore(inicioMes))
                .filter(cs -> cs.getServicio() != null && cs.getServicio().getCategoria() != null)
                .collect(Collectors.groupingBy(cs -> cs.getServicio().getCategoria().getNombreCategoria(), Collectors.counting()));

        long totalServiciosMes = conteoPorCategoria.values().stream().mapToLong(Long::longValue).sum();
        List<Map<String, Object>> mezclaServicios = new ArrayList<>();
        double circunferencia = 2 * Math.PI * 46;
        double acumuladoPorcentaje = 0;
        int colorIndex = 0;

        List<Map.Entry<String, Long>> categoriasOrdenadas = conteoPorCategoria.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .collect(Collectors.toList());

        for (Map.Entry<String, Long> entry : categoriasOrdenadas) {
            double porcentaje = totalServiciosMes > 0 ? (entry.getValue() * 100.0 / totalServiciosMes) : 0;
            double largoSegmento = (porcentaje / 100.0) * circunferencia;
            double desfase = circunferencia - (acumuladoPorcentaje / 100.0) * circunferencia;

            Map<String, Object> slice = new LinkedHashMap<>();
            slice.put("nombre", entry.getKey());
            slice.put("porcentaje", Math.round(porcentaje));
            slice.put("color", PALETA_DONA[colorIndex % PALETA_DONA.length]);
            slice.put("dashArray", String.format(Locale.US, "%.2f %.2f", largoSegmento, circunferencia));
            slice.put("dashOffset", String.format(Locale.US, "%.2f", desfase));
            mezclaServicios.add(slice);

            acumuladoPorcentaje += porcentaje;
            colorIndex++;
        }

        // ---- Carga del equipo (estimada) ----
        List<Map<String, Object>> cargaEquipo = new ArrayList<>();
        for (Empleado emp : empleadosActivos) {
            long citasEmpleadoHoy = citasHoyList.stream()
                    .filter(c -> c.getEmpleado() != null && emp.getIdEmpleado().equals(c.getEmpleado().getIdEmpleado()))
                    .count();
            int porcentaje = (int) Math.min(100, Math.round((citasEmpleadoHoy * 100.0) / CITAS_POR_EMPLEADO_AL_DIA));

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("nombre", emp.getPersona().getNombre());
            row.put("porcentaje", porcentaje);
            cargaEquipo.add(row);
        }
        cargaEquipo.sort((a, b) -> ((Integer) b.get("porcentaje")).compareTo((Integer) a.get("porcentaje")));

        // ---- Próximas reservas ----
        List<Map<String, Object>> proximasReservas = todasLasCitas.stream()
                .filter(c -> c.getFecha() != null && !c.getFecha().isBefore(hoy))
                .sorted(Comparator.comparing(Cita::getFecha).thenComparing(Cita::getHora))
                .limit(6)
                .map(c -> {
                    String estadoNombre = c.getEstado() != null ? c.getEstado().getNombreEstado() : "";
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("cliente", nombreCompleto(c.getCliente().getPersona().getNombre(), c.getCliente().getPersona().getApellido()));
                    row.put("servicio", nombresServiciosDeCita(todosCitaServicio, c));
                    row.put("empleado", c.getEmpleado().getPersona().getNombre());
                    row.put("fechaLabel", etiquetaFecha(c.getFecha(), hoy) + ", " + c.getHora());
                    row.put("estado", estadoNombre);
                    row.put("pillClass", clasePildoraReserva(estadoNombre));
                    return row;
                })
                .collect(Collectors.toList());

        // ---- Modelo ----
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("nombreAdmin", "Administrador"); // TODO: reemplazar por el usuario autenticado real
        model.addAttribute("pageTitle", "Buenos días, Administrador");
        model.addAttribute("pageSubtitle", capitalizar(hoy.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es")))
                + " " + hoy.getDayOfMonth() + " de " + hoy.getMonth().getDisplayName(TextStyle.FULL, new Locale("es"))
                + " · Semana " + hoy.get(WeekFields.ISO.weekOfYear()));

        model.addAttribute("ingresosHoy", ingresosHoy);
        model.addAttribute("variacionPorcentual", variacionPorcentual);
        model.addAttribute("citasHoyCount", citasHoyList.size());
        model.addAttribute("clientasEstaSemana", clientasEstaSemana);
        model.addAttribute("ocupacionPorcentaje", ocupacionPorcentaje);

        model.addAttribute("agenda", agenda);
        model.addAttribute("mezclaServicios", mezclaServicios);
        model.addAttribute("cargaEquipo", cargaEquipo);
        model.addAttribute("proximasReservas", proximasReservas);

        return "admin/dashboard";
    }

    private BigDecimal sumaIngresos(List<CitaServicio> citaServicios, LocalDate fecha) {
        return citaServicios.stream()
                .filter(cs -> cs.getCita() != null && fecha.equals(cs.getCita().getFecha()))
                .map(CitaServicio::getPrecioAlMomento)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String nombresServiciosDeCita(List<CitaServicio> todosCitaServicio, Cita cita) {
        String servicios = todosCitaServicio.stream()
                .filter(cs -> cs.getCita() != null && cita.getIdCita().equals(cs.getCita().getIdCita()))
                .map(cs -> cs.getServicio().getNombreServicio())
                .collect(Collectors.joining(" + "));
        return servicios.isEmpty() ? "Sin servicios asignados" : servicios;
    }

    private String nombreCompleto(String nombre, String apellido) {
        return (nombre != null ? nombre : "") + " " + (apellido != null ? apellido : "");
    }

    private String claseEtiquetaAgenda(String estadoNombre) {
        if ("Confirmada".equalsIgnoreCase(estadoNombre)) return "confirmed";
        if ("Pendiente".equalsIgnoreCase(estadoNombre)) return "pending";
        return "";
    }

    private String clasePildoraReserva(String estadoNombre) {
        if ("Completada".equalsIgnoreCase(estadoNombre)) return "done";
        if ("Cancelada".equalsIgnoreCase(estadoNombre)) return "cancelled";
        return "upcoming";
    }

    private String etiquetaFecha(LocalDate fecha, LocalDate hoy) {
        if (fecha.equals(hoy)) return "Hoy";
        if (fecha.equals(hoy.plusDays(1))) return "Mañana";
        return fecha.getDayOfMonth() + "/" + fecha.getMonthValue();
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }
}