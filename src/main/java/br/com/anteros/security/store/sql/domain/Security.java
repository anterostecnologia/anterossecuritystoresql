/*******************************************************************************
 * Copyright 2012 Anteros Tecnologia
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package br.com.anteros.security.store.sql.domain;

import java.io.Serializable;
import java.util.Set;

import br.com.anteros.persistence.metadata.annotation.Cascade;
import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.DiscriminatorColumn;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.Fetch;
import br.com.anteros.persistence.metadata.annotation.GeneratedValue;
import br.com.anteros.persistence.metadata.annotation.Id;
import br.com.anteros.persistence.metadata.annotation.Index;
import br.com.anteros.persistence.metadata.annotation.Indexes;
import br.com.anteros.persistence.metadata.annotation.Inheritance;
import br.com.anteros.persistence.metadata.annotation.JoinColumn;
import br.com.anteros.persistence.metadata.annotation.JoinTable;
import br.com.anteros.persistence.metadata.annotation.SequenceGenerator;
import br.com.anteros.persistence.metadata.annotation.Table;
import br.com.anteros.persistence.metadata.annotation.type.CascadeType;
import br.com.anteros.persistence.metadata.annotation.type.FetchMode;
import br.com.anteros.persistence.metadata.annotation.type.FetchType;
import br.com.anteros.persistence.metadata.annotation.type.GeneratedType;
import br.com.anteros.persistence.metadata.annotation.type.InheritanceType;

/**
 * Seguranca
 * 
 * Classe abstrata que vai representar qualquer objeto que necessite de controle de acesso a determinados
 * Recursos/Ações dentro de um Sistema.
 * 
 * @author Edson Martins edsonmartins2005@gmail.com
 */
@Entity
@Table(name = "SEGURANCA")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TP_SEGURANCA", length = 40)
@Indexes({ @Index(name = "PK_SEGURANCA", columnNames = {"ID_SEGURANCA"}),
	@Index(name = "IX_SEGURANCA_ID_HORARIO", columnNames = {"ID_HORARIO"}),
	@Index(name = "SEGURANCA_PERFIL", columnNames = {"ID_PERFIL"}),
	@Index(name = "UK_SEGURANCA_LOGIN", columnNames = {"LOGIN"})})
public abstract class Security implements Serializable {

	
	public static final String SECURITY_PACKAGE = "br.com.anteros.security.model";
	/*
	 * Identificação do Objeto de Segurança
	 */
	@Id
	@Column(name = "ID_SEGURANCA", length = 8)
	@GeneratedValue(strategy = GeneratedType.AUTO)
	@SequenceGenerator(sequenceName = "SEQ_SEGURANCA", initialValue = 1)
	private Long id;

	@Column(name = "NOME", length = 40, required = true)
	private String name;

	@Column(name = "DESCRICAO", length = 40, required = true)
	private String description;

	@Column(name = "TP_SEGURANCA", length = 30, required = true)
	private String securityType;

	/*
	 * Lista de Ações permitidas para um determinado objeto de Segurança.
	 */
	@Fetch(type = FetchType.LAZY, mode = FetchMode.MANY_TO_MANY, statement="SELECT AC.*, REC.*, SIS.* FROM  SEGURANCAACAOACAO SACAO, SEGURANCAACAO AC, SEGURANCARECURSO REC, SEGURANCASISTEMA SIS WHERE SACAO.ID_SEGURANCA = :ID_SEGURANCA AND AC.ID_ACAO = SACAO.ID_ACAO AND REC.ID_RECURSO = AC.ID_RECURSO AND SIS.ID_SISTEMA = REC.ID_SISTEMA")
	@JoinTable(name = "SEGURANCAACAOACAO", joinColumns = @JoinColumn(name = "ID_SEGURANCA"), inversedJoinColumns = @JoinColumn(name = "ID_ACAO"))
	private Set<Action> actions;

	/*
	 * Lista de horários de acesso permitidos para um determinado objeto de
	 * Segurança.
	 */
	@Fetch(type = FetchType.LAZY, mode = FetchMode.ONE_TO_MANY, mappedBy = "security")
	@Cascade(values = { CascadeType.DELETE_ORPHAN })
	private Set<SecurityAccess> securityAccess;

	/*
	 * Email do usuário
	 */
	@Column(name = "EMAIL", length = 250)
	private String email;

	public String getId() {
		return id+"";
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSecurityType() {
		return securityType;
	}

	public void setSecurityType(String securityType) {
		this.securityType = securityType;
	}

	public Set<Action> getActions() {
		return actions;
	}

	public void setActions(Set<Action> actions) {
		this.actions = actions;
	}

	public Set<SecurityAccess> getSecurityAccess() {
		return securityAccess;
	}

	public void setSecurityAccess(Set<SecurityAccess> securityAccess) {
		this.securityAccess = securityAccess;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public static String getSecurityPackage() {
		return SECURITY_PACKAGE;
	}

	

	
}
