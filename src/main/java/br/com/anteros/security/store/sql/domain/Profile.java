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

import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

import br.com.anteros.bean.validation.constraints.UUID;
import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.DiscriminatorValue;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.TenantId;


/**
 * Pefil
 * 
 * Classe que representa um perfil de um Usuário dentro de um sistema. Ações poderão ser
 * atribuidas a um perfil e consequentemente estarão atribuídas aos usuários que possuírem o perfil.
 * 
 * @author Edson Martins edsonmartins2005@gmail.com
 */

@Entity
@DiscriminatorValue(value = "PERFIL")
public class Profile extends Security {

	@Required
	@UUID
	@TenantId
	@Column(name="ID_OWNER", length = 40, label="Proprietário do banco de dados")
	private String owner;

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
}
