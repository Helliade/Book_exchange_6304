import { authFetch, showNotification, refreshTokens, redirectToAuth, setAuthHeader, showSuccess, logout, setupLogoutButton } from './utils.js';

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
    setupLogoutButton();

    // Установите токен в заголовки по умолчанию
    setAuthHeader(accessToken);

    // Загрузка заказов
    try {
        await loadOrders(accessToken);
    } catch (error) {
        console.error('Failed to load orders:', error);
        showNotification('Не удалось загрузить историю заказов. Попробуйте позже.', 'error');
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
            if (!response.ok) throw new Error('Filter request failed');

            const orders = await response.json();
            renderOrders(orders);

            if (orders.length === 0) {
                showNotification('Заказы по заданным фильтрам не найдены', 'info');
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
        const response = await authFetch('/api/users/search', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
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
                    const newResponse = authFetch('/api/users/search');
                    if (!newResponse.ok) throw new Error(`HTTP error! status: ${newResponse.status}`);
                    const orders = await newResponse.json();
                    renderOrders(orders);
                    return; // Завершаем выполнение после успешного обновления
                } else {
                    redirectToAuth();
                    return Promise.reject('Failed to refresh token');
                }
            } catch (refreshError) {
                redirectToAuth();
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
        showNotification('Не удалось загрузить заказы. Попробуйте позже.', 'error');
        renderOrders([]);
    }
}

function renderOrders(orders) {
    const container = document.getElementById('orders-container');
    const emptyMessage = document.getElementById('empty-orders');   //??????????????

    container.innerHTML = '';
    if (emptyMessage) emptyMessage.style.display = 'none'; // Проверка на существование

    if (!orders || orders.length === 0) {
        showNotification('Заказы по заданным фильтрам не найдены', 'info');
        return;
    }


    const tbody = container.querySelector('tbody') || container;
    orders.forEach((order, index) => {
        const row = document.createElement('tr');
        
        // Форматирование списка книг
        const booksList = order.books.map(book => {
            const bookYear = book.yearOfPubl || 'не указан';
            const publisher = book.publHouse || 'не указано';

            const worksList = (book.works || [])
                .map(w => `<li>${w.name}${w.writer ? `, ${w.writer}` : ''}</li>`)
                .join('');

            return `
                <div class="book-card">
                    <div class="book-meta">
                        <div><strong>Год издания:</strong> ${bookYear}</div>
                        <div><strong>Издательство:</strong> ${publisher}</div>
                    </div>
                    ${book.works?.length ? `
                    <div class="book-works">
                        <strong>Произведения:</strong>
                        <ul>${worksList}</ul>
                    </div>
                    ` : ''}
                </div>
            `;
        }).join('<hr class="book-divider">');
        
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
                await updateOrderStatus(orderId);
                await loadOrders();
                showSuccess('Заказ успешно отменен');
            } catch (error) {
                showNotification('Не удалось отменить заказ', 'error');
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

async function updateOrderStatus(orderId) {
    const response = await authFetch(`/api/orders/${orderId}/status?status=CANCELLED`, {
        method: 'PATCH'
    });

    if (!response.ok) {
        throw new Error('Failed to update order status');
    }
}