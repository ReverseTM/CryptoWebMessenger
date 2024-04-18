document.addEventListener('DOMContentLoaded', function () {
    fetchChats();

    const placeholderRoom = document.getElementById('placeholderRoom');
    const selectedRoom = document.getElementById('selectedRoom');

    sessionStorage.setItem('placeholderRoom', 'placeholderRoom');
    sessionStorage.setItem('selectedRoom', 'selectedRoom');

    document.getElementById('submitCreateChatBtn').addEventListener('click', submitCreateRoomForm);
    document.getElementById('submitJoinChatBtn').addEventListener('click', submitJoinForm);

    document.getElementById('confirmLogoutBtn').addEventListener('click', redirectToAuthPage);
    document.getElementById('cancelLogoutBtn').addEventListener('click', closeLogoutModal);

    document.getElementById('logoutBtn').addEventListener('click', openLogoutModal);
    document.getElementById('createChatBtn').addEventListener('click', openCreateChatModal);
    document.getElementById('joinChatBtn').addEventListener('click', openJoinChatModal);

    document.getElementById('logoutClose').addEventListener('click', closeLogoutModal);
    document.getElementById('createChatClose').addEventListener('click', closeCreateChatModal);
    document.getElementById('joinChatClose').addEventListener('click', closeJoinChatModal);

    document.querySelector('.rooms-container').addEventListener('click', function (event) {
        if (event.target.classList.contains('room')) {
            const roomId = event.target.dataset.roomId;
            sessionStorage.setItem('roomId', roomId);
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
                chatElement.classList.add('room');
                chatElement.textContent = chat.name;
                chatElement.setAttribute('data-room-id', chat.id);
                chatsContainer.appendChild(chatElement);
            });
        })
        .catch(error => {
            console.error('Ошибка при загрузке чатов:', error);
        });
}

async function submitCreateRoomForm(event) {
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
            closeCreateChatModal();

            const { name } = await response.json();
            await joinRoom(name);
            await fetchChats();
        } else {
            throw new Error('Ошибка при создании чата');
        }
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Ошибка при создании чата. Пожалуйста, попробуйте еще раз.');
    }
}

async function submitJoinForm(event) {
    event.preventDefault();
    try {
        const roomName = document.getElementById('roomName').value;
        await joinRoom(roomName);
        closeJoinChatModal();
        await fetchChats();
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Ошибка подключения к чату. Пожалуйста, попробуйте еще раз.');
    }
}

async function submitLeaveForm() {
    try {
        const roomId = sessionStorage.getItem('roomId');
        await leaveRoom(roomId);

        hideSelectedRoom();
        showPlaceholderRoom();

        await fetchChats();
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Ошибка отключения от чата. Пожалуйста, попробуйте еще раз.');
    }
}

async function createRoom(formData) {
    try {
        return await fetch('/api/rooms/create', {
            method: 'POST',
            mode: "cors",
            credentials: "include",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Ошибка при создании чата. Пожалуйста, попробуйте еще раз.');
    }
}

async function joinRoom(roomName) {
    const userId = sessionStorage.getItem('userId');
    if (!userId) {
        alert('Пользователь не аутентифицирован');
        return;
    }

    try {
        const response = await fetch(`/api/rooms/${roomName}/join`, {
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

async function leaveRoom(roomId) {
    const userId = sessionStorage.getItem('userId');
    if (!userId) {
        alert('Пользователь не аутентифицирован');
        return;
    }

    try {
        const response = await fetch(`/api/rooms/${roomId}/leave`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ userId: userId })
        });

        if (response.ok) {
            alert('Пользователь успешно отключён');
        } else {
            const errorMessage = await response.text();
            alert(errorMessage);
        }
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Произошла ошибка при отключении пользователя от комнаты');
    }
}

async function updateChat(roomId) {
    try {
        const response = await fetch(`/api/rooms/${roomId}`);
        if (!response.ok) {
            throw new Error('Ошибка при получении данных о чате');
        }

        const room = await response.json();

        const selectedRoom = document.getElementById('selectedRoom');
        selectedRoom.innerHTML = `
            <div class="room-header">
                <h2>Выбрана комната: ${room.name}</h2>
                <button id="leaveChatBtn">Покинуть комнату</button>
            </div>
            <div class="room-details">
                <div class="room-parameters">
                    <h2>Параметры чата:</h2>
                    <p>Алгоритм шифрования: ${room.encryptionAlgorithm}</p>
                    <p>Режим шифрования: ${room.cipherMode}</p>
                    <p>Режим набивки: ${room.paddingMode}</p>
                </div>
                <div class="room-participants">
                    <h2>Участники комнаты:</h2>
                </div>
            </div>
            <div class="chat">
                <div class="messages"></div>
                <div class="input-area">
                    <textarea id="messageText" name="messageText" placeholder="Введите сообщение..."></textarea>
                    <button id="sendMessageBtn">Отправить</button>
                </div>
            </div>
        `;

        const roomParticipants = document.querySelector('.room-participants');

        room.users.forEach(user => {
            const userParagraph = document.createElement('p');
            userParagraph.textContent = user.username;
            roomParticipants.appendChild(userParagraph);
        });
        
        document.getElementById('leaveChatBtn').addEventListener('click', submitLeaveForm);

        hidePlaceholderRoom();
        showSelectedRoom();
    } catch (error) {
        console.error('Ошибка при получении данных о чате:', error);
    }
}

function openLogoutModal() {
    document.getElementById('logoutModal').style.display = 'block';
}

function closeLogoutModal() {
    document.getElementById('logoutModal').style.display = 'none';
}

function openCreateChatModal() {
    document.getElementById('createChatModal').style.display = 'block';
}

function closeCreateChatModal() {
    document.getElementById('createChatModal').style.display = 'none';
}

function openJoinChatModal() {
    document.getElementById('joinChatModal').style.display = 'block';
}

function closeJoinChatModal() {
    document.getElementById('joinChatModal').style.display = 'none';
}

function redirectToAuthPage() {
    window.location.href = 'index.html';
}

function hidePlaceholderRoom() {
    const placeholderRoomId = sessionStorage.getItem('placeholderRoom');
    document.getElementById(placeholderRoomId).style.display = 'none';
}

function hideSelectedRoom() {
    const selectedRoom = sessionStorage.getItem('selectedRoom');
    document.getElementById(selectedRoom).style.display = 'none';
}

function showPlaceholderRoom() {
    const placeholderRoomId = sessionStorage.getItem('placeholderRoom');
    document.getElementById(placeholderRoomId).style.display = 'block';
}

function showSelectedRoom() {
    const selectedRoom = sessionStorage.getItem('selectedRoom');
    document.getElementById(selectedRoom).style.display = 'flex';
}