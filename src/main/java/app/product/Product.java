package app.product;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

/**
 * Класс, описывающий продукт.
 */
public class Product implements Comparable<Product>, Printable, Serializable {
    private static int nextId = 1;//общий счётчик id для всех продуктов
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private final Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Float price; //Поле не может быть null, Значение поля должно быть больше 0
    private static HashSet<String> allPartNumbers = new HashSet<>();
    private String partNumber; //Значение этого поля должно быть уникальным, Длина строки не должна быть больше 51, Длина строки должна быть не меньше 23, Поле может быть null
    private Double manufactureCost; //Поле может быть null
    private UnitOfMeasure unitOfMeasure; //Поле не может быть null
    private Person owner; //Поле не может быть null


    public Product() {
        id = nextId;
        creationDate = new Date();
    }

    /**
     * Основной конструктор.
     * Проверяет, соответствуют ли значения требованиям полей. В случае, когда значения не удовлетворяют требованиям выбрасывает исключение UnexceptableValue.
     *
     * @param name имя, не может являться null
     * @param coordinates координаты продукта, не могут являться null
     * @param price цена, должна быть больше 0
     * @param partNumber партийный номер, должен быть длиннее 23 и короче 51 символа (может являться null)
     * @param manufactureCost стоимость производства, должна быть больше 0
     * @param unitOfMeasure единица измерения, не может являться null
     * @param owner владелец продукта, не может являться null
     */
    public Product(int id, String name, Coordinates coordinates, float price,
                   String partNumber, Double manufactureCost, UnitOfMeasure unitOfMeasure,
                   Person owner) {
        this.id = id;

        setName(name);
        setCoordinates(coordinates);

        creationDate = new Date();

        setPrice(price);
        setPartNumber(partNumber);

        this.manufactureCost = manufactureCost;

        setUnitOfMeasure(unitOfMeasure);
        setOwner(owner);

    }

    public int getId() {return id;}

    public String getName() {return name;}

    public String getPartNumber() {return partNumber;}

    public Coordinates getCoordinates() {return coordinates;}

    public Date getCreationDate() {return creationDate;}

    public Float getPrice() {return price;}

    public Double getManufactureCost() {return manufactureCost;}

    public UnitOfMeasure getUnitOfMeasure() {return unitOfMeasure;}

    public Person getOwner() {return owner;}

    public void setId(int id) {this.id = id;}

    public void setName(String name) {
        if (name == null || name.equals("")){
            //throw new app.UnexceptibleValue("name");
        } else {
            this.name = name;
        }
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) throw new app.exceptions.UnexceptableValue("coordinates");
        else this.coordinates = coordinates;
    }

    public void setPrice(float price) {
        if (price <= 0) throw new app.exceptions.UnexceptableValue("price");
        else this.price = Float.valueOf(price);
    }

    public void setPartNumber(String partNumber) {
        if (partNumber == null ||
                ((!allPartNumbers.contains(partNumber)) && partNumber.length() <= 51 && partNumber.length() >= 23) ||
            partNumber.equals("null")) {
            allPartNumbers.add(partNumber);
            this.partNumber = partNumber;
        } else {
            throw new app.exceptions.UnexceptableValue("partNumber");
        }
    }

    public void setManufactureCost(Double manufactureCost) {
        this.manufactureCost = manufactureCost;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        if (unitOfMeasure == null) throw new app.exceptions.UnexceptableValue("unitOfMasure");
        else this.unitOfMeasure = unitOfMeasure;
    }

    public void setOwner(Person owner) {
        if (owner == null) throw new app.exceptions.UnexceptableValue("owner");
        else this.owner = owner;
    }

    /**
     * Проверяет, занят ли указанный партинъйный номер.
     *
     * @param number партийный номер для проверки
     * @return true, если номер занят, в противном случае false
     */
    public static boolean isPartNumberBusy(String number) {
        return allPartNumbers.contains(number);
    }

    @Override
    public int compareTo(Product other){
        return this.id - other.id;
    }

    @Override
    public String toString() {
        return name + "=[" +
                "id=" + id +
                ", name=" + name
                + ", coordinates=" + coordinates
                + ", creation date=" + creationDate
                + ", price=" + price
                + ", part number=" + partNumber
                + ", manufacture cost=" + manufactureCost
                + ", unit of measure=" + unitOfMeasure
                + ", owner=" + owner
                + "]";
    }

    @Override
    public String print() {
        return name + "\n" +
                "---------------------------------------------" +
                "\nid: " + id +
                "\nname: " + name
                + "\ncoordinates: " + coordinates.print()
                + "\ncreation date: " + creationDate
                + "\nprice: " + price
                + "\npart number: " + partNumber
                + "\nmanufacture cost: " + manufactureCost
                + "\nunit of measure: " + unitOfMeasure
                + "\nowner: " + owner.print()
                + "\n---------------------------------------------";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;

        if (other == null) return false;

        if (getClass() != other.getClass()) return false;

        Product o = (Product) other;

        return id == o.id;
    }
}





