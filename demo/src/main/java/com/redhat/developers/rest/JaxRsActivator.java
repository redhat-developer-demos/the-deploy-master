package com.redhat.developers.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.swagger.jaxrs.config.BeanConfig;

/**
 * A class extending {@link Application} and annotated with @ApplicationPath is the Java EE 7 "no XML" approach to activating
 * JAX-RS.
 *
 * <p>
 * Resources are served relative to the servlet path specified in the {@link ApplicationPath} annotation.
 * </p>
 */
@ApplicationPath("/api")
public class JaxRsActivator extends Application {
    /* class body intentionally left blank */
	
	public JaxRsActivator() {
		BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.0");
        beanConfig.setSchemes(new String[] { "http" });
        beanConfig.setTitle("The Deploy Master demo REST API");
        beanConfig.setDescription("Operations that can be invoked in this demo");
        beanConfig.setResourcePackage("com.redhat.developers.rest");
        beanConfig.setLicense("Apache 2.0");
        beanConfig.setLicenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html");
        beanConfig.setContact("developer@redhat.com");
        beanConfig.setBasePath("/demo/api");
        beanConfig.setPrettyPrint(true);
        beanConfig.setScan(true);
	}
}