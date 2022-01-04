package br.com.anteros.security.store.sql.domain;

import java.io.Serializable;
import java.util.Date;

import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.ForeignKey;
import br.com.anteros.persistence.metadata.annotation.GeneratedValue;
import br.com.anteros.persistence.metadata.annotation.Id;
import br.com.anteros.persistence.metadata.annotation.SequenceGenerator;
import br.com.anteros.persistence.metadata.annotation.Table;
import br.com.anteros.persistence.metadata.annotation.Temporal;
import br.com.anteros.persistence.metadata.annotation.type.FetchType;
import br.com.anteros.persistence.metadata.annotation.type.GeneratedType;
import br.com.anteros.persistence.metadata.annotation.type.TemporalType;

/**
 * 
 * @author eduardogreco
 *
 */

@Entity
@Table(name = "SEGURANCASESSAO")
public class SecuritySession implements Serializable {

	/*
	 * Identificação do Objeto de sessão
	 */
	@Id
	@Column(name = "ID_SESSAO", length = 8)
	@GeneratedValue(strategy = GeneratedType.AUTO)
	@SequenceGenerator(sequenceName = "SEQ_SEGURANCASESSAO", initialValue = 1)
	private Long id;

	/*
	 * Id. da sessão no banco de dados
	 */
	@Column(name = "ID_SESSAO_BANCODADOS", length = 8, required = true)
	private Long databaseSessionId;

	/*
	 * Usuário da sessão
	 */
	@ForeignKey(type = FetchType.EAGER)
	@Column(name = "ID_USUARIO", inversedColumn = "ID_SEGURANCA", required = true)
	private Security user;

	/*
	 * Data/hora do login da sessão
	 */
	@Temporal(TemporalType.DATE_TIME)
	@Column(name = "DH_LOGIN_SESSAO", required = true)
	private Date dtSessionLogin;

	/*
	 * Data/hora do logout da sessão
	 */
	@Temporal(TemporalType.DATE_TIME)
	@Column(name = "DH_LOGOUT_SESSAO")
	private Date dtSessionLogout;

	/*
	 * Endereço IP
	 */
	@Column(name = "ENDERECO_IP_CLIENTE", length = 20, required = true)
	private String ipAddress;

	/*
	 * Nome do sistema
	 */
	@Column(name = "SISTEMA", length = 30, required = true)
	private String system;

	/*
	 * Versão do sistema
	 */
	@Column(name = "VERSAO", length = 15, required = true)
	private String version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDatabaseSessionId() {
		return databaseSessionId;
	}

	public void setDatabaseSessionId(Long databaseSessionId) {
		this.databaseSessionId = databaseSessionId;
	}

	public Security getUser() {
		return user;
	}

	public void setUser(Security user) {
		this.user = user;
	}

	public Date getDtSessionLogin() {
		return dtSessionLogin;
	}

	public void setDtSessionLogin(Date dtSessionLogin) {
		this.dtSessionLogin = dtSessionLogin;
	}

	public Date getDtSessionLogout() {
		return dtSessionLogout;
	}

	public void setDtSessionLogout(Date dtSessionLogout) {
		this.dtSessionLogout = dtSessionLogout;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public SecuritySession() {
	}
}
