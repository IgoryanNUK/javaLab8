package app.messages.requests;

import app.product.Product;

import java.util.function.Predicate;

public class GetReq implements Request {
    private final RequestType type = RequestType.GET;
    private final Condition condition;

    public GetReq(Condition c) {
        condition = c;
    }

    @Override
    public RequestType getType() {return type;}

    public Predicate<Product> getPredicate() {return condition;}

}
