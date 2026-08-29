document.addEventListener("DOMContentLoaded", () => {
    const siteHeader = document.getElementById("siteHeader");

    const updateHeaderScroll = () => {
        if (window.scrollY > 20) {
            siteHeader?.classList.add("scrolled");
        } else {
            siteHeader?.classList.remove("scrolled");
        }
    };

    window.addEventListener("scroll", updateHeaderScroll, { passive: true });
    updateHeaderScroll();

    const navToggleBtn = document.getElementById("navToggleBtn");
    const mobileNavPanel = document.getElementById("mobileNavPanel");
    const mobileNavLinks = mobileNavPanel?.querySelectorAll(".nav-link");

    if (navToggleBtn && mobileNavPanel) {
        navToggleBtn.addEventListener("click", (e) => {
            e.stopPropagation();
            const isOpen = mobileNavPanel.classList.toggle("open");
            navToggleBtn.setAttribute("aria-expanded", String(isOpen));
        });

        mobileNavLinks?.forEach((link) => {
            link.addEventListener("click", () => {
                mobileNavPanel.classList.remove("open");
                navToggleBtn.setAttribute("aria-expanded", "false");
            });
        });

        document.addEventListener("click", (e) => {
            if (!mobileNavPanel.contains(e.target) && !navToggleBtn.contains(e.target)) {
                mobileNavPanel.classList.remove("open");
                navToggleBtn.setAttribute("aria-expanded", "false");
            }
        });
    }

    const currentPath = window.location.pathname;
    const currentHash = window.location.hash;
    const navLinks = document.querySelectorAll(".nav-link");

    navLinks.forEach((link) => {
        const href = link.getAttribute("href");
        if (!href) return;
        if (href === currentPath || (currentHash && href.endsWith(currentHash))) {
            link.classList.add("active");
        }
    });
});