package com.ute.library.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class để sinh bcrypt hash cho password
 * Chạy main method để sinh hash từ password bất kỳ
 */
public class BcryptUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Sinh hash cho password "123456"
        String rawPassword = "123456";
        String hashedPassword = encoder.encode(rawPassword);
        
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Bcrypt hash: " + hashedPassword);
        
        // Verify
        boolean matches = encoder.matches(rawPassword, hashedPassword);
        System.out.println("Verify matches: " + matches);
        
        // Cập nhật SQL INSERT:
        System.out.println("\n--- SQL UPDATE ---");
        System.out.println("UPDATE librarians SET password_hash = '" + hashedPassword + "' WHERE username IN ('admin', 'vana', 'thib');");
        
        System.out.println("\n--- SQL INSERT (cho file mới) ---");
        System.out.println("INSERT INTO librarians (librarian_code, full_name, username, password_hash) VALUES");
        System.out.println("('ADMIN001', 'Admin User', 'admin', '" + hashedPassword + "'),");
        System.out.println("('LIB001', 'Nguyễn Văn A', 'vana', '" + hashedPassword + "'),");
        System.out.println("('LIB002', 'Trần Thị B', 'thib', '" + hashedPassword + "');");
    }
}
