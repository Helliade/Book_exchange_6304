document.addEventListener('DOMContentLoaded', () => {
    // Регистрация
    document.getElementById('register-form')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams(formData)
            });

            if (response.ok) {
                showAlert('Регистрация прошла успешно!');
                e.target.reset(); // Очистка формы
            } else {
                const error = await response.text();
                showAlert(`Ошибка: ${error}`, false);
            }
        } catch (error) {
            showAlert('Сетевая ошибка', false);
        }
    });

    // Логин
   document.getElementById('login-form')?.addEventListener('submit', async (e) => {
       e.preventDefault();
       const formData = new FormData(e.target);

       try {
           const response = await fetch('/api/auth/login', {
               method: 'POST',
               headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
               body: new URLSearchParams(formData)
           });

           if (response.ok) {
               const { accessToken, refreshToken } = await response.json();
               localStorage.setItem('accessToken', accessToken);
               localStorage.setItem('refreshToken', refreshToken);
               window.location.href = '/main'; // Редирект после успешного входа
           } else {
               const error = await response.text();
               showAlert(`Ошибка: ${error}`, false);
           }
       } catch (error) {
           showAlert('Сетевая ошибка', false);
       }
   });

    // Универсальная функция для показа уведомлений
    function showAlert(message, isSuccess = true) {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert ${isSuccess ? 'alert-success' : 'alert-error'}`;
        alertDiv.textContent = message;
        document.body.prepend(alertDiv);

        setTimeout(() => alertDiv.remove(), 3000);
    }
});