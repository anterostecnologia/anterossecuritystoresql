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
import br.com.anteros.security.store.domain.IAction;
import br.com.anteros.security.store.domain.IResource;
import br.com.anteros.security.store.domain.ISystem;
import br.com.anteros.security.store.exception.AnterosSecurityStoreException;
import br.com.anteros.security.store.sql.domain.Action;
import br.com.anteros.security.store.sql.domain.Resource;

@Repository("actionRepositorySql")
@Scope("prototype")
public class ActionRepositoryImpl extends GenericSQLRepository<Action, Long> implements ActionRepository {

	@Autowired
	public ActionRepositoryImpl(@Qualifier("securitySessionFactory") SQLSessionFactory sessionFactory)
			throws Exception {
		super(sessionFactory);
	}

	@Override
	public Action addAction(ISystem system, IResource resource, String actionName, String category, String description,
			String version) {
		try {
			this.getSession().getTransaction().begin();
			Action action = Action.of(actionName, description, category, resource, version);
			this.getSession().save(action);
			this.getSession().getTransaction().commit();
			return action;
		} catch (Exception e) {
			try {
				this.getSession().getTransaction().rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			throw new AnterosSecurityStoreException(
					"Não foi possível salvar a ação " + actionName + ". " + e.getMessage(), e);
		}

	}

	@Override
	public void removeActionByAllUsers(IAction act) {
		try {
			this.getSession().getTransaction().begin();
			this.getSession().createQuery("delete from SEGURANCAACAOACAO where id_acao = :pid_acao",
					new NamedParameter("pid_acao", act.getActionId())).executeQuery();
			this.remove((Action) act);
			this.getSession().getTransaction().commit();
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
