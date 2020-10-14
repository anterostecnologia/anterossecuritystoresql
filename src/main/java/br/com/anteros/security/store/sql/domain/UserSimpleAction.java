package br.com.anteros.security.store.sql.domain;

import java.io.Serializable;
import java.util.Date;

import br.com.anteros.bean.validation.constraints.Required;
import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.ForeignKey;
import br.com.anteros.persistence.metadata.annotation.GeneratedValue;
import br.com.anteros.persistence.metadata.annotation.Id;
import br.com.anteros.persistence.metadata.annotation.SequenceGenerator;
import br.com.anteros.persistence.metadata.annotation.Table;
import br.com.anteros.persistence.metadata.annotation.Temporal;
import br.com.anteros.persistence.metadata.annotation.TenantId;
import br.com.anteros.persistence.metadata.annotation.Version;
import br.com.anteros.persistence.metadata.annotation.type.GeneratedType;
import br.com.anteros.persistence.metadata.annotation.type.TemporalType;
import br.com.anteros.validation.api.constraints.Size;

@Entity
@Table(name = "USUARIOACAO")
public class UserSimpleAction implements Serializable {
	
	/*
	 * Identificação da ação
	 */
	@Id
	@Column(name = "ID_USUARIOACAO", length = 8, label = "ID")
	@GeneratedValue(strategy = GeneratedType.AUTO)
	@SequenceGenerator(sequenceName = "SEQ_USUARIOACAO", initialValue = 1)
	private Long id;

	/*
	 * Número de versão
	 */
	@Version
	@Temporal(TemporalType.DATE_TIME)
	@Column(name = "DH_VERSAO", required = true, label = "Versão")
	private Date dhVersao;

	/*
	 * Owner
	 */
	@Required
	@Size(max = 40)
	@TenantId
	@Column(name = "ID_OWNER", required = true, length = 40, label = "Identificador do proprietário do banco de dados")
	private String owner;
	
	/*
	 * Ação que o usuário pode executar
	 */
	@Column(name="NM_ACAO",length = 100, required = true)
	private String action;
	
	/*
	 * Descrição da Ação que o usuário pode executar
	 */
	@Column(name="DS_ACAO",length = 100, required = true)
	private String descriptionAction;
	
	/*
	 * Usuário
	 */
	@ForeignKey
	@Column(name="ID_USUARIO", inversedColumn = "ID_SEGURANCA", required = true, label="Id.Usuário")
	private User user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDhVersao() {
		return dhVersao;
	}

	public void setDhVersao(Date dhVersao) {
		this.dhVersao = dhVersao;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDescriptionAction() {
		return descriptionAction;
	}

	public void setDescriptionAction(String descriptionAction) {
		this.descriptionAction = descriptionAction;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
