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
package org.jnosql.diana.arangodb.document;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentUpdateEntity;
import org.jnosql.diana.api.ValueWriter;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;
import org.jnosql.diana.api.writer.ValueWriterDecorator;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.jnosql.diana.arangodb.document.ArangoDBUtil.getBaseDocument;

class DefaultArangoDBDocumentCollectionManager implements ArangoDBDocumentCollectionManager {


    public static final String KEY = "_key";
    public static final String ID = "_id";
    public static final String REV = "_rev";
    private final String database;

    private final ArangoDB arangoDB;

    private final ValueWriter writerField = ValueWriterDecorator.getInstance();

    DefaultArangoDBDocumentCollectionManager(String database, ArangoDB arangoDB) {
        this.database = database;
        this.arangoDB = arangoDB;
    }


    @Override
    public DocumentEntity insert(DocumentEntity entity) throws NullPointerException {
        String collectionName = entity.getName();
        checkCollection(collectionName);
        BaseDocument baseDocument = getBaseDocument(entity);
        DocumentCreateEntity<BaseDocument> arandoDocument = arangoDB.db(database).collection(collectionName).insertDocument(baseDocument);
        updateEntity(entity, arandoDocument.getKey(), arandoDocument.getId(), arandoDocument.getRev());
        return entity;
    }

    @Override
    public DocumentEntity update(DocumentEntity entity) {
        String collectionName = entity.getName();
        checkCollection(collectionName);
        BaseDocument baseDocument = getBaseDocument(entity);
        DocumentUpdateEntity<BaseDocument> arandoDocument = arangoDB.db(database)
                .collection(collectionName).updateDocument(baseDocument.getKey(), baseDocument);
        updateEntity(entity, arandoDocument.getKey(), arandoDocument.getId(), arandoDocument.getRev());
        return entity;
    }

    @Override
    public void delete(DocumentDeleteQuery query) {
        requireNonNull(query, "query is required");
        if (checkCondition(query.getCondition())) {
            return;
        }

        AQLQueryResult delete = QueryAQLConverter.delete(query);
        arangoDB.db(database).query(delete.getQuery(), delete.getValues(),
                null, BaseDocument.class);


    }

    @Override
    public List<DocumentEntity> select(DocumentQuery query) throws NullPointerException {
        requireNonNull(query, "query is required");

        AQLQueryResult result = QueryAQLConverter.select(query);
        ArangoCursor<BaseDocument> documents = arangoDB.db(database).query(result.getQuery(),
                result.getValues(), null, BaseDocument.class);

        return StreamSupport.stream(documents.spliterator(), false)
                .map(ArangoDBUtil::toEntity)
                .collect(toList());
    }


    @Override
    public List<DocumentEntity> aql(String query, Map<String, Object> values) throws NullPointerException {
        requireNonNull(query, "query is required");
        requireNonNull(values, "values is required");

        ArangoCursor<BaseDocument> result = arangoDB.db(database).query(query, values, null, BaseDocument.class);
        return StreamSupport.stream(result.spliterator(), false)
                .map(ArangoDBUtil::toEntity)
                .collect(toList());

    }


    @Override
    public void close() {

    }


    private void checkCollection(String collectionName) {
        ArangoDBUtil.checkCollection(database, arangoDB, collectionName);
    }

    private boolean checkCondition(Optional<DocumentCondition> query) {
        return !query.isPresent();
    }


    @Override
    public DocumentEntity insert(DocumentEntity entity, Duration ttl) {
        throw new UnsupportedOperationException("TTL is not supported on ArangoDB implementation");
    }

    private void updateEntity(DocumentEntity entity, String key, String id, String rev) {
        entity.add(Document.of(KEY, key));
        entity.add(Document.of(ID, id));
        entity.add(Document.of(REV, rev));
    }


}
