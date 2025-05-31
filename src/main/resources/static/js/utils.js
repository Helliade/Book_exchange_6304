export async function authFetch(url, options = {}) {
    const token = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');

    if (!token || !refreshToken) {
        redirectToAuth();
        return Promise.reject('No tokens available');
    }

    // Устанавливаем заголовки авторизации
    options.headers = {
        ...options.headers,
        'Authorization': `Bearer ${token}`,
        'RefreshToken': refreshToken,
        'Content-Type': 'application/json'
    };

    try {
        let response = await fetch(url, options);

        // Если токен устарел (401), пробуем обновить
        if (response.status === 401) {
            console.log('Access token expired, trying to refresh...');

            try {
                const newTokens = await refreshTokens();

                if (newTokens) {
                    // Обновляем токены в localStorage
                    localStorage.setItem('accessToken', newTokens.accessToken);
                    localStorage.setItem('refreshToken', newTokens.refreshToken);

                    // Повторяем запрос с новыми токенами
                    options.headers['Authorization'] = `Bearer ${newTokens.accessToken}`;
                    options.headers['RefreshToken'] = newTokens.refreshToken;

                    response = await fetch(url, options);

                    // Если после обновления токена снова 401 - перенаправляем на авторизацию
                    if (response.status === 401) {
                        redirectToAuth();
                        return Promise.reject('Failed to refresh token');
                    }

                    return response;
                } else {
                    // Не удалось обновить токен - перенаправляем на авторизацию
                    redirectToAuth();
                    return Promise.reject('Failed to refresh token');
                }
            } catch (refreshError) {
                console.error('Token refresh failed:', refreshError);
                redirectToAuth();
                return Promise.reject(refreshError);
            }
        }
        // Если новый accessToken пришёл в заголовке
        const newAccessTokenFromHeader = response.headers.get('accessToken');
        if (newAccessTokenFromHeader) {
            localStorage.setItem('accessToken', newAccessTokenFromHeader);
        }

        return response;
    } catch (error) {
        console.error('Request failed:', error);
        throw error;
    }
}

export async function refreshTokens() {
    try {
        const refreshToken = localStorage.getItem('refreshToken');
        const accessToken = localStorage.getItem('accessToken');

        const response = await fetch('/api/auth/refresh', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${accessToken}`,
                'RefreshToken': refreshToken,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Refresh failed');
        }

        const data = await response.json();
        const newAccessToken = data.access_token || data.accessToken || data.token;
        const newRefreshToken = data.refresh_token || data.refreshToken;

        // Сохраняем новые токены
        localStorage.setItem('accessToken', newAccessToken);
        localStorage.setItem('refreshToken', newRefreshToken);

        return {
            accessToken: newAccessToken,
            refreshToken: newRefreshToken
        };
    } catch (error) {
        console.error('Token refresh failed:', error);
        redirectToAuth();
        return null;
    }
}

export function redirectToAuth() {
    // Очищаем токены и редиректим
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    window.location.href = '/auth';
}

export function setAuthHeader(token) {
    const originalFetch = window.fetch;
    window.fetch = async (url, options = {}) => {
        options.headers = options.headers || {};
        if (!options.headers['Authorization'] && token) {
            options.headers['Authorization'] = `Bearer ${token}`;
        }
        return originalFetch(url, options);
    };
}

export function showNotification(message, type) {
    const alert = document.createElement('div');
    alert.className = `alert alert-${type} position-fixed top-0 end-0 m-3`;
    alert.textContent = message;
    document.body.appendChild(alert);

    setTimeout(() => alert.remove(), 5000);
}

export function showSuccess(message) {
    const alert = document.createElement('div');
    alert.className = 'alert alert-success position-fixed top-0 end-0 m-3';
    alert.textContent = message;
    document.body.appendChild(alert);

    setTimeout(() => alert.remove(), 5000);
}

export async function logout() {
    const accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');

    try {
        // Отправляем запрос на сервер для выхода
        const response = await fetch('/api/auth/logout', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${accessToken}`,
                'RefreshToken': refreshToken
            }
        });

        if (!response.ok) {
            console.error('Logout failed:', await response.text());
        }
    } catch (error) {
        console.error('Logout error:', error);
    } finally {
        // В любом случае очищаем localStorage и перенаправляем
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/hello';
    }
}

// Добавим также функцию для инициализации обработчика логаута
export function setupLogoutButton(selector = '.logout-btn') {
    document.querySelector(selector)?.addEventListener('click', async (e) => {
        e.preventDefault();
        await logout();
    });
}