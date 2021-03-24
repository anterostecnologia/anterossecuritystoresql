package br.com.anteros.security.store.sql.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.anteros.persistence.metadata.annotation.CollectionTable;
import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.Fetch;
import br.com.anteros.persistence.metadata.annotation.Id;
import br.com.anteros.persistence.metadata.annotation.JoinColumn;
import br.com.anteros.persistence.metadata.annotation.MapKeyColumn;
import br.com.anteros.persistence.metadata.annotation.type.FetchMode;
import br.com.anteros.security.store.domain.IClient;

@Entity(name = "CLIENTE")
public class Client implements Serializable, IClient {

	@Id
	@Column(name="ID_CLIENTE", required=true)
	private Long id;
	
	@Column(name="CLIENT_ID", length=200)
	private String clientId;
	
	@Column(name="CLIENT_SECRET", length=200)
    private String clientSecret;
	
	@Column(name="CLIENT_DESCRIPTION", length=100)
    private String clientDescription;
	
	@Fetch(mode=FetchMode.ELEMENT_COLLECTION)
	@CollectionTable(joinColumns= {@JoinColumn(name="ID_CLIENTE")}, name="CLIENTSCOPE")
	@Column(name = "SCOPE", label = "Scope", length = 150)	
    private Set<String> scope;
	
	@Fetch(mode=FetchMode.ELEMENT_COLLECTION)
	@CollectionTable(joinColumns= {@JoinColumn(name="ID_CLIENTE")}, name="CLIENTRESOURCES")
	@Column(name = "RESOURCEID", label = "Resource Id", length = 150)
    private Set<String> resourceIds;
	
	@Fetch(mode=FetchMode.ELEMENT_COLLECTION)
	@CollectionTable(joinColumns= {@JoinColumn(name="ID_CLIENTE")}, name="CLIENTGRANTS")
	@Column(name = "GRANT_TYPES", label = "Grant types", length = 150)
    private Set<String> authorizedGrantTypes;
	
	@Fetch(mode=FetchMode.ELEMENT_COLLECTION)
	@CollectionTable(joinColumns= {@JoinColumn(name="ID_CLIENTE")}, name="CLIENTREDIRECT")
	@Column(name = "REDIRECT_URIS", label = "Redirect Uri", length = 150)
    private Set<String> registeredRedirectUris;
	
	@Fetch(mode=FetchMode.ELEMENT_COLLECTION)
	@CollectionTable(joinColumns= {@JoinColumn(name="ID_CLIENTE")}, name="CLIENTAUTHORITIES")
	@Column(name = "AUTHORITIES", label = "Authorities", length = 150)
    private List<String> authorities;
	
	@Column(name="ACCESS_TOKEN_VALID_SECONDS", precision=7)
    private Integer accessTokenValiditySeconds;
	
	@Column(name="REFRESH_TOKEN_VALID_SECONDS", precision=7)
    private Integer refreshTokenValiditySeconds;
	
//	@Fetch(mode=FetchMode.ELEMENT_COLLECTION)
//	@CollectionTable(joinColumns= {@JoinColumn(name="ID_CLIENTE")}, name="CLIENTINFORMATION")
//	@MapKeyColumn(name="CHAVE")
//	@Column(name="VALOR")
//    private Map<String, String> additionalInformation;
	
	@Fetch(mode=FetchMode.ELEMENT_COLLECTION)
	@CollectionTable(joinColumns= {@JoinColumn(name="ID_CLIENTE")}, name="CLIENTAUTOAPPROVE")
    private Set<String> autoApproveScopes;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getClientDescription() {
		return clientDescription;
	}

	public void setClientDescription(String clientDescription) {
		this.clientDescription = clientDescription;
	}

	public Set<String> getScope() {
		return scope;
	}

	public void setScope(Set<String> scope) {
		this.scope = scope;
	}

	public Set<String> getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(Set<String> resourceIds) {
		this.resourceIds = resourceIds;
	}

	public Set<String> getAuthorizedGrantTypes() {
		return authorizedGrantTypes;
	}

	public void setAuthorizedGrantTypes(Set<String> authorizedGrantTypes) {
		this.authorizedGrantTypes = authorizedGrantTypes;
	}

	public Set<String> getRegisteredRedirectUris() {
		return registeredRedirectUris;
	}

	public void setRegisteredRedirectUris(Set<String> registeredRedirectUris) {
		this.registeredRedirectUris = registeredRedirectUris;
	}

	public List<String> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<String> authorities) {
		this.authorities = authorities;
	}

	public Integer getAccessTokenValiditySeconds() {
		return accessTokenValiditySeconds;
	}

	public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
		this.accessTokenValiditySeconds = accessTokenValiditySeconds;
	}

	public Integer getRefreshTokenValiditySeconds() {
		return refreshTokenValiditySeconds;
	}

	public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
		this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
	}

//	public Map<String, String> getAdditionalInformation() {
//		return additionalInformation;
//	}
//
//	public void setAdditionalInformation(Map<String, String> additionalInformation) {
//		this.additionalInformation = additionalInformation;
//	}

	public Set<String> getAutoApproveScopes() {
		return autoApproveScopes;
	}

	public void setAutoApproveScopes(Set<String> autoApproveScopes) {
		this.autoApproveScopes = autoApproveScopes;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


}
