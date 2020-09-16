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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;

import br.com.anteros.bean.validation.constraints.UUID;
import br.com.anteros.persistence.metadata.annotation.BooleanValue;
import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.DiscriminatorValue;
import br.com.anteros.persistence.metadata.annotation.Entity;
import br.com.anteros.persistence.metadata.annotation.Fetch;
import br.com.anteros.persistence.metadata.annotation.ForeignKey;
import br.com.anteros.persistence.metadata.annotation.JoinColumn;
import br.com.anteros.persistence.metadata.annotation.JoinTable;
import br.com.anteros.persistence.metadata.annotation.Lob;
import br.com.anteros.persistence.metadata.annotation.TenantId;
import br.com.anteros.persistence.metadata.annotation.type.BooleanType;
import br.com.anteros.persistence.metadata.annotation.type.FetchMode;
import br.com.anteros.persistence.metadata.annotation.type.FetchType;
import br.com.anteros.security.store.domain.IAction;
import br.com.anteros.security.store.domain.IProfile;
import br.com.anteros.security.store.domain.IUser;
import br.com.anteros.validation.api.constraints.Size;

/**
 * Usuario
 * 
 * Classe que representa um usuário dentro de um Sistema. Pode ser uma Pessoa ou até mesmo um usuário virtual como
 * o próprio Sistema.
 * 
 * @author Edson Martins edsonmartins2005@gmail.com
 */

 
@JsonIgnoreProperties({ "actionList", "getActionList" })
@Entity
@DiscriminatorValue(value = "USUARIO")
public class User extends Security implements IUser {

	@UUID
	@TenantId
	@Column(name="ID_OWNER", length = 40, label="Proprietário do banco de dados")
	private String owner;
	
	/*
	 * Login do usuário
	 */
	@Required
	@Size(max=20, min=5)
	@Column(name = "LOGIN", length = 20, label="Login")
	private String login;

	/*
	 * Senha do usuário
	 */
	@Required
	@Size(max=100, min=8)
	@Column(name = "SENHA", length = 100, label="Senha")
	private String password;

	/*
	 * O usuário deve alterar a senha no próximo Login?
	 */
	@Required
	@BooleanValue(trueValue = "S", falseValue = "N", type=BooleanType.STRING)
	@Column(name = "BO_ALTERAR_SENHA_PROX_LOGIN", required = true, defaultValue = "'N'", label="Alterar senha no próximo login?")
	private Boolean changePasswordOnNextLogin;

	/*
	 * O usuário pode alterar a senha?
	 */
	@Required
	@BooleanValue(trueValue = "S", falseValue = "N", type=BooleanType.STRING)
	@Column(name = "BO_PERMITE_ALTERAR_SENHA", required = true, defaultValue = "'N'", label="Permite alterar a senha?")
	private Boolean allowChangePassword;

	/*
	 * Permite o usuário efetuar vários logins em um mesmo sistema?
	 */
	@Required
	@BooleanValue(trueValue = "S", falseValue = "N", type=BooleanType.STRING)
	@Column(name = "BO_PERMITE_MULTIPLOS_LOGINS", required = true, defaultValue = "'N'", label="Permite múltiplos logins?")
	private Boolean allowMultipleLogins;

	/*
	 * A senha do usuário nunca expira?
	 */
	@Required
	@BooleanValue(trueValue = "S", falseValue = "N", type=BooleanType.STRING)
	@Column(name = "BO_SENHA_NUNCA_EXPIRA", required = true, defaultValue = "'N'",label="Senha nunca expira?")
	private Boolean passwordNeverExpire;

	/*
	 * Conta do usuário está desativada?
	 */
	@Required
	@BooleanValue(trueValue = "S", falseValue = "N", type=BooleanType.STRING)
	@Column(name = "BO_CONTA_DESATIVADA", required = true, defaultValue = "'N'", label="Conta desativada?")
	private Boolean inactiveAccount=Boolean.FALSE;

	/*
	 * Conta do usuário está bloqueada?
	 */
	@Required
	@BooleanValue(trueValue = "S", falseValue = "N", type=BooleanType.STRING)
	@Column(name = "BO_CONTA_BLOQUEADA", required = true, defaultValue = "'N'", label="Conta bloqueada?")
	private Boolean blockedAccount=Boolean.FALSE;
	
	@Required
	@BooleanValue(falseValue = "N", trueValue = "S", type=BooleanType.STRING)
	@Column(name = "BO_HORARIO_LIVRE", length = 1, required = true, defaultValue = "'N'", label="Possuí horário livre acesso?")
	private Boolean boFreeAccessTime;

	@Required
	@BooleanValue(falseValue = "N", trueValue = "S", type=BooleanType.STRING)
	@Column(name = "BO_ADMINISTRADOR", length = 1, required = true, defaultValue = "'N'", label="É um administrador?")
	private Boolean boAdministrator;
	
	@Lob
	@Column(name="AVATAR", label="Avatar(Foto)")
	private String avatar;

	/*
	 * Horário de acesso do usuário
	 */
	@ForeignKey
	@Column(name = "ID_HORARIO", inversedColumn = "ID_HORARIO", label="Horário de acesso")
	private AccessTime accessTime;

	/*
	 * Grupos que o usuário é membro
	 */
	@Fetch(type = FetchType.LAZY, mode = FetchMode.MANY_TO_MANY)
	@JoinTable(name = "SEGURANCAGRUPOMEMBRO", joinColumns = @JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID_SEGURANCA"), inversedJoinColumns = @JoinColumn(name = "ID_GRUPO", referencedColumnName = "ID_SEGURANCA"))
	private List<Group> groups;

	/*
	 * Perfil (papel) do usuário dentro do sistema
	 */
	@ForeignKey
	@Column(name = "ID_PERFIL", inversedColumn = "ID_SEGURANCA", label="Perfil de segurança")
	private Profile profile;


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((login == null) ? 0 : login.hashCode());
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
		User other = (User) obj;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		return true;
	}

	@Override
	public boolean isPasswordNeverExpire() {
		return passwordNeverExpire;
	}

	@Override
	public boolean isInactiveAccount() {
		return inactiveAccount;
	}

	@Override
	public boolean isBlockedAccount() {
		return blockedAccount;
	}

	@Override
	public boolean isAdministrator() {
		return boAdministrator;
	}
	
	@JsonIgnore(true)
	public Set<IAction> getActionList() {
		Set<IAction> result = new HashSet<IAction>();
		for (Action action : getActions()) {
			result.add(action);
		}
		return result;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getChangePasswordOnNextLogin() {
		return changePasswordOnNextLogin;
	}

	public void setChangePasswordOnNextLogin(Boolean changePasswordOnNextLogin) {
		this.changePasswordOnNextLogin = changePasswordOnNextLogin;
	}

	public Boolean getAllowChangePassword() {
		return allowChangePassword;
	}

	public void setAllowChangePassword(Boolean allowChangePassword) {
		this.allowChangePassword = allowChangePassword;
	}

	public Boolean getAllowMultipleLogins() {
		return allowMultipleLogins;
	}

	public void setAllowMultipleLogins(Boolean allowMultipleLogins) {
		this.allowMultipleLogins = allowMultipleLogins;
	}

	public Boolean getPasswordNeverExpire() {
		return passwordNeverExpire;
	}

	public void setPasswordNeverExpire(Boolean passwordNeverExpire) {
		this.passwordNeverExpire = passwordNeverExpire;
	}

	public Boolean getInactiveAccount() {
		return inactiveAccount;
	}

	public void setInactiveAccount(Boolean inactiveAccount) {
		this.inactiveAccount = inactiveAccount;
	}

	public Boolean getBlockedAccount() {
		return blockedAccount;
	}

	public void setBlockedAccount(Boolean blockedAccount) {
		this.blockedAccount = blockedAccount;
	}

	public Boolean getBoFreeAccessTime() {
		return boFreeAccessTime;
	}

	public void setBoFreeAccessTime(Boolean boFreeAccessTime) {
		this.boFreeAccessTime = boFreeAccessTime;
	}

	public Boolean getBoAdministrator() {
		return boAdministrator;
	}

	public void setBoAdministrator(Boolean boAdministrator) {
		this.boAdministrator = boAdministrator;
	}

	public AccessTime getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(AccessTime accessTime) {
		this.accessTime = accessTime;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public IProfile getUserProfile() {
		return (IProfile) profile;
	}
	
	public Profile getProfile() {
		return profile;
	}


	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	@Override
	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@JsonIgnore
	@Override
	public String getUserId() {
		return this.getId()+"";
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

}