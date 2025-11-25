package com.smartshop.entities;

import com.smartshop.enums.UserRole;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("ADMIN")
@Table(name = "admins")
@lombok.EqualsAndHashCode(callSuper = true)
public class Admin extends User {
    private String fullName;

    @PrePersist
    private void assignRole() {
        this.setRole(UserRole.ADMIN);
    }
}
