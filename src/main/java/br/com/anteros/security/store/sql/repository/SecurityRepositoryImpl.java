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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import br.com.anteros.persistence.metadata.EntityCache;
import br.com.anteros.persistence.metadata.descriptor.DescriptionField;
import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.session.SQLSessionFactory;
import br.com.anteros.persistence.session.query.SQLQueryException;
import br.com.anteros.persistence.session.repository.impl.GenericSQLRepository;
import br.com.anteros.security.store.sql.domain.Security;
import br.com.anteros.security.store.sql.domain.User;

@Repository("securityRepositorySql")
@Scope("prototype")
public class SecurityRepositoryImpl extends GenericSQLRepository<Security, Long> implements
		SecurityRepository {

	@Autowired
	public SecurityRepositoryImpl(@Qualifier("securitySessionFactory") SQLSessionFactory sessionFactory) throws Exception {
		super(sessionFactory);
	}
	
	protected DescriptionField getTenantId() {
		List<EntityCache> entityCaches = getSession().getEntityCacheManager().getEntityCachesByTableName(getEntityCache().getTableName());
		for (EntityCache entityCache : entityCaches) {
			if (entityCache.getTenantId()!=null) {
				return entityCache.getTenantId();
			}
		}
		return null;
	}

	public User findUserByName(String userName) {
		String sql= "select * from SEGURANCA SEG where SEG.login = :plogin";
		String[] sUserName = userName.split("/@");
		DescriptionField tenantId = getTenantId();
		if (tenantId!=null && sUserName.length>1) {
			sql = sql + " AND SEG."+tenantId.getSimpleColumn().getColumnName()+" = "+'"'+sUserName[1]+'"';
		}

		
		return (User) findOneBySql(sql, new NamedParameter("plogin", sUserName[0]),null);
	}

	@Override
	public User getUserByUserName(String username) {
		User user = this.findUserByName(username);
		if (user == null)
			return null;
		return user;
	}
	
	

}
