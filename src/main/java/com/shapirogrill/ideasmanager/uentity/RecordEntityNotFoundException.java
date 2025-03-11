package com.shapirogrill.ideasmanager.uentity;

public class RecordEntityNotFoundException extends RuntimeException {
    public RecordEntityNotFoundException(Long id) {
        super("Could not find Record Entity %s".formatted(id));
    }
}
