/**
 * HAVEN GLAM — citas.js
 * Lógica del Stepper, Selección de Servicios (máx 3),
 * Grilla de horarios y consumo de datos reales del backend Java.
 */

document.addEventListener('DOMContentLoaded', () => {

    // ==========================================
    // 1. ESTADO GLOBAL EN EL CLIENTE
    // ==========================================
    const bookingState = {
        currentStep: 1,
        maxServices: 3,
        dayOffset: 0,
        catalogServices: [],
        professionals: [],
        selectedServices: [],
        selectedProfessional: null,
        selectedDate: null,
        selectedTime: null,
        totalPrice: 0,
        totalDuration: 0,
        notes: ''
    };

    const MAX_DAY_OFFSET = 84; // 12 semanas hacia adelante

    // ==========================================
    // 2. GENERACIÓN DE TURNOS (08:00 A 17:00 CADA 30 MIN)
    // ==========================================
    function generateDailyTimeSlots() {
        const slots = [];
        for (let hour = 8; hour <= 17; hour++) {
            const hStr = hour.toString().padStart(2, '0');
            slots.push(`${hStr}:00`);
            if (hour < 17) {
                slots.push(`${hStr}:30`);
            }
        }
        return slots;
    }
    const ALL_SLOTS = generateDailyTimeSlots();

    // ==========================================
    // 3. ELEMENTOS DEL DOM
    // ==========================================
    const stepPanels = {
        1: document.getElementById('step-panel-1'),
        2: document.getElementById('step-panel-2'),
        3: document.getElementById('step-panel-3'),
        4: document.getElementById('step-panel-4'),
        success: document.getElementById('step-panel-success')
    };

    const stepIndicators = document.querySelectorAll('[data-step-indicator]');
    const stepEyebrow = document.getElementById('step-eyebrow');
    const btnNext = document.getElementById('btn-step-next');
    const btnBack = document.getElementById('btn-step-back');
    const footerActions = document.getElementById('step-footer-actions');

    const categoryTabsContainer = document.getElementById('category-tabs-container');
    const servicesContainer = document.getElementById('services-list-container');
    const serviceCountBadge = document.getElementById('service-count-badge');
    const limitWarning = document.getElementById('limit-warning');

    const prosContainer = document.getElementById('professionals-list-container');
    const dayScroller = document.getElementById('day-scroller');
    const timeSlotsGrid = document.getElementById('time-slots-grid');

    const summaryEmptyMsg = document.getElementById('summary-empty-msg');
    const summaryList = document.getElementById('summary-services-list');
    const summaryMeta = document.getElementById('summary-meta-block');
    const summaryTotalBlock = document.getElementById('summary-total-block');
    const sumPro = document.getElementById('sum-pro');
    const sumDate = document.getElementById('sum-date');
    const sumTime = document.getElementById('sum-time');
    const sumDuration = document.getElementById('sum-duration');
    const sumTotal = document.getElementById('sum-total');

    // ==========================================
    // 4. INICIALIZACIÓN
    // ==========================================
    loadInitialData();
    renderDayScroller();
    setupDayNavButtons();
    setupNavigationButtons();

    // ==========================================
    // 5. CARGA DE DATOS REALES (SERVICIOS / EMPLEADOS)
    // ==========================================
    async function loadInitialData() {
        try {
            const [serviciosRes, empleadosRes] = await Promise.all([
                fetch('/citas/api/servicios', { headers: { 'Accept': 'application/json' } }),
                fetch('/citas/api/empleados', { headers: { 'Accept': 'application/json' } })
            ]);

            bookingState.catalogServices = serviciosRes.ok ? await serviciosRes.json() : [];
            bookingState.professionals = empleadosRes.ok ? await empleadosRes.json() : [];
        } catch (error) {
            console.error('Error cargando datos del servidor:', error);
            bookingState.catalogServices = [];
            bookingState.professionals = [];
        }

        buildCategoryTabs();
        renderServices('all');
        renderProfessionals();
    }

    // ==========================================
    // 6. RENDERIZADO DEL PASO 1 (SERVICIOS)
    // ==========================================
    function buildCategoryTabs() {
        if (!categoryTabsContainer) return;

        const categorias = [...new Set(bookingState.catalogServices.map(s => s.category))].sort();

        categoryTabsContainer.innerHTML = '';

        const btnTodos = document.createElement('button');
        btnTodos.type = 'button';
        btnTodos.className = 'hg-tab-btn is-active';
        btnTodos.dataset.category = 'all';
        btnTodos.textContent = 'Todos';
        categoryTabsContainer.appendChild(btnTodos);

        categorias.forEach(cat => {
            const btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'hg-tab-btn';
            btn.dataset.category = cat;
            btn.textContent = cat;
            categoryTabsContainer.appendChild(btn);
        });

        categoryTabsContainer.querySelectorAll('.hg-tab-btn').forEach(tab => {
            tab.addEventListener('click', () => {
                categoryTabsContainer.querySelectorAll('.hg-tab-btn').forEach(t => t.classList.remove('is-active'));
                tab.classList.add('is-active');
                renderServices(tab.dataset.category);
            });
        });
    }

    function renderServices(categoryFilter = 'all') {
        if (!servicesContainer) return;
        servicesContainer.innerHTML = '';

        if (bookingState.catalogServices.length === 0) {
            servicesContainer.innerHTML = '<p class="hg-empty-msg">Aún no hay servicios disponibles. Vuelve pronto.</p>';
            updateServiceBadges();
            return;
        }

        const filtered = categoryFilter === 'all'
            ? bookingState.catalogServices
            : bookingState.catalogServices.filter(s => s.category === categoryFilter);

        filtered.forEach(srv => {
            const isSelected = bookingState.selectedServices.some(s => s.id === srv.id);
            const isLimitReached = bookingState.selectedServices.length >= bookingState.maxServices && !isSelected;

            const card = document.createElement('div');
            card.className = `hg-service-card ${isSelected ? 'is-selected' : ''} ${isLimitReached ? 'is-disabled' : ''}`;
            card.dataset.id = srv.id;

            card.innerHTML = `
                <div class="hg-srv-info">
                    <h4>${srv.name}</h4>
                    <div class="hg-srv-meta">
                        <span>⏱ ${srv.duration} min</span>
                        <span>•</span>
                        <span>Categoría: ${srv.category.toUpperCase()}</span>
                    </div>
                </div>
                <div class="hg-srv-price-action">
                    <span class="hg-srv-price">$${srv.price}</span>
                    <button type="button" class="hg-add-circle" aria-label="Seleccionar">
                        ${isSelected ? '✓' : '+'}
                    </button>
                </div>
            `;

            card.addEventListener('click', () => toggleService(srv));
            servicesContainer.appendChild(card);
        });

        updateServiceBadges();
    }

    function toggleService(srv) {
        const index = bookingState.selectedServices.findIndex(s => s.id === srv.id);

        if (index > -1) {
            bookingState.selectedServices.splice(index, 1);
            if (limitWarning) limitWarning.hidden = true;
        } else {
            if (bookingState.selectedServices.length >= bookingState.maxServices) {
                if (limitWarning) limitWarning.hidden = false;
                return;
            }
            bookingState.selectedServices.push(srv);
            if (bookingState.selectedServices.length >= bookingState.maxServices) {
                if (limitWarning) limitWarning.hidden = false;
            }
        }

        recalculateTotals();
        renderServices(document.querySelector('.hg-tab-btn.is-active')?.dataset.category || 'all');
        updateSummary();
        updateNextButtonState();
    }

    function recalculateTotals() {
        bookingState.totalPrice = bookingState.selectedServices.reduce((acc, cur) => acc + Number(cur.price), 0);
        bookingState.totalDuration = bookingState.selectedServices.reduce((acc, cur) => acc + Number(cur.duration), 0);
    }

    function updateServiceBadges() {
        if (serviceCountBadge) {
            serviceCountBadge.textContent = `${bookingState.selectedServices.length} / ${bookingState.maxServices} máx.`;
        }
    }

    // ==========================================
    // 7. RENDERIZADO DEL PASO 2 (PROFESIONAL)
    // ==========================================
    function renderProfessionals() {
        if (!prosContainer) return;
        prosContainer.innerHTML = '';

        if (bookingState.professionals.length === 0) {
            prosContainer.innerHTML = '<p class="hg-empty-msg">Aún no hay especialistas disponibles.</p>';
            return;
        }

        bookingState.professionals.forEach(pro => {
            const isSelected = bookingState.selectedProfessional?.id === pro.id;
            const card = document.createElement('div');
            card.className = `hg-pro-card ${isSelected ? 'is-selected' : ''}`;
            card.innerHTML = `
                <div class="hg-pro-avatar">${pro.avatar}</div>
                <div>
                    <h4>${pro.name}</h4>
                    <p>${pro.role}</p>
                </div>
            `;
            card.addEventListener('click', () => {
                bookingState.selectedProfessional = pro;
                renderProfessionals();
                updateSummary();
                updateNextButtonState();
            });
            prosContainer.appendChild(card);
        });
    }

    // ==========================================
    // 8. RENDERIZADO DEL PASO 3 (FECHA Y TURNOS)
    // ==========================================
    function renderDayScroller() {
        if (!dayScroller) return;
        dayScroller.innerHTML = '';

        const days = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
        const today = new Date();

        for (let i = 0; i < 7; i++) {
            const d = new Date();
            d.setDate(today.getDate() + bookingState.dayOffset + i);

            const dateStr = d.toISOString().split('T')[0];
            const isSelected = bookingState.selectedDate === dateStr;

            const btn = document.createElement('button');
            btn.type = 'button';
            btn.className = `hg-day-btn ${isSelected ? 'is-selected' : ''}`;
            btn.innerHTML = `
                <span class="hg-day-label">${days[d.getDay()]}</span>
                <span class="hg-day-num">${d.getDate()}</span>
            `;

            btn.addEventListener('click', () => {
                bookingState.selectedDate = dateStr;
                bookingState.selectedTime = null;
                renderDayScroller();
                fetchOccupiedSlotsAndRenderGrid(dateStr);
                updateSummary();
                updateNextButtonState();
            });

            dayScroller.appendChild(btn);

            if (i === 0 && !bookingState.selectedDate) {
                btn.click();
            }
        }

        updateDayNavButtons();
    }

    function setupDayNavButtons() {
        const prevBtn = document.getElementById('btn-prev-week');
        const nextBtn = document.getElementById('btn-next-week');

        if (prevBtn) {
            prevBtn.addEventListener('click', () => {
                bookingState.dayOffset = Math.max(0, bookingState.dayOffset - 7);
                renderDayScroller();
            });
        }
        if (nextBtn) {
            nextBtn.addEventListener('click', () => {
                bookingState.dayOffset = Math.min(MAX_DAY_OFFSET, bookingState.dayOffset + 7);
                renderDayScroller();
            });
        }
    }

    function updateDayNavButtons() {
        const prevBtn = document.getElementById('btn-prev-week');
        const nextBtn = document.getElementById('btn-next-week');
        if (prevBtn) prevBtn.disabled = bookingState.dayOffset <= 0;
        if (nextBtn) nextBtn.disabled = bookingState.dayOffset >= MAX_DAY_OFFSET;
    }

    async function fetchOccupiedSlotsAndRenderGrid(dateStr) {
        if (!timeSlotsGrid) return;
        timeSlotsGrid.innerHTML = '<div style="grid-column: 1/-1; text-align:center; padding:15px; color:#888;">Verificando horarios en el servidor...</div>';

        let busySlots = [];
        try {
            const response = await fetch(`/citas/api/disponibilidad?fecha=${dateStr}`, {
                headers: { 'Accept': 'application/json' }
            });
            if (response.ok) {
                busySlots = await response.json();
            }
        } catch (err) {
            console.error('Error consultando disponibilidad:', err);
        }

        renderTimeSlotsGrid(busySlots);
    }

    function renderTimeSlotsGrid(busySlots = []) {
        if (!timeSlotsGrid) return;
        timeSlotsGrid.innerHTML = '';

        ALL_SLOTS.forEach(time => {
            const isOccupied = busySlots.includes(time);
            const isSelected = bookingState.selectedTime === time;

            const btn = document.createElement('button');
            btn.type = 'button';
            btn.className = `hg-slot-btn ${isOccupied ? 'is-occupied' : 'is-available'} ${isSelected ? 'is-selected' : ''}`;
            btn.textContent = time;
            btn.disabled = isOccupied;

            if (!isOccupied) {
                btn.addEventListener('click', () => {
                    bookingState.selectedTime = time;
                    renderTimeSlotsGrid(busySlots);
                    updateSummary();
                    updateNextButtonState();
                });
            }

            timeSlotsGrid.appendChild(btn);
        });
    }

    // ==========================================
    // 9. RENDERIZADO DEL PASO 4 (CONFIRMACIÓN)
    // ==========================================
    function renderConfirmationStep() {
        const table = document.getElementById('confirm-receipt-table');
        if (!table) return;

        let rowsHtml = '';
        bookingState.selectedServices.forEach(s => {
            rowsHtml += `
                <div class="hg-receipt-row">
                    <span>${s.name} (${s.duration} min)</span>
                    <strong>$${s.price}</strong>
                </div>
            `;
        });

        rowsHtml += `
            <div class="hg-receipt-row">
                <span>Especialista asignado:</span>
                <strong>${bookingState.selectedProfessional ? bookingState.selectedProfessional.name : 'No asignado'}</strong>
            </div>
            <div class="hg-receipt-row">
                <span>Fecha y Horario:</span>
                <strong>${bookingState.selectedDate} a las ${bookingState.selectedTime} hrs</strong>
            </div>
            <div class="hg-receipt-row">
                <span>Duración estimada total:</span>
                <strong>${bookingState.totalDuration} minutos</strong>
            </div>
            <div class="hg-receipt-row hg-receipt-row--total">
                <dt>Total a Pagar en Sucursal:</dt>
                <dd>$${bookingState.totalPrice}</dd>
            </div>
        `;

        table.innerHTML = rowsHtml;
    }

    // ==========================================
    // 10. ACTUALIZACIÓN DEL RESUMEN LATERAL
    // ==========================================
    function updateSummary() {
        const hasServices = bookingState.selectedServices.length > 0;

        if (summaryEmptyMsg) summaryEmptyMsg.hidden = hasServices;
        if (summaryList) summaryList.hidden = !hasServices;
        if (summaryMeta) summaryMeta.hidden = !hasServices;
        if (summaryTotalBlock) summaryTotalBlock.hidden = !hasServices;

        if (hasServices && summaryList) {
            summaryList.innerHTML = bookingState.selectedServices
                .map(s => `<li><span>${s.name}</span><strong>$${s.price}</strong></li>`)
                .join('');
        }

        if (sumPro) sumPro.textContent = bookingState.selectedProfessional ? bookingState.selectedProfessional.name : '—';
        if (sumDate) sumDate.textContent = bookingState.selectedDate || '—';
        if (sumTime) sumTime.textContent = bookingState.selectedTime ? `${bookingState.selectedTime} hrs` : '—';
        if (sumDuration) sumDuration.textContent = `${bookingState.totalDuration} min`;
        if (sumTotal) sumTotal.textContent = `$${bookingState.totalPrice}`;
    }

    // ==========================================
    // 11. VALIDACIÓN Y TRANSICIÓN DE PASOS
    // ==========================================
    function updateNextButtonState() {
        if (!btnNext) return;

        let canProceed = false;
        let nextLabel = 'Siguiente';

        switch (bookingState.currentStep) {
            case 1:
                canProceed = bookingState.selectedServices.length > 0 &&
                    bookingState.selectedServices.length <= bookingState.maxServices;
                nextLabel = 'Siguiente: Profesional →';
                break;
            case 2:
                canProceed = bookingState.selectedProfessional !== null;
                nextLabel = 'Siguiente: Fecha y Hora →';
                break;
            case 3:
                canProceed = bookingState.selectedDate !== null && bookingState.selectedTime !== null;
                nextLabel = 'Siguiente: Confirmar →';
                break;
            case 4:
                canProceed = true;
                nextLabel = '🔒 Confirmar y Bloquear Cita';
                break;
        }

        btnNext.disabled = !canProceed;
        btnNext.textContent = nextLabel;
    }

    function setupNavigationButtons() {
        if (btnNext) {
            btnNext.addEventListener('click', () => {
                if (bookingState.currentStep < 4) {
                    goToStep(bookingState.currentStep + 1);
                } else if (bookingState.currentStep === 4) {
                    submitBookingToJavaBackend();
                }
            });
        }

        if (btnBack) {
            btnBack.addEventListener('click', () => {
                if (bookingState.currentStep > 1) {
                    goToStep(bookingState.currentStep - 1);
                }
            });
        }

        stepIndicators.forEach(ind => {
            ind.addEventListener('click', () => {
                const target = parseInt(ind.dataset.stepIndicator, 10);
                if (target < bookingState.currentStep) {
                    goToStep(target);
                }
            });
        });

        const restartBtn = document.getElementById('btn-restart-flow');
        if (restartBtn) {
            restartBtn.addEventListener('click', () => {
                bookingState.selectedServices = [];
                bookingState.selectedProfessional = null;
                bookingState.selectedTime = null;
                bookingState.totalPrice = 0;
                bookingState.totalDuration = 0;
                renderServices('all');
                renderProfessionals();
                goToStep(1);
            });
        }
    }

    function goToStep(stepNumber) {
        bookingState.currentStep = stepNumber;

        Object.values(stepPanels).forEach(p => p && p.classList.remove('is-active'));
        if (stepPanels[stepNumber]) {
            stepPanels[stepNumber].classList.add('is-active');
        }

        stepIndicators.forEach(ind => {
            const num = parseInt(ind.dataset.stepIndicator, 10);
            ind.classList.toggle('is-active', num === stepNumber);
            ind.classList.toggle('is-done', num < stepNumber);
        });

        if (stepEyebrow) {
            stepEyebrow.textContent = `Paso ${stepNumber} de 4`;
        }

        if (btnBack) {
            btnBack.hidden = stepNumber === 1;
        }

        if (stepNumber === 4) {
            renderConfirmationStep();
        }

        updateNextButtonState();
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    // ==========================================
    // 12. ENVÍO FINAL A JAVA SPRING MVC
    // ==========================================
    async function submitBookingToJavaBackend() {
        btnNext.disabled = true;
        btnNext.textContent = 'Bloqueando cita en Base de Datos...';

        const authUserId = document.getElementById('auth-user-id')?.value || null;
        const notesInput = document.getElementById('booking-notes')?.value || '';

        const payload = {
            usuarioId: authUserId ? parseInt(authUserId, 10) : null,
            profesionalId: bookingState.selectedProfessional.id,
            fecha: bookingState.selectedDate,
            horaInicio: bookingState.selectedTime,
            serviciosIds: bookingState.selectedServices.map(s => s.id),
            precioTotal: bookingState.totalPrice,
            duracionTotalMinutos: bookingState.totalDuration,
            notas: notesInput
        };

        try {
            const response = await fetch('/citas/api/reservar', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            const resData = await response.json().catch(() => ({}));
            showSuccessScreen(resData.codigoCita || 'HG-2026-0000');
        } catch (error) {
            console.error('Error al enviar la reserva:', error);
            btnNext.disabled = false;
            btnNext.textContent = '🔒 Confirmar y Bloquear Cita';
            alert('No se pudo procesar la reserva. Intenta de nuevo.');
        }
    }

    function showSuccessScreen(code) {
        Object.values(stepPanels).forEach(p => p && p.classList.remove('is-active'));
        if (stepPanels.success) {
            stepPanels.success.classList.add('is-active');
        }
        const codeBox = document.getElementById('success-booking-code');
        if (codeBox) codeBox.textContent = code;

        if (footerActions) footerActions.hidden = true;
        if (stepEyebrow) stepEyebrow.textContent = '¡Completado con éxito!';
    }

});