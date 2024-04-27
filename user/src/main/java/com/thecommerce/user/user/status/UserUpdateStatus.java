package com.thecommerce.user.user.status;

public enum UserUpdateStatus {
    OK("OK"),            
    INVALID_USER("INVALID_USER"),   
    SERVER_ERROR("SERVER_ERROR");

    private final String value;

    UserUpdateStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
