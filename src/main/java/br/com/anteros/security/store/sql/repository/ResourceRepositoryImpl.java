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
import br.com.anteros.security.store.domain.IResource;
import br.com.anteros.security.store.domain.ISystem;
import br.com.anteros.security.store.exception.AnterosSecurityStoreException;
import br.com.anteros.security.store.sql.domain.Resource;
import br.com.anteros.security.store.sql.domain.System;

@Repository("resourceRepositorySql")
@Scope("prototype")
public class ResourceRepositoryImpl extends GenericSQLRepository<Resource, Long> implements ResourceRepository {

	@Autowired
	public ResourceRepositoryImpl(@Qualifier("securitySessionFactory") SQLSessionFactory sessionFactory)
			throws Exception {
		super(sessionFactory);
	}

	@Override
	public Resource getResourceByName(String systemName, String resourceName) {
		Resource resource = this.findOneBySql(
				"select rec.* from SEGURANCARECURSO rec, SEGURANCASISTEMA sis where sis.nome_sistema = :pnome_sistema and rec.nome_recurso = :pnome_recurso and rec.id_sistema = sis.id_sistema ",
				NamedParameter.list().addParameter("pnome_sistema", systemName)
						.addParameter("pnome_recurso", resourceName).values());
		return resource;
	}

	@Override
	public Resource addResource(ISystem system, String resourceName, String description) {
		Resource resource = Resource.of(resourceName,description,(System)system);
		try {
			this.getSession().getTransaction().begin();
			this.getSession().save(resource);
			this.getSession().getTransaction().commit();
		} catch (Exception e) {
			try {
				this.getSession().getTransaction().rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			throw new AnterosSecurityStoreException(
					"Não foi possível salvar o recurso " + resourceName + ". " + e.getMessage(), e);
		}

		return resource;
	}

	@Override
	public Resource refreshResource(IResource resource) {
		this.refresh((Resource) resource);
		return (Resource)resource;
	}

}
