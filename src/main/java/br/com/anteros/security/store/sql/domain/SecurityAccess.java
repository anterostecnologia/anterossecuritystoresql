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

import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

import br.com.anteros.bean.validation.constraints.UUID;
import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.ForeignKey;
import br.com.anteros.persistence.metadata.annotation.GeneratedValue;
import br.com.anteros.persistence.metadata.annotation.Id;
import br.com.anteros.persistence.metadata.annotation.SequenceGenerator;
import br.com.anteros.persistence.metadata.annotation.Table;
import br.com.anteros.persistence.metadata.annotation.TenantId;
import br.com.anteros.persistence.metadata.annotation.type.FetchType;
import br.com.anteros.persistence.metadata.annotation.type.GeneratedType;

/**
 *SegurancaHorarioAcesso
 *
 * Classe que representa os terminais e sistemas que um usuário pode acessar em um de
 * determinado horário de acesso
 * 
 * @author Edson Martins edsonmartins2005@gmail.com
 */

@Entity
@Table(name = "SEGURANCAACESSO")
public class SecurityAccess implements Serializable {

	/*
	 * Identificador do segurança horário de acesso
	 */
	@Id
	@Column(name = "ID_ACESSO", length = 8)
	@GeneratedValue(strategy = GeneratedType.AUTO)
	@SequenceGenerator(sequenceName = "SEQ_ACESSO", initialValue = 1)
	private Long id;
	
	@Required
	@UUID
	@TenantId
	@Column(name="ID_OWNER", length = 40, label="Proprietário do banco de dados")
	private String owner;

	/*
	 * Identificador do horário de acesso
	 */
	@ForeignKey
	@Column(name = "ID_HORARIO", required = true, label="Horário de acesso")
	private AccessTime accessTime;

	/*
	 * Identificador do segurança
	 */
	@ForeignKey
	@Column(name = "ID_SEGURANCA", required = true, label="Usuário/perfil")
	private Security security;

	/*
	 * Terminal de acesso
	 */
	@ForeignKey
	@Column(name = "ID_TERMINAL", required = true, label="Terminal")
	private Terminal terminal;

	/*
	 * Identificador do sistema
	 */
	@ForeignKey
	@Column(name = "ID_SISTEMA", required = true, label="Sistema")
	private System system;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AccessTime getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(AccessTime accessTime) {
		this.accessTime = accessTime;
	}

	public Security getSecurity() {
		return security;
	}

	public void setSecurity(Security security) {
		this.security = security;
	}

	public Terminal getTerminal() {
		return terminal;
	}

	public void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}

	public System getSystem() {
		return system;
	}

	public void setSystem(System system) {
		this.system = system;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public SecurityAccess() {
	}
}
