package org.dynamodb.driver.key;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.StreamSupport;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

public class DynamoDBKeyValueUtils {
	
	public static final String KEY = "key"; 
	
	public static final String VALUE = "value"; 

	public static Item createDynamoItem(String key){
		
		return new Item().withPrimaryKey(KEY,key);
		
	}
	
	public static TableWriteItems createTableWriteItems(String tableName , Collection<Item> items){
		return  new TableWriteItems(tableName).withItemsToPut(items);
	}
	
	public static DeleteItemSpec createDeleteItemSpec(String key){ 
		return new DeleteItemSpec().withPrimaryKey(new PrimaryKey(KEY,key));
	}
	
	public static <K> TableKeysAndAttributes createTableKeysAndAttributes(String tableName, Iterable<K> keys){
		
		TableKeysAndAttributes tableKeysAndAttributes = new TableKeysAndAttributes(tableName);
		
		StreamSupport.stream(keys.spliterator(),false)
			.forEach(key -> tableKeysAndAttributes.addHashOnlyPrimaryKey(KEY,key.toString()));
		
		return tableKeysAndAttributes;
		
	}
	
	public static Item createDynamoItem(GetItemSpec e){
		return null;
	}
	
	public static CreateTableRequest createTableRequest(String tableName) {
		
		List<KeySchemaElement> keySchema = Arrays.asList(new KeySchemaElement(KEY,KeyType.HASH));
		List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
	    attributeDefinitions.add(new AttributeDefinition(KEY,ScalarAttributeType.S));
	    
	    ProvisionedThroughput provisionedthroughput = new ProvisionedThroughput(1000L, 1000L);
	    
	    return new CreateTableRequest()
				.withTableName(tableName)
				.withAttributeDefinitions(attributeDefinitions)
				.withProvisionedThroughput(provisionedthroughput)
				.withKeySchema(keySchema);
		
	}
}
