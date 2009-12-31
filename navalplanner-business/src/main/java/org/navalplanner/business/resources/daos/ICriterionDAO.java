/*
 * This file is part of ###PROJECT_NAME###
 *
 * Copyright (C) 2009 Fundación para o Fomento da Calidade Industrial e
 *                    Desenvolvemento Tecnolóxico de Galicia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.navalplanner.business.resources.daos;

import java.util.List;

import org.navalplanner.business.common.daos.IGenericDAO;
import org.navalplanner.business.common.exceptions.InstanceNotFoundException;
import org.navalplanner.business.resources.entities.Criterion;
import org.navalplanner.business.resources.entities.ICriterionType;
import org.springframework.transaction.annotation.Transactional;

/**
 * Contract for {@link CriterionDAO} <br />
 *
 * @author Óscar González Fernández <ogonzalez@igalia.com>
 * @author Diego Pino García <dpino@igalia.com>
 */
public interface ICriterionDAO extends IGenericDAO<Criterion, Long> {

    public void removeByNameAndType(Criterion criterion);

    List<Criterion> findByNameAndType(Criterion criterion);

    Criterion findUniqueByNameAndType(Criterion criterion) throws InstanceNotFoundException;

    boolean existsByNameAndType(Criterion entity);

    Criterion find(Criterion criterion) throws InstanceNotFoundException;

    List<Criterion> findByType(ICriterionType<?> type);

    @Transactional(readOnly = true)
    List<Criterion> getAll();

    boolean thereIsOtherWithSameNameAndType(Criterion criterion);

    List<Criterion> findByNameAndType(String name, String type);

}
