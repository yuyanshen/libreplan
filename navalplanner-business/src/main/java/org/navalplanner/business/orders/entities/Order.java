/*
 * This file is part of NavalPlan
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

package org.navalplanner.business.orders.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.validator.AssertTrue;
import org.hibernate.validator.NotNull;
import org.navalplanner.business.calendars.entities.BaseCalendar;
import org.navalplanner.business.common.entities.OrderSequence;
import org.navalplanner.business.externalcompanies.entities.ExternalCompany;
import org.navalplanner.business.planner.entities.DayAssignment;
import org.navalplanner.business.planner.entities.Task;
import org.navalplanner.business.planner.entities.TaskElement;
import org.navalplanner.business.planner.entities.TaskGroup;
import org.navalplanner.business.resources.entities.Resource;
import org.navalplanner.business.scenarios.entities.OrderVersion;
import org.navalplanner.business.scenarios.entities.Scenario;
import org.navalplanner.business.templates.entities.OrderTemplate;
import org.navalplanner.business.users.entities.OrderAuthorization;

/**
 * It represents an {@link Order} with its related information. <br />
 * @author Óscar González Fernández <ogonzalez@igalia.com>
 */
public class Order extends OrderLineGroup {

    public static Order create() {
        Order order = new Order();
        order.setNewObject(true);

        OrderLineGroup.setupOrderLineGroup(order);

        return order;
    }


    public static Order createUnvalidated(String code) {
        Order order = create(new Order(), code);
        OrderLineGroup.setupOrderLineGroup(order);
        return order;
    }

    /**
     * Constructor for hibernate. Do not use!
     */
    public Order() {

    }

    private String responsible;

    // TODO turn into a many to one relationship when Customer entity is defined

    private Boolean dependenciesConstraintsHavePriority;

    private BaseCalendar calendar;

    private Boolean codeAutogenerated = false;

    private Integer lastOrderElementSequenceCode = 0;

    private BigDecimal workBudget = BigDecimal.ZERO.setScale(2);

    private BigDecimal materialsBudget = BigDecimal.ZERO.setScale(2);

    private Integer totalHours = 0;

    private OrderStatusEnum state = OrderStatusEnum.getDefault();

    private ExternalCompany customer;

    private String customerReference;

    private Map<Scenario, OrderVersion> scenarios = new HashMap<Scenario, OrderVersion>();

    private Set<OrderAuthorization> orderAuthorizations = new HashSet<OrderAuthorization>();

    public void addOrderAuthorization(OrderAuthorization orderAuthorization) {
        orderAuthorization.setOrder(this);
        orderAuthorizations.add(orderAuthorization);
    }

    public Map<Scenario, OrderVersion> getScenarios() {
        return Collections.unmodifiableMap(scenarios);
    }

    public BigDecimal getWorkBudget() {
        if (workBudget == null) {
            return BigDecimal.ZERO;
        }
        return workBudget;
    }

    public void setWorkBudget(BigDecimal workBudget) {
        if (workBudget == null) {
            workBudget = BigDecimal.ZERO.setScale(2);
        }
        this.workBudget = workBudget;
    }

    public BigDecimal getMaterialsBudget() {
        if (materialsBudget == null) {
            return BigDecimal.ZERO;
        }
        return materialsBudget;
    }

    public void setMaterialsBudget(BigDecimal materialsBudget) {
        if (materialsBudget == null) {
            materialsBudget = BigDecimal.ZERO.setScale(2);
        }
        this.materialsBudget = materialsBudget;
    }

    public BigDecimal getTotalBudget() {
        return getWorkBudget().add(getMaterialsBudget());
    }

    public Integer getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Integer totalHours) {
        this.totalHours = totalHours;
    }

    public OrderStatusEnum getState() {
        return state;
    }

    public void setState(OrderStatusEnum state) {
        this.state = state;
    }

    public String getCustomerReference() {
        return this.customerReference;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public ExternalCompany getCustomer() {
        return this.customer;
    }

    public void setCustomer(ExternalCompany customer) {
        this.customer = customer;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public boolean isDeadlineBeforeStart() {
        return getDeadline() != null && getDeadline().before(getInitDate());
    }

    public List<OrderElement> getOrderElements() {
        return new ArrayList<OrderElement>(getChildren());
    }

    public TaskGroup getAssociatedTaskElement() {
        return (TaskGroup) super.getAssociatedTaskElement();
    }

    public List<TaskElement> getAllChildrenAssociatedTaskElements() {
        List<TaskElement> result = new ArrayList<TaskElement>();

        List<OrderElement> children = getAllChildren();
        for (OrderElement orderElement : children) {
            result.add(orderElement.getAssociatedTaskElement());
        }

        return result;
    }

    public List<TaskElement> getAssociatedTasks() {
        ArrayList<TaskElement> result = new ArrayList<TaskElement>();
        TaskGroup taskGroup = getAssociatedTaskElement();
        if (taskGroup != null) {
            result.addAll(taskGroup.getChildren());
        }
        return result;
    }

    public boolean isSomeTaskElementScheduled() {
        return isScheduled();
    }

    @SuppressWarnings("unused")
    @AssertTrue(message = "the order must have a init date")
    private boolean checkConstraintOrderMustHaveStartDate() {
        return getInitDate() != null;
    }

    @SuppressWarnings("unused")
    @AssertTrue(message = "deadline must be after start date")
    private boolean checkConstraintDeadlineMustBeAfterStart() {
        return !this.isDeadlineBeforeStart();
    }

    @SuppressWarnings("unused")
    @AssertTrue(message = "At least one HoursGroup is needed for each OrderElement")
    private boolean checkConstraintAtLeastOneHoursGroupForEachOrderElement() {
        for (OrderElement orderElement : this.getOrderElements()) {
            if (!orderElement.checkAtLeastOneHoursGroup()) {
                return false;
            }
        }
        return true;
    }

    public List<DayAssignment> getDayAssignments() {
        List<DayAssignment> dayAssignments = new ArrayList<DayAssignment>();
        for (OrderElement orderElement : getAllOrderElements()) {
            Set<TaskElement> taskElements = orderElement.getTaskElements();
            for (TaskElement taskElement : taskElements) {
                if (taskElement instanceof Task) {
                    dayAssignments.addAll(taskElement.getDayAssignments());
                }
            }
        }
        return dayAssignments;
    }

    public List<OrderElement> getAllOrderElements() {
        List<OrderElement> result = new ArrayList<OrderElement>(
                this
                .getChildren());
        for (OrderElement orderElement : this.getChildren()) {
            result.addAll(orderElement.getAllChildren());
        }
        return result;
    }

    public Set<Resource> getResources() {
        Set<Resource> resources = new HashSet<Resource>();
        for (DayAssignment dayAssignment : getDayAssignments()) {
            resources.add(dayAssignment.getResource());
        }
        return resources;
    }

    @Override
    protected void applyStartConstraintTo(Task task) {
        // the initDate of order don't imply a start constraint at a task
    }

    public boolean getDependenciesConstraintsHavePriority() {
        return dependenciesConstraintsHavePriority != null
                && dependenciesConstraintsHavePriority;
    }

    public void setDependenciesConstraintsHavePriority(
            Boolean dependenciesConstraintsHavePriority) {
        this.dependenciesConstraintsHavePriority = dependenciesConstraintsHavePriority;
    }

    public void setCalendar(BaseCalendar calendar) {
        this.calendar = calendar;
    }

    @NotNull(message = "order calendar not specified")
    public BaseCalendar getCalendar() {
        return calendar;
    }

    public void setCodeAutogenerated(Boolean codeAutogenerated) {
        this.codeAutogenerated = codeAutogenerated;
    }

    public boolean isCodeAutogenerated() {
        return codeAutogenerated != null && codeAutogenerated;
    }

    public void incrementLastOrderElementSequenceCode() {
        if (this.lastOrderElementSequenceCode == null) {
            this.lastOrderElementSequenceCode = 0;
        }
        this.lastOrderElementSequenceCode++;
    }

    @NotNull(message = "last order element sequence code not specified")
    public Integer getLastOrderElementSequenceCode() {
        return lastOrderElementSequenceCode;
    }

    @AssertTrue(message = "some code is repeated between order code and order element codes")
    public boolean checkConstraintCodeNotRepeated() {
        Set<String> codes = new HashSet<String>();
        codes.add(getCode());

        for (OrderElement orderElement : getAllOrderElements()) {
            String code = orderElement.getCode();
            if (codes.contains(code)) {
                return false;
            }
            codes.add(code);
        }

        return true;
    }

    @Override
    public Order getOrder() {
        return this;
    }

    @Override
    public OrderTemplate createTemplate() {
        return OrderTemplate.create(this);
    }

    public void generateOrderElementCodes(int numberOfDigits) {
        for (OrderElement orderElement : this.getAllOrderElements()) {
            if ((orderElement.getCode() == null)
                    || (orderElement.getCode().isEmpty())
                    || (!orderElement.getCode().startsWith(this.getCode()))) {
                this.incrementLastOrderElementSequenceCode();
                String orderElementCode = OrderSequence.formatValue(
                        numberOfDigits, this.getLastOrderElementSequenceCode());
                orderElement.setCode(this.getCode()
                        + OrderSequence.CODE_SEPARATOR + orderElementCode);
            }

            if (orderElement instanceof OrderLine) {
                for (HoursGroup hoursGroup : orderElement.getHoursGroups()) {
                    if ((hoursGroup.getCode() == null)
                            || (hoursGroup.getCode().isEmpty())
                            || (!hoursGroup.getCode().startsWith(
                                    orderElement.getCode()))) {
                        ((OrderLine) orderElement)
                                .incrementLastHoursGroupSequenceCode();
                        String hoursGroupCode = OrderSequence.formatValue(
                                numberOfDigits, ((OrderLine) orderElement)
                                        .getLastHoursGroupSequenceCode());
                        hoursGroup
                                .setCode(orderElement.getCode()
                                        + OrderSequence.CODE_SEPARATOR
                                        + hoursGroupCode);
                    }
                }
            }
        }
    }

    @AssertTrue(message = "some code is repeated between hours group codes")
    public boolean checkConstraintHoursGroupCodeNotRepeated() {
        Set<String> codes = new HashSet<String>();

        for (HoursGroup hoursGroup : getHoursGroups()) {
            String code = hoursGroup.getCode();
            if (codes.contains(code)) {
                return false;
            }
            codes.add(code);
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((getId() == null || isNewObject()) ? super.hashCode()
                        : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || isNewObject())
            return false;
        Order other = (Order) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }

}
