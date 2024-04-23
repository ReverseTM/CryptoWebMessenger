document.addEventListener('DOMContentLoaded', function () {
    fetchChats();

    sessionStorage.setItem('placeholderRoom', 'placeholderRoom');
    sessionStorage.setItem('selectedRoom', 'selectedRoom');

    document.getElementById('submitCreateChatBtn').addEventListener('click', createRoomByForm);
    document.getElementById('submitJoinChatBtn').addEventListener('click', joinUserToRoom);

    document.getElementById('logoutBtn').addEventListener('click', openLogoutModal);
    document.getElementById('createChatBtn').addEventListener('click', openCreateChatModal);
    document.getElementById('joinChatBtn').addEventListener('click', openJoinChatModal);

    document.getElementById('confirmClose').addEventListener('click', closeConfirmModal);
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

async function fetchChats() {
    try {
        const userId = sessionStorage.getItem('userId');
        if (!userId) {
            throw new Error('Пользователь не аутентифицирован');
        }

        const chats = await getRooms(userId);
        const chatsContainer = document.querySelector('.rooms-container');
        chatsContainer.innerHTML = '';

        chats.forEach(chat => {
            const chatElement = document.createElement('div');
            chatElement.classList.add('room');
            chatElement.textContent = chat.name;
            chatElement.setAttribute('data-room-id', chat.id);
            chatsContainer.appendChild(chatElement);
        });
    } catch (error) {
        alert(error);
    }
}

async function updateChat(roomId) {
    try {
        if (!roomId) {
            throw new Error('Отсутствует идентификатор комнаты');
        }

        await loadRoomContent(roomId);

        hidePlaceholderRoom();
        showSelectedRoom();
    } catch (error) {
        alert(error);
    }
}

async function createRoomByForm(event) {
    event.preventDefault();

    try {
        const roomData = {
            name: document.getElementById('chatName').value,
            encryptionAlgorithm: document.getElementById('encryptionAlgorithm').value,
            cipherMode: document.getElementById('cipherMode').value,
            paddingMode: document.getElementById('paddingMode').value
        };

        const room = await createRoom(roomData);

        await joinUser(room.name);
        await fetchChats();

        closeCreateChatModal();
    } catch (error) {
        alert(error);
    }
}

async function joinUserToRoom(event) {
    event.preventDefault();

    try {
        await joinUser(document.getElementById('roomName').value);
        await fetchChats();

        closeJoinChatModal();
    } catch (error) {
        alert(error);
    }
}

async function leaveUserFromRoom() {
    try {
        const roomId = sessionStorage.getItem('roomId');
        if (!roomId) {
            throw new Error('Не удалось получить идентификатор комнаты');
        }

        await leaveUser(roomId);

        closeConfirmModal();
        hideSelectedRoom();
        showPlaceholderRoom();

        await fetchChats();
    } catch (error) {
        alert(error);
    }
}

async function getRooms(userId) {
    const response = await fetch(`/api/user/${userId}/rooms`);
    if (response.ok) {
        return response.json();
    }
    throw new Error('Ошибка при загрузке комнат');
}

async function createRoom(roomData) {
    const response = await fetch('/api/user/room/create', {
        method: 'POST',
        mode: "cors",
        credentials: "include",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(roomData)
    });

    if (response.ok) {
        alert('Комната успешно создана');
        return response.json();
    }
    throw new Error('Ошибка при создании комнаты');
}

async function joinUser(roomName) {
    const userId = sessionStorage.getItem('userId');
    if (!userId) {
        throw new Error('Пользователь не аутентифицирован');
    }

    if (!roomName) {
        throw new Error('Отсутствует имя комнаты');
    }

    const response = await fetch(`/api/user/room/${roomName}/join`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({userId: userId})
    });

    if (response.ok) {
        alert('Пользователь успешно присоединился');
    } else {
        throw new Error('Ошибка при подключении пользователя к комнате');
    }
}

async function leaveUser(roomId) {
    const userId = sessionStorage.getItem('userId');
    if (!userId) {
        throw new Error('Пользователь не аутентифицирован');
    }

    const response = await fetch(`/api/user/room/${roomId}/leave`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({userId: userId})
    });

    if (response.ok) {
        alert('Пользователь успешно отключён');
    } else {
        throw new Error('Ошибка при отключении пользователя от комнаты');
    }
}

async function loadRoomContent(roomId) {
    const response = await fetch(`/api/user/room/${roomId}`);
    if (!response.ok) {
        throw new Error('Ошибка при получении данных о чате');
    }

    const room = await response.json();

    const selectedRoom = document.getElementById('selectedRoom');
    selectedRoom.innerHTML = `
            <div class="room-details">
                <div>
                    <h2>${room.name}</h2>
                    <p id="participantsCount">${room.users.length} участника</p>
                </div>
                <button id="leaveChatBtn" title="Отключится"><img src="images/disconnectChatIcon.png" alt="Покинуть комнату"></button>
            </div>
            <div class="chat">
                <div class="messages"></div>
                <div class="input-area">
                    <input type="file" id="fileInput" name="fileInput" style="display: none;">
                    <label for="fileInput">
                        <img src="images/addFilesIcon.png" alt="Загрузить файл">
                    </label>
                    <textarea id="messageText" name="messageText" placeholder="Введите сообщение..."></textarea>
                    <button id="sendMessageBtn"><img src="images/sendMessageIcon.png" alt="Send Message"></button>
                </div>
            </div>
        `;

    getUsers(room);

    document.getElementById('leaveChatBtn').addEventListener('click', openLeaveChatModal);
}

function getUsers(room) {
    const participantsCount = document.getElementById('participantsCount');
    const roomUsers = room.users;

    let tooltip;

    participantsCount.addEventListener("mouseover", showTooltip);
    participantsCount.addEventListener("mouseout", hideTooltip);

    function showTooltip() {
        tooltip = createTooltip();
        document.body.appendChild(tooltip);
        positionTooltip();
        setTimeout(function() {
            tooltip.classList.add('show');
        }, 10);
    }

    function hideTooltip() {
        if (tooltip) {
            tooltip.classList.remove('show');
            setTimeout(function() {
                document.body.removeChild(tooltip);
                tooltip = null;
            }, 300);
        }
    }

    function createTooltip() {
        const tooltip = document.createElement("div");
        tooltip.classList.add("tooltip");

        roomUsers.forEach(user => {
            const item = document.createElement('div');
            item.className = 'tooltip-item';
            item.textContent = user.username;
            tooltip.appendChild(item);
        });

        return tooltip;
    }

    function positionTooltip() {
        const roomDetails = document.querySelector('.room-details');
        const rectParticipantsCount = participantsCount.getBoundingClientRect();
        const rectRoomDetails = roomDetails.getBoundingClientRect();
        const tooltipLeft = rectParticipantsCount.left + window.scrollX;
        const tooltipTop = rectRoomDetails.top + window.scrollY + roomDetails.offsetHeight;
        tooltip.style.left = tooltipLeft + "px";
        tooltip.style.top = tooltipTop + "px";
    }
}


function openLeaveChatModal() {
    document.getElementById('confirmBtn').addEventListener('click', leaveUserFromRoom);
    document.getElementById('cancelBtn').addEventListener('click', closeConfirmModal);

    document.getElementById('confirmModal').style.display = 'block';
}

function openLogoutModal() {
    document.getElementById('confirmBtn').addEventListener('click', redirectToAuthPage);
    document.getElementById('cancelBtn').addEventListener('click', closeConfirmModal);

    document.getElementById('confirmModal').style.display = 'block';
}

function closeConfirmModal() {
    document.getElementById('confirmModal').style.display = 'none';
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

function redirectToAuthPage() {
    window.location.href = 'index.html';
}