package br.com.anteros.security.store.sql.repository;

import java.util.List;

import br.com.anteros.security.store.sql.domain.TUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;

import br.com.anteros.security.store.sql.domain.User;
import br.com.anteros.persistence.dsl.osql.OSQLQuery;
import br.com.anteros.persistence.dsl.osql.types.expr.BooleanExpression;
import br.com.anteros.persistence.dsl.osql.types.expr.params.StringParam;
import br.com.anteros.persistence.session.SQLSessionFactory;
import br.com.anteros.persistence.session.repository.impl.GenericSQLRepository;

/**
*  Generated by Anteros Generator Maven Plugin at 23/10/2019 10:25:34
**/

@Repository("userRepositorySql")
@Scope("prototype")
public class UserRepositoryImpl extends GenericSQLRepository<User, Long> implements UserRepository {

	@Autowired
	public UserRepositoryImpl(@Qualifier("securitySessionFactory") SQLSessionFactory sessionFactory) throws Exception {
		super(sessionFactory);
	}
	
	@Override
	public User getUserByLoginName(String login) {
		User user = this.getUserByLoginNameWithPassword(login);
		if (user!=null) {
			user.setPassword(null);
		}
		return user;
	}
	
	@Override
	public User getUserByLoginNameWithPassword(String login) {
		StringParam pLogin = new StringParam("PLOGIN");
		StringParam pOwner = new StringParam("POWNER");
		
		TUser tUser = new TUser("USU");

		BooleanExpression where = tUser.login.equalsIgnoreCase(pLogin);
		if (getSession().getTenantId()!=null) {
			where = where.and(tUser.owner.eq(pOwner));
		}

		OSQLQuery consulta = new OSQLQuery(getSession())
				.from(tUser)
				.where(where);
		consulta.set(pLogin, login).readOnly(true);
		if (getSession().getTenantId()!=null) {
			consulta.set(pOwner,getSession().getTenantId().toString());
		}
		List<User> list = consulta.list(tUser);

		if (list != null && list.size()>0) {
			User result = list.iterator().next();
			if (list.size()>0) {
				for (User user : list){
					if (!user.isInactiveAccount()){
						result = user;
						break;
					}
				}
			}
			result.getSimpleActions().size();
			if (result.getProfile() != null) {
				result.getProfile().getSimpleActions().size();
			}
			return result;
		}
		
		return null;
	}


}
