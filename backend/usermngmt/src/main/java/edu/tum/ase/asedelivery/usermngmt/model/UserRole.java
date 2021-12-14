package edu.tum.ase.asedelivery.usermngmt.model;

import org.springframework.security.core.GrantedAuthority;

// !! Don't change the roles' names! May give trouble if you use hasRole()
// https://docs.spring.io/spring-security/site/docs/5.2.x/reference/html/authorization.html#:~:text=example%2C%20hasRole(%27admin%27)-,By%20default,-if%20the%20supplied

public enum UserRole implements GrantedAuthority {
    ROLE_CUSTOMER, ROLE_DELIVERER, ROLE_DISPATCHER;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
