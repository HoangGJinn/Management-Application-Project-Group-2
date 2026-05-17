DROP DATABASE IF EXISTS cafe_management;
CREATE DATABASE cafe_management;
USE cafe_management;

CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT,
    product_name VARCHAR(100),
    description TEXT,
    base_price DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(255),
	status ENUM('ACTIVE', 'OUT_OF_STOCK', 'INACTIVE') DEFAULT 'ACTIVE',
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);


CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'STAFF') DEFAULT 'STAFF',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);




CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    staff_id INT, -- Ai là người tạo đơn (có thể NULL nếu khách tự order)
    total_amount DECIMAL(10, 2) NOT NULL, -- Tổng tiền của đơn
	order_status ENUM('Preparing', 'Ready', 'Served', 'Cancelled') DEFAULT 'Preparing',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    size ENUM('S', 'M', 'L') DEFAULT 'M',
    ice_level ENUM('0%', '30%', '50%', '70%', '100%') DEFAULT '100%',
    sugar_level ENUM('0%', '30%', '50%', '70%', '100%') DEFAULT '100%',
    temperature ENUM('ICE', 'HOT') DEFAULT 'ICE',
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);


CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
	payment_method ENUM('CASH', 'VNPAY') NOT NULL,
    payment_status ENUM('PENDING', 'COMPLETED', 'CANCELLED') DEFAULT 'COMPLETED',
    payment_date DATETIME,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);


CREATE TABLE receipts (
    receipt_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    receipt_content TEXT,
    created_at DATETIME,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

