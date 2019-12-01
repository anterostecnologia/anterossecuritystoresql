package br.com.anteros.security.store.sql.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.session.SQLSessionFactory;
import br.com.anteros.persistence.session.repository.impl.GenericSQLRepository;
import br.com.anteros.security.store.exception.AnterosSecurityStoreException;
import br.com.anteros.security.store.sql.domain.RefreshToken;

@Repository("refreshTokenRepositorySql")
@Scope("prototype")
public class RefreshTokenRepositoryImpl extends GenericSQLRepository<RefreshToken, Long>
		implements RefreshTokenRepository {

	@Autowired
	public RefreshTokenRepositoryImpl(@Qualifier("securitySessionFactory") SQLSessionFactory sessionFactory)
			throws Exception {
		super(sessionFactory);
	}

	@Override
	public RefreshToken findByTokenId(String tokenId) {
		RefreshToken refreshToken = this.findOneBySql(
				"select rt.* from REFRESH_TOKEN_ACESSO rt where rt.TOKEN_ID = :PTOKEN_ID ",
				NamedParameter.list().addParameter("PTOKEN_ID", tokenId).values(),null);
		return refreshToken;
	}

	@Override
	public boolean deleteByTokenId(String tokenId) {
		try {
			this.getSession().getTransaction().begin();
			RefreshToken refreshToken = this.findByTokenId(tokenId);
			if (refreshToken != null) {
				this.remove(refreshToken);
			}
			this.getSession().getTransaction().commit();
			return true;
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
