document.addEventListener('DOMContentLoaded', () => {
    const categoryButtons = document.querySelectorAll('.cat-btn');
    const searchInput = document.getElementById('menuSearch');
    const menuCards = document.querySelectorAll('.menu-card');
    const emptyState = document.getElementById('emptyState');

    const customizeModal = document.getElementById('customizeModal');
    const modalTitle = document.getElementById('modalTitle');
    const sizeOpts = document.getElementById('sizeOpts');
    const iceOpts = document.getElementById('iceOpts');
    const sugarOpts = document.getElementById('sugarOpts');
    const tempOpts = document.getElementById('tempOpts');
    const modalQty = document.getElementById('modalQty');
    const modalDec = document.getElementById('modalDec');
    const modalInc = document.getElementById('modalInc');
    const modalCancel = document.getElementById('modalCancel');
    const modalConfirm = document.getElementById('modalConfirm');

    const cartItems = document.getElementById('cartItems');
    const cartCount = document.getElementById('cartCount');
    const cartQty = document.getElementById('cartQty');
    const cartTotal = document.getElementById('cartTotal');
    const cartJson = document.getElementById('cartJson');
    const checkoutForm = document.getElementById('checkoutForm');
    const checkoutBtn = document.getElementById('checkoutBtn');
    const clearCartBtn = document.getElementById('clearCartBtn');
    const btnCash = document.getElementById('btnCash');
    const btnVnpay = document.getElementById('btnVnpay');
    const payMethodInput = document.getElementById('payMethodInput');
    const qrZone = document.getElementById('qrZone');
    const qrImage = document.getElementById('qrImage');
    const toast = document.getElementById('toast');

    const currencyFormatter = new Intl.NumberFormat('vi-VN');
    const storageKey = 'posCart';
    const qrPlaceholder = 'https://api.qrserver.com/v1/create-qr-code/?size=220x220&data=BrewBean%20VNPay';

    let selectedCategory = 'All';
    let searchTerm = '';
    let pendingItem = null;

    function getCart() {
        const raw = sessionStorage.getItem(storageKey);
        return raw ? JSON.parse(raw) : [];
    }

    function saveCart(cart) {
        sessionStorage.setItem(storageKey, JSON.stringify(cart));
    }

    function formatMoney(value) {
        return `${currencyFormatter.format(Number(value) || 0)}đ`;
    }

    function showToast(message) {
        if (!toast) return;
        toast.textContent = message;
        toast.classList.add('show');
        window.clearTimeout(showToast.timer);
        showToast.timer = window.setTimeout(() => {
            toast.classList.remove('show');
        }, 2200);
    }

    function selectOption(group, value) {
        if (!group) return;
        group.querySelectorAll('.opt-btn').forEach(btn => {
            btn.classList.toggle('active', btn.dataset.v === value);
        });
    }

    function openModal(item) {
        pendingItem = {
            productId: Number(item.dataset.id),
            name: item.dataset.name,
            price: Number(item.dataset.price),
            size: 'M',
            iceLevel: '100%',
            sugarLevel: '100%',
            temperature: 'ICE',
            quantity: 1
        };

        modalTitle.textContent = item.dataset.name;
        modalQty.value = '1';
        selectOption(sizeOpts, 'M');
        selectOption(iceOpts, '100%');
        selectOption(sugarOpts, '100%');
        selectOption(tempOpts, 'ICE');
        customizeModal.classList.add('is-open');
        customizeModal.style.display = 'flex';
    }

    function closeModal() {
        pendingItem = null;
        customizeModal.classList.remove('is-open');
        customizeModal.style.display = 'none';
    }

    function applyFilters() {
        const term = searchTerm.trim().toLowerCase();
        let visibleCount = 0;

        menuCards.forEach(card => {
            const name = (card.dataset.name || '').toLowerCase();
            const category = card.dataset.category || 'Other';
            const matchesCategory = selectedCategory === 'All' || category === selectedCategory;
            const matchesSearch = term === '' || name.includes(term);
            const visible = matchesCategory && matchesSearch;

            card.classList.toggle('hidden', !visible);
            if (visible) visibleCount += 1;
        });

        if (emptyState) {
            emptyState.style.display = visibleCount === 0 ? 'block' : 'none';
        }
    }

    function cartForCheckout(cart) {
        return cart.map(item => ({
            productId: item.productId,
            quantity: item.quantity,
            size: item.size,
            iceLevel: item.iceLevel,
            sugarLevel: item.sugarLevel,
            temperature: item.temperature
        }));
    }

    function updateCheckoutState(cart) {
        const hasItems = cart.length > 0;
        checkoutBtn.disabled = !hasItems;
        if (cartJson) {
            cartJson.value = JSON.stringify(cartForCheckout(cart));
        }
    }

    function renderCart() {
        const cart = getCart();
        cartItems.innerHTML = '';

        let totalQuantity = 0;
        let totalAmount = 0;

        if (cart.length === 0) {
            cartItems.innerHTML = '<div class="cart-empty">Chưa có món nào.<br>Chọn món từ menu bên trái.</div>';
        } else {
            cart.forEach((item, index) => {
                totalQuantity += item.quantity;
                totalAmount += (Number(item.price) || 0) * item.quantity;

                const row = document.createElement('div');
                row.className = 'cart-item';
                row.innerHTML = `
                    <div class="cart-item-main">
                        <div class="cart-item-name">${item.name}</div>
                        <div class="cart-item-sub">
                            ${item.size} | ${item.iceLevel} đá | ${item.sugarLevel} đường | ${item.temperature === 'HOT' ? 'Nóng' : 'Đá'}
                        </div>
                        <div class="cart-item-qty">
                            <button type="button" class="qty-btn" data-action="dec" data-index="${index}">−</button>
                            <input class="qty-input" type="text" value="${item.quantity}" readonly>
                            <button type="button" class="qty-btn" data-action="inc" data-index="${index}">+</button>
                        </div>
                    </div>
                    <div class="cart-item-actions">
                        <div class="menu-price">${formatMoney((Number(item.price) || 0) * item.quantity)}</div>
                        <button type="button" class="remove-btn" data-action="remove" data-index="${index}">Xóa</button>
                    </div>
                `;
                cartItems.appendChild(row);
            });
        }

        cartCount.textContent = String(cart.length);
        cartQty.textContent = String(totalQuantity);
        cartTotal.textContent = formatMoney(totalAmount);
        updateCheckoutState(cart);
    }

    function addPendingItem() {
        if (!pendingItem) return;

        const cart = getCart();
        const match = cart.find(item =>
            item.productId === pendingItem.productId &&
            item.size === pendingItem.size &&
            item.iceLevel === pendingItem.iceLevel &&
            item.sugarLevel === pendingItem.sugarLevel &&
            item.temperature === pendingItem.temperature
        );

        if (match) {
            match.quantity += pendingItem.quantity;
        } else {
            cart.push({ ...pendingItem });
        }

        saveCart(cart);
        renderCart();
        closeModal();
        showToast('Đã thêm món vào đơn');
    }

    categoryButtons.forEach(button => {
        button.addEventListener('click', () => {
            selectedCategory = button.dataset.category || 'All';
            categoryButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
            applyFilters();
        });
    });

    searchInput.addEventListener('input', event => {
        searchTerm = event.target.value || '';
        applyFilters();
    });

    document.addEventListener('click', event => {
        const addButton = event.target.closest('.add-btn');
        if (!addButton || addButton.disabled) {
            return;
        }
        openModal(addButton);
    });

    if (sizeOpts) {
        sizeOpts.addEventListener('click', event => {
            const button = event.target.closest('.opt-btn');
            if (!button) return;
            selectOption(sizeOpts, button.dataset.v);
            if (pendingItem) pendingItem.size = button.dataset.v;
        });
    }

    if (iceOpts) {
        iceOpts.addEventListener('click', event => {
            const button = event.target.closest('.opt-btn');
            if (!button) return;
            selectOption(iceOpts, button.dataset.v);
            if (pendingItem) pendingItem.iceLevel = button.dataset.v;
        });
    }

    if (sugarOpts) {
        sugarOpts.addEventListener('click', event => {
            const button = event.target.closest('.opt-btn');
            if (!button) return;
            selectOption(sugarOpts, button.dataset.v);
            if (pendingItem) pendingItem.sugarLevel = button.dataset.v;
        });
    }

    if (tempOpts) {
        tempOpts.addEventListener('click', event => {
            const button = event.target.closest('.opt-btn');
            if (!button) return;
            selectOption(tempOpts, button.dataset.v);
            if (pendingItem) pendingItem.temperature = button.dataset.v;
        });
    }

    if (modalDec) {
        modalDec.addEventListener('click', () => {
            const value = Math.max(1, (Number(modalQty.value) || 1) - 1);
            modalQty.value = String(value);
            if (pendingItem) pendingItem.quantity = value;
        });
    }

    if (modalInc) {
        modalInc.addEventListener('click', () => {
            const value = Math.min(99, (Number(modalQty.value) || 1) + 1);
            modalQty.value = String(value);
            if (pendingItem) pendingItem.quantity = value;
        });
    }

    if (modalQty) {
        modalQty.addEventListener('change', () => {
            const value = Math.max(1, Math.min(99, Number(modalQty.value) || 1));
            modalQty.value = String(value);
            if (pendingItem) pendingItem.quantity = value;
        });
    }

    if (modalCancel) {
        modalCancel.addEventListener('click', closeModal);
    }

    if (modalConfirm) {
        modalConfirm.addEventListener('click', addPendingItem);
    }

    if (customizeModal) {
        customizeModal.addEventListener('click', event => {
            if (event.target === customizeModal) {
                closeModal();
            }
        });
    }

    if (cartItems) {
        cartItems.addEventListener('click', event => {
            const button = event.target.closest('button[data-action]');
            if (!button) return;

            const index = Number(button.dataset.index);
            const action = button.dataset.action;
            const cart = getCart();
            const item = cart[index];

            if (!item) return;

            if (action === 'remove') {
                cart.splice(index, 1);
            } else if (action === 'dec') {
                item.quantity -= 1;
                if (item.quantity <= 0) {
                    cart.splice(index, 1);
                }
            } else if (action === 'inc') {
                item.quantity += 1;
            }

            saveCart(cart);
            renderCart();
        });
    }

    if (clearCartBtn) {
        clearCartBtn.addEventListener('click', () => {
            const cart = getCart();
            if (cart.length === 0) return;
            if (window.confirm('Xóa toàn bộ món trong đơn?')) {
                saveCart([]);
                renderCart();
                showToast('Đã xóa toàn bộ đơn');
            }
        });
    }

    if (btnCash && btnVnpay && payMethodInput) {
        const setPaymentMethod = method => {
            payMethodInput.value = method;
            btnCash.classList.toggle('active', method === 'CASH');
            btnVnpay.classList.toggle('active', method === 'VNPAY');
            if (qrZone) {
                qrZone.style.display = method === 'VNPAY' ? 'block' : 'none';
            }
            if (checkoutBtn) {
                checkoutBtn.textContent = method === 'VNPAY' ? 'Thanh toán (VNPay QR)' : 'Thanh toán (Tiền mặt)';
            }
            if (method === 'VNPAY' && qrImage && !qrImage.src) {
                qrImage.src = qrPlaceholder;
            }
        };

        btnCash.addEventListener('click', () => setPaymentMethod('CASH'));
        btnVnpay.addEventListener('click', () => setPaymentMethod('VNPAY'));
        setPaymentMethod('CASH');
    }

    if (checkoutForm) {
        checkoutForm.addEventListener('submit', event => {
            const cart = getCart();
            if (cart.length === 0) {
                event.preventDefault();
                showToast('Vui lòng thêm món trước khi thanh toán');
                return;
            }

            if (cartJson) {
                cartJson.value = JSON.stringify(cartForCheckout(cart));
            }
        });
    }

    const success = new URLSearchParams(window.location.search).get('success');
    if (success === 'true') {
        sessionStorage.removeItem(storageKey);
        showToast('Thanh toán thành công');
    }

    applyFilters();
    renderCart();
});