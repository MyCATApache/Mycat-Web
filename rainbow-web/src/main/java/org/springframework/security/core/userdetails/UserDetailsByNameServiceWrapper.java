/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package org.springframework.security.core.userdetails;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

/**
 * This implementation for AuthenticationUserDetailsService wraps a regular
 * Spring Security UserDetailsService implementation, to retrieve a UserDetails object
 * based on the user name contained in an <tt>Authentication</tt> object.
 *
 * @author Ruud Senden
 * @author Scott Battaglia
 * @since 2.0
 */
public class UserDetailsByNameServiceWrapper<T extends Authentication> implements AuthenticationUserDetailsService<T>, InitializingBean {
    private UserDetailsService userDetailsService = null;

    /**
     * Constructs an empty wrapper for compatibility with Spring Security 2.0.x's method of using a setter.
     */
    public UserDetailsByNameServiceWrapper() {
        // constructor for backwards compatibility with 2.0
    }

    /**
     * Constructs a new wrapper using the supplied {@link org.springframework.security.core.userdetails.UserDetailsService}
     * as the service to delegate to.
     *
     * @param userDetailsService the UserDetailsService to delegate to.
     */
    public UserDetailsByNameServiceWrapper(final UserDetailsService userDetailsService) {
        Assert.notNull(userDetailsService, "userDetailsService cannot be null.");
        this.userDetailsService = userDetailsService;
    }

    /**
     * Check whether all required properties have been set.
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.userDetailsService, "UserDetailsService must be set");
    }

    /**
     * Get the UserDetails object from the wrapped UserDetailsService
     * implementation
     */
    public UserDetails loadUserDetails(T authentication) throws UsernameNotFoundException {
        return this.userDetailsService.loadUserByUsername(authentication.getName() + ";" + authentication.getCredentials());
    }

    /**
     * Set the wrapped UserDetailsService implementation
     *
     * @param aUserDetailsService
     *            The wrapped UserDetailsService to set
     */
    public void setUserDetailsService(UserDetailsService aUserDetailsService) {
        this.userDetailsService = aUserDetailsService;
    }
}