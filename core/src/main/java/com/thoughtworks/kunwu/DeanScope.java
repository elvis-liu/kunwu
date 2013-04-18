package com.thoughtworks.kunwu;

public enum DeanScope {
    SINGLETON("singleton"),
    PROTOTYPE("prototype");
    private final String scopeName;

    private DeanScope(String scopeName) {
        this.scopeName = scopeName;
    }

    public static DeanScope scopeNameOf(String scopeName) {
        for (DeanScope deanScope : DeanScope.values()) {
            if (deanScope.scopeName.equals(scopeName)) {
                return deanScope;
            }
        }

        throw new IllegalArgumentException("Unknown scope: " + scopeName);
    }
}
