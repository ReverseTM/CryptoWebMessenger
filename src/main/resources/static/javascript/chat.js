document.addEventListener('DOMContentLoaded', function () {
    fetchChats();

    document.getElementById('createChatBtn').addEventListener('click', openCreateChatModal);
    document.querySelector('.close').addEventListener('click', closeCreateChatModal);
    document.getElementById('submitChatBtn').addEventListener('click', submitRoomForm);

    document.querySelector('.rooms-container').addEventListener('click', function (event) {
        if (event.target.classList.contains('chat')) {
            const roomId = event.target.dataset.roomId;
            updateChat(roomId);
        }
    });
});

function fetchChats() {
    const userId = sessionStorage.getItem('userId');
    if (!userId) {
        alert('Пользователь не аутентифицирован');
        return;
    }

    fetch(`/api/rooms/user/${userId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка при загрузке чатов');
            }
            return response.json();
        })
        .then(chats => {
            const chatsContainer = document.querySelector('.rooms-container');
            chatsContainer.innerHTML = '';
            chats.forEach(chat => {
                const chatElement = document.createElement('div');
                chatElement.classList.add('chat');
                chatElement.textContent = chat.name;
                chatElement.setAttribute('data-room-id', chat.id);
                chatsContainer.appendChild(chatElement);
            });
        })
        .catch(error => {
            console.error('Ошибка при загрузке чатов:', error);
        });
}

async function submitRoomForm(event) {
    event.preventDefault();
    try {
        const chatName = document.getElementById('chatName').value;
        const encryptionAlgorithm = document.getElementById('encryptionAlgorithm').value;
        const cipherMode = document.getElementById('cipherMode').value;
        const paddingMode = document.getElementById('paddingMode').value;

        const formData = {
            name: chatName,
            encryptionAlgorithm: encryptionAlgorithm,
            cipherMode: cipherMode,
            paddingMode: paddingMode
        };

        const response = await createRoom(formData);

        if (response.ok) {
            alert('Комната успешно создана!');
            console.log(response);
            closeCreateChatModal();

            const { id } = await response.json();
            console.log(id);
            await joinRoom(id);
            await fetchChats();
        } else {
            throw new Error('Ошибка при создании чата');
        }
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Ошибка при создании чата. Пожалуйста, попробуйте еще раз.');
    }
}

async function createRoom(formData) {
    try {
        const response = await fetch('/api/rooms/create', {
            method: 'POST',
            mode: "cors",
            credentials: "include",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        return response;
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Ошибка при создании чата. Пожалуйста, попробуйте еще раз.');
    }
}

async function joinRoom(roomId) {
    const userId = sessionStorage.getItem('userId');
    if (!userId) {
        alert('Пользователь не аутентифицирован');
        return;
    }

    try {
        const response = await fetch(`/api/rooms/${roomId}/join`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ userId: userId })
        });

        if (response.ok) {
            alert('Пользователь успешно присоединился');
        } else {
            const errorMessage = await response.text();
            alert(errorMessage);
        }
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Произошла ошибка при подключении пользователя к комнате');
    }
}

async function updateChat(roomId) {
    try {
        const response = await fetch(`/api/rooms/${roomId}`);
        if (!response.ok) {
            throw new Error('Ошибка при получении данных о чате');
        }

        const room = await response.json();
        const selectedChat = document.getElementById('selectedChat');
        selectedChat.innerHTML = `
            <h2>Выбран чат ${room.name}</h2>
            <div class="chat">
                <textarea id="messageText" name="messageText" rows="4" cols="50" placeholder="Введите сообщение..."></textarea>
                <input type="file" id="fileInput" name="fileInput">
                <button id="sendMessageBtn">Отправить сообщение</button>
                <button id="cancelBtn">Отменить</button>
            </div>
            <hr>
            <div class="chat-deteils">
                <h2>Параметры чата</h2>
                <p>Алгоритм шифрования: ${room.encryptionAlgorithm}</p>
                <p>Режим шифрования: ${room.cipherMode}</p>
                <p>Режим набивки: ${room.paddingMode}</p>
                <button id="leaveChatBtn">Покинуть чат</button>
                <button id="generateIVBtn">Сгенерировать IV</button>
            </div>
        `;
        const placeholderChat = document.getElementById('placeholderChat');
        if (placeholderChat) {
            placeholderChat.style.display = 'none';
        }
    } catch (error) {
        console.error('Ошибка при получении данных о чате:', error);
    }
}


function openCreateChatModal() {
    document.getElementById('createChatModal').style.display = 'block';
}

function closeCreateChatModal() {
    document.getElementById('createChatModal').style.display = 'none';
}
