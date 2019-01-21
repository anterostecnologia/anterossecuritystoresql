package br.com.anteros.security.store.sql.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import br.com.anteros.persistence.session.SQLSessionFactory;
import br.com.anteros.persistence.session.query.TypedSQLQuery;
import br.com.anteros.persistence.session.repository.impl.GenericSQLRepository;
import br.com.anteros.security.store.exception.AnterosSecurityStoreException;
import br.com.anteros.security.store.sql.domain.Approval;
import br.com.anteros.security.store.sql.exception.SQLStoreException;

@Repository("approvalRepositorySql")
@Scope("prototype")
public class ApprovalRepositoryImpl extends GenericSQLRepository<Approval, Long> implements ApprovalRepository {

	@Autowired
	public ApprovalRepositoryImpl(@Qualifier("securitySessionFactory") SQLSessionFactory sessionFactory)
			throws Exception {
		super(sessionFactory);
	}

	@Override
	public void updateOrCreate(Collection<Approval> approvals) {
		try {
			this.getSession().getTransaction().begin();
			save(approvals);
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

	@Override
	public void updateExpiresAt(Date now, Approval approval) {
		try {
			this.getSession().getTransaction().begin();
			TypedSQLQuery<Approval> query = getSession().createQuery(
					"SELECT * FROM A APROVACAO WHERE A.USER_ID = " + approval.getUserId() + " AND A.CLIENT_ID = "
							+ approval.getClientId() + " A.ESCOPO = " + approval.getScope(),
					Approval.class);
			List<Approval> resultList = query.getResultList();
			for (Approval app : resultList) {
				app.setExpiresAt(now);
			}
			save(resultList);
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

	@Override
	public void deleteByUserIdAndClientIdAndScope(Approval approval) {
		try {
			TypedSQLQuery<Approval> query = getSession().createQuery(
					"SELECT * FROM A APROVACAO WHERE A.USER_ID = " + approval.getUserId() + " AND A.CLIENT_ID = "
							+ approval.getClientId() + " A.ESCOPO = " + approval.getScope(),
					Approval.class);
			List<Approval> resultList = query.getResultList();
			remove(resultList);
		} catch (Exception e) {
			throw new SQLStoreException("Erro criando consulta para remove approvals. ", e);
		}
	}

	@Override
	public List<Approval> findByUserIdAndClientId(String userId, String clientId) {
		try {
			TypedSQLQuery<Approval> query = getSession().createQuery(
					"SELECT * FROM A APROVACAO WHERE A.USER_ID = " + userId + " AND A.CLIENT_ID = " + clientId,
					Approval.class);
			return query.getResultList();
		} catch (Exception e) {
			throw new SQLStoreException("Erro criando consulta para remove approvals. ", e);
		}
	}

}
