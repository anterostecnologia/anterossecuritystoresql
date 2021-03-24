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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.session.SQLSessionFactory;
import br.com.anteros.persistence.session.repository.impl.GenericSQLRepository;
import br.com.anteros.security.store.exception.AnterosSecurityStoreException;
import br.com.anteros.security.store.sql.domain.AccessToken;

@Repository("accessTokenRepositorySql")
@Scope("prototype")
public class AccessTokenRepositoryImpl extends GenericSQLRepository<AccessToken, Long> implements AccessTokenRepository {

	@Autowired
	public AccessTokenRepositoryImpl(@Qualifier("securitySessionFactory") SQLSessionFactory sessionFactory) throws Exception {
		super(sessionFactory);
	}

	@Override
	public AccessToken findByToken(String tokenKey) {
		AccessToken accessToken = this.findOneBySql(
				"select tk.* from TOKEN_ACESSO tk where tk.TOKEN_ID = :PTOKEN_ID",
				new NamedParameter("PTOKEN_ID", tokenKey),null);
		return accessToken;
	}

	@Override
	public AccessToken findByAuthenticationId(String key) {
		AccessToken accessToken = this.findOneBySql(
				"select tk.* from TOKEN_ACESSO tk where tk.AUTENTICACAO_ID = :PAUTENTICACAO_ID",
				new NamedParameter("PAUTENTICACAO_ID", key),null);
		return accessToken;
	}

	@Override
	public List<AccessToken> findByUsernameAndClientId(String userName, String clientId) {
		List<AccessToken> result = this.find(
				"select tk.* from TOKEN_ACESSO tk where tk.USUARIO = :PUSUARIO AND tk.CLIENT_ID = :PCLIENT_ID",
				NamedParameter.list().addParameter("PUSUARIO", userName).addParameter("PCLIENT_ID",clientId),null);
		return result;
	}

	@Override
	public List<AccessToken> findByClientId(String clientId) {
		List<AccessToken> result = this.find(
				"select tk.* from TOKEN_ACESSO tk where tk.CLIENT_ID = :PCLIENT_ID",
				NamedParameter.list().addParameter("PCLIENT_ID",clientId),null);
		return result;
	}

	@Override
	public void deleteByRefreshTokenId(String tokenId) {
		AccessToken accessToken = this.findOneBySql(
				"select tk.* from TOKEN_ACESSO tk where tk.REFRESH_TOKEN = :PREFRESH_TOKEN",
				new NamedParameter("PREFRESH_TOKEN", tokenId),null);
		this.remove(accessToken);		
	}

	@Override
	public void deleteByTokenId(String tokenKey) {
		boolean executeCommit = false;
		try {			
			if (!this.getSession().getTransaction().isActive()) {
				this.getSession().getTransaction().begin();
				executeCommit = true;
			}
			AccessToken accessToken = this.findByTokenId(tokenKey);
			if (accessToken!=null) {
				this.remove(accessToken);
			}	
			if (executeCommit) {
				this.getSession().getTransaction().commit();
			}
		} catch (Exception e) {
			try {
				if (executeCommit) {
					this.getSession().getTransaction().rollback();
				}	
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			throw new AnterosSecurityStoreException(e);
		}
			
	}

	@Override
	public AccessToken findByTokenId(String tokenId) {
		AccessToken accessToken = this.findOneBySql(
				"select tk.* from TOKEN_ACESSO tk where tk.TOKEN_ID = :PTOKEN_ID",
				new NamedParameter("PTOKEN_ID", tokenId), null);
		return accessToken;
	}
	
	

}
