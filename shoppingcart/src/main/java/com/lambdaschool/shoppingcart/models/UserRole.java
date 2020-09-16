package com.lambdaschool.shoppingcart.models;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lambdaschool.shoppingcart.models.User;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "userroles")
@IdClass(UserRolesId.class)
public class UserRole
    extends Auditable implements Serializable {


    @Id
    @ManyToOne
    @JoinColumn(name = "roleid")
    @JsonIgnoreProperties(value = "users", allowSetters = true)
    private Role role;

    @Id
    @ManyToOne
    @JoinColumn(name = "userid")
    @JsonIgnoreProperties(value = "roles", allowSetters = true)
    private User user;

    public UserRole() {
    }

    public UserRole(Role role, User user) {
        this.role = role;
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public com.lambdaschool.shoppingcart.models.User getUser() {
        return user;
    }

    public void setUser(com.lambdaschool.shoppingcart.models.User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRole)) return false;
        UserRole userRole = (UserRole) o;
        return getRole().equals(userRole.getRole()) &&
                getUser().equals(userRole.getUser());
    }

    @Override
    public int hashCode() {
        return 37;
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "role=" + role +
                '}';
    }
}
