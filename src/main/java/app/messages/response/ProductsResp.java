package app.messages.response;

import app.product.Product;

import java.util.List;

public class ProductsResp implements Response {
    private final ResponseType type = ResponseType.PRODUCTS;
    private final List<Product> products;

    public ProductsResp(List<Product> pr) {
        products = pr;
    }

    @Override
    public final ResponseType getType() {return type;}

    public List<Product> getProducts() {return products;}

}
