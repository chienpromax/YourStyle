(function () {
    'use strict';
    const forms = document.querySelectorAll('form');
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }

            const usernameField = form.querySelector('#username');
            const passwordField = form.querySelector('#password');

            if (usernameField && !usernameField.validity.valid) {
                usernameField.setCustomValidity('Tên người dùng không hợp lệ.');
            } else {
                usernameField.setCustomValidity('');
            }

            if (passwordField) {
                if (passwordField.value.length < 6) {
                    passwordField.setCustomValidity('Mật khẩu phải có ít nhất 6 ký tự.');
                } else {
                    passwordField.setCustomValidity('');
                }
            }

            form.classList.add('was-validated');
        }, false);
    });
})();