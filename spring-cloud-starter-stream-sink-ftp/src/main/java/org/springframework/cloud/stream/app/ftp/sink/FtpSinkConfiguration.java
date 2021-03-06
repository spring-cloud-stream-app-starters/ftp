/*
 * Copyright 2015-2018 the original author or authors.
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

package org.springframework.cloud.stream.app.ftp.sink;

import org.apache.commons.net.ftp.FTPFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.app.ftp.FtpSessionFactoryConfiguration;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.ftp.dsl.Ftp;
import org.springframework.integration.ftp.dsl.FtpMessageHandlerSpec;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;

/**
 * @author Gary Russell
 * @author Artem Bilan
 */
@EnableBinding(Sink.class)
@EnableConfigurationProperties(FtpSinkProperties.class)
@Import(FtpSessionFactoryConfiguration.class)
public class FtpSinkConfiguration {

	@Autowired
	private Sink sink;

	@Bean
	public IntegrationFlow ftpInboundFlow(FtpSinkProperties properties, SessionFactory<FTPFile> ftpSessionFactory) {
		FtpMessageHandlerSpec handlerSpec =
				Ftp.outboundAdapter(new FtpRemoteFileTemplate(ftpSessionFactory), properties.getMode())
						.remoteDirectory(properties.getRemoteDir())
						.remoteFileSeparator(properties.getRemoteFileSeparator())
						.autoCreateDirectory(properties.isAutoCreateDir())
						.temporaryFileSuffix(properties.getTmpFileSuffix());
		if (properties.getFilenameExpression() != null) {
			handlerSpec.fileNameExpression(properties.getFilenameExpression().getExpressionString());
		}
		return IntegrationFlows.from(Sink.INPUT)
				.handle(handlerSpec)
				.get();
	}

}
