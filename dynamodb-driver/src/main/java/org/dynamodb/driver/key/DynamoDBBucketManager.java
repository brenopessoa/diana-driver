package org.dynamodb.driver.key;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.json.bind.Jsonb;

import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.key.BucketManager;
import org.jnosql.diana.api.key.KeyValueEntity;
import org.jnosql.diana.driver.ValueJSON;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.BatchGetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.TimeToLiveSpecification;
import com.amazonaws.services.dynamodbv2.model.UpdateTimeToLiveRequest;

public class DynamoDBBucketManager implements BucketManager{

	private final AmazonDynamoDB client;
	private final Table table;
	private final DynamoDB dynamoDB;
	private final Jsonb jsonB;
	
	public DynamoDBBucketManager(AmazonDynamoDB client, Table table, DynamoDB dynamoDB, Jsonb jsonB) {
		this.client = client;
		this.table = table;
		this.dynamoDB = dynamoDB;
		this.jsonB = jsonB;
	}

	@Override
	public <K, V> void put(K key, V value) throws NullPointerException {
		
	    Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
	    AttributeValue attributeKey = new AttributeValue(key.toString());
	    AttributeValue atrributeValue = new AttributeValue(jsonB.toJson(value.toString()));
	    item.put("key",attributeKey);
	    item.put("value", atrributeValue);
	       
	    PutItemRequest putItemRequest = new PutItemRequest(table.getTableName(),item);
	    client.putItem(putItemRequest);
		
	}

	@Override
	public <K> void put(KeyValueEntity<K> entity) throws NullPointerException {
		put(entity.getKey(),entity.getValue());
	}

	@Override
	public <K> void put(KeyValueEntity<K> entity, Duration ttl)
			throws NullPointerException, UnsupportedOperationException {
		
        UpdateTimeToLiveRequest req = new UpdateTimeToLiveRequest();
        req.setTableName(table.getTableName());

        TimeToLiveSpecification ttlSpec = new TimeToLiveSpecification();
        ttlSpec.setEnabled(true);
        req.withTimeToLiveSpecification(ttlSpec);
        
        client.updateTimeToLive(req);
        put(entity);
	}

	@Override
	public <K> void put(Iterable<KeyValueEntity<K>> entities) throws NullPointerException {
		
		List<Item> items = StreamSupport.stream(entities.spliterator(),false)
			.map(e -> new Item().withPrimaryKey("key",e.getKey().toString()))
			.collect(Collectors.toList());
		
		TableWriteItems tableWriteItems = new TableWriteItems(table.getTableName()).withItemsToPut(items);
		
		dynamoDB.batchWriteItem(tableWriteItems);
	}

	@Override
	public <K> void put(Iterable<KeyValueEntity<K>> entities, Duration ttl)
			throws NullPointerException, UnsupportedOperationException {
		
		UpdateTimeToLiveRequest req = new UpdateTimeToLiveRequest();
        req.setTableName(table.getTableName());

        TimeToLiveSpecification ttlSpec = new TimeToLiveSpecification();
        ttlSpec.setEnabled(true);
        req.withTimeToLiveSpecification(ttlSpec);
        
        client.updateTimeToLive(req);
        put(entities);
		
	}

	@Override
	public <K> Optional<Value> get(K key) throws NullPointerException {
		
		Objects.requireNonNull(key);
		GetItemSpec spec = new GetItemSpec().withPrimaryKey("key",key.toString());
		Item item = table.getItem(spec);
		Value of = ValueJSON.of(item.get("value"));
		return Optional.ofNullable(of);
		
	}

	@Override
	public <K> Iterable<Value> get(Iterable<K> keys) throws NullPointerException {
		
		TableKeysAndAttributes tableKeysAndAttributes = new TableKeysAndAttributes(table.getTableName());
		StreamSupport.stream(keys.spliterator(),false)
			.forEach(key -> tableKeysAndAttributes.addHashOnlyPrimaryKey("key",key.toString()));
	
		BatchGetItemOutcome batchGetItem = dynamoDB.batchGetItem(tableKeysAndAttributes);
		
		Map<String, List<Item>> itemsTable = batchGetItem.getTableItems();
		
		return StreamSupport.stream(itemsTable.keySet().spliterator(),false)
			.flatMap(table -> itemsTable.get(table).stream())
			.map(item -> ValueJSON.of(item.get("value")))
			.collect(Collectors.toList());
	}

	@Override
	public <K> void remove(K key) throws NullPointerException {
		
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
        		.withPrimaryKey(new PrimaryKey("key",key.toString()));
        
        table.deleteItem(deleteItemSpec);
	}

	@Override
	public <K> void remove(Iterable<K> keys) throws NullPointerException {
		StreamSupport.stream(keys.spliterator(),false).forEach(this::remove);
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
