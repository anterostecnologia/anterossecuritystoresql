package br.com.anteros.security.store.sql.domain;

import java.io.Serializable;
import java.util.Arrays;

import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.GeneratedValue;
import br.com.anteros.persistence.metadata.annotation.Id;
import br.com.anteros.persistence.metadata.annotation.Lob;
import br.com.anteros.persistence.metadata.annotation.SequenceGenerator;
import br.com.anteros.persistence.metadata.annotation.Table;
import br.com.anteros.persistence.metadata.annotation.type.GeneratedType;

/**
 * RefreshToken de acesso
 * 
 * @author Edson Martins edsonmartins2005@gmail.com
 */
@Entity
@Table(name = "REFRESH_TOKEN_ACESSO")
public class RefreshToken implements Serializable {
	
	public static final String ID_REFRESH = "ID_REFRESH";

	public static final String AUTENTICACAO = "AUTENTICACAO";

	public static final String TOKEN = "TOKEN";

	public static final String TOKEN_ID = "TOKEN_ID";

	/*
	 * Identificador do token de acesso
	 */
	@Id
	@Column(name = ID_REFRESH, length = 8)
	@GeneratedValue(strategy = GeneratedType.AUTO)
	@SequenceGenerator(sequenceName = "SEQ_REFRESH", initialValue = 1)
	private Long id;

	@Column(name=TOKEN_ID)
    private String tokenId;
	
	@Lob
	@Column(name=TOKEN)
    private byte[] token;
	
	@Lob
	@Column(name=AUTENTICACAO)
    private byte[] authentication;

	public RefreshToken() {
		
	}

	public RefreshToken(String tokenId, byte[] token, byte[] authentication) {
		super();
		this.tokenId = tokenId;
		this.token = token;
		this.authentication = authentication;
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

	public byte[] getAuthentication() {
		return authentication;
	}

	public void setAuthentication(byte[] authentication) {
		this.authentication = authentication;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(authentication);
		result = prime * result + Arrays.hashCode(token);
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
		RefreshToken other = (RefreshToken) obj;
		if (!Arrays.equals(authentication, other.authentication))
			return false;
		if (!Arrays.equals(token, other.token))
			return false;
		if (tokenId == null) {
			if (other.tokenId != null)
				return false;
		} else if (!tokenId.equals(other.tokenId))
			return false;
		return true;
	}

}
