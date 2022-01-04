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
 * HorarioAcessoIntervalo
 * 
 * Classe que representa o intervalo de Horário de Acesso que será permitido ao Usuário utilizar o sistema.
 * 
 * @author Edson Martins edsonmartins2005@gmail.com
 */
@Entity
@Table(name = "SEGURANCAHORARIOINTERVALO")
public class AccessTimeInterval implements Serializable {

	
	/*
	 * Identificação
	 */
	@Id
	@Column(name = "ID_HORARIO_INTERVALO", length = 8)
	@GeneratedValue(strategy = GeneratedType.AUTO)
	@SequenceGenerator(sequenceName = "SEQ_HORARIO_INTERVALO", initialValue = 1)
	private Long id;
	
	/*
	 * Horário de acesso a qual pertence o intervalo
	 */
	@ForeignKey(type = FetchType.EAGER)
	@Column(name = "ID_HORARIO", inversedColumn = "ID_HORARIO", required = true, label="Horário de acesso")
	private AccessTime accessTime;
	
	@Required
	@UUID
	@TenantId
	@Column(name="ID_OWNER", length = 40, label="Proprietário do banco de dados")
	private String owner;

	/*
	 * Dia da semana
	 */
	@Column(name = "DIA_SEMANA", length = 2, required = true, label="Dia da semana")
	private Long dayOfWeek;

	/*
	 * Hora Inicial
	 */
	@Column(name = "HORA_INICIAL", required = true, length=4, label="Hora inicial")
	private String startTime;

	/*
	 * Hora Final
	 */
	@Column(name = "HORA_FINAL", required = true, length=4, label="hora final")
	private String endTime;

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

	public Long getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(Long dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public AccessTimeInterval() {
	}
}
