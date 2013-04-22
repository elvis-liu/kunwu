package com.thoughtworks.kunwu.exception;

public class NoSuchDeanException extends RuntimeException {
    private final String id;

    public NoSuchDeanException(String id) {
        super("no dean with id: " + id);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
