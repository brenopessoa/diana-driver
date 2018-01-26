package org.dynamodb.driver.key;

import java.time.Duration;
import java.util.Optional;

import org.jnosql.diana.api.Value;
import org.jnosql.diana.api.key.BucketManager;
import org.jnosql.diana.api.key.KeyValueEntity;

public class DynamoDBBucketManager implements BucketManager{

	@Override
	public <K, V> void put(K key, V value) throws NullPointerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <K> void put(KeyValueEntity<K> entity) throws NullPointerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <K> void put(KeyValueEntity<K> entity, Duration ttl)
			throws NullPointerException, UnsupportedOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <K> void put(Iterable<KeyValueEntity<K>> entities) throws NullPointerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <K> void put(Iterable<KeyValueEntity<K>> entities, Duration ttl)
			throws NullPointerException, UnsupportedOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <K> Optional<Value> get(K key) throws NullPointerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> Iterable<Value> get(Iterable<K> keys) throws NullPointerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K> void remove(K key) throws NullPointerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <K> void remove(Iterable<K> keys) throws NullPointerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
