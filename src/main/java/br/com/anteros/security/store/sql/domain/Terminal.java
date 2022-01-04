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
import br.com.anteros.persistence.metadata.annotation.GeneratedValue;
import br.com.anteros.persistence.metadata.annotation.Id;
import br.com.anteros.persistence.metadata.annotation.SequenceGenerator;
import br.com.anteros.persistence.metadata.annotation.Table;
import br.com.anteros.persistence.metadata.annotation.TenantId;
import br.com.anteros.persistence.metadata.annotation.type.GeneratedType;
import br.com.anteros.validation.api.constraints.Size;

/**
 * Terminal
 * 
 * Classe que representa o cadastro dos terminais de Acesso ex: computador, coletor etc
 * 
 * @author Edson Martins edsonmartins2005@gmail.com
 */

@Entity
@Table(name="SEGURANCATERMINALACESSO")
public class Terminal implements Serializable {
	/*
	 * Identificador do terminal de acesso
	 */
	@Id
	@SequenceGenerator(sequenceName="SEQ_TERMINAL")
	@GeneratedValue(strategy=GeneratedType.AUTO)
	@Column(name="ID_TERMINAL", required= true)
	private Long id;
	
	/*
	 * Nome do terminal de acesso
	 */
	@Required
	@Column(name="NOME_TERMINAL", length=40, required=true, label="Nome do terminal")
	private String name;
	
	@Required
	@UUID
	@TenantId
	@Column(name="ID_OWNER", length = 40, label="Proprietário do banco de dados")
	private String owner;
	
	/*
	 * Descrição do terminal de acesso
	 */
	@Required
	@Size(max=40, min=5)
	@Column(name="DS_TERMINAL", length=40, required=true, label="Descrição do terminal")
	private String description;
	
	/*
	 * Endereço IP do terminal de acesso
	 */
	@Required
	@Size(max=15)
	@Column(name="ENDERECO_IP", length=15, required=true, label="Endereço IP")
	private String ipAddress;

	public Long getId() {
		return id;
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

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Terminal() {
	}
}
