/**
 * HAVEN GLAM — Script de Agenda del Empleado (agendaEmpleado.js)
 * Carga de citas reales desde la base de datos y sincronización interactiva de estados.
 */

(function () {
    'use strict';

    let currentDate = new Date();
    let selectedAppointmentId = null;
    let appointmentsList = [];

    const MONTH_NAMES = [
        'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
        'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
    ];

    // Elementos DOM
    const btnPrevMonth = document.getElementById('btnPrevMonth');
    const btnNextMonth = document.getElementById('btnNextMonth');
    const btnToday = document.getElementById('btnToday');
    const monthNameText = document.getElementById('monthNameText');
    const yearNumText = document.getElementById('yearNumText');
    const calendarDaysGrid = document.getElementById('calendarDaysGrid');

    // Panel Lateral
    const appointmentSidebar = document.getElementById('appointmentSidebar');
    const btnCloseSidebar = document.getElementById('btnCloseSidebar');
    const panelStatusBadge = document.getElementById('panelStatusBadge');
    const panelClientAvatar = document.getElementById('panelClientAvatar');
    const panelClientName = document.getElementById('panelClientName');
    const panelClientEmail = document.getElementById('panelClientEmail');
    const panelClientPhone = document.getElementById('panelClientPhone');
    const panelDateStr = document.getElementById('panelDateStr');
    const panelTimeSlot = document.getElementById('panelTimeSlot');
    const panelProfessionalName = document.getElementById('panelProfessionalName');
    const panelServicesList = document.getElementById('panelServicesList');
    const panelTotalAmount = document.getElementById('panelTotalAmount');
    const panelNotesBox = document.getElementById('panelNotesBox');
    const panelNotesText = document.getElementById('panelNotesText');

    // Botones de acción (Empleado solo puede Confirmar o Completar)
    const btnActionConfirm = document.getElementById('btnActionConfirm');
    const btnActionComplete = document.getElementById('btnActionComplete');

    function init() {
        bindEvents();
        fetchAppointments();
    }

    function bindEvents() {
        if (btnPrevMonth) {
            btnPrevMonth.addEventListener('click', () => {
                currentDate = new Date(currentDate.getFullYear(), currentDate.getMonth() - 1, 1);
                renderCalendar();
            });
        }

        if (btnNextMonth) {
            btnNextMonth.addEventListener('click', () => {
                currentDate = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 1);
                renderCalendar();
            });
        }

        if (btnToday) {
            btnToday.addEventListener('click', () => {
                currentDate = new Date();
                renderCalendar();
            });
        }

        if (btnCloseSidebar) {
            btnCloseSidebar.addEventListener('click', () => {
                if (appointmentSidebar) appointmentSidebar.hidden = true;
            });
        }

        if (btnActionConfirm) {
            btnActionConfirm.addEventListener('click', () => {
                updateAppointmentStatus(selectedAppointmentId, 'Confirmada');
            });
        }

        if (btnActionComplete) {
            btnActionComplete.addEventListener('click', () => {
                updateAppointmentStatus(selectedAppointmentId, 'Completada');
            });
        }
    }

    async function fetchAppointments() {
        try {
            const response = await fetch('/agendaEmpleado/api/citas');
            if (!response.ok) {
                console.warn('No se pudieron obtener las citas desde la base de datos:', response.status);
                return;
            }
            const data = await response.json();
            if (Array.isArray(data)) {
                appointmentsList = data;
                renderCalendar();
            }
        } catch (error) {
            console.error('Error al conectar con la API de citas:', error);
        }
    }

    function getStatusClass(statusStr) {
        if (!statusStr) return 'status-pendiente';
        const s = statusStr.trim().toLowerCase();
        if (s.startsWith('confirm')) return 'status-confirmada';
        if (s.startsWith('complet')) return 'status-completada';
        if (s.startsWith('cancel')) return 'status-cancelada';
        return 'status-pendiente';
    }

    function renderCalendar() {
        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();

        if (monthNameText) monthNameText.textContent = MONTH_NAMES[month];
        if (yearNumText) yearNumText.textContent = year;

        if (!calendarDaysGrid) return;
        calendarDaysGrid.innerHTML = '';

        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);
        const totalDays = lastDay.getDate();

        let startDayIndex = firstDay.getDay() - 1;
        if (startDayIndex === -1) startDayIndex = 6;

        const prevMonthLastDay = new Date(year, month, 0).getDate();
        const todayStr = formatYMD(new Date());

        // Días de relleno anterior
        for (let i = startDayIndex - 1; i >= 0; i--) {
            const dNum = prevMonthLastDay - i;
            const cell = createDayCell(dNum, true, null);
            calendarDaysGrid.appendChild(cell);
        }

        // Días del mes en curso
        for (let day = 1; day <= totalDays; day++) {
            const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
            const isToday = dateStr === todayStr;

            const dayApts = appointmentsList.filter(a => a.dateStr === dateStr);
            dayApts.sort((a, b) => (a.timeSlot || '').localeCompare(b.timeSlot || ''));

            const cell = createDayCell(day, false, dateStr, isToday, dayApts);
            calendarDaysGrid.appendChild(cell);
        }

        // Días de relleno posterior
        const totalCells = calendarDaysGrid.children.length;
        const remainder = (7 - (totalCells % 7)) % 7;
        for (let n = 1; n <= remainder; n++) {
            const cell = createDayCell(n, true, null);
            calendarDaysGrid.appendChild(cell);
        }
    }

    function createDayCell(dayNumber, isOtherMonth, dateStr, isToday, appointments = []) {
        const cell = document.createElement('div');
        cell.className = 'hg-day-cell';
        if (isOtherMonth) cell.classList.add('hg-other-month');
        if (isToday) cell.classList.add('hg-is-today');

        const header = document.createElement('div');
        header.className = 'hg-day-header';

        const numSpan = document.createElement('span');
        numSpan.className = 'hg-day-number';
        numSpan.textContent = dayNumber;
        header.appendChild(numSpan);

        if (!isOtherMonth && appointments.length > 0) {
            const badge = document.createElement('span');
            badge.className = 'hg-badge-day-count';
            badge.textContent = appointments.length;
            header.appendChild(badge);
        }

        cell.appendChild(header);

        if (!isOtherMonth && appointments.length > 0) {
            const aptsContainer = document.createElement('div');
            aptsContainer.className = 'hg-day-appointments';

            appointments.forEach(apt => {
                const chip = document.createElement('button');
                chip.type = 'button';
                const sClass = getStatusClass(apt.status);
                chip.className = `hg-appointment-chip ${sClass}`;
                chip.title = `${apt.timeSlot || ''} — ${apt.client || 'Cliente'} (${apt.service || 'Servicio'})`;

                const clientFirstName = (apt.client || 'Cliente').split(' ')[0];
                chip.innerHTML = `<span class="hg-chip-time">${apt.timeSlot || ''}</span> ${clientFirstName}`;

                chip.addEventListener('click', (e) => {
                    e.stopPropagation();
                    loadAppointmentDetails(apt.id);
                });

                aptsContainer.appendChild(chip);
            });

            cell.appendChild(aptsContainer);
        }

        return cell;
    }

    function loadAppointmentDetails(appointmentId) {
        const apt = appointmentsList.find(a => a.id === appointmentId);
        if (!apt) return;

        selectedAppointmentId = appointmentId;
        if (appointmentSidebar) appointmentSidebar.hidden = false;

        const sClass = getStatusClass(apt.status);

        if (panelStatusBadge) {
            panelStatusBadge.textContent = apt.status ? apt.status.toUpperCase() : 'PENDIENTE';
            panelStatusBadge.className = `status-pill ${sClass}`;
        }

        if (panelClientAvatar) {
            panelClientAvatar.textContent = (apt.client && apt.client.trim().length > 0) ? apt.client.trim().charAt(0).toUpperCase() : 'C';
        }
        if (panelClientName) panelClientName.textContent = apt.client || 'Cliente';
        if (panelClientEmail) panelClientEmail.textContent = apt.email || 'Sin correo registrado';
        if (panelClientPhone) panelClientPhone.textContent = `Tel: ${apt.phone || 'N/A'}`;

        if (panelDateStr) panelDateStr.textContent = apt.dateStr || '—';
        if (panelTimeSlot) panelTimeSlot.textContent = apt.timeSlot ? `${apt.timeSlot} hrs` : '—';
        if (panelProfessionalName) panelProfessionalName.textContent = apt.professional || 'Especialista';

        if (panelServicesList) {
            if (Array.isArray(apt.services) && apt.services.length > 0) {
                panelServicesList.innerHTML = apt.services.map(s => `
                    <li class="hg-service-item">
                        <span>${s.name}</span>
                        <strong>$${Number(s.price || 0).toFixed(2)}</strong>
                    </li>
                `).join('');
            } else {
                panelServicesList.innerHTML = `
                    <li class="hg-service-item">
                        <span>${apt.service || 'Servicio General'}</span>
                        <strong>$${Number(apt.price || 0).toFixed(2)}</strong>
                    </li>
                `;
            }
        }

        if (panelTotalAmount) panelTotalAmount.textContent = `$${Number(apt.price || 0).toFixed(2)}`;

        if (panelNotesBox) {
            if (apt.notes && apt.notes.trim() !== '' && apt.notes.trim() !== 'Sin observaciones') {
                panelNotesBox.style.display = 'block';
                if (panelNotesText) panelNotesText.textContent = apt.notes;
            } else {
                panelNotesBox.style.display = 'none';
            }
        }
    }

    async function updateAppointmentStatus(appointmentId, newStatus) {
        const apt = appointmentsList.find(a => a.id === appointmentId);
        if (!apt) return;

        try {
            const response = await fetch(`/agendaEmpleado/api/citas/${appointmentId}/estado`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ estado: newStatus })
            });

            if (response.ok) {
                const data = await response.json();
                apt.status = data.nuevoEstado || newStatus;
                loadAppointmentDetails(appointmentId);
                renderCalendar();
            } else {
                apt.status = newStatus;
                loadAppointmentDetails(appointmentId);
                renderCalendar();
            }
        } catch (err) {
            console.error('Error al guardar nuevo estado:', err);
            apt.status = newStatus;
            loadAppointmentDetails(appointmentId);
            renderCalendar();
        }
    }

    function formatYMD(d) {
        return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

})();