package br.com.anteros.security.store.sql.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.security.store.sql.domain.Approval;

public interface ApprovalRepository  extends SQLRepository<Approval, Long> {
	

	public void updateOrCreate(Collection<Approval> approvals) ;

	public void updateExpiresAt(Date now, Approval approval);

	public void deleteByUserIdAndClientIdAndScope(Approval approval) ;

	public List<Approval> findByUserIdAndClientId(String userId, String clientId);


}
