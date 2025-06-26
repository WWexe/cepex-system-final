package com.biopark.cepex_system.domain.user;

public enum UserRole {
    ADMIN("admin"),
    STUDENT("student"),
    PROFESSOR("professor"),
    COORDENATION("coordenation"),
    SECRETARY("secretary");

    private String role;

    UserRole(String role) {
        this.role = role;
    }
    public String getRole() {
        return role;
    }
}
