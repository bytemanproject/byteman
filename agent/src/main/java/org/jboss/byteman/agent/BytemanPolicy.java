/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * @authors Andrew Dinn
 */

package org.jboss.byteman.agent;

/**
 * Security Policy class used to give the Byteman agent code all permissions
 * see JIRA issue BYTEMAN-233 for details
 */
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.security.Provider;

final class BytemanPolicy extends Policy {

    private static final AllPermission ALL_PERMISSION = new AllPermission();

    static final Permissions DEFAULT_PERMISSION_COLLECTION = getAllPermission();

    private static final CodeSource ourCodeSource = BytemanPolicy.class.getProtectionDomain().getCodeSource();

    private final Policy policy;

    private static Permissions getAllPermission() {
        final Permissions permissions = new Permissions();
        permissions.add(ALL_PERMISSION);
        return permissions;
    }

    public BytemanPolicy(final Policy policy) {
        this.policy = policy;
    }

    public Provider getProvider() {
        return policy.getProvider();
    }

    public String getType() {
        return policy.getType();
    }

    public Parameters getParameters() {
        return policy.getParameters();
    }

    public PermissionCollection getPermissions(final CodeSource codesource) {
        return codesource.equals(ourCodeSource) ? getAllPermission() : policy.getPermissions(codesource);
    }

    public PermissionCollection getPermissions(final ProtectionDomain domain) {
        return domain.getCodeSource().equals(ourCodeSource) ? getAllPermission() : policy.getPermissions(domain);
    }

    public boolean implies(final ProtectionDomain domain, final Permission permission) {
        return domain.getCodeSource().equals(ourCodeSource) || policy.implies(domain, permission);
    }

    public void refresh() {
        policy.refresh();
    }
}
