/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.orm.jpa;

import java.util.Map;

import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.ejb.HibernateEntityManager;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Hibernate JPA.
 * 
 * @author Phillip Webb
 */
@Configuration
@ConditionalOnClass(HibernateEntityManager.class)
@EnableTransactionManagement
public class HibernateJpaAutoConfiguration extends JpaBaseConfiguration {

	private RelaxedPropertyResolver environment;

	@Override
	public void setEnvironment(Environment environment) {
		super.setEnvironment(environment);
		this.environment = new RelaxedPropertyResolver(environment,
				"spring.jpa.hibernate.");
	}

	@Override
	protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
		return new HibernateJpaVendorAdapter();
	}

	@Override
	protected void configure(
			LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
		Map<String, Object> properties = entityManagerFactoryBean.getJpaPropertyMap();
		properties.put("hibernate.cache.provider_class", this.environment.getProperty(
				"cache-provider", "org.hibernate.cache.HashtableCacheProvider"));
		properties.put("hibernate.ejb.naming_strategy", this.environment.getProperty(
				"naming-strategy", ImprovedNamingStrategy.class.getName()));
		String ddlAuto = this.environment.getProperty("ddl-auto", "none");
		if (!"none".equals(ddlAuto)) {
			properties.put("hibernate.hbm2ddl.auto", ddlAuto);
		}
	}
}
