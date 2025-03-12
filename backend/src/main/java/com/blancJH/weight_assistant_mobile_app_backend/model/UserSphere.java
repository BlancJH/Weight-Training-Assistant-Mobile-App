package com.blancJH.weight_assistant_mobile_app_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_spheres")
@Getter @Setter
public class UserSphere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sphere_id", nullable = false)
    private Sphere sphere;

    @Column(nullable = false)
    private int quantity;  // How many of this sphere user owns

    @Column(nullable = false)
    private int level;  // Level of the sphere

    public UserSphere() {}

    public UserSphere(User user, Sphere sphere, int quantity, int level) {
        this.user = user;
        this.sphere = sphere;
        this.quantity = quantity;
        this.level = level;
    }
}
