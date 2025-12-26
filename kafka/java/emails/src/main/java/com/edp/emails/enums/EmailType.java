package com.edp.emails.enums;

public enum EmailType {
    CONFIRMATION("Confirmação de cadastro"),
    WELCOME("Bem-vindo ao nosso sistema"),
    PASSWORD_RECOVERY("Recuperação de senha");

    private final String description;

    EmailType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

