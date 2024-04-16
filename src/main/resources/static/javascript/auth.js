document.getElementById('loginForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const { username, password } = event.target.elements;

    const { success, error } = await loginUser(username.value, password.value);

    if (success) {
        alert('Login successful');
        redirectToChatPage();
    } else {
        handleLoginError(error);
    }
});

async function loginUser(username, password) {
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const { userId } = await response.json();
            sessionStorage.setItem('userId', userId);
            return { success: true };
        } else {
            return { success: false };
        }
    } catch (error) {
        console.error('Error:', error);
        return { success: false, error };
    }
}

function redirectToChatPage() {
    window.location.href = 'chat.html';
}

function handleLoginError() {
    alert('Incorrect credentials. Please try again.');
    document.getElementById('password').value = '';
}
