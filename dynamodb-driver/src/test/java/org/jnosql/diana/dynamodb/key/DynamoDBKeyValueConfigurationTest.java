/*
 *  Copyright (c) 2018 Otávio Santana and others
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
package org.jnosql.diana.dynamodb.key;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.nosql.key.BucketManagerFactory;
import jakarta.nosql.key.KeyValueConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DynamoDBKeyValueConfigurationTest {
	
	private DynamoDBKeyValueConfiguration configuration;
	
	@BeforeEach
	public void setUp(){
		configuration = new DynamoDBKeyValueConfiguration();
	}

	@Test
	public void shouldCreateKeyValueFactoryFromFile() {
		BucketManagerFactory managerFactory = configuration.get();
		assertNotNull(managerFactory);
	}

	@Test
	public void shouldReturnFromConfiguration() {
		DynamoDBKeyValueConfiguration configuration = KeyValueConfiguration.getConfiguration();
		Assertions.assertNotNull(configuration);
		Assertions.assertTrue(configuration instanceof DynamoDBKeyValueConfiguration);
	}

	@Test
	public void shouldReturnFromConfigurationQuery() {
		DynamoDBKeyValueConfiguration configuration = KeyValueConfiguration
				.getConfiguration(DynamoDBKeyValueConfiguration.class);
		Assertions.assertNotNull(configuration);
		Assertions.assertTrue(configuration instanceof DynamoDBKeyValueConfiguration);
	}
}
