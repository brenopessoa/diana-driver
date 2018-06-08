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

package org.dynamodb.driver.key;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.jnosql.diana.api.key.BucketManagerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import static org.dynamodb.driver.key.DynamoDBKeyValueUtils.VALUE;

public class DynamoDBBucketManagerFactory implements BucketManagerFactory<DynamoDBBucketManager>{
	
	private final AmazonDynamoDB client = DynamoDBEmbedded.create().amazonDynamoDB();
	private final DynamoDB dynamoDB = new DynamoDB(client);
	private static final Jsonb JSON = JsonbBuilder.create();

	@Override
	public DynamoDBBucketManager getBucketManager(String bucketName)
			throws UnsupportedOperationException, NullPointerException {
		
		Objects.requireNonNull(bucketName,"teste");
		
		Table table = dynamoDB.getTable(bucketName);

		if(table == null){
		    table = dynamoDB.createTable(DynamoDBKeyValueUtils.createTableRequest(bucketName));
		    try {
				table.waitForActive();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		
		return new DynamoDBBucketManager(client, table, dynamoDB,JSON);
	}

	@Override
	public <T> List<T> getList(String bucketName, Class<T> clazz)
			throws UnsupportedOperationException, NullPointerException {
		
		
		
		Objects.requireNonNull(bucketName,"teste");
		
		Table table = dynamoDB.getTable(bucketName);
		
		Spliterator<Item> spliterator = table.query(new QuerySpec()).spliterator();
		
		StreamSupport.stream(spliterator, false)
			.map(i -> i.get(VALUE))
			.map(o -> JSON.fromJson(o.toString(),clazz));
		
		
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Set<T> getSet(String bucketName, Class<T> clazz)
			throws UnsupportedOperationException, NullPointerException {
		
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> Queue<T> getQueue(String bucketName, Class<T> clazz)
			throws UnsupportedOperationException, NullPointerException {
		
		throw new UnsupportedOperationException();
	}

	@Override
	public <K, V> Map<K, V> getMap(String bucketName, Class<K> keyValue, Class<V> valueValue)
			throws UnsupportedOperationException, NullPointerException {
		
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
