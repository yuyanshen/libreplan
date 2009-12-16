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

package org.navalplanner.ws.orders.api;

import java.util.Date;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.navalplanner.business.orders.entities.OrderLine;

/**
 * DTO for {@link OrderLine} entity.
 *
 * @author Manuel Rego Casasnovas <mrego@igalia.com>
 */
@XmlRootElement(name = "order-line")
public class OrderLineDTO extends OrderElementDTO {

    @XmlElementWrapper(name = "hours-groups")
    @XmlElement(name = "hours-group")
    public Set<HoursGroupDTO> hoursGroups;

    public OrderLineDTO() {
        super();
    }

    public OrderLineDTO(String name, String code, Date initDate, Date deadline,
            String description, Set<LabelDTO> labels,
            Set<MaterialAssignmentDTO> materialAssignments,
            Set<HoursGroupDTO> hoursGroups) {
        super(name, code, initDate, deadline, description, labels, materialAssignments);
        this.hoursGroups = hoursGroups;
    }

}
