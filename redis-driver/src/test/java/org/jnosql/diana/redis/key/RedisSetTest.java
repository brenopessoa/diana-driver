/*
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */

package org.jnosql.diana.redis.key;


import org.jnosql.diana.api.key.BucketManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RedisSetTest {

    private BucketManagerFactory keyValueEntityManagerFactory;
    private User userOtavioJava = new User("otaviojava");
    private User felipe = new User("ffrancesquini");
    private Set<User> users;

    @BeforeEach
    public void init() {
        keyValueEntityManagerFactory = RedisTestUtils.get();
        users = keyValueEntityManagerFactory.getSet("social-media", User.class);
    }

    @Test
    public void shouldAddUsers() {
        users.add(userOtavioJava);
        assertTrue(users.size() == 1);
    }

    @Test
    public void shouldRemove() {
        users.add(userOtavioJava);
        users.add(felipe);
        users.remove(felipe);

        assertTrue(users.size() == 1);
        assertThat(users, not(contains(felipe)));
   }

    @Test
    public void shouldRemoveAll() {
        users.add(userOtavioJava);
        users.add(felipe);
        users.removeAll(Arrays.asList(felipe, userOtavioJava));

        assertTrue(users.size() == 0);
    }

    @SuppressWarnings("unused")
    @Test
    public void shouldIterate() {
        users.add(userOtavioJava);
        users.add(userOtavioJava);
        users.add(felipe);
        users.add(userOtavioJava);
        users.add(felipe);
        int count = 0;
        for (User user : users) {
            count++;
        }
        assertTrue(count == 2);
    }

    @Test
    public void shouldContains() {
        users.add(userOtavioJava);
        assertTrue(users.contains(userOtavioJava));
    }

    @Test
    public void shouldContainsAll() {
        users.add(userOtavioJava);
        users.add(felipe);
        assertTrue(users.containsAll(Arrays.asList(userOtavioJava, felipe)));
    }

    @Test
    public void shouldReturnSize() {
        users.add(userOtavioJava);
        users.add(felipe);
        assertTrue(users.size() == 2);
    }

    @Test
    public void shouldClear() {
        users.add(userOtavioJava);
        users.add(felipe);

        users.clear();
        assertTrue(users.isEmpty());
    }

    @Test
    public void shouldThrowExceptionRetainAll() {
        assertThrows(UnsupportedOperationException.class, () -> users.retainAll(Collections.singletonList(userOtavioJava)));
    }

    @AfterEach
    public void dispose() {
        users.clear();
    }
}
