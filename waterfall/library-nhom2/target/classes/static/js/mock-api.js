// Mock data and simple UI helpers for the library mock UI

// Mock Data Storage
const mockData = {
    books: [
        { id: 1, title: 'Introduction to Java', author: 'Nguyen Van A', category: 'Programming', isbn: '123-456', year: 2020, status: 'AVAILABLE' },
        { id: 2, title: 'Database Systems', author: 'Tran Thi B', category: 'Database', isbn: '789-012', year: 2021, status: 'BORROWED' },
        { id: 3, title: 'Web Development Guide', author: 'Le Van C', category: 'Web', isbn: '345-678', year: 2022, status: 'AVAILABLE' },
    ],
    members: [
        { id: 1, name: 'Le Van C', email: 'levan.c@example.com', phone: '0123456789', address: '123 Nguyen Hue' },
        { id: 2, name: 'Pham Thi D', email: 'pham.t.d@example.com', phone: '0987654321', address: '456 Le Loi' },
    ],
    loans: [
        { id: 1, bookTitle: 'Database Systems', memberId: 1, memberName: 'Le Van C', borrowDate: '2026-05-01', dueDate: '2026-06-01', status: 'ACTIVE' },
    ]
};

// ===== DASHBOARD FUNCTIONS =====
function loadDashboardStats() {
    fetch('/api/dashboard/stats')
        .then(res => res.json())
        .then(data => {
            document.getElementById('total-members').textContent = data.totalMembers || '1223';
            document.getElementById('borrowed-books').textContent = data.borrowedBooks || '740';
            document.getElementById('overdue-books').textContent = data.overdueBooks || '22';
            document.getElementById('new-members').textContent = data.newMembers || '60';
        })
        .catch(err => console.error('Failed to load stats', err));
}

function loadDashboardMembers() {
    const tbody = document.querySelector('#members-table-dashboard tbody');
    if(!tbody) return;
    tbody.innerHTML = '';
    mockData.members.slice(0, 3).forEach(m => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${m.id}</td><td>${m.name}</td><td>2</td><td>IT Department</td><td><button class="btn-small">...</button></td>`;
        tbody.appendChild(tr);
    });
}

function loadDashboardBooks() {
    const tbody = document.querySelector('#books-table-dashboard tbody');
    if(!tbody) return;
    tbody.innerHTML = '';
    mockData.books.slice(0, 3).forEach(b => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${b.id}</td><td>${b.title}</td><td>${b.author}</td><td>${b.category}</td><td>${b.status === 'AVAILABLE' ? '✓' : '✗'}</td>`;
        tbody.appendChild(tr);
    });
}

function loadTopChoices() {
    const div = document.getElementById('top-choices-books');
    if(!div) return;
    div.innerHTML = '';
    mockData.books.forEach(b => {
        const card = document.createElement('div');
        card.className = 'book-card';
        card.innerHTML = `
            <div style="width:100%; height:200px; background:#ddd; border-radius:4px; display:flex; align-items:center; justify-content:center;">
                📖 ${b.title.substring(0, 10)}
            </div>
            <h4>${b.title}</h4>
            <p>${b.author}</p>
        `;
        div.appendChild(card);
    });
}

// ===== BOOKS MANAGE =====
function loadBooksManage() {
    const tbody = document.querySelector('#books-manage-table tbody');
    if(!tbody) return;
    tbody.innerHTML = '';
    mockData.books.forEach(b => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${b.id}</td>
            <td>${b.title}</td>
            <td>${b.author}</td>
            <td>${b.category}</td>
            <td><span style="background:${b.status==='AVAILABLE'?'#90EE90':'#FFB6C6'}; padding:4px 8px; border-radius:4px;">${b.status}</span></td>
            <td><button class="btn-small" onclick="editBook(${b.id})">Sửa</button> <button class="btn-small btn-danger" onclick="deleteBook(${b.id})">Xóa</button></td>
        `;
        tbody.appendChild(tr);
    });
}

// ===== MEMBERS MANAGE =====
function loadMembersManage() {
    const tbody = document.querySelector('#members-manage-table tbody');
    if(!tbody) return;
    tbody.innerHTML = '';
    mockData.members.forEach(m => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${m.id}</td>
            <td>${m.name}</td>
            <td>${m.email}</td>
            <td>${m.phone}</td>
            <td>${m.address}</td>
            <td><button class="btn-small" onclick="editMember(${m.id})">Sửa</button> <button class="btn-small btn-danger" onclick="deleteMember(${m.id})">Xóa</button></td>
        `;
        tbody.appendChild(tr);
    });
}

// ===== LOANS MANAGE =====
function loadLoansManage() {
    const tbody = document.querySelector('#loans-active-table tbody');
    if(!tbody) return;
    tbody.innerHTML = '';
    mockData.loans.filter(l => l.status === 'ACTIVE').forEach(l => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${l.id}</td>
            <td>${l.memberName}</td>
            <td>${l.bookTitle}</td>
            <td>${l.borrowDate}</td>
            <td>${l.dueDate}</td>
            <td><span style="background:#90EE90; padding:4px 8px; border-radius:4px;">Đang Mượn</span></td>
            <td><button class="btn-small" onclick="returnBook(${l.id})">Trả Sách</button></td>
        `;
        tbody.appendChild(tr);
    });
}

function loadOverdueBooks() {
    const tbody = document.querySelector('#loans-overdue-table tbody');
    if(!tbody) return;
    tbody.innerHTML = '';
    const overdueLoans = mockData.loans.filter(l => new Date(l.dueDate) < new Date());
    if(overdueLoans.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; color:#888;">Không có sách quá hạn</td></tr>';
        return;
    }
    overdueLoans.forEach(l => {
        const today = new Date();
        const dueDate = new Date(l.dueDate);
        const daysOverdue = Math.floor((today - dueDate) / (1000 * 60 * 60 * 24));
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${l.id}</td>
            <td>${l.memberName}</td>
            <td>${l.bookTitle}</td>
            <td>${l.dueDate}</td>
            <td><span style="background:#FFB6C6; padding:4px 8px; border-radius:4px;">${daysOverdue} ngày</span></td>
            <td><button class="btn-small btn-warning" onclick="returnBook(${l.id})">Trả Ngay</button></td>
        `;
        tbody.appendChild(tr);
    });
}

// ===== SEARCH =====
function searchBooks(query) {
    if(!query) {
        document.querySelector('#search-books-result tbody').innerHTML = '';
        return;
    }
    const results = mockData.books.filter(b => 
        b.title.toLowerCase().includes(query.toLowerCase()) ||
        b.author.toLowerCase().includes(query.toLowerCase()) ||
        b.isbn.includes(query)
    );
    const tbody = document.querySelector('#search-books-result tbody');
    tbody.innerHTML = '';
    results.forEach(b => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${b.id}</td><td>${b.title}</td><td>${b.author}</td><td>${b.category}</td><td>${b.status}</td>`;
        tbody.appendChild(tr);
    });
}

function searchMembers(query) {
    if(!query) {
        document.querySelector('#search-members-result tbody').innerHTML = '';
        return;
    }
    const results = mockData.members.filter(m => 
        m.name.toLowerCase().includes(query.toLowerCase()) ||
        m.email.toLowerCase().includes(query.toLowerCase())
    );
    const tbody = document.querySelector('#search-members-result tbody');
    tbody.innerHTML = '';
    results.forEach(m => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${m.id}</td><td>${m.name}</td><td>${m.email}</td><td>${m.phone}</td>`;
        tbody.appendChild(tr);
    });
}

// ===== REPORTS =====
function loadReportStats() {
    const totalBooks = mockData.books.length;
    const totalMembers = mockData.members.length;
    const totalLoans = mockData.loans.length;
    const overdue = mockData.loans.filter(l => new Date(l.dueDate) < new Date()).length;

    if(document.getElementById('total-books')) document.getElementById('total-books').textContent = totalBooks;
    if(document.getElementById('total-members')) document.getElementById('total-members').textContent = totalMembers;
    if(document.getElementById('total-loans')) document.getElementById('total-loans').textContent = totalLoans * 100; // mock multiplier
    if(document.getElementById('overdue-count')) document.getElementById('overdue-count').textContent = overdue;
}

function loadPopularBooks() {
    const tbody = document.querySelector('#popular-books-table tbody');
    if(!tbody) return;
    tbody.innerHTML = '';
    mockData.books.forEach((b, idx) => {
        const tr = document.createElement('tr');
        const borrowed = Math.floor(Math.random() * 100) + 20;
        tr.innerHTML = `<td>${idx + 1}</td><td>${b.title}</td><td>${b.author}</td><td>${borrowed}</td>`;
        tbody.appendChild(tr);
    });
}

// ===== EXPOSED FUNCTIONS =====
window.loadBooks = function() {};
window.loadMembers = function() {};
window.loadLoans = function() {};
window.borrowBook = function(id){ alert('Borrow book ' + id); };
window.createBook = function(payload){ return fetch('/api/books', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify(payload) }).then(res => res.json()); };
window.editBook = function(id){ alert('Edit book ' + id); };
window.deleteBook = function(id){ alert('Delete book ' + id); };
window.editMember = function(id){ alert('Edit member ' + id); };
window.deleteMember = function(id){ alert('Delete member ' + id); };
window.returnBook = function(id){ alert('Return book ' + id); };
window.searchBooks = searchBooks;
window.searchMembers = searchMembers;
window.loadBooksManage = loadBooksManage;
window.loadMembersManage = loadMembersManage;
window.loadLoansManage = loadLoansManage;
window.loadOverdueBooks = loadOverdueBooks;
window.loadReportStats = loadReportStats;
window.loadPopularBooks = loadPopularBooks;
