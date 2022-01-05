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

import br.com.anteros.security.store.sql.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.anteros.core.utils.SerializationUtils;
import br.com.anteros.persistence.dsl.osql.OSQLQuery;
import br.com.anteros.persistence.session.SQLSession;
import br.com.anteros.security.store.SecurityDataStore;
import br.com.anteros.security.store.domain.IAction;
import br.com.anteros.security.store.domain.IResource;
import br.com.anteros.security.store.domain.ISystem;
import br.com.anteros.security.store.domain.IUser;
import br.com.anteros.security.store.exception.AnterosSecurityStoreException;
import br.com.anteros.security.store.sql.exception.SQLStoreException;
import br.com.anteros.security.store.sql.repository.AccessTokenRepository;
import br.com.anteros.security.store.sql.repository.ActionRepository;
import br.com.anteros.security.store.sql.repository.ApprovalRepository;
import br.com.anteros.security.store.sql.repository.ClientRepository;
import br.com.anteros.security.store.sql.repository.RefreshTokenRepository;
import br.com.anteros.security.store.sql.repository.ResourceRepository;
import br.com.anteros.security.store.sql.repository.SystemRepository;
import br.com.anteros.security.store.sql.repository.UserRepository;

@Service("securityDataStore")
@Scope("prototype")
@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "securityTransactionManager")
public class SQLSecurityDataStore implements SecurityDataStore {

	@Autowired
	protected UserRepository userRepositorySql;

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
	
	@Autowired
	protected ClientRepository clientRepositorySql;

	private boolean handleRevocationsAsExpiry = false;

	public void setHandleRevocationsAsExpiry(boolean handleRevocationsAsExpiry) {
		this.handleRevocationsAsExpiry = handleRevocationsAsExpiry;
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "securityTransactionManager")
	public IResource getResourceByName(String systemName, String resourceName) {
		return resourceRepositorySql.getResourceByName(systemName, resourceName);
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "securityTransactionManager")
	public ISystem getSystemByName(String systemName) {
		return systemRepositorySql.getSystemByName(systemName);
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "securityTransactionManager")
	public ISystem addSystem(String systemName, String description) {
		return systemRepositorySql.addSystem(systemName, description);
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "securityTransactionManager")
	public IResource addResource(ISystem system, String resourceName, String description) throws Exception {
		return resourceRepositorySql.addResource(system, resourceName, description);
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "securityTransactionManager")
	public IAction addAction(ISystem system, IResource resource, String actionName, String category, String description,
			String version) throws Exception {
		return actionRepositorySql.addAction(system, resource, actionName, category, description, version);
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "securityTransactionManager")
	public IAction saveAction(IAction action) throws Exception {
		try {			
			actionRepositorySql.getSession().save(action);
		} catch (Exception e) {
			throw new AnterosSecurityStoreException(e);
		}
		return action;
	}
	
	
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "securityTransactionManager")
	public IResource refreshResource(IResource resource) throws Exception {
		resourceRepositorySql.refresh((Resource) resource,null);
		if (resource != null)
			resource.getActionList().size();
		return resource;
	}

	
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "securityTransactionManager")
	public void removeActionByAllUsers(IAction act) throws Exception {
		actionRepositorySql.removeActionByAllUsers(act);
	}

	
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "securityTransactionManager")
	public IUser getUserByUserName(String username) {
		try {
			User user = userRepositorySql.getUserByLoginName(username);
			if (user == null) {
				Object tenantId = userRepositorySql.getSession().getTenantId();
				Object companyId =  userRepositorySql.getSession().getCompanyId();
				SQLSession session = userRepositorySql.getSQLSessionFactory().openSession();
				session.setTenantId(tenantId);
				session.setCompanyId(companyId);
				user = userRepositorySql.getUserByLoginName(username);
				userRepositorySql.getSession().clear();
				userRepositorySql.getSession().close();
				userRepositorySql.setSession(null);
			}
			return user;
		} catch (Exception e) {
		}
		
		return null;		
	}
	
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "securityTransactionManager")
	public IUser getUserByUserNameWithPassword(String username) {
		try {
			User user = userRepositorySql.getUserByLoginNameWithPassword(username);
			if (user == null) {
				Object tenantId = userRepositorySql.getSession().getTenantId();
				Object companyId =  userRepositorySql.getSession().getCompanyId();
				SQLSession session = userRepositorySql.getSQLSessionFactory().openSession();
				session.setTenantId(tenantId);
				session.setCompanyId(companyId);
				userRepositorySql.setSession(session);
				user = userRepositorySql.getUserByLoginName(username);
				user.getActionList().size();
				user.getGroups().size();
				user.getActions().size();
				user.getSimpleActions().size();
				user.getSecurityAccess().size();
			}
			return user;
		} catch (Exception e) {
		}
		
		return null;		
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "securityTransactionManager")
	@Override
	public void addApprovals(Collection<org.springframework.security.oauth2.provider.approval.Approval> approvals) {
		try {			
			final Collection<Approval> _approvals = transformToApproval(approvals);
			approvalRepositorySql.updateOrCreate(_approvals);
		} catch (Exception e) {
			throw new AnterosSecurityStoreException(e);
		}
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

	
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "securityTransactionManager")
	@Override
	public void revokeApprovals(Collection<org.springframework.security.oauth2.provider.approval.Approval> approvals) {
		try {			
			final Collection<Approval> tmpApprovals = transformToApproval(approvals);
	
			for (final Approval mongoApproval : tmpApprovals) {
				if (handleRevocationsAsExpiry) {
					approvalRepositorySql.updateExpiresAt(new Date(), mongoApproval);
				} else {
					approvalRepositorySql.deleteByUserIdAndClientIdAndScope(mongoApproval);
				}
			}
		} catch (Exception e) {
			throw new AnterosSecurityStoreException(e);
		}
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "securityTransactionManager")
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
	
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "securityTransactionManager")
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

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "securityTransactionManager")
	@Override
	public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication, AuthenticationKeyGenerator authenticationKeyGenerator) {
		try {			
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
		} catch (Exception e) {
			throw new AnterosSecurityStoreException(e);
		}
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "securityTransactionManager")
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

	
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "securityTransactionManager")
	@Override
	public void removeAccessToken(OAuth2AccessToken token) {
		try {			
			removeAccessToken(token.getValue());
		} catch (Exception e) {
			throw new AnterosSecurityStoreException(e);
		}

	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "securityTransactionManager")
	@Override
	public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication oAuth2Authentication) {
		try {	
			final String tokenKey = extractTokenKey(refreshToken.getValue());
			final byte[] token = serializeRefreshToken(refreshToken);
			final byte[] authentication = serializeAuthentication(oAuth2Authentication);
			final RefreshToken oAuth2RefreshToken = new RefreshToken(tokenKey, token, authentication);
			refreshTokenRepositorySql.save(oAuth2RefreshToken);
		} catch (Exception e) {
			throw new AnterosSecurityStoreException(e);
		}

	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "securityTransactionManager")
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

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "securityTransactionManager")
	@Override
	public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
		try {			
			OAuth2Authentication result = readAuthenticationForRefreshToken(token.getValue());
			return result;
		} catch (Exception e) {
			throw new AnterosSecurityStoreException(e);
		}
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "securityTransactionManager")
	@Override
	public void removeRefreshToken(OAuth2RefreshToken token) {
		try {			
			removeRefreshToken(token.getValue());
		} catch (Exception e) {
			throw new AnterosSecurityStoreException(e);
		}
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "securityTransactionManager")
	@Override
	public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
		try {			
			removeAccessTokenUsingRefreshToken(refreshToken.getValue());
		} catch (Exception e) {
			throw new AnterosSecurityStoreException(e);
		}	
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "securityTransactionManager")
	@Override
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication, AuthenticationKeyGenerator authenticationKeyGenerator) {
		try {	
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
		} catch (Exception e) {
			throw new AnterosSecurityStoreException(e);
		}	
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "securityTransactionManager")
	@Override
	public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
		final List<AccessToken> oAuth2AccessTokens = accessTokenRepositorySql
				.findByUsernameAndClientId(userName, clientId);
		return transformToOAuth2AccessTokens(oAuth2AccessTokens);
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "securityTransactionManager")
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

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "securityTransactionManager")
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

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "securityTransactionManager")
	@Override
	public void addClientDetails(ClientDetails clientDetails) {
		try {			
			Client client = new Client();
			client.setAccessTokenValiditySeconds(clientDetails.getAccessTokenValiditySeconds());
			client.setAuthorities(
					clientDetails.getAuthorities().stream().map(i -> i.getAuthority()).collect(Collectors.toList()));
			client.setAuthorizedGrantTypes(clientDetails.getAuthorizedGrantTypes());
			client.setClientId(clientDetails.getClientId());
			client.setClientSecret(clientDetails.getClientSecret());
			client.setRegisteredRedirectUris(clientDetails.getRegisteredRedirectUri());
			client.setResourceIds(clientDetails.getResourceIds());
			client.setScope(clientDetails.getScope());
			clientRepositorySql.save(client);
		} catch (Exception e) {
			throw new AnterosSecurityStoreException(e);
		}
		
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "securityTransactionManager")
	@Override
	public void updateClientDetails(ClientDetails clientDetails) {
		try {			
			Client clientToSave = clientRepositorySql.findOne(Long.valueOf(clientDetails.getClientId()),"");
			if (clientToSave == null) {
				throw new SQLStoreException("Client " + clientDetails.getClientId() + " não encontrado.");
			}
			clientToSave.setAccessTokenValiditySeconds(clientDetails.getAccessTokenValiditySeconds());
			clientToSave.setAuthorities(
					clientDetails.getAuthorities().stream().map(i -> i.getAuthority()).collect(Collectors.toList()));
			clientToSave.setAuthorizedGrantTypes(clientDetails.getAuthorizedGrantTypes());
			clientToSave.setClientSecret(clientDetails.getClientSecret());
			clientToSave.setRegisteredRedirectUris(clientDetails.getRegisteredRedirectUri());
			clientToSave.setResourceIds(clientDetails.getResourceIds());
			clientToSave.setScope(clientDetails.getScope());
			clientRepositorySql.save(clientToSave);
		} catch (Exception e) {
			throw new AnterosSecurityStoreException(e);
		}
		
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "securityTransactionManager")
	@Override
	public void updateClientSecret(String clientId, String secret) {
		try {			
			Client clientToSave = clientRepositorySql.findOne(Long.valueOf(clientId),"");
			if (clientToSave == null) {
				throw new SQLStoreException("Client " + clientId+ " não encontrado.");
			}
			clientToSave.setClientSecret(secret);
			clientRepositorySql.save(clientToSave);
		} catch (Exception e) {
			throw new AnterosSecurityStoreException(e);
		}
		
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "securityTransactionManager")
	@Override
	public void removeClientDetails(String clientId) {
		try {			
			clientRepositorySql.remove(Long.valueOf(clientId));
		} catch (Exception e) {
			throw new AnterosSecurityStoreException(e);
		}
		
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "securityTransactionManager")
	@Override
	public List<ClientDetails> listClientDetails() {
		try {			
			Iterable<Client> iterable = clientRepositorySql.findAll("");
			List<ClientDetails> result = new ArrayList<>();
			for (Client client : iterable) {
				BaseClientDetails bc = new BaseClientDetails();
				bc.setAccessTokenValiditySeconds(client.getAccessTokenValiditySeconds());
				bc.setAuthorities(client.getAuthorities().stream().map(item -> new SimpleGrantedAuthority(item))
						.collect(Collectors.toList()));
				bc.setAuthorizedGrantTypes(client.getAuthorizedGrantTypes());
				bc.setAutoApproveScopes(client.getAutoApproveScopes());
				bc.setClientSecret(client.getClientSecret());
				bc.setClientId(client.getClientId());
				bc.setRefreshTokenValiditySeconds(client.getRefreshTokenValiditySeconds());
				bc.setRegisteredRedirectUri(client.getRegisteredRedirectUris());
				bc.setResourceIds(client.getResourceIds());
				bc.setScope(client.getScope());
				result.add(bc);
			}
			return result;
		} catch (Exception e) {
			throw new AnterosSecurityStoreException(e);
		}
		
	}

	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "securityTransactionManager")
	@Override
	public ClientDetails loadClientByClientId(String clientId) {
		OSQLQuery query = clientRepositorySql.createObjectQuery();
		TClient tClient = new TClient("CLI");
		Client client = query.from(tClient).where(tClient.clientId.eq(clientId)).singleResult(tClient);
		if (client != null) {
			BaseClientDetails bc = new BaseClientDetails();
			bc.setAccessTokenValiditySeconds(client.getAccessTokenValiditySeconds());

			if (client.getAuthorities() != null) {
				bc.setAuthorities(client.getAuthorities().stream().map(item -> new SimpleGrantedAuthority(item))
						.collect(Collectors.toList()));
			}
			if (client.getAuthorizedGrantTypes() != null) {
				bc.setAuthorizedGrantTypes(client.getAuthorizedGrantTypes());
			}
			if (client.getAutoApproveScopes() != null) {
				bc.setAutoApproveScopes(client.getAutoApproveScopes());
			}
			bc.setClientSecret(client.getClientSecret());
			bc.setClientId(client.getClientId());
			bc.setRefreshTokenValiditySeconds(client.getRefreshTokenValiditySeconds());
			if (client.getRegisteredRedirectUris() != null) {
				bc.setRegisteredRedirectUri(client.getRegisteredRedirectUris());
			}
			if (client.getResourceIds() != null) {
				bc.setResourceIds(client.getResourceIds());
			}
			bc.setScope(client.getScope());
			return bc;
		}
		return null;
	}

	@Override
	public void initializeCurrentSession() throws Exception {		
//		SQLSession session = userRepositorySql.getSQLSessionFactory().openSession();
//		userRepositorySql.setSession(session);
//		systemRepositorySql.setSession(session);
//		resourceRepositorySql.setSession(session);
//		actionRepositorySql.setSession(session);
//		approvalRepositorySql.setSession(session);
//		accessTokenRepositorySql.setSession(session);
//		refreshTokenRepositorySql.setSession(session);
//		clientRepositorySql.setSession(session);
	}

	@Override
	public void clearCurrentSession() throws Exception {
//		userRepositorySql.getSession().clear();
//		systemRepositorySql.getSession().clear();
//		resourceRepositorySql.getSession().clear();
//		actionRepositorySql.getSession().clear();
//		approvalRepositorySql.getSession().clear();
//		accessTokenRepositorySql.getSession().clear();
//		refreshTokenRepositorySql.getSession().clear();
//		clientRepositorySql.getSession().clear();
		
	}


}
