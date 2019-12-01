package br.com.anteros.security.store.sql.service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.stereotype.Service;

import br.com.anteros.core.utils.SerializationUtils;
import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.session.SQLSession;
import br.com.anteros.security.store.SecurityDataStore;
import br.com.anteros.security.store.domain.IAction;
import br.com.anteros.security.store.domain.IResource;
import br.com.anteros.security.store.domain.ISystem;
import br.com.anteros.security.store.domain.IUser;
import br.com.anteros.security.store.exception.AnterosSecurityStoreException;
import br.com.anteros.security.store.sql.domain.AccessToken;
import br.com.anteros.security.store.sql.domain.Action;
import br.com.anteros.security.store.sql.domain.Approval;
import br.com.anteros.security.store.sql.domain.RefreshToken;
import br.com.anteros.security.store.sql.domain.Resource;
import br.com.anteros.security.store.sql.domain.System;
import br.com.anteros.security.store.sql.domain.User;
import br.com.anteros.security.store.sql.repository.AccessTokenRepository;
import br.com.anteros.security.store.sql.repository.ActionRepository;
import br.com.anteros.security.store.sql.repository.ApprovalRepository;
import br.com.anteros.security.store.sql.repository.RefreshTokenRepository;
import br.com.anteros.security.store.sql.repository.ResourceRepository;
import br.com.anteros.security.store.sql.repository.SecurityRepository;
import br.com.anteros.security.store.sql.repository.SystemRepository;

@Service("securityDataStore")
@Scope("prototype")
public class SQLSecurityDataStore implements SecurityDataStore {

	@Autowired
	protected SecurityRepository securityRepositorySql;

	@Autowired
	protected SystemRepository systemRepositorySql;

	@Autowired
	protected ResourceRepository resourceRepositorySql;

	@Autowired
	protected ActionRepository actionRepositorySql;

	@Autowired
	protected ApprovalRepository approvalRepositorySql;
	
	@Autowired
	protected AccessTokenRepository accessTokenRepositorySql;
	
	@Autowired
	protected RefreshTokenRepository refreshTokenRepositorySql;

	private boolean handleRevocationsAsExpiry = false;

	public void setHandleRevocationsAsExpiry(boolean handleRevocationsAsExpiry) {
		this.handleRevocationsAsExpiry = handleRevocationsAsExpiry;
	}

	public IResource getResourceByName(String systemName, String resourceName) {
		return resourceRepositorySql.getResourceByName(systemName, resourceName);
	}

	public ISystem getSystemByName(String systemName) {
		return systemRepositorySql.getSystemByName(systemName);
	}

	public ISystem addSystem(String systemName, String description) {
		return systemRepositorySql.addSystem(systemName, description);
	}

	public IResource addResource(ISystem system, String resourceName, String description) throws Exception {
		return resourceRepositorySql.addResource(system, resourceName, description);
	}

	public IAction addAction(ISystem system, IResource resource, String actionName, String category, String description,
			String version) throws Exception {
		return actionRepositorySql.addAction(system, resource, actionName, category, description, version);
	}

	public IAction saveAction(IAction action) throws Exception {
		try {
			actionRepositorySql.getSession().getTransaction().begin();
			actionRepositorySql.getSession().save(action);
			actionRepositorySql.getSession().getTransaction().commit();
		} catch (Exception e) {
			actionRepositorySql.getSession().getTransaction().rollback();
			throw new AnterosSecurityStoreException(e);
		}
		return action;
	}

	public IResource refreshResource(IResource resource) throws Exception {
		resourceRepositorySql.refresh((Resource) resource,null);
		return resource;
	}

	public void removeActionByAllUsers(IAction act) throws Exception {
		actionRepositorySql.removeActionByAllUsers(act);
	}

	public IUser getUserByUserName(String username) {
		return securityRepositorySql.getUserByUserName(username);
	}

	@Override
	public void addApprovals(Collection<org.springframework.security.oauth2.provider.approval.Approval> approvals) {
		final Collection<Approval> mongoApprovals = transformToApproval(approvals);
		approvalRepositorySql.updateOrCreate(mongoApprovals);
	}

	private Collection<Approval> transformToApproval(
			Collection<org.springframework.security.oauth2.provider.approval.Approval> approvals) {
		Collection<Approval> result = new ArrayList<>();
		for (org.springframework.security.oauth2.provider.approval.Approval app : approvals) {
			result.add(new Approval(app.getExpiresAt(), app.getStatus() + "", app.getLastUpdatedAt(), app.getUserId(),
					app.getClientId(), app.getScope()));
		}
		return result;
	}

	@Override
	public void revokeApprovals(Collection<org.springframework.security.oauth2.provider.approval.Approval> approvals) {
		final Collection<Approval> tmpApprovals = transformToApproval(approvals);

		for (final Approval mongoApproval : tmpApprovals) {
			if (handleRevocationsAsExpiry) {
				approvalRepositorySql.updateExpiresAt(new Date(), mongoApproval);
			} else {
				approvalRepositorySql.deleteByUserIdAndClientIdAndScope(mongoApproval);
			}
		}
	}

	@Override
	public Collection<org.springframework.security.oauth2.provider.approval.Approval> getApprovals(String userId,
			String clientId) {
		final List<Approval> approvals = approvalRepositorySql.findByUserIdAndClientId(userId, clientId);
		return transformToSecurityApprovals(approvals);
	}

	private Collection<org.springframework.security.oauth2.provider.approval.Approval> transformToSecurityApprovals(
			List<Approval> approvals) {
		Collection<org.springframework.security.oauth2.provider.approval.Approval> result = new ArrayList<>();

		for (Approval app : approvals) {
			org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus status = app.getStatus()
					.equals(org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus.APPROVED
							.toString())
									? org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus.APPROVED
									: org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus.DENIED;
			result.add(new org.springframework.security.oauth2.provider.approval.Approval(app.getUserId(),
					app.getClientId(), app.getScope(), app.getExpiresAt(), status, app.getLastModifiedAt()));
		}
		return result;
	}
	
	@Override
	public OAuth2Authentication readAuthentication(final String token) {
        final String tokenId = extractTokenKey(token);

        final AccessToken accessToken = accessTokenRepositorySql.findByTokenId(tokenId);

        if (Objects.nonNull(accessToken)) {
            try {
                return deserializeAuthentication(accessToken.getAuthentication());
            } catch (IllegalArgumentException e) {
                removeAccessToken(token);
            }
        }

        return null;
    }

	@Override
	public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication, AuthenticationKeyGenerator authenticationKeyGenerator) {
		String refreshToken = null;
		if (Objects.nonNull(token.getRefreshToken())) {
			refreshToken = token.getRefreshToken().getValue();
		}

		if (Objects.nonNull(readAccessToken(token.getValue()))) {
			removeAccessToken(token.getValue());
		}

		final String tokenKey = extractTokenKey(token.getValue());

		final AccessToken oAuth2AccessToken = new AccessToken(tokenKey, serializeAccessToken(token),
				authenticationKeyGenerator.extractKey(authentication),
				authentication.isClientOnly() ? null : authentication.getName(),
				authentication.getOAuth2Request().getClientId(), serializeAuthentication(authentication),
				extractTokenKey(refreshToken));

		accessTokenRepositorySql.save(oAuth2AccessToken);
	}

	@Override
	public OAuth2AccessToken readAccessToken(String tokenValue) {
		final String tokenKey = extractTokenKey(tokenValue);
		final AccessToken AccessToken = accessTokenRepositorySql.findByToken(tokenKey);
		if (Objects.nonNull(AccessToken)) {
			try {
				return deserializeAccessToken(AccessToken.getToken());
			} catch (IllegalArgumentException e) {
				removeAccessToken(tokenValue);
			}
		}
		return null;
	}

	@Override
	public void removeAccessToken(OAuth2AccessToken token) {
		removeAccessToken(token.getValue());

	}

	@Override
	public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication oAuth2Authentication) {
		final String tokenKey = extractTokenKey(refreshToken.getValue());
		final byte[] token = serializeRefreshToken(refreshToken);
		final byte[] authentication = serializeAuthentication(oAuth2Authentication);

		final RefreshToken oAuth2RefreshToken = new RefreshToken(tokenKey, token, authentication);

		refreshTokenRepositorySql.save(oAuth2RefreshToken);

	}

	@Override
	public OAuth2RefreshToken readRefreshToken(String tokenValue) {
		final String tokenKey = extractTokenKey(tokenValue);
		final RefreshToken refreshToken = refreshTokenRepositorySql
				.findByTokenId(tokenKey);

		if (Objects.nonNull(refreshToken)) {
			try {
				return deserializeRefreshToken(refreshToken.getToken());
			} catch (IllegalArgumentException e) {
				removeRefreshToken(tokenValue);
			}
		}

		return null;
	}

	@Override
	public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
		return readAuthenticationForRefreshToken(token.getValue());
	}

	@Override
	public void removeRefreshToken(OAuth2RefreshToken token) {
		removeRefreshToken(token.getValue());
	}

	@Override
	public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
		removeAccessTokenUsingRefreshToken(refreshToken.getValue());
	}

	@Override
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication, AuthenticationKeyGenerator authenticationKeyGenerator) {
		OAuth2AccessToken accessToken = null;

		String key = authenticationKeyGenerator.extractKey(authentication);

		final AccessToken oAuth2AccessToken = accessTokenRepositorySql.findByAuthenticationId(key);

		if (oAuth2AccessToken != null) {
			accessToken = deserializeAccessToken(oAuth2AccessToken.getToken());
		}

		if (accessToken != null
				&& !key.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {
			removeAccessToken(accessToken.getValue());
			storeAccessToken(accessToken, authentication, authenticationKeyGenerator);
		}
		return accessToken;
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
		final List<AccessToken> oAuth2AccessTokens = accessTokenRepositorySql
				.findByUsernameAndClientId(userName, clientId);
		return transformToOAuth2AccessTokens(oAuth2AccessTokens);
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
		final List<AccessToken> oAuth2AccessTokens = accessTokenRepositorySql
				.findByClientId(clientId);
		return transformToOAuth2AccessTokens(oAuth2AccessTokens);
	}

	protected String extractTokenKey(final String value) {
		if (Objects.isNull(value)) {
			return null;
		}
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
		}

		try {
			byte[] bytes = digest.digest(value.getBytes("UTF-8"));
			return String.format("%032x", new BigInteger(1, bytes));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
		}
	}

	protected byte[] serializeAccessToken(OAuth2AccessToken token) {
		return SerializationUtils.serialize(token);
	}

	protected byte[] serializeRefreshToken(OAuth2RefreshToken token) {
		return SerializationUtils.serialize(token);
	}

	protected byte[] serializeAuthentication(OAuth2Authentication authentication) {
		return SerializationUtils.serialize(authentication);
	}

	protected OAuth2AccessToken deserializeAccessToken(final byte[] token) {
		return SerializationUtils.deserialize(token);
	}

	protected OAuth2RefreshToken deserializeRefreshToken(final byte[] token) {
		return SerializationUtils.deserialize(token);
	}

	protected OAuth2Authentication deserializeAuthentication(final byte[] authentication) {
		return SerializationUtils.deserialize(authentication);
	}

	public OAuth2Authentication readAuthenticationForRefreshToken(final String value) {
		final String tokenId = extractTokenKey(value);

		final RefreshToken mongoOAuth2RefreshToken = refreshTokenRepositorySql
				.findByTokenId(tokenId);

		if (Objects.nonNull(mongoOAuth2RefreshToken)) {
			try {
				return deserializeAuthentication(mongoOAuth2RefreshToken.getAuthentication());
			} catch (IllegalArgumentException e) {
				removeRefreshToken(value);
			}
		}

		return null;
	}

	private void removeRefreshToken(final String token) {
		final String tokenId = extractTokenKey(token);
		refreshTokenRepositorySql.deleteByTokenId(tokenId);
	}

	private void removeAccessTokenUsingRefreshToken(final String refreshToken) {
		final String tokenId = extractTokenKey(refreshToken);
		accessTokenRepositorySql.deleteByRefreshTokenId(tokenId);

	}

	private void removeAccessToken(final String tokenValue) {
		final String tokenKey = extractTokenKey(tokenValue);
		accessTokenRepositorySql.deleteByTokenId(tokenKey);
	}

	private Collection<OAuth2AccessToken> transformToOAuth2AccessTokens(
			final List<AccessToken> oAuth2AccessTokens) {
		return oAuth2AccessTokens.stream().filter(Objects::nonNull)
				.map(token -> SerializationUtils.<OAuth2AccessToken>deserialize(token.getToken()))
				.collect(Collectors.toList());
	}

	@Override
	public void addClientDetails(ClientDetails clientDetails) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateClientDetails(ClientDetails clientDetails) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateClientSecret(String clientId, String secret) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeClientDetails(String clientId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ClientDetails> listClientDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClientDetails loadClientByClientId(String clientId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeCurrentSession() throws Exception {
		
		SQLSession session = securityRepositorySql.getSQLSessionFactory().openSession();
		securityRepositorySql.setSession(session);
		systemRepositorySql.setSession(session);
		resourceRepositorySql.setSession(session);
		actionRepositorySql.setSession(session);
		approvalRepositorySql.setSession(session);
		accessTokenRepositorySql.setSession(session);
		refreshTokenRepositorySql.setSession(session);
	}


}
