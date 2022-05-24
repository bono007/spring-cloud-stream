/*
 * Copyright 2022-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.binder.kinesis;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClientBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
/**
 *
 * @author Artem Bilan
 * @author Chris Bono
 *
 * @since 4.0
 */
@Testcontainers(disabledWithoutDocker = true)
@DisabledOnOs(OS.MAC)
public interface LocalstackContainerTest {

	LocalStackContainer LOCAL_STACK_CONTAINER =
		new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
			.withServices(
				LocalStackContainer.Service.DYNAMODB,
				LocalStackContainer.Service.KINESIS,
				LocalStackContainer.Service.CLOUDWATCH);

	@BeforeAll
	static void startContainer() {
		LOCAL_STACK_CONTAINER.start();
	}

	static AmazonDynamoDBAsync dynamoDbClient() {
		return applyAwsClientOptions(AmazonDynamoDBAsyncClientBuilder.standard(), LocalStackContainer.Service.DYNAMODB);
	}

	static AmazonKinesisAsync kinesisClient() {
		return applyAwsClientOptions(AmazonKinesisAsyncClientBuilder.standard(), LocalStackContainer.Service.KINESIS);
	}

	static AmazonCloudWatch cloudWatchClient() {
		return applyAwsClientOptions(AmazonCloudWatchClientBuilder.standard(), LocalStackContainer.Service.CLOUDWATCH);
	}

	private static <B extends AwsClientBuilder<B, T>, T> T applyAwsClientOptions(B clientBuilder,
			LocalStackContainer.Service serviceToBuild) {

		return clientBuilder.withEndpointConfiguration(LOCAL_STACK_CONTAINER.getEndpointConfiguration(serviceToBuild))
				.withCredentials(LOCAL_STACK_CONTAINER.getDefaultCredentialsProvider())
				.build();
	}

}
