package com.redhat.developers.helloworld;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class CacheInstance {

	private static Cache<String, String> INSTANCE;

	public static synchronized Cache<String, String> getCache() {
		if (INSTANCE == null) {
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
			INSTANCE = manager.getCache("default");
		}
		return INSTANCE;
	}

}
