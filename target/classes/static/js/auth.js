// auth.js
document.addEventListener('DOMContentLoaded', () => {
    // Регистрация
    document.getElementById('register-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                credentials: 'include', // Важно для куков
                body: JSON.stringify({
                    login: e.target.login.value,
                    rawPassword: e.target.rawPassword.value
                })
            });

            const data = await response.json();

            if (response.ok) {
                showAlert('Регистрация прошла успешно!');
                e.target.reset();
            } else {
                showAlert(`Ошибка: ${data.message || response.statusText}`, false);
            }
        } catch (error) {
            showAlert('Сетевая ошибка: ' + error.message, false);
        }
    });

    // Логин
    document.getElementById('login-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                credentials: 'include', // Важно для куков и аутентификации
                body: JSON.stringify({
                    login: e.target.login.value,
                    rawPassword: e.target.rawPassword.value
                })
            });

            const data = await response.json();

            if (response.ok) {
                // Унифицируем названия полей токенов
                const accessToken = data.accessToken || data.access_token;
                const refreshToken = data.refreshToken || data.refresh_token;

                if (!accessToken || !refreshToken) {
                    throw new Error('Не получили токены от сервера');
                }

                // Сохраняем токены и данные пользователя
                localStorage.setItem('accessToken', accessToken);
                localStorage.setItem('refreshToken', refreshToken);
                localStorage.setItem('role', data.role);

                // Для дополнительной безопасности можно использовать sessionStorage
                sessionStorage.setItem('accessToken', accessToken);
                sessionStorage.setItem('isAuthenticated', 'true');

                // Устанавливаем токен в заголовки по умолчанию
                setAuthHeader(data.accessToken);

                window.location.href = '/home?token=' + encodeURIComponent(accessToken);
            } else {
                showAlert(`Ошибка: ${data.message || response.statusText}`, false);
            }
        } catch (error) {
            showAlert('Сетевая ошибка: ' + error.message, false);
        }
    });

    // Функция для установки заголовка авторизации
    function setAuthHeader(token) {
        // Устанавливаем заголовок для всех последующих fetch-запросов
        const _fetch = window.fetch;
        window.fetch = function(url, options = {}) {
            options.headers = options.headers || {};
            if (token && !options.headers['Authorization']) {
                options.headers['Authorization'] = `Bearer ${token}`;
            }
            options.credentials = 'include'; // Всегда включаем credentials
            return _fetch(url, options);
        };
    }

    // Проверяем наличие токена при загрузке страницы
    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
        setAuthHeader(accessToken);
    }

    // Универсальная функция для показа уведомлений
    function showAlert(message, isSuccess = true) {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert ${isSuccess ? 'alert-success' : 'alert-error'}`;
        alertDiv.textContent = message;
        document.body.prepend(alertDiv);
        setTimeout(() => alertDiv.remove(), 3000);
    }
});
