package br.com.anteros.security.store.sql.domain;

import java.io.Serializable;

import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.GeneratedValue;
import br.com.anteros.persistence.metadata.annotation.Id;
import br.com.anteros.persistence.metadata.annotation.Lob;
import br.com.anteros.persistence.metadata.annotation.SequenceGenerator;
import br.com.anteros.persistence.metadata.annotation.Table;
import br.com.anteros.persistence.metadata.annotation.type.GeneratedType;

/**
 * Token de acesso
 * 
 * @author Edson Martins edsonmartins2005@gmail.com
 */
@Entity
@Table(name = "TOKEN_ACESSO")
public class AccessToken implements Serializable {

	/*
	 * Identificador do token de acesso
	 */
	@Id
	@Column(name = "ID_TOKEN", length = 8)
	@GeneratedValue(strategy = GeneratedType.AUTO)
	@SequenceGenerator(sequenceName = "SEQ_TOKEN", initialValue = 1)
	private Long id;

	@Column(name = "TOKEN_ID", required = true)
	private String tokenId;
	@Lob
	@Column(name = "TOKEN")
	private byte[] token;

	@Column(name = "AUTENTICACAO_ID")
	private String authenticationId;
	
	@Column(name = "USUARIO")
	private String username;

	@Column(name = "CLIENT_ID")
	private String clientId;

	@Lob
	@Column(name = "AUTENTICACAO")
	private byte[] authentication;

	@Column(name = "REFRESH_TOKEN")
	private String refreshToken;
	
	public AccessToken(){
		
	}
	

	public AccessToken(String tokenId, byte[] token, String authenticationId, String username, String clientId,
			byte[] authentication, String refreshToken) {
		super();
		this.tokenId = tokenId;
		this.token = token;
		this.authenticationId = authenticationId;
		this.username = username;
		this.clientId = clientId;
		this.authentication = authentication;
		this.refreshToken = refreshToken;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public byte[] getToken() {
		return token;
	}

	public void setToken(byte[] token) {
		this.token = token;
	}

	public String getAuthenticationId() {
		return authenticationId;
	}

	public void setAuthenticationId(String authenticationId) {
		this.authenticationId = authenticationId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public byte[] getAuthentication() {
		return authentication;
	}

	public void setAuthentication(byte[] authentication) {
		this.authentication = authentication;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tokenId == null) ? 0 : tokenId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccessToken other = (AccessToken) obj;
		if (tokenId == null) {
			if (other.tokenId != null)
				return false;
		} else if (!tokenId.equals(other.tokenId))
			return false;
		return true;
	}

}
