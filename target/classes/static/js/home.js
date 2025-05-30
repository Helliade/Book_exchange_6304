document.addEventListener('DOMContentLoaded', async () => {
    // Извлекаем токен из URL и сохраняем в localStorage
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');
    if (token) {
        localStorage.setItem('accessToken', token);
        // Удаляем токен из URL
        window.history.replaceState({}, document.title, "/home");
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

    // 2. Пробуем загрузить книги
    try {
        await loadBooks(accessToken);
    } catch (error) {
        console.error('Failed to load books:', error);
        showError('Не удалось загрузить книги. Попробуйте позже.');
    }

    // Добавляем обработчики для кнопок фильтров
    document.querySelector('.apply-filters')?.addEventListener('click', async () => {
        const filters = {
            search: document.querySelector('.search-input').value,
            yearOfPubl: document.querySelector('.year-input').value,
            publHouse: document.querySelector('.publisher-input').value,
            language: document.querySelector('.language-input').value,
            condit: document.querySelector('.condition-select').value
        };

        try {
            const queryString = new URLSearchParams();
            for (const [key, value] of Object.entries(filters)) {
                if (value) queryString.append(key, value);
            }

            const response = await authFetch(`/api/books/search?${queryString}`);
            if (response.ok) {
                const books = await response.json();
                renderBooks(books);
            }
        } catch (error) {
            console.error('Filter error:', error);
        }
    });

    document.querySelector('.reset-filters')?.addEventListener('click', () => {
        document.querySelector('.search-input').value = '';
        document.querySelector('.year-input').value = '';
        document.querySelector('.publisher-input').value = '';
        document.querySelector('.language-input').value = '';
        document.querySelector('.condition-select').value = '';
        loadBooks(localStorage.getItem('accessToken'));
    });

    document.querySelector('.logout-btn')?.addEventListener('click', () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/auth.html';
    });
});

async function loadBooks(token) {
    try {
        const response = await authFetch('/api/books', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        });

        if (response.status === 403) {
            throw new Error('Доступ запрещен. Недостаточно прав.');
        }

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const books = await response.json();
        renderBooks(books);
    } catch (error) {
        console.error('Error loading books:', error);
        showError('Не удалось загрузить книги. Попробуйте позже.');
        throw error;
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

function renderBooks(books) {
    const container = document.querySelector('.books-container');
    if (!container) return;

    container.innerHTML = '';

    if (!books || books.length === 0) {
        container.innerHTML = '<p class="text-muted">Книги не найдены</p>';
        return;
    }

    books.forEach(book => {
        const firstWork = book.works && book.works.length > 0 ? [...book.works][0] : null;
        const workName = firstWork ? firstWork.name : 'Без названия';
        const workWriter = firstWork ? firstWork.writer : 'Автор не указан';
        const bookYear = book.yearOfPubl || 'Год не указан';
        const publisher = book.publHouse || 'Не указано';
        const bookStatus = book.status || 'Не указано';
        // приводим к нижнему регистру, чтобы в CSS классы были простые
        const statusClass = (book.status || 'unknown').toLowerCase();
        const worksList = (book.works || [])
            .map(w => `<li>${w.name}, ${w.writer}</li>`)
            .join('');

        const bookCard = document.createElement('div');
        bookCard.className = 'col-md-4 mb-4';
        bookCard.innerHTML = `
            <div class="wrapper">
                <div class="container">
                    <div class="top" style="background: url('/images/placeholder.jpg') no-repeat center center; background-size: cover;"></div>
                    <div class="bottom">
                        <div class="left">
                            <div class="details">
                                <h1>${publisher}</h1>
                                <p>${bookYear}</p>
                                <p>
                                    <span class="status-pill status-${statusClass}">
                                     ${bookStatus}
                                    </span>
                                </p>
                            </div>
                            <div class="buy add-to-cart" data-book-id="${book.id}">
                                <i class="material-icons">add_shopping_cart</i>
                            </div>
                        </div>
                        <div class="right">
                            <div class="done"><i class="material-icons">done</i></div>
                            <div class="details">
                                <h1>${workName}</h1>
                                <p>Добавлено в корзину</p>
                            </div>
                            <div class="remove"><i class="material-icons">clear</i></div>
                        </div>
                    </div>
                </div>
                <div class="inside">
                    <div class="icon"><i class="material-icons">info_outline</i></div>
                    <div class="contents">
                        <table>
                            <tr>
                                <th>Язык</th>
                                <td>${book.language || 'Не указан'}</td>
                            </tr>
                            <tr>
                                <th>Состояние</th>
                                <td>${book.condit || 'Не указано'}</td>
                            </tr>
                        </table>
                        <hr class="border-light">
                        <table><tr><th>Произведения:</th></tr></table>
                        <ul class="mb-0 ps-3">
                         ${worksList || '<li>—</li>'}
                         </ul>
                    </div>
                </div>
            </div>
        `;
        container.appendChild(bookCard);
    });

    // Обработчики событий
    document.querySelectorAll('.add-to-cart').forEach(button => {
        button.addEventListener('click', async (e) => {
            const bookId = e.currentTarget.dataset.bookId;
            const wrapper = e.currentTarget.closest('.wrapper');
            const bottom = wrapper.querySelector('.bottom');

            try {
                await addToCart(bookId);
                bottom.classList.add('clicked');

                // Возвращаем в исходное состояние через 3 секунды
                setTimeout(() => {
                    bottom.classList.remove('clicked');
                }, 3000);
            } catch (error) {
                showError('Не удалось добавить книгу в корзину');
            }
        });
    });
}

async function addToCart(bookId) {
    const response = await authFetch(`/api/orders/addBook`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ bookId })
    });

    if (!response.ok) {
        throw new Error('Failed to add to cart');
    }
}

function redirectToAuth() {
    // Очищаем токены и редиректим
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