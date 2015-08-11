/*
 * Copyright (c) 2002-2015 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.locking;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LockClientStateHolderTest
{

    @Test
    public void shouldAllowIncrementDecrementClientsWhileNotClosed()
    {
        // given
        LockClientStateHolder lockClientStateHolder = new LockClientStateHolder();

        // expect
        assertFalse( lockClientStateHolder.hasActiveClients() );
        assertTrue( lockClientStateHolder.incrementActiveClients() );
        assertTrue( lockClientStateHolder.hasActiveClients() );
        assertTrue( lockClientStateHolder.incrementActiveClients() );
        assertTrue( lockClientStateHolder.incrementActiveClients() );
        lockClientStateHolder.decrementActiveClients();
        lockClientStateHolder.decrementActiveClients();
        lockClientStateHolder.decrementActiveClients();
        assertFalse( lockClientStateHolder.hasActiveClients() );
    }

    @Test
    public void shouldNotAllowNewClientsWhenClosed()
    {
        // given
        LockClientStateHolder lockClientStateHolder = new LockClientStateHolder();

        // when
        lockClientStateHolder.closeClient();

        // then
        assertFalse( lockClientStateHolder.hasActiveClients() );
        assertFalse( lockClientStateHolder.incrementActiveClients() );
    }

    @Test
    public void shouldBeAbleToDecrementActiveItemAndDetectWhenFree()
    {
        // given
        LockClientStateHolder lockClientStateHolder = new LockClientStateHolder();

        // when
        lockClientStateHolder.incrementActiveClients();
        lockClientStateHolder.incrementActiveClients();
        lockClientStateHolder.decrementActiveClients();
        lockClientStateHolder.incrementActiveClients();

        // expect
        assertTrue( lockClientStateHolder.hasActiveClients() );

        // and when
        lockClientStateHolder.closeClient();

        // expect
        assertTrue( lockClientStateHolder.hasActiveClients() );
        lockClientStateHolder.decrementActiveClients();
        assertTrue( lockClientStateHolder.hasActiveClients() );
        lockClientStateHolder.decrementActiveClients();
        assertFalse( lockClientStateHolder.hasActiveClients() );
    }

    @Test
    public void shouldBeAbleToResetAndReuseClientState()
    {
        // given
        LockClientStateHolder lockClientStateHolder = new LockClientStateHolder();

        // when
        assertTrue( lockClientStateHolder.incrementActiveClients() );
        assertTrue( lockClientStateHolder.incrementActiveClients() );
        lockClientStateHolder.decrementActiveClients();

        // expect
        assertTrue(lockClientStateHolder.hasActiveClients());

        // and when
        lockClientStateHolder.closeClient();

        // expect
        assertTrue( lockClientStateHolder.hasActiveClients() );
        assertTrue( lockClientStateHolder.isClosed() );

        // and when
        lockClientStateHolder.reset();

        // expect
        assertFalse( lockClientStateHolder.hasActiveClients() );
        assertFalse( lockClientStateHolder.isClosed() );

        // when
        assertTrue( lockClientStateHolder.incrementActiveClients() );
        assertTrue( lockClientStateHolder.hasActiveClients() );
        assertFalse( lockClientStateHolder.isClosed() );
    }

}