package org.dynamodb.driver.key;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jnosql.diana.api.Settings;
import org.jnosql.diana.api.key.KeyValueConfiguration;

public class DynamoDBKeyValueConfiguration implements KeyValueConfiguration<DynamoDBBucketManagerFactory> {

	private static final String DYNAMODB_FILE_CONFIGURATION = "diana-dynamodb.properties";

	private static final Logger LOGGER = Logger.getLogger(DynamoDBKeyValueConfiguration.class.getName());
	
	public DynamoDBBucketManagerFactory getManagerFactory(Map<String, String> configurations) {
       // JedisPoolConfig poolConfig = getJedisPoolConfig(configurations);
        //JedisPool jedisPool = getJedisPool(configurations, poolConfig);

        return new DynamoDBBucketManagerFactory();
    }
	
//	private JedisPool getJedisPool(Map<String, String> configurations, JedisPoolConfig poolConfig) {
//        String localhost = configurations.getOrDefault("redis-master-host", "localhost");
//        Integer port = Integer.valueOf(configurations.getOrDefault("redis-master-port", "6379"));
//        Integer timeout = Integer.valueOf(configurations.getOrDefault("redis-timeout", "2000"));
//        String password = configurations.getOrDefault("redis-password", null);
//        Integer database = Integer.valueOf(configurations.getOrDefault("redis-database", "0"));
//        String clientName = configurations.getOrDefault("redis-clientName", null);
//        return new JedisPool(poolConfig, localhost, port, timeout, password, database, clientName);
//    }
//
//    private JedisPoolConfig getJedisPoolConfig(Map<String, String> configurations) {
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxTotal(Integer.valueOf(configurations.getOrDefault("redis-configuration-max-total", "1000")));
//        poolConfig.setMaxIdle(Integer.valueOf(configurations.getOrDefault("redis-configuration-max-idle", "10")));
//        poolConfig.setMinIdle(Integer.valueOf(configurations.getOrDefault("redis-configuration-min-idle", "1")));
//        poolConfig.setMaxWaitMillis(Integer.valueOf(configurations
//                .getOrDefault("redis-configuration-max--wait-millis", "3000")));
//        return poolConfig;
//    }


	@Override
	public DynamoDBBucketManagerFactory get() {
		try {
			
			Properties properties = new Properties();
			
			InputStream stream = DynamoDBKeyValueConfiguration.class.getClassLoader()
					.getResourceAsStream(DYNAMODB_FILE_CONFIGURATION);
			properties.load(stream);
			
			Map<String, String> collect = properties.keySet().stream()
					.collect(Collectors.toMap(Object::toString, s -> properties.get(s).toString()));
			
			return getManagerFactory(collect);
		} catch (IOException e) {
			LOGGER.info("File does not found using default configuration");
			return getManagerFactory(Collections.emptyMap());
		}
	}

	@Override
	public DynamoDBBucketManagerFactory get(Settings settings) {
		Objects.requireNonNull(settings, "settings is required");
		Map<String, String> configurations = new HashMap<>();
		settings.forEach((key, value) -> configurations.put(key, value.toString()));
		return getManagerFactory(configurations);
	}

}
