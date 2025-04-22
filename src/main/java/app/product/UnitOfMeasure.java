package app.product;

public enum UnitOfMeasure implements Comparable<UnitOfMeasure> {
    METERS("метры"),
    CENTIMETERS("сантиметры"),
    PCS("штуки"),
    LITERS("литры"),
    MILLIGRAMS("миллиграммы");

    private final String naming;

    public String getNaming() {return naming;}

    UnitOfMeasure(String naming) {
        this.naming = naming;
    }
}
