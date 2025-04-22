package app.messages.requests;

import app.product.Product;

import java.util.function.Predicate;

/**
 * Класс запроса на удаление объектов из коллекции.
 */
public class RemoveReq implements Request {
    private final RequestType type = RequestType.REMOVE;
    private final Condition predicate;

    /**
     *
     * @param predicate условие удаления объекта коллекции
     */
    public RemoveReq(Condition predicate) {
        this.predicate = predicate;
    }

    public Predicate<Product> getPredicate() {
        return predicate;
    }

    @Override
    public RequestType getType() {return type;}
}
