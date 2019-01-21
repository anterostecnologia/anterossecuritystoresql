package br.com.anteros.security.store.sql.domain;

import java.io.Serializable;
import java.util.Date;

import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.GeneratedValue;
import br.com.anteros.persistence.metadata.annotation.Id;
import br.com.anteros.persistence.metadata.annotation.SequenceGenerator;
import br.com.anteros.persistence.metadata.annotation.Table;
import br.com.anteros.persistence.metadata.annotation.Temporal;
import br.com.anteros.persistence.metadata.annotation.type.GeneratedType;
import br.com.anteros.persistence.metadata.annotation.type.TemporalType;
import br.com.anteros.security.store.domain.IApproval;

/**
 * Ação
 * 
 * Classe que representa Ação executada por um Usuário dentro de um sistema.
 * 
 * @author Edson Martins edsonmartins2005@gmail.com
 */
@Entity
@Table(name = "APROVACAO")
public class Approval implements Serializable, IApproval {

	/*
	 * Identificador da Ação
	 */
	@Id
	@Column(name = "ID_APROVACAO", length = 8)
	@GeneratedValue(strategy = GeneratedType.AUTO)
	@SequenceGenerator(sequenceName = "SEQ_APROVACAO", initialValue = 1)
	private Long id;

	@Temporal(TemporalType.DATE_TIME)
	@Column(name = "DH_EXPIRACAO", required = true)
	private Date expiresAt;

	@Column(name = "TP_STATUS")
	private String status;

	@Temporal(TemporalType.DATE_TIME)
	@Column(name = "DH_ULTIMA_MODIFICACAO")
	private Date lastModifiedAt;

	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "CLIENT_ID")
	private String clientId;

	@Column(name = "ESCOPO")
	private String scope;

	public Approval() {

	}

	public Approval(Date expiresAt, String status, Date lastModifiedAt, String userId, String clientId, String scope) {
		super();
		this.expiresAt = expiresAt;
		this.status = status;
		this.lastModifiedAt = lastModifiedAt;
		this.userId = userId;
		this.clientId = clientId;
		this.scope = scope;
	}

	

	public Date getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result + ((expiresAt == null) ? 0 : expiresAt.hashCode());
		result = prime * result + ((lastModifiedAt == null) ? 0 : lastModifiedAt.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		Approval other = (Approval) obj;
		if (clientId == null) {
			if (other.clientId != null)
				return false;
		} else if (!clientId.equals(other.clientId))
			return false;
		if (expiresAt == null) {
			if (other.expiresAt != null)
				return false;
		} else if (!expiresAt.equals(other.expiresAt))
			return false;
		if (lastModifiedAt == null) {
			if (other.lastModifiedAt != null)
				return false;
		} else if (!lastModifiedAt.equals(other.lastModifiedAt))
			return false;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
