/*******************************************************************************
 * Copyright 2012 Anteros Tecnologia
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package br.com.anteros.security.store.sql.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.session.SQLSessionFactory;
import br.com.anteros.persistence.session.repository.impl.GenericSQLRepository;
import br.com.anteros.security.store.exception.AnterosSecurityStoreException;
import br.com.anteros.security.store.sql.domain.System;

@Repository("systemRepositorySql")
@Scope("prototype")
public class SystemRepositoryImpl extends GenericSQLRepository<System, Long> implements SystemRepository {

	@Autowired
	public SystemRepositoryImpl(@Qualifier("securitySessionFactory") SQLSessionFactory sessionFactory) throws Exception {
		super(sessionFactory);
	}

	@Override
	public System getSystemByName(String systemName) {
		System system = this.findOneBySql(
				"select sis.* from SEGURANCASISTEMA sis where sis.nome_sistema = :pnome_sistema",
				new NamedParameter("pnome_sistema", systemName));
		return system;
	}

	@Override
	public System addSystem(String systemName, String description) {
		try {
			System newSystem = System.of(systemName,description);
			this.getSession().getTransaction().begin();
			this.getSession().save(newSystem);
			this.getSession().getTransaction().commit();
			return newSystem;
		} catch (Exception e) {
			try {
				this.getSession().getTransaction().rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			throw new AnterosSecurityStoreException(e);
		}
	}
	
	

}
