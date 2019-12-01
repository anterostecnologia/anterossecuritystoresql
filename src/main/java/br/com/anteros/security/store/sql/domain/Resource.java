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
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import br.com.anteros.persistence.metadata.annotation.Cascade;
import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.Fetch;
import br.com.anteros.persistence.metadata.annotation.ForeignKey;
import br.com.anteros.persistence.metadata.annotation.GeneratedValue;
import br.com.anteros.persistence.metadata.annotation.Id;
import br.com.anteros.persistence.metadata.annotation.Index;
import br.com.anteros.persistence.metadata.annotation.Indexes;
import br.com.anteros.persistence.metadata.annotation.SequenceGenerator;
import br.com.anteros.persistence.metadata.annotation.Table;
import br.com.anteros.persistence.metadata.annotation.type.CascadeType;
import br.com.anteros.persistence.metadata.annotation.type.FetchMode;
import br.com.anteros.persistence.metadata.annotation.type.FetchType;
import br.com.anteros.persistence.metadata.annotation.type.GeneratedType;
import br.com.anteros.security.store.domain.IAction;
import br.com.anteros.security.store.domain.IResource;

/**
 * Recurso
 * 
 * Classe que representa o objeto que será controlado o acesso pelo usuário
 * dentro de um sistema. Ex: Formulário, relatório, etc. Terá uma lista de ações
 * específicas que serão atribuídas posteriormente a qualquer objeto que extenda
 * Seguranca como um Papel, um Usuario ou um Grupo.
 * 
 * @author Edson Martins edsonmartins2005@gmail.com
 */
@Entity
@Table(name = "SEGURANCARECURSO")
@Indexes(value = {
		@Index(name = "UK_SEGURANCARECURSO_NOME_RECUR", columnNames = { "ID_SISTEMA, NOME_RECURSO" }, unique = true) })
public class Resource implements Serializable, IResource {

	/*
	 * Identificação do Recurso
	 */
	@Id
	@Column(name = "ID_RECURSO", length = 8)
	@GeneratedValue(strategy = GeneratedType.AUTO)
	@SequenceGenerator(sequenceName = "SEQ_SEGURANCARECURSO", initialValue = 1)
	private Long id;

	/*
	 * Nome do Recurso
	 */
	@Column(name = "NOME_RECURSO", length = 40, required = true)
	private String name;

	/*
	 * Descrição do Recurso
	 */
	@Column(name = "DS_RECURSO", length = 40, required = true)
	private String description;

	/*
	 * Lista de Ações que serão controladas acesso para um Recurso.
	 */
	@Fetch(type = FetchType.LAZY, mode = FetchMode.ONE_TO_MANY, mappedBy = "resource")
	@Cascade(values = CascadeType.DELETE_ORPHAN)
	private List<Action> actions;

	/*
	 * Sistema a qual pertence o Recurso.
	 */
	@ForeignKey(type = FetchType.EAGER)
	@Column(name = "ID_SISTEMA", inversedColumn = "ID_SISTEMA")
	private System system;

	public Resource() {

	}

	public Resource(String resourceName, String description, System system) {
		this.name = resourceName;
		this.description = description;
		this.system = system;
	}

	@JsonIgnore
	public List<IAction> getActionList() {
		List<IAction> result = new ArrayList<IAction>();
		if (actions != null) {
			for (Action action : actions) {
				result.add(action);
			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((system == null) ? 0 : system.hashCode());
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
		Resource other = (Resource) obj;
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
		if (system == null) {
			if (other.system != null)
				return false;
		} else if (!system.equals(other.system))
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

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public System getSystem() {
		return system;
	}

	public void setSystem(System system) {
		this.system = system;
	}

	@Override
	public String getResourceName() {
		return name;
	}

	public static Resource of(String resourceName, String description, System system) {
		return new Resource(resourceName, description, system);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	@Override
	public IResource addAction(IAction action) {
		actions.add((Action) action);
		return this;
	}

	@JsonIgnore
	@Override
	public String getResourceId() {
		return id+"";
	}

}
