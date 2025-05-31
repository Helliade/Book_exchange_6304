import { authFetch, showNotification, refreshTokens, redirectToAuth, setAuthHeader, showSuccess, logout, setupLogoutButton } from './utils.js';

document.addEventListener('DOMContentLoaded', async () => {
    // Проверка авторизации
    const accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');

    if (!accessToken || !refreshToken) {
        window.location.href = '/auth';
        return;
    }

    setupLogoutButton();
    setAuthHeader(accessToken);

    // Получаем элементы управления
    const orderTypeBtns = document.querySelectorAll('.order-type-btn');
    const checkoutBtn = document.querySelector('.checkout-btn');

    // Изначально блокируем кнопки
    orderTypeBtns.forEach(btn => {
        btn.disabled = false; // Делаем доступными для клика, но без активного состояния
        btn.classList.remove('active');
    });

    if (checkoutBtn) {
        checkoutBtn.disabled = true; // Блокируем кнопку оформления
    }

    // Обработчики кнопок типа заказа
    orderTypeBtns.forEach(btn => {
        btn.addEventListener('click', async function() {
            // Снимаем активное состояние со всех кнопок
            orderTypeBtns.forEach(b => {
                b.classList.remove('active');
            });

            // Устанавливаем активное состояние текущей кнопке
            this.classList.add('active');

            // Разблокируем кнопку оформления
            if (checkoutBtn) {
                checkoutBtn.disabled = false;
            }

            // Загружаем книги выбранного типа
            const type = this.dataset.type;
            try {
                await loadCartBooks(type);
            } catch (error) {
                console.error('Failed to load cart books:', error);
                showNotification('Не удалось загрузить книги в корзине', 'error');
            }
        });
    });

    // Обработчик кнопки оформления заказа
    checkoutBtn?.addEventListener('click', async () => {
        const activeBtn = document.querySelector('.order-type-btn.active');
        if (!activeBtn) {
            showNotification('Выберите тип корзины', 'error');
            return;
        }

        const type = activeBtn.dataset.type;
        try {
            await checkoutOrder(type);
            showSuccess('Заказ успешно оформлен');
            await loadCartBooks(type);
        } catch (error) {
            showNotification('Не удалось оформить заказ', 'error');
        }
    });

    // Обработчик кнопки выхода
    document.querySelector('.logout-btn')?.addEventListener('click', () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/auth.html';
    });
});


async function loadCartBooks(type = 'TAKE') {
    try {
        const response = await authFetch(`/api/users/cart?type=${type}`);
        if (response.ok) {
            const order = await response.json();
            renderCartBooks(order?.books || []);
        }
    } catch (error) {
        throw error;
    }
}

function renderCartBooks(books) {
    const container = document.querySelector('.books-container');
    if (!container) return;

    container.innerHTML = '';

    if (!books || books.length === 0) {
        container.innerHTML = `
            <div class="col-12 text-center py-5">
                <i class="material-icons" style="font-size: 60px; color: #bdbdbd;">shopping_cart</i>
                <h3 class="mt-3">Корзина пуста</h3>
                <p>Добавьте книги из каталога</p>
            </div>
        `;
        return;
    }

    books.forEach(book => {
        const firstWork = book.works?.[0] || {};
        const worksList = (book.works || [])
            .map(w => `<li>${w.name}${w.writer ? `, ${w.writer}` : ''}</li>`)
            .join('');

        const bookCard = document.createElement('div');
        bookCard.className = 'col-md-4 mb-4';
        bookCard.innerHTML = `
            <div class="card h-100">
                <div class="card-img-top" style="height: 200px; background: url('/images/placeholder.jpg') no-repeat center center; background-size: cover;"></div>
                <div class="card-body">
                    <h5 class="card-title">${firstWork.name || 'Без названия'}</h5>
                    <div class="card-text">
                        <p><strong>Год издания:</strong> ${book.yearOfPubl || 'не указан'}</p>
                        <p><strong>Издательство:</strong> ${book.publHouse || 'не указано'}</p>
                        <p><strong>Состояние:</strong> <span class="status-pill status-${book.status?.toLowerCase() || 'unknown'}">${book.status || 'Не указано'}</span></p>

                        ${worksList ? `
                        <div class="mt-3">
                            <strong>Произведения:</strong>
                            <ul class="mb-0 ps-3">${worksList}</ul>
                        </div>
                        ` : ''}
                    </div>
                </div>
                <div class="card-footer bg-transparent">
                    <button class="btn btn-danger w-100 remove-from-cart-btn" data-book-id="${book.id}">
                        <i class="material-icons">delete</i> Удалить
                    </button>
                </div>
            </div>
        `;

        container.appendChild(bookCard);
    });

    // Обработчики кнопок удаления
    document.querySelectorAll('.remove-from-cart-btn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const bookId = e.currentTarget.dataset.bookId;
            const type = document.querySelector('.order-type-btn.active').dataset.type;
            try {
                await removeFromCart(bookId, type);
                await loadCartBooks(type);
                showSuccess('Книга удалена из корзины');
            } catch (error) {
                showNotification('Не удалось удалить книгу из корзины', 'error');
            }
        });
    });
}

async function removeFromCart(bookId, type) {
    const response = await authFetch(`/api/orders/removeBook?bookId=${bookId}&type=${type}`, {
        method: 'DELETE'
    });

    if (!response.ok) {
        throw new Error('Failed to remove from cart');
    }
}

async function checkoutOrder(type) {
    const response = await authFetch(`/api/orders/checkout?type=${type}`, {
        method: 'POST'
    });

    if (!response.ok) {
        throw new Error('Failed to checkout');
    }
}