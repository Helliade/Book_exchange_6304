document.addEventListener('DOMContentLoaded', async () => {
    // Извлекаем токен из URL и сохраняем в localStorage
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    if (token) {
        localStorage.setItem('accessToken', token);
        // Удаляем токен из URL
        window.history.replaceState({}, document.title, "/history");
    }

    // 1. Проверяем авторизацию при загрузке страницы
    let accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');

    if (!accessToken || !refreshToken) {
        window.location.href = '/auth';
        return;
    }

    // Установите токен в заголовки по умолчанию
    setAuthHeader(accessToken);

    // Загрузка заказов
    try {
        await loadOrders(accessToken);
    } catch (error) {
        console.error('Failed to load orders:', error);
        showError('Не удалось загрузить историю заказов. Попробуйте позже.');
    }

    // Добавляем обработчики для кнопок фильтров
    document.querySelector('.apply-filters')?.addEventListener('click', async () => {
        const filters = {
            status: document.querySelector('.status-select').value,
            type: document.querySelector('.type-select').value
        };

        try {
            const queryString = new URLSearchParams();
            for (const [key, value] of Object.entries(filters)) {
                if (value) queryString.append(key, value);
            }

            const response = await authFetch(`/api/users/search?${queryString}`);
            if (response.ok) {
                const orders = await response.json();
                renderOrders(orders);
            }
        } catch (error) {
            console.error('Filter error:', error);
        }
    });

    document.querySelector('.reset-filters')?.addEventListener('click', () => {
        document.querySelector('.status-select').value = '';
        document.querySelector('.type-select').value = '';
        loadOrders(localStorage.getItem('accessToken'));
    });

    document.querySelector('.logout-btn').addEventListener('click', () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/auth.html';
    });
});

async function loadOrders(token) {
    try {
        const response = await fetch('/api/users/search', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
                        'Content-Type': 'application/json'
                    }
                });

        if (response.status === 403) {
            throw new Error('Доступ запрещен. Недостаточно прав.');
        }

        // Если токен устарел
        if (response.status === 401) {
            try {
                const newTokens = await refreshToken();
                if (newTokens) {
                    const newResponse = await fetch('/api/users/search', {
                        method: 'GET',
                        headers: {
                            'Authorization': `Bearer ${newTokens.accessToken}`,
                            'Content-Type': 'application/json'
                        }
                    });
                    if (!newResponse.ok) throw new Error(`HTTP error! status: ${newResponse.status}`);
                        const orders = await newResponse.json();
                        renderOrders(orders);
                        return; // Завершаем выполнение после успешного обновления
                } else {
                    redirectToLogin();
                    return Promise.reject('Failed to refresh token');
                }
            } catch (refreshError) {
                redirectToLogin();
                return Promise.reject(refreshError);
            }
        }

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const orders = await response.json();
        renderOrders(orders);
    } catch (error) {
        console.error('Error loading orders:', error);
        showError('Не удалось загрузить заказы. Попробуйте позже.');
        renderOrders([]);
    }
}

async function authFetch(url, options = {}) {
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
                const newTokens = await refreshToken();

                if (newTokens) {
                    // Обновляем токены в localStorage
                    localStorage.setItem('accessToken', newTokens.accessToken);
                    localStorage.setItem('refreshToken', newTokens.refreshToken);

                    // Повторяем запрос с новыми токенами
                    options.headers['Authorization'] = `Bearer ${newTokens.accessToken}`;
                    options.headers['RefreshToken'] = newTokens.refreshToken;

                    return fetch(url, options);
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

        return response;
    } catch (error) {
        console.error('Request failed:', error);
        throw error;
    }
}

async function refreshToken() {
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
        const newAccessToken = data.access_token || data.accessToken;
        const newRefreshToken = data.refresh_token || data.refreshToken;

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

function renderOrders(orders) {
    const container = document.getElementById('orders-container');
    const emptyMessage = document.getElementById('empty-orders');   //??????????????

    container.innerHTML = '';
    if (emptyMessage) emptyMessage.style.display = 'none'; // Проверка на существование

    if (!orders || orders.length === 0) {
        container.innerHTML = '<p class="text-muted">Заказы не найдены</p>';
        return;
    }


    const tbody = container.querySelector('tbody') || container;
    orders.forEach((order, index) => {
        const row = document.createElement('tr');
        
        // Список книг
        const booksList = order.books.map(book => {
            const bookYear = book.yearOfPubl || 'Год не указан';
            const publisher = book.publHouse || 'Не указано';
            const statusClass = (book.status || 'unknown').toLowerCase();
            const worksList = (book.works || [])
                .map(w => `<li>${w.name}, ${w.writer}</li>`)
                .join('');
            
            return `<div class="book-info">
                <div class="book-year">${bookYear}</div>
                <div class="book-publ">${publisher}</div>
                <div class="book-status">${statusClass}</div>
                <div class="book-works">${worksList}</div>
            </div>`;
        }).join('');
        
        // Кнопка "Отменить" для незавершенных заказов ?????????????????????????????? \/
        const cancelButton = (order.status !== 'COMPLETED' && order.status !== 'CANCELLED') 
            ? `<button class="btn btn-outline-danger btn-sm cancel-order" data-order-id="${order.id}">
                Отменить
               </button>`
            : '';

        row.innerHTML = `
            <td>${index + 1}</td>
            <td><span class="type-badge">${order.type === 'TAKE' ? 'Получение' : 'Отправка'}</span></td>
            <td>
                <span class="status-badge status-${order.status}">${getStatusText(order.status)}</span>
                ${cancelButton}
            </td>
            <td>${booksList}</td>
        `;
        
        container.appendChild(row);
    });
    
    // Обработчики для кнопок отмены
    document.querySelectorAll('.cancel-order').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const orderId = e.currentTarget.dataset.orderId;
            try {
                await updateOrderStatus(orderId, 'CANCELLED');
                await loadOrders();
                showSuccess('Заказ успешно отменен');
            } catch (error) {
                showError('Не удалось отменить заказ');
            }
        });
    });
}

function getStatusText(status) {
    const statusMap = {
        'CREATED': 'Создан',
        'PROCESSING': 'В обработке',
        'COMPLETED': 'Завершен',
        'CANCELLED': 'Отменен'
    };
    return statusMap[status] || status;
}

async function updateOrderStatus(orderId, status) {
    const response = await authFetch(`/api/orders/${orderId}/status`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ status })
    });

    if (!response.ok) {
        throw new Error('Failed to update order status');
    }
}

function redirectToAuth() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    window.location.href = '/auth';
}

function showError(message) {
    const alert = document.createElement('div');
    alert.className = 'alert alert-danger position-fixed top-0 end-0 m-3';
    alert.textContent = message;
    document.body.appendChild(alert);

    setTimeout(() => alert.remove(), 5000);
}

function showSuccess(message) {
    const alert = document.createElement('div');
    alert.className = 'alert alert-success position-fixed top-0 end-0 m-3';
    alert.textContent = message;
    document.body.appendChild(alert);

    setTimeout(() => alert.remove(), 5000);
}

function setAuthHeader(token) {
    const originalFetch = window.fetch;
    window.fetch = async (url, options = {}) => {
        options.headers = options.headers || {};
        if (!options.headers['Authorization'] && token) {
            options.headers['Authorization'] = `Bearer ${token}`;
        }
        return originalFetch(url, options);
    };
}

function redirectToLogin() {
    window.location.href = '/auth'; // или ваш URL для страницы входа
}