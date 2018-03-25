package export.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A source of ground truth bindings for the evaluation of export column expressions.
 * Created by shengli on 12/27/15.
 */
public interface ExpressionContext {

    default String name() {
        return this.getClass().getName() + "[" + this.hashCode() + "]";
    }

    /**
     * Return an iterable of all the bound identifiers.
     * @return an iterable over all the bound identifier names
     */
    Iterable<String> identifiers();

    Object resolve(String identifier);

    default String resolveString(String identifier) {
        Object value = resolve(identifier);
        return value == null ? null : String.valueOf(value);
    };

    default Boolean resolveBoolean(String identifier) {
        try {
            return (Boolean)resolve(identifier);
        } catch (ClassCastException e) {
            throw new ResolutionException("Value was not of type boolean for identifier: " + identifier, e);
        }
    };

    default Integer resolveInteger(String identifier) {
        try {
            return (Integer) resolve(identifier);
        } catch (ClassCastException e) {
            throw new ResolutionException("Value was not of type Integer for identifier: " + identifier, e);
        }
    };

    default Long resolveLong(String identifier) {
        try {
            return (Long)resolve(identifier);
        } catch (ClassCastException e) {
            throw new ResolutionException("Value was not of type Long for identifier: " + identifier, e);
        }
    };

    default Double resolveDouble(String identifier) {
        try {
            return (Double) resolve(identifier);
        } catch (ClassCastException e) {
            throw new ResolutionException("Value was not of type Double for identifier: " + identifier, e);
        }
    };

    default Float resolveFloat(String identifier) {
        try {
            return (Float)resolve(identifier);
        } catch (ClassCastException e) {
            throw new ResolutionException("Value was not of type Float for identifier: " + identifier, e);
        }
    };

    default BigInteger resolveBigInteger(String identifier) {
        try {
            return (BigInteger)resolve(identifier);
        } catch (ClassCastException e) {
            throw new ResolutionException("Value was not of type BigInteger for identifier: " + identifier, e);
        }
    };

    default BigDecimal resolveBigDecimal(String identifier) {
        try {
            return (BigDecimal) resolve(identifier);
        } catch (ClassCastException e) {
            throw new ResolutionException("Value was not of type BigDecimal for identifier: " + identifier, e);
        }
    };

    default Number resolveNumber(String identifier) {
        try {
            return (Number)resolve(identifier);
        } catch (ClassCastException e) {
            throw new ResolutionException("Value was not of type Number for identifier: " + identifier, e);
        }
    };

    default List<?> resolveList(String identifier) {
        try {
            return (List)resolve(identifier);
        } catch (ClassCastException e) {
            throw new ResolutionException("Value was not of type List for identifier: " + identifier, e);
        }
    };

    default Set<?> resolveSet(String identifier) {
        try {
            return (Set)resolve(identifier);
        } catch (ClassCastException e) {
            throw new ResolutionException("Value was not of type Set for identifier: " + identifier, e);
        }
    };

    default Iterable<?> resolveIterable(String identifier) {
        try {
            return (Iterable)resolve(identifier);
        } catch (ClassCastException e) {
            throw new ResolutionException("Value was not of type Iterable for identifier: " + identifier, e);
        }
    };

    default Map<?, ?> resolveMap(String identifier) {
        try {
            return (Map)resolve(identifier);
        } catch (ClassCastException e) {
            throw new ResolutionException("Value was not of type Map for identifier: " + identifier, e);
        }
    };


}
