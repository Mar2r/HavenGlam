document.addEventListener('DOMContentLoaded', function () {

    // Solo letras y espacios (con tildes y ñ)
    function soloLetras(input) {
        input.addEventListener('input', function () {
            this.value = this.value.replace(/[^\p{L}\s]/gu, '');
        });
    }

    // Solo números, máximo 8 dígitos
    function soloNumerosTelefono(input) {
        input.addEventListener('input', function () {
            let valor = this.value.replace(/[^0-9]/g, '');
            if (valor.length > 8) {
                valor = valor.substring(0, 8);
            }
            this.value = valor;
        });
    }

    // Solo números + guión automático antes del último dígito (formato DUI: 00000000-0)
    function formatoDui(input) {
        input.addEventListener('input', function () {
            let valor = this.value.replace(/[^0-9]/g, '');
            if (valor.length > 9) {
                valor = valor.substring(0, 9);
            }
            if (valor.length > 8) {
                valor = valor.substring(0, 8) + '-' + valor.substring(8);
            }
            this.value = valor;
        });
    }

    const nombre = document.getElementById('nombre');
    const apellido = document.getElementById('apellido');
    const telefono = document.getElementById('telefono');
    const dui = document.getElementById('dui');

    if (nombre) soloLetras(nombre);
    if (apellido) soloLetras(apellido);
    if (telefono) soloNumerosTelefono(telefono);
    if (dui) formatoDui(dui);
});