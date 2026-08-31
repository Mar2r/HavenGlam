document.addEventListener('DOMContentLoaded', function () {

    // Toggle del sidebar en móvil
    const sidebarToggleBtn = document.getElementById('sidebarToggleBtn');
    const adminSidebar = document.getElementById('adminSidebar');

    if (sidebarToggleBtn && adminSidebar) {
        sidebarToggleBtn.addEventListener('click', function () {
            const isOpen = adminSidebar.classList.toggle('is-open');
            sidebarToggleBtn.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
        });

        document.addEventListener('click', function (event) {
            const clickedInsideSidebar = adminSidebar.contains(event.target);
            const clickedToggle = sidebarToggleBtn.contains(event.target);
            if (!clickedInsideSidebar && !clickedToggle) {
                adminSidebar.classList.remove('is-open');
                sidebarToggleBtn.setAttribute('aria-expanded', 'false');
            }
        });
    }

    // Menú de perfil (dropdown)
    const profileTrigger = document.getElementById('profileTrigger');
    const topbarProfile = profileTrigger ? profileTrigger.closest('.topbar-profile') : null;

    if (profileTrigger && topbarProfile) {
        profileTrigger.addEventListener('click', function (event) {
            event.stopPropagation();
            const isOpen = topbarProfile.classList.toggle('is-open');
            profileTrigger.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
        });

        document.addEventListener('click', function (event) {
            if (!topbarProfile.contains(event.target)) {
                topbarProfile.classList.remove('is-open');
                profileTrigger.setAttribute('aria-expanded', 'false');
            }
        });
    }
});