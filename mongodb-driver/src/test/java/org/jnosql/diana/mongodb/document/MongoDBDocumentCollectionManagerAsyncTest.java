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
package org.jnosql.diana.mongodb.document;

import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCollectionManagerAsync;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;
import org.jnosql.diana.api.document.Documents;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.notNullValue;
import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.delete;
import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.select;
import static org.jnosql.diana.mongodb.document.DocumentConfigurationUtils.getAsync;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class MongoDBDocumentCollectionManagerAsyncTest {

    public static final String COLLECTION_NAME = "person";

    private static DocumentCollectionManagerAsync entityManager;

    @BeforeAll
    public static void setUp() throws IOException, InterruptedException {
        MongoDbHelper.startMongoDb();
        entityManager = getAsync().getAsync("database");
    }


    @Test
    public void shouldInsertAsync() throws InterruptedException {
        AtomicBoolean condition = new AtomicBoolean(false);
        DocumentEntity entity = getEntity();
        entityManager.insert(entity, c -> condition.set(true));
        await().untilTrue(condition);
        assertTrue(condition.get());
    }

    @Test
    public void shouldInsertIterableAsync() throws InterruptedException {
        List<DocumentEntity> entities = Collections.singletonList(getEntity());
        entityManager.insert(entities);
    }

    @Test
    public void shouldThrowExceptionWhenInsertWithTTL() {
        assertThrows(UnsupportedOperationException.class, () -> entityManager.insert(getEntity(), Duration.ofSeconds(10)));
    }

    @Test
    public void shouldThrowExceptionWhenInsertWithTTLWithCallback() {
        assertThrows(UnsupportedOperationException.class, () -> entityManager.insert(getEntity(), Duration.ofSeconds(10), r -> {}));
    }

    @Test
    public void shouldUpdateAsync() throws InterruptedException {

        Random random = new Random();
        long id = random.nextLong();
        
        AtomicBoolean condition = new AtomicBoolean(false);
        AtomicReference<DocumentEntity> reference = new AtomicReference<>();
        DocumentEntity entity = getEntity();
        entity.add("_id", id);
        entityManager.insert(entity, c -> {
            condition.set(true);
            reference.set(c);
        });
        await().untilTrue(condition);
        entityManager.update(reference.get(), c -> condition.set(true));
        assertTrue(condition.get());
    }

    @Test
    public void shouldUpdateIterableAsync() throws InterruptedException {
        Random random = new Random();
        long id = random.nextLong();

        AtomicBoolean condition = new AtomicBoolean(false);
        AtomicReference<DocumentEntity> reference = new AtomicReference<>();
        DocumentEntity entity = getEntity();
        entity.add("_id", id);
        entityManager.insert(entity, c -> {
            condition.set(true);
            reference.set(c);
        });
        await().untilTrue(condition);
        entityManager.update(Collections.singletonList(entity));

    }


    @Test
    public void shouldSelect() throws InterruptedException {
        DocumentEntity entity = getEntity();
        for (int index = 0; index < 10; index++) {
            entityManager.insert(entity);
        }
        AtomicBoolean condition = new AtomicBoolean(false);
        DocumentQuery query = select().from(entity.getName()).build();
        AtomicReference<List<DocumentEntity>> atomicReference = new AtomicReference<>();

        entityManager.select(query, c -> {
            condition.set(true);
            atomicReference.set(c);
        });

        await().untilTrue(condition);
        List<DocumentEntity> entities = atomicReference.get();
        assertTrue(condition.get());
        assertFalse(entities.isEmpty());

    }


    @Test
    public void shouldRemoveEntityAsync() throws InterruptedException {
        AtomicReference<DocumentEntity> entityAtomic = new AtomicReference<>();
        entityManager.insert(getEntity(), entityAtomic::set);
        await().until(entityAtomic::get, notNullValue(DocumentEntity.class));

        DocumentEntity entity = entityAtomic.get();
        Document document = entity.find("name").get();
        assertNotNull(entity);
        assertNotNull(document);

        String collection = entity.getName();
        DocumentDeleteQuery deleteQuery = delete().from(collection)
                .where("name").eq(document.get())
                .build();
        AtomicBoolean condition = new AtomicBoolean(false);
        entityManager.delete(deleteQuery, c -> condition.set(true));
        await().untilTrue(condition);

        AtomicBoolean selectCondition = new AtomicBoolean(false);
        AtomicReference<List<DocumentEntity>> reference = new AtomicReference<>();
        DocumentQuery selectQuery = select().from(collection)
                .where("name").eq(document.get())
                .build();
        entityManager.select(selectQuery, c -> {
            selectCondition.set(true);
            reference.set(c);
        });
        await().untilTrue(selectCondition);

        assertTrue(reference.get().isEmpty());
    }

    @Test
    public void shouldRemoveEntity() {
        AtomicReference<DocumentEntity> entityAtomic = new AtomicReference<>();
        entityManager.insert(getEntity(), entityAtomic::set);
        await().until(entityAtomic::get, notNullValue(DocumentEntity.class));

        DocumentEntity entity = entityAtomic.get();
        Document document = entity.find("name").get();

        DocumentDeleteQuery deleteQuery = delete().from(entity.getName())
                .where(document.getName()).eq(document.get())
                .build();
        entityManager.delete(deleteQuery);
    }

    private DocumentEntity getEntity() {
        DocumentEntity entity = DocumentEntity.of(COLLECTION_NAME);
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Poliana");
        map.put("city", "Salvador");
        List<Document> documents = Documents.of(map);
        documents.forEach(entity::add);
        return entity;
    }

    @AfterAll
    public static void end() {
        MongoDbHelper.stopMongoDb();
    }
}