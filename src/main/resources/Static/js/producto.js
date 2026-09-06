/**
 * Haven Glam - producto.js
 * Validaciones dinámicas por tipo de campo y previsualización de imágenes para Productos
 */
document.addEventListener('DOMContentLoaded', function () {

    const form = document.querySelector('form.needs-validation');

    if (form) {
        const nombreInput = form.querySelector('#nombreProducto');
        const descripcionTextarea = form.querySelector('#descripcion');
        const precioInput = form.querySelector('#precio');
        const stockInput = form.querySelector('#stock');
        const categoriaSelect = form.querySelector('#categoria');
        const estadoSelect = form.querySelector('#estado');
        const fileInput = form.querySelector('#file');
        const submitBtn = form.querySelector('button[type="submit"]');

        // ---------- 1. Contador dinámico de caracteres para Descripción ----------
        if (descripcionTextarea) {
            const maxChars = 200;
            const counterSpan = document.createElement('span');
            counterSpan.className = 'char-counter';
            descripcionTextarea.parentNode.appendChild(counterSpan);

            function updateCharCounter() {
                const currentLength = descripcionTextarea.value.length;
                counterSpan.textContent = `${currentLength} / ${maxChars} caracteres`;
                if (currentLength >= maxChars) {
                    counterSpan.classList.add('limit-reached');
                } else {
                    counterSpan.classList.remove('limit-reached');
                }
            }

            updateCharCounter();
            descripcionTextarea.addEventListener('input', updateCharCounter);
        }

        // ---------- 2. Bloqueo de teclas no numéricas inválidas en Precio ----------
        if (precioInput) {
            precioInput.addEventListener('keydown', function (e) {
                // Bloquear 'e', 'E', '+', '-'
                if (['e', 'E', '+', '-'].includes(e.key)) {
                    e.preventDefault();
                }
            });
        }

        // ---------- 3. Bloqueo de decimales y negativos en Stock (solo enteros >= 0) ----------
        if (stockInput) {
            stockInput.addEventListener('keydown', function (e) {
                // Bloquear '.', ',', 'e', 'E', '+', '-'
                if (['.', ',', 'e', 'E', '+', '-'].includes(e.key)) {
                    e.preventDefault();
                }
            });
        }

        // ---------- 4. Previsualización y validación de archivo de Imagen ----------
        if (fileInput) {
            const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg', 'image/webp'];
            const maxSizeBytes = 5 * 1024 * 1024; // 5 MB

            let previewContainer = document.createElement('div');
            previewContainer.className = 'image-preview-container';
            previewContainer.style.display = 'none';

            const previewLabel = document.createElement('span');
            previewLabel.className = 'image-preview-label';
            previewLabel.textContent = 'Nueva imagen seleccionada:';

            const previewImg = document.createElement('img');
            previewImg.alt = 'Vista previa de imagen';

            previewContainer.appendChild(previewLabel);
            previewContainer.appendChild(previewImg);
            fileInput.parentNode.appendChild(previewContainer);

            fileInput.addEventListener('change', function () {
                clearFieldError(fileInput);
                const file = fileInput.files[0];

                if (!file) {
                    previewContainer.style.display = 'none';
                    return;
                }

                // Validar formato
                if (!allowedTypes.includes(file.type.toLowerCase())) {
                    showFieldError(fileInput, 'Formato inválido. Solo se admiten imágenes JPG, PNG o WEBP.');
                    fileInput.value = '';
                    previewContainer.style.display = 'none';
                    return;
                }

                // Validar tamaño
                if (file.size > maxSizeBytes) {
                    showFieldError(fileInput, 'La imagen supera el límite de 5 MB.');
                    fileInput.value = '';
                    previewContainer.style.display = 'none';
                    return;
                }

                // Previsualizar
                const reader = new FileReader();
                reader.onload = function (e) {
                    previewImg.src = e.target.result;
                    previewContainer.style.display = 'inline-flex';
                };
                reader.readAsDataURL(file);
            });
        }

        // ---------- 5. Limpieza de errores al interactuar con campos ----------
        const allFields = [nombreInput, descripcionTextarea, precioInput, stockInput, categoriaSelect, estadoSelect];
        allFields.forEach(function (field) {
            if (!field) return;
            const eventType = (field.tagName === 'SELECT') ? 'change' : 'input';
            field.addEventListener(eventType, function () {
                clearFieldError(field);
            });
        });

        // ---------- 6. Validación completa al enviar el formulario ----------
        form.addEventListener('submit', function (event) {
            let isValid = true;
            clearAllErrors(form);

            // Validar Nombre de Producto (3 a 50 caracteres)
            if (nombreInput) {
                const nombre = nombreInput.value.trim();
                if (nombre.length === 0) {
                    showFieldError(nombreInput, 'El nombre del producto es obligatorio.');
                    isValid = false;
                } else if (nombre.length < 3) {
                    showFieldError(nombreInput, 'El nombre debe tener al menos 3 caracteres.');
                    isValid = false;
                } else if (nombre.length > 50) {
                    showFieldError(nombreInput, 'El nombre no puede superar los 50 caracteres.');
                    isValid = false;
                }
            }

            // Validar Descripción (máx 200)
            if (descripcionTextarea && descripcionTextarea.value.length > 200) {
                showFieldError(descripcionTextarea, 'La descripción no puede superar los 200 caracteres.');
                isValid = false;
            }

            // Validar Precio (Decimal positivo > 0)
            if (precioInput) {
                const precioVal = parseFloat(precioInput.value);
                if (isNaN(precioVal) || precioInput.value.trim() === '') {
                    showFieldError(precioInput, 'El precio es obligatorio.');
                    isValid = false;
                } else if (precioVal <= 0) {
                    showFieldError(precioInput, 'El precio debe ser mayor a $0.00.');
                    isValid = false;
                } else if (precioVal > 99999.99) {
                    showFieldError(precioInput, 'El precio excede el límite permitido ($99,999.99).');
                    isValid = false;
                }
            }

            // Validar Stock (Entero >= 0)
            if (stockInput) {
                const stockValStr = stockInput.value.trim();
                const stockVal = parseInt(stockValStr, 10);
                if (stockValStr === '' || isNaN(stockVal)) {
                    showFieldError(stockInput, 'El stock es obligatorio.');
                    isValid = false;
                } else if (stockVal < 0) {
                    showFieldError(stockInput, 'El stock no puede ser negativo.');
                    isValid = false;
                } else if (!Number.isInteger(Number(stockValStr))) {
                    showFieldError(stockInput, 'El stock debe ser un número entero sin decimales.');
                    isValid = false;
                } else if (stockVal > 999999) {
                    showFieldError(stockInput, 'El stock no puede exceder 999,999 unidades.');
                    isValid = false;
                }
            }

            // Validar Categoría (Select obligatorio)
            if (categoriaSelect && categoriaSelect.value === '') {
                showFieldError(categoriaSelect, 'Debes seleccionar una categoría.');
                isValid = false;
            }

            // Validar Estado (Select obligatorio)
            if (estadoSelect && estadoSelect.value === '') {
                showFieldError(estadoSelect, 'Debes seleccionar un estado.');
                isValid = false;
            }

            if (!isValid) {
                event.preventDefault();
                const firstInvalid = form.querySelector('.is-invalid, .input-error');
                if (firstInvalid) {
                    firstInvalid.focus();
                }
                return;
            }

            // Prevenir doble envío
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.textContent = 'Guardando…';
            }
        });
    }

    // Funciones auxiliares de mensajes de error
    function showFieldError(input, message) {
        input.classList.add('is-invalid', 'input-error');
        const group = input.closest('.admin-form-group');
        if (!group) return;

        let err = group.querySelector('.admin-error--js');
        if (!err) {
            err = document.createElement('div');
            err.className = 'admin-error admin-error--js';
            group.appendChild(err);
        }
        err.textContent = message;
    }

    function clearFieldError(input) {
        input.classList.remove('is-invalid', 'input-error');
        const group = input.closest('.admin-form-group');
        if (!group) return;
        const err = group.querySelector('.admin-error--js');
        if (err) err.remove();
    }

    function clearAllErrors(scope) {
        scope.querySelectorAll('.admin-error--js').forEach(function (el) { el.remove(); });
        scope.querySelectorAll('.is-invalid, .input-error').forEach(function (el) {
            el.classList.remove('is-invalid', 'input-error');
        });
    }
});
