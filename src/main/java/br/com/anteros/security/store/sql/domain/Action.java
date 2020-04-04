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

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.anteros.persistence.metadata.annotation.BooleanValue;
import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.ForeignKey;
import br.com.anteros.persistence.metadata.annotation.GeneratedValue;
import br.com.anteros.persistence.metadata.annotation.Id;
import br.com.anteros.persistence.metadata.annotation.SequenceGenerator;
import br.com.anteros.persistence.metadata.annotation.Table;
import br.com.anteros.persistence.metadata.annotation.type.BooleanType;
import br.com.anteros.persistence.metadata.annotation.type.FetchType;
import br.com.anteros.persistence.metadata.annotation.type.GeneratedType;
import br.com.anteros.security.store.domain.IAction;
import br.com.anteros.security.store.domain.IResource;

/**
 * Ação
 * 
 * Classe que representa Ação executada por um Usuário dentro de um sistema.
 * 
 * @author Edson Martins edsonmartins2005@gmail.com
 */
@Entity
@Table(name = "SEGURANCAACAO") 
public class Action implements Serializable, IAction {

	/*
	 * Identificador da Ação
	 */
	@Id
	@Column(name = "ID_ACAO", length = 8)
	@GeneratedValue(strategy = GeneratedType.AUTO)
	@SequenceGenerator(sequenceName = "SEQ_ACAO", initialValue = 1)
	private Long id;

	/*
	 * Nome da Ação
	 */
	@Column(name = "ACAO", length = 250, required = true)
	private String name;

	/*
	 * Descrição da Ação
	 */
	@Column(name = "DS_ACAO", length = 250, required = true)
	private String description;

	/*
	 * Recurso do sistema a qual pertence a Ação
	 */
	@ForeignKey(type = FetchType.EAGER)
	@Column(name = "ID_RECURSO", inversedColumn = "ID_RECURSO", required = true)
	private Resource resource;

	/*
	 * Categoria a qual pertence a ação
	 */
	@Column(name = "CATEGORIA", length = 30, required = true)
	private String category;

	/*
	 * Ação ativa?
	 */
	@BooleanValue(falseValue = "N", trueValue = "S", type = BooleanType.STRING)
	@Column(name = "BO_ATIVA", length = 1, required = true, defaultValue = "'S'")
	private Boolean active;

	/*
	 * Nome da Ação
	 */
	@Column(name = "VERSAO", length = 15, required = false, defaultValue = "'0.0.0.0'")
	private String version;
	
	public Action() {
		
	}

	public Action(String actionName, String description, String category, IResource resource, String version) {
		this.name = actionName;
		this.description = description;
		this.category = category;
		this.resource = (Resource) resource;
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((resource == null) ? 0 : resource.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		Action other = (Action) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
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

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Boolean getActive() {
		if (active==null)
			return true;
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@JsonIgnore
	@Override
	public String getActionName() {
		return name;
	}

	@JsonIgnore
	@Override
	public boolean isActionActive() {
		if (active==null)
			return true;
		return active;
	}

	@JsonIgnore
	@Override
	public void setActiveAction(boolean value) {
		this.active = value;		
	}

	public static Action of(String actionName, String description, String category, IResource resource,
			String version) {
		return new Action(actionName,description,category,resource,version);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	@Override
	public String getActionId() {
		return id+"";
	}


}
