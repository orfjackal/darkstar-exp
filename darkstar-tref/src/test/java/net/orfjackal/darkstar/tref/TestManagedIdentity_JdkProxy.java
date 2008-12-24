/*
 * Copyright (c) 2008  Esko Luontola, www.orfjackal.net
 *
 * This program is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation and
 * distributed hereunder to you.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.orfjackal.darkstar.tref;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Esko Luontola
 * @since 1.2.2008
 */
public class TestManagedIdentity_JdkProxy extends TestManagedIdentity {

    public static Test suite() {
        return new TestSuite(TestManagedIdentity_JdkProxy.class.getDeclaredClasses());
    }

    public static class ManagedIdentityContracts_JdkProxy
            extends TestManagedIdentity.ManagedIdentityContracts {

        protected void setUp() throws Exception {
            factory = new TransparentReferenceJdkProxyFactory();
            super.setUp();
        }
    }
}
