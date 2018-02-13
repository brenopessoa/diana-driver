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


import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.key.BucketManager;
import org.jnosql.diana.api.key.BucketManagerFactory;
import org.jnosql.diana.api.key.KeyValueEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RedisBucketManagerTest {

    private BucketManager keyValueEntityManager;

    private BucketManagerFactory keyValueEntityManagerFactory;

    private User userOtavio = new User("otavio");
    private KeyValueEntity keyValueOtavio = KeyValueEntity.of("otavio", Value.of(userOtavio));

    private User userSoro = new User("soro");
    private KeyValueEntity keyValueSoro = KeyValueEntity.of("soro", Value.of(userSoro));

    @BeforeEach
    public void init() {
        keyValueEntityManagerFactory = RedisTestUtils.get();
        keyValueEntityManager = keyValueEntityManagerFactory.getBucketManager("users-entity");
    }


    @Test
    public void shouldPutValue() {
        keyValueEntityManager.put("otavio", userOtavio);
        Optional<Value> otavio = keyValueEntityManager.get("otavio");
        assertTrue(otavio.isPresent());
        assertEquals(userOtavio, otavio.get().get(User.class));
    }

    @Test
    public void shouldPutKeyValue() {
        keyValueEntityManager.put(keyValueOtavio);
        Optional<Value> otavio = keyValueEntityManager.get("otavio");
        assertTrue(otavio.isPresent());
        assertEquals(userOtavio, otavio.get().get(User.class));
    }

    @Test
    public void shouldPutIterableKeyValue() {
        keyValueEntityManager.put(asList(keyValueSoro, keyValueOtavio));
        Optional<Value> otavio = keyValueEntityManager.get("otavio");
        assertTrue(otavio.isPresent());
        assertEquals(userOtavio, otavio.get().get(User.class));

        Optional<Value> soro = keyValueEntityManager.get("soro");
        assertTrue(soro.isPresent());
        assertEquals(userSoro, soro.get().get(User.class));
    }

    @Test
    public void shouldMultiGet() {
        User user = new User("otavio");
        KeyValueEntity keyValue = KeyValueEntity.of("otavio", Value.of(user));
        keyValueEntityManager.put(keyValue);
        assertNotNull(keyValueEntityManager.get("otavio"));
    }

    @Test
    public void shouldRemoveKey() {
        keyValueEntityManager.put(keyValueOtavio);
        assertTrue(keyValueEntityManager.get("otavio").isPresent());
        keyValueEntityManager.remove("otavio");
        assertFalse(keyValueEntityManager.get("otavio").isPresent());
    }

    @Test
    public void shouldRemoveMultiKey() {
        keyValueEntityManager.put(asList(keyValueSoro, keyValueOtavio));
        List<String> keys = asList("otavio", "soro");
        Iterable<Value> values = keyValueEntityManager.get(keys);
        assertThat(StreamSupport.stream(values.spliterator(), false).map(value -> value.get(User.class)).collect(Collectors.toList()), containsInAnyOrder(userOtavio, userSoro));
        keyValueEntityManager.remove(keys);
        assertEquals(0L, StreamSupport.stream(keyValueEntityManager.get(keys).spliterator(), false).count());
    }

    @AfterEach
    public void remove() {
        keyValueEntityManager.remove(Arrays.asList("otavio", "soro"));
    }
}
