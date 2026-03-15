package dev.muthukumar.ai_crm.exception;
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
    public ResourceNotFoundException(String resource, Long id) { super(resource + " not found: " + id); }
}
