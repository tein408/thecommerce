package com.thecommerce.user.user.status;

public enum UserRegistrationStatus {
    OK("OK"),
    ALREADY_EXIST_EMAIL("ALREADY_EXIST_EMAIL"),
    ALREADY_EXIST_USER_NAME("ALREADY_EXIST_USER_NAME"),
    FAIL("FAIL");

    private final String value;

    UserRegistrationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
