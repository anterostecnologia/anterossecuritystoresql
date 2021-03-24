package br.com.anteros.security.store.sql.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import br.com.anteros.persistence.session.SQLSessionFactory;
import br.com.anteros.persistence.session.repository.impl.GenericSQLRepository;
import br.com.anteros.security.store.sql.domain.Client;

@Repository("clientRepositorySql")
@Scope("prototype")
public class ClientRepositoryImpl extends GenericSQLRepository<Client, Long> implements ClientRepository {

	@Autowired
	public ClientRepositoryImpl(@Qualifier("securitySessionFactory") SQLSessionFactory sessionFactory)
			throws Exception {
		super(sessionFactory);
	}

}
