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
            const orderId = document.querySelector('.books-container').dataset.orderId;
            await checkoutOrder(orderId);
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
            console.log('Received order:', order); // Проверьте структуру ответа
            renderCartBooks(order);
        }
    } catch (error) {
        throw error;
    }
}

function renderCartBooks(order) {
    const container = document.querySelector('.books-container');
    container.dataset.orderId = order.id; // Сохраняем в data-атрибуте
    if (!container) return;

    container.innerHTML = '';

    const books = order?.books || [];
    const orderId = order.id;
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

    books.forEach((book, index) => {
        const bookYear = book.yearOfPubl || 'не указан';
        const publisher = book.publHouse || 'не указано';

        const worksList = (book.works || [])
            .map(w => `<li>${w.name}${w.writer ? `, ${w.writer}` : ''}</li>`)
            .join('');

        const bookCard = document.createElement('div');
        bookCard.className = 'col-md-4 mb-4';
        bookCard.innerHTML = `
            <div class="card h-100">
                <div class="card-img-top" style="height: 200px; background: url('/images/placeholder.jpg') no-repeat center center; background-size: cover;"></div>
                <div class="card-body">
                    <h5 class="card-title">Книга ${index + 1}</h5>
                    <div class="card-text">
                        <div class="book-meta">
                            <p><strong>Год издания:</strong> ${bookYear}</p>
                            <p><strong>Издательство:</strong> ${publisher}</p>
                        </div>

                        ${book.works?.length ? `
                        <div class="book-works mt-3">
                            <strong>Произведения:</strong>
                            <ul class="mb-0 ps-3">${worksList}</ul>
                        </div>
                        ` : ''}
                    </div>
                </div>
                <div class="card-footer bg-transparent">
                    <button class="btn btn-danger w-100 remove-from-cart-btn" data-book-id="${book.id}" data-order-id="${orderId}">
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
            const orderId = e.currentTarget.dataset.orderId;
            const type = document.querySelector('.order-type-btn.active').dataset.type;
            try {
                await removeFromCart(bookId, orderId);
                await loadCartBooks(type);
                showSuccess('Книга удалена из корзины');
            } catch (error) {
                showNotification('Не удалось удалить книгу из корзины', 'error');
            }
        });
    });
}

async function removeFromCart(bookId, orderId) {
    const response = await authFetch(`/api/orders/${orderId}/books?bookId=${bookId}`, {
        method: 'DELETE'
    });

    if (!response.ok) {
        throw new Error('Failed to remove from cart');
    }
}

async function checkoutOrder(orderId) {
    const response = await authFetch(`/api/orders/${orderId}/status?status=CREATED`, {
        method: 'PATCH'
    });

    if (!response.ok) {
        throw new Error('Failed to checkout');
    }
}