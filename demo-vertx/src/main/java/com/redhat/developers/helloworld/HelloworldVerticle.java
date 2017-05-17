/**
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.developers.helloworld;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;

public class HelloworldVerticle extends AbstractVerticle {

	private static final String version = "1.0";
	private Logger logger = LoggerFactory.getLogger(HelloworldVerticle.class);
	private final String hostname = System.getenv().getOrDefault("HOSTNAME", "unknown");
	private static Cache<String, String> cacheInstance;

	@Override
	public void start() throws Exception {
		Router router = Router.router(vertx);

		// Config CORS
		router.route().handler(CorsHandler.create("*").allowedMethod(HttpMethod.GET).allowedHeader("Content-Type"));

		// hello endpoint
		router.get("/api/hello/:name").handler(ctx -> {
			String helloMsg = hello(ctx.request().getParam("name"));
			logger.info("New request from " + ctx.request().getHeader("User-Agent") + "\nSaying...: " + helloMsg);
			ctx.response().end(helloMsg);
		});

		// Add Stuff
		router.get("/api/addstuff").blockingHandler(ctx -> {
			String name = ctx.request().getParam("name");
			if (name == null) {
				ctx.response().end("Missing name parameter");
			} else {
				Cache<String, String> cache = getConfiguredCache();
				int currentSize = cache.keySet().size();
				// using the size as a way to provide uniqueness to keys
				String key = currentSize + "_" + hostname;
				cache.put(key, name);
				ctx.response().end("Added " + name + " to " + key);
			}
		});

		// Clear
		router.get("/api/clearstuff").blockingHandler(ctx -> {
			Cache<String, String> cache = getConfiguredCache();
			cache.clear();
			ctx.response().end("Cleared");
		});

		// Get Stuff
		router.get("/api/getstuff").blockingHandler(ctx -> {
			Cache<String, String> cache = getConfiguredCache();
			StringBuilder sb = new StringBuilder();
			Set<String> keySet = cache.keySet();
			sb.append("VERSION " + version + " - " + hostname + " has values: ");
			for (String key : keySet) {
				String value = cache.get(key);
				System.out.println("k: " + key + " v: " + value);
				sb.append(key + "=" + value);
				sb.append(" | ");
			} // for
			ctx.response().end(sb.toString());
		});

		// health check endpoint
		router.get("/api/health").handler(ctx -> {
			ctx.response().end("I'm ok");
		});
		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
	}

	private Cache<String, String> getConfiguredCache() {
		if (cacheInstance == null) {
			GlobalConfiguration gc = GlobalConfigurationBuilder.defaultClusteredBuilder()
					// Use this line for testing in Kubernetes. But it requires
					// additional configuration:
					// oc policy add-role-to-user view
					// system:serviceaccount:$(oc
					// project -q):default -n $(oc project -q)
					// And setting OPENSHIFT_KUBE_PING_NAMESPACE env variable to
					// your namespace
					.transport().defaultTransport()
					.addProperty("configurationFile", "default-configs/default-jgroups-kubernetes.xml")

					// Or use, multicast stack to simplify local testing:
					// .transport().defaultTransport().addProperty("configurationFile",
					// "default-configs/default-jgroups-udp.xml")
					.build();

			EmbeddedCacheManager manager = new DefaultCacheManager(gc);

			// And here are per-cache configuration, e.g. eviction, replication
			// scheme etc.
			ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
			configurationBuilder.clustering().cacheMode(CacheMode.REPL_ASYNC);
			manager.defineConfiguration("default", configurationBuilder.build());
			cacheInstance = manager.getCache("default");
		}
		return cacheInstance;
	}

	private String hello(String name) {
		String greeting = "Hello {name} from {hostname} with {version}";
		Map<String, String> values = new HashMap<String, String>();
		values.put("name", name);
		values.put("hostname", System.getenv().getOrDefault("HOSTNAME", "unknown"));
		values.put("version", version);
		return new StrSubstitutor(values, "{", "}").replace(greeting);
	}

}
