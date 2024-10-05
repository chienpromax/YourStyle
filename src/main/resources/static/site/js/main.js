
let timeout;

const dropdown = document.querySelector(".nav-item.dropdown");
const dropdownMenu = dropdown.querySelector(".dropdown-menu");

dropdown.addEventListener("mouseenter", function () {
  clearTimeout(timeout);
  dropdownMenu.style.display = "block";
});

dropdown.addEventListener("mouseleave", function () {
  timeout = setTimeout(function () {
    dropdownMenu.style.display = "none";
  }, 200);
});

dropdownMenu.addEventListener("mouseenter", function () {
  clearTimeout(timeout);
});

dropdownMenu.addEventListener("mouseleave", function () {
  timeout = setTimeout(function () {
    dropdownMenu.style.display = "none";
  }, 200);
});


document.addEventListener("DOMContentLoaded", function() {
  const dropdowns = document.querySelectorAll('.nav-item.dropdown');

  dropdowns.forEach(function(dropdown) {
      const link = dropdown.querySelector('.nav-link');
      const menu = dropdown.querySelector('.dropdown-menu');
      let hideTimeout;

      link.addEventListener('mouseenter', function() {
          clearTimeout(hideTimeout);
          menu.style.opacity = '1';
          menu.style.visibility = 'visible';
          menu.style.transform = 'translateY(0)';
      });

      dropdown.addEventListener('mouseleave', function() {
          // Thêm delay trước khi ẩn menu
          hideTimeout = setTimeout(function() {
              menu.style.opacity = '0';
              menu.style.visibility = 'hidden';
              menu.style.transform = 'translateY(-10px)';
          }, 1000);
      });
  });
});
