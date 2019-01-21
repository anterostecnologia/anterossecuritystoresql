package br.com.anteros.security.store.sql.repository;

import java.util.List;

import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.security.store.sql.domain.AccessToken;

public interface AccessTokenRepository  extends SQLRepository<AccessToken, Long> {

	AccessToken findByToken(String tokenKey);

	AccessToken findByAuthenticationId(String key);

	List<AccessToken> findByUsernameAndClientId(String userName, String clientId);

	List<AccessToken> findByClientId(String clientId);

	void deleteByRefreshTokenId(String tokenId);

	void deleteByTokenId(String tokenKey);

	AccessToken findByTokenId(String tokenId);
	



}
