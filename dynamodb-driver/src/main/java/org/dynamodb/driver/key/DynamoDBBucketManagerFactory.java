package org.dynamodb.driver.key;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jnosql.diana.api.key.BucketManagerFactory;

public class DynamoDBBucketManagerFactory implements BucketManagerFactory<DynamoDBBucketManager>{

	@Override
	public DynamoDBBucketManager getBucketManager(String bucketName)
			throws UnsupportedOperationException, NullPointerException {
		// TODO Auto-generated method stub
		return null;
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
