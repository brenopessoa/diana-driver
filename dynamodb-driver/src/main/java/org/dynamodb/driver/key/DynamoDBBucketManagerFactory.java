package org.dynamodb.driver.key;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.jnosql.diana.api.key.BucketManagerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

public class DynamoDBBucketManagerFactory implements BucketManagerFactory<DynamoDBBucketManager>{
	
	private final AmazonDynamoDB client = DynamoDBEmbedded.create().amazonDynamoDB();
	private final DynamoDB dynamoDB = new DynamoDB(client);
	private static final Jsonb JSON = JsonbBuilder.create();

	@Override
	public DynamoDBBucketManager getBucketManager(String bucketName)
			throws UnsupportedOperationException, NullPointerException {
		
		Objects.requireNonNull(bucketName,"teste");
		
		Table table = dynamoDB.getTable(bucketName);

		if(table != null){
		
			String key = "key";
	
			List<KeySchemaElement> keySchema = Arrays.asList(new KeySchemaElement(key,KeyType.HASH));
			List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
		    attributeDefinitions.add(new AttributeDefinition(key,ScalarAttributeType.S));
		    
		    ProvisionedThroughput provisionedthroughput = new ProvisionedThroughput(1000L, 1000L);
		    
		    CreateTableRequest createTableRequest = new CreateTableRequest()
					.withTableName(bucketName)
					.withAttributeDefinitions(attributeDefinitions)
					.withProvisionedThroughput(provisionedthroughput)
					.withKeySchema(keySchema);
		    
		    
		   
		    table = dynamoDB.createTable(createTableRequest);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Set<T> getSet(String bucketName, Class<T> clazz)
			throws UnsupportedOperationException, NullPointerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Queue<T> getQueue(String bucketName, Class<T> clazz)
			throws UnsupportedOperationException, NullPointerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, V> Map<K, V> getMap(String bucketName, Class<K> keyValue, Class<V> valueValue)
			throws UnsupportedOperationException, NullPointerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
