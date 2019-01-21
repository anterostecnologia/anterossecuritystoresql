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
package br.com.anteros.security.store.sql.repository;

import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.security.store.domain.IResource;
import br.com.anteros.security.store.domain.ISystem;
import br.com.anteros.security.store.sql.domain.Resource;

public interface ResourceRepository extends SQLRepository<Resource, Long> {

	Resource getResourceByName(String systemName, String resourceName);

	Resource addResource(ISystem system, String resourceName, String description);

	Resource refreshResource(IResource resource);
	
}
