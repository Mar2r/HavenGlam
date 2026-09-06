/**
 * HAVEN GLAM — Script de Agenda del Empleado (agenda-empleado.js)
 * Funcionalidad:
 * 1. Desplazamiento mensual exclusivo (Botones Anterior, Siguiente y Hoy).
 * 2. Visualización y renderizado dinámico de días y citas de todo el mes.
 * 3. Despliegue de panel lateral derecho con detalles de la clienta.
 * 4. Actualización de estados: Confirmada, Completada, Cancelada.
 * 5. Modal popup de validación obligatoria antes de cancelar cita.
 */

(function () {
    'use strict';

    let currentDate = new Date();
    let selectedAppointmentId = null;
    let appointmentPendingCancelId = null;

    const MONTH_NAMES = [
        'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
        'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
    ];

    // Citas iniciales en memoria
    let appointmentsList = [
        {
            id: 'HG-2026-0891',
            client: 'Beatriz Peña',
            email: 'bp25001@esfe.agape.edu.sv',
            phone: '7854-9921',
            service: 'Corte de Autor & Estilizado',
            professional: 'Elena Ross',
            dateStr: formatYMD(new Date()),
            timeSlot: '09:30',
            price: 35.00,
            status: 'CONFIRMADO',
            notes: 'Tratamiento delicado para cabello con tintura previa.'
        },
        {
            id: 'HG-2026-0892',
            client: 'Carlos Méndez',
            email: 'carlos.m@ejemplo.com',
            phone: '7234-5678',
            service: 'Masaje Relajante Piedras Calientes',
            professional: 'Marcus Vane',
            dateStr: formatYMD(new Date()),
            timeSlot: '11:00',
            price: 65.00,
            status: 'BLOQUEADO',
            notes: ''
        },
        {
            id: 'HG-2026-0893',
            client: 'Valeria Gómez',
            email: 'valeria.g@ejemplo.com',
            phone: '7345-6789',
            service: 'Manicura Rusa & Esmaltado',
            professional: 'Claire Dupont',
            dateStr: formatYMD(new Date()),
            timeSlot: '14:30',
            price: 35.00,
            status: 'COMPLETADO',
            notes: ''
        },
        {
            id: 'HG-2026-0894',
            client: 'Mariana Silva',
            email: 'mariana.s@ejemplo.com',
            phone: '7111-2233',
            service: 'Balayage & Iluminación',
            professional: 'Elena Ross',
            dateStr: formatYMD(addDays(new Date(), 2)),
            timeSlot: '10:00',
            price: 95.00,
            status: 'CONFIRMADO',
            notes: 'Decoloración previa requerida.'
        },
        {
            id: 'HG-2026-0895',
            client: 'Roberto Castillo',
            email: 'roberto.c@ejemplo.com',
            phone: '7444-5566',
            service: 'Corte Caballero & Barba Deluxe',
            professional: 'Marcus Vane',
            dateStr: formatYMD(addDays(new Date(), 5)),
            timeSlot: '16:00',
            price: 45.00,
            status: 'BLOQUEADO',
            notes: ''
        },
        {
            id: 'HG-2026-0896',
            client: 'Gabriela Morales',
            email: 'gaby.m@ejemplo.com',
            phone: '7888-9900',
            service: 'Lifting de Pestañas & Cejas',
            professional: 'Claire Dupont',
            dateStr: formatYMD(addDays(new Date(), -3)),
            timeSlot: '15:30',
            price: 30.00,
            status: 'COMPLETADO',
            notes: ''
        }
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

    // Botones de acción
    const btnActionConfirm = document.getElementById('btnActionConfirm');
    const btnActionComplete = document.getElementById('btnActionComplete');
    const btnActionCancel = document.getElementById('btnActionCancel');

    // Modal de Confirmación
    const cancelConfirmModal = document.getElementById('cancelConfirmModal');
    const modalCancelTimeSlot = document.getElementById('modalCancelTimeSlot');
    const btnModalCancelBack = document.getElementById('btnModalCancelBack');
    const btnModalConfirmCancellation = document.getElementById('btnModalConfirmCancellation');

    function init() {
        bindEvents();
        renderCalendar();
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
                updateAppointmentStatus(selectedAppointmentId, 'CONFIRMADO');
            });
        }

        if (btnActionComplete) {
            btnActionComplete.addEventListener('click', () => {
                updateAppointmentStatus(selectedAppointmentId, 'COMPLETADO');
            });
        }

        if (btnActionCancel) {
            btnActionCancel.addEventListener('click', () => {
                openCancelModal(selectedAppointmentId);
            });
        }

        if (btnModalCancelBack) {
            btnModalCancelBack.addEventListener('click', () => {
                closeCancelModal();
            });
        }

        if (btnModalConfirmCancellation) {
            btnModalConfirmCancellation.addEventListener('click', () => {
                if (appointmentPendingCancelId) {
                    updateAppointmentStatus(appointmentPendingCancelId, 'CANCELADO');
                    closeCancelModal();
                }
            });
        }
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
            dayApts.sort((a, b) => a.timeSlot.localeCompare(b.timeSlot));

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
                chip.className = `hg-appointment-chip status-${apt.status.toLowerCase()}`;
                chip.title = `${apt.timeSlot} — ${apt.client} (${apt.service})`;
                chip.innerHTML = `<span class="hg-chip-time">${apt.timeSlot}</span> ${apt.client.split(' ')[0]}`;

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

        if (panelStatusBadge) {
            panelStatusBadge.textContent = apt.status;
            panelStatusBadge.className = `status-pill status-${apt.status.toLowerCase()}`;
        }

        if (panelClientAvatar) panelClientAvatar.textContent = apt.client.charAt(0);
        if (panelClientName) panelClientName.textContent = apt.client;
        if (panelClientEmail) panelClientEmail.textContent = apt.email;
        if (panelClientPhone) panelClientPhone.textContent = `Tel: ${apt.phone}`;

        if (panelDateStr) panelDateStr.textContent = apt.dateStr;
        if (panelTimeSlot) panelTimeSlot.textContent = `${apt.timeSlot} hrs`;
        if (panelProfessionalName) panelProfessionalName.textContent = apt.professional;

        if (panelServicesList) {
            panelServicesList.innerHTML = `
                <li class="hg-service-item">
                    <span>${apt.service}</span>
                    <strong>$${apt.price.toFixed(2)}</strong>
                </li>
            `;
        }

        if (panelTotalAmount) panelTotalAmount.textContent = `$${apt.price.toFixed(2)}`;

        if (panelNotesBox) {
            if (apt.notes) {
                panelNotesBox.style.display = 'block';
                if (panelNotesText) panelNotesText.textContent = apt.notes;
            } else {
                panelNotesBox.style.display = 'none';
            }
        }
    }

    function updateAppointmentStatus(appointmentId, newStatus) {
        const apt = appointmentsList.find(a => a.id === appointmentId);
        if (!apt) return;

        apt.status = newStatus;
        loadAppointmentDetails(appointmentId);
        renderCalendar();
    }

    function openCancelModal(appointmentId) {
        const apt = appointmentsList.find(a => a.id === appointmentId);
        if (!apt) return;

        appointmentPendingCancelId = appointmentId;
        if (modalCancelTimeSlot) modalCancelTimeSlot.textContent = `${apt.timeSlot} hrs (${apt.dateStr})`;
        if (cancelConfirmModal) cancelConfirmModal.hidden = false;
    }

    function closeCancelModal() {
        appointmentPendingCancelId = null;
        if (cancelConfirmModal) cancelConfirmModal.hidden = true;
    }

    function formatYMD(d) {
        return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
    }

    function addDays(d, days) {
        const result = new Date(d);
        result.setDate(result.getDate() + days);
        return result;
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

})();