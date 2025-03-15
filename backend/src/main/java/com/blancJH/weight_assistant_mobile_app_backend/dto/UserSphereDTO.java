package com.blancJH.weight_assistant_mobile_app_backend.dto;

public class UserSphereDTO {
    private Long id;
    private String sphereName;
    private int level;
    private int quantity;
    private boolean representator;

    // Constructors, getters, setters

    public UserSphereDTO(Long id, String sphereName, int level, int quantity, boolean representator) {
        this.id = id;
        this.sphereName = sphereName;
        this.level = level;
        this.quantity = quantity;
        this.representator = representator;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSphereName() {
        return sphereName;
    }

    public void setSphereName(String sphereName) {
        this.sphereName = sphereName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isRepresentator() {
        return representator;
    }

    public void setRepresentator(boolean representator) {
        this.representator = representator;
    }
}

