package io.helidon.examples.data.pokemons.repository.generated;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.helidon.data.runtime.DynamicFinderCriteria;
import io.helidon.data.runtime.DynamicFinderOrder;

public interface TypeFilter extends TypeCriteria, TypeOrder {

    interface Builder extends TypeCriteria.Builder<Builder, TypeFilter>, TypeOrder.Builder<Builder, TypeFilter> {
    }

}
class TypeFilterImpl implements TypeFilter {

    // Entity name.
    static final String ENTITY = "Type";
    // Parameter id
    static final String ID = "id";
    // Parameter name
    static final String NAME = "name";

    // Entity attributes case-insensitive matching Map.
    static final Map<String, String> ENTITY_ATTRS = initEntityAttrs();

    private final Optional<DynamicFinderOrder> order;
    private final Optional<DynamicFinderCriteria> criteria;

    private TypeFilterImpl(Optional<DynamicFinderCriteria> criteria, Optional<DynamicFinderOrder> order) {
        this.criteria = criteria;
        this.order = order;
    }

    public Optional<DynamicFinderOrder> order() {
        return order;
    }

    public Optional<DynamicFinderCriteria> criteria() {
        return criteria;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    // Initialize entity attributes case-insensitive matching Map.
    private static Map<String, String> initEntityAttrs() {
        Map<String, String> map = new HashMap<>();
        map.put(ID.toLowerCase(), ID);
        map.put(NAME.toLowerCase(), NAME);
        return map;
    }

    static class BuilderImpl implements Builder {

        // Criteria part of the Helidon dynamic finder query: id parameter conditions
        private final List<DynamicFinderCriteria.Condition> idList;
        // Criteria part of the Helidon dynamic finder query: name parameter conditions
        private final List<DynamicFinderCriteria.Condition> nameList;

        // Order part of the Helidon dynamic finder query.
        private final List<DynamicFinderOrder.Order> orderList;

        BuilderImpl() {
            this.idList = new LinkedList<>();
            this.nameList = new LinkedList<>();
            this.orderList = new LinkedList<>();
        }

        // Equal for id
        @Override
        public Builder id(int id) {
            add(idList, NAME, DynamicFinderCriteria.Condition.Operator.EQUALS, Integer.class, id);
            return this;
        }

        // Equal for ids
        @Override
        public Builder id(Iterable<Integer> ids) {
            ids.forEach(id -> add(idList, NAME, DynamicFinderCriteria.Condition.Operator.EQUALS, Integer.class, id));
            return this;
        }

        // Equal for name
        @Override
        public Builder name(String name) {
            add(nameList, NAME, DynamicFinderCriteria.Condition.Operator.EQUALS, String.class, name);
            return this;
        }

        // Equal for names
        @Override
        public Builder name(Iterable<String> names) {
            names.forEach(name -> add(nameList, NAME, DynamicFinderCriteria.Condition.Operator.EQUALS, String.class, name));
            return this;
        }

        /**
         * Set ordering rule for entity attribute id.
         *
         * @param order ordering keyword
         * @return ordering builder
         */
        public Builder orderById(String order) {
            return orderBy(ID, DynamicFinderOrder.Order.Method.parse(order));
        }

        // Add next expression to the expressions list.
        private static <T> void add(List<DynamicFinderCriteria.Condition> list,
                                    String property,
                                    DynamicFinderCriteria.Condition.Operator criteriaOperator,
                                    Class<T> valueClass,
                                    T... value) {
            int size = value == null ? 0 : value.length;
            @SuppressWarnings("unchecked")
            DynamicFinderCriteria.Condition.Parameter<T>[] values
                    = new DynamicFinderCriteria.Condition.Parameter[size];
            for (int i = 0; i < size; i++) {
                values[i] = DynamicFinderCriteria.Condition.Parameter.Value.build(valueClass, value[i]);
            }
            list.add(DynamicFinderCriteria.Condition.build(property, criteriaOperator, values));
        }

        /**
         * Set ordering rule for entity attribute id.
         *
         * @param order ordering keyword
         * @return ordering method
         */
        public Builder orderById(DynamicFinderOrder.Order.Method order) {
            return orderBy(ID, order);
        }

        /**
         * Set ordering rule for entity attribute id.
         *
         * @param order ordering keyword
         * @return ordering builder
         */
        public Builder orderByName(String order) {
            return orderBy(ID, DynamicFinderOrder.Order.Method.parse(order));
        }

        /**
         * Set ordering rule for entity attribute id.
         *
         * @param order ordering keyword
         * @return ordering method
         */
        public Builder orderByName(DynamicFinderOrder.Order.Method order) {
            return orderBy(ID, order);
        }

        /**
         * Set ordering rule for provided entity attribute.
         *
         * @param name  name of the entity attribute
         * @param order ordering keyword
         * @return ordering builder
         */
        public Builder orderBy(String name, String order) {
            Objects.requireNonNull(name, "Name of entity attribute is null");
            Objects.requireNonNull(order, "Ordering keyword is null.");
            return orderBy(name, DynamicFinderOrder.Order.Method.parse(order));
        }

        /**
         * Set ordering rule for provided entity attribute.
         *
         * @param name  name of the entity attribute
         * @param order ordering method
         * @return ordering builder
         */
        public Builder orderBy(String name, DynamicFinderOrder.Order.Method order) {
            Objects.requireNonNull(name, "Name of entity attribute is null");
            Objects.requireNonNull(order, "Ordering method is null.");
            // Case-insensitive entity attribute matching.
            String attributeName = ENTITY_ATTRS.get(name);
            if (attributeName == null) {
                throw new IllegalArgumentException(String.format("Attribute %s was not found in entity %s.", name, ENTITY));
            }
            orderList.add(DynamicFinderOrder.Order.build(order, attributeName));
            return this;
        }

        @Override
        public TypeFilter build() {
            List<DynamicFinderCriteria.Expression> expressions = FilterHelper.buildExpressions(idList, nameList);
            Optional<DynamicFinderCriteria> criteria = switch (expressions.size()) {
                case 0 -> Optional.empty();
                case 1 -> Optional.of(DynamicFinderCriteria.build(expressions.get(0)));
                default -> Optional.of(
                        DynamicFinderCriteria.build(
                                FilterHelper.buildCompound(
                                        expressions,
                                        DynamicFinderCriteria.Compound.NextExpression.Operator.AND)));
            };
            Optional<DynamicFinderOrder> order = orderList.isEmpty()
                    ? Optional.empty()
                    : Optional.of(DynamicFinderOrder.build(orderList));
            return new TypeFilterImpl(criteria, order);
        }

    }

}
