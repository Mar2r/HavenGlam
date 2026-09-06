document.addEventListener('DOMContentLoaded', function () {

    // ---------- Validación de los formularios de crear / editar ----------
    const form = document.querySelector('form.needs-validation');

    if (form) {
        const nombreInput = form.querySelector('#nombreCategoria');
        const estadoSelect = form.querySelector('#estado');
        const submitBtn = form.querySelector('button[type="submit"]');

        form.addEventListener('submit', function (event) {
            let valid = true;
            clearJsErrors(form);

            if (nombreInput) {
                const valor = nombreInput.value.trim();
                if (valor.length === 0) {
                    showJsError(nombreInput, 'El nombre de la categoría es obligatorio.');
                    valid = false;
                } else if (valor.length < 2 || valor.length > 60) {
                    showJsError(nombreInput, 'El nombre debe tener entre 2 y 60 caracteres.');
                    valid = false;
                }
            }

            if (estadoSelect && estadoSelect.value === '') {
                showJsError(estadoSelect, 'Debes seleccionar un estado.');
                valid = false;
            }

            if (!valid) {
                event.preventDefault();
                return;
            }

            // Evita doble envío mientras el servidor procesa la petición
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.textContent = 'Guardando…';
            }
        });

        // Limpia el error de un campo en cuanto el usuario corrige
        [nombreInput, estadoSelect].forEach(function (field) {
            if (!field) return;
            field.addEventListener('input', function () {
                field.classList.remove('input-error');
                const group = field.closest('.admin-form-group');
                const err = group ? group.querySelector('.admin-error--js') : null;
                if (err) err.remove();
            });
        });
    }

    // ---------- Confirmación adicional al eliminar ----------
    const deleteForm = document.querySelector('form.admin-delete-form');
    if (deleteForm) {
        deleteForm.addEventListener('submit', function (event) {
            const confirmado = window.confirm('Esta acción eliminará la categoría de forma permanente. ¿Deseas continuar?');
            if (!confirmado) {
                event.preventDefault();
                return;
            }
            const btn = deleteForm.querySelector('button[type="submit"]');
            if (btn) {
                btn.disabled = true;
                btn.textContent = 'Eliminando…';
            }
        });
    }

    // ---------- Oculta el mensaje de éxito tras unos segundos ----------
    const successBox = document.querySelector('.admin-success');
    if (successBox) {
        setTimeout(function () {
            successBox.style.transition = 'opacity 0.4s ease';
            successBox.style.opacity = '0';
            setTimeout(function () { successBox.remove(); }, 500);
        }, 3500);
    }

    function showJsError(input, message) {
        input.classList.add('input-error');
        const group = input.closest('.admin-form-group');
        if (!group) return;
        const err = document.createElement('div');
        err.className = 'admin-error admin-error--js';
        err.textContent = message;
        group.appendChild(err);
    }

    function clearJsErrors(scope) {
        scope.querySelectorAll('.admin-error--js').forEach(function (el) { el.remove(); });
        scope.querySelectorAll('.input-error').forEach(function (el) { el.classList.remove('input-error'); });
    }
});