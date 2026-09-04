document.addEventListener('DOMContentLoaded', function () {

    // Toggle del sidebar en móvil
    const sidebarToggleBtn = document.getElementById('sidebarToggleBtn');
    const adminSidebar = document.getElementById('adminSidebar');

    if (sidebarToggleBtn && adminSidebar) {
        sidebarToggleBtn.addEventListener('click', function () {
            adminSidebar.classList.toggle('is-open');
        });

        document.addEventListener('click', function (event) {
            const clickedInsideSidebar = adminSidebar.contains(event.target);
            const clickedToggle = sidebarToggleBtn.contains(event.target);
            if (!clickedInsideSidebar && !clickedToggle) {
                adminSidebar.classList.remove('is-open');
            }
        });
    }

    // Menú de perfil (dropdown)
    const profileTrigger = document.getElementById('profileTrigger');
    const topbarProfile = document.getElementById('topbarProfile');

    if (profileTrigger && topbarProfile) {
        profileTrigger.addEventListener('click', function (event) {
            event.stopPropagation();
            topbarProfile.classList.toggle('is-open');
        });

        document.addEventListener('click', function (event) {
            if (!topbarProfile.contains(event.target)) {
                topbarProfile.classList.remove('is-open');
            }
        });
    }
});