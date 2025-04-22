package app.messages.requests;

import app.product.Product;

import java.io.Serializable;
import java.util.function.Predicate;

@FunctionalInterface
public interface Condition extends Serializable, Predicate<Product> {
    boolean test(Product p);
}
