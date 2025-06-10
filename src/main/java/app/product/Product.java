package app.product;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;

/**
 * Класс, описывающий продукт.
 */
@Entity
@Table(name = "products")
@Data
@DynamicUpdate
@DynamicInsert
public class Product implements Comparable<Product>, Printable, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private double x;
    private double y;

    private final Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Float price;
    private String partNumber;
    private Double manufactureCost;
    private String unitOfMeasure; //Поле не может быть null
    private String personName;
    private float personHeight;
    private String personEyeColor;
    private String nationality;
    private int userId;


    public Product() {
        creationDate = new Date();
    }


    public Product(String name, double x, double y, float price,
                   String partNumber, Double manufactureCost, String unitOfMeasure,
                   String pName, float height, String color, String nationality) {
        setName(name);
        setX(x);
        setY(y);

        creationDate = new Date();

        setPrice(price);
        setPartNumber(partNumber);

        this.manufactureCost = manufactureCost;

        setUnitOfMeasure(unitOfMeasure);
        this.personName = pName;
        this.personHeight = height;
        this.personEyeColor = color;
        this.nationality = nationality;
    }

    public int getId() {return id;}

    public String getName() {return name;}

    public String getPartNumber() {return partNumber;}

    public double getX() {return x;}

    public double getY() {return y;}

    public Date getCreationDate() {return creationDate;}

    public Float getPrice() {return price;}

    public Double getManufactureCost() {return manufactureCost;}

    public int getUserId() {return userId;}

    public String getUnitOfMeasure() {return unitOfMeasure;}

    public void setId(int id) {this.id = id;}

    public void setName(String name) {
        if (name == null || name.equals("")){
            throw new app.exceptions.UnexceptableValue("name");
        } else {
            this.name = name;
        }
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setPrice(float price) {
        if (price <= 0) throw new app.exceptions.UnexceptableValue("price");
        else this.price = Float.valueOf(price);
    }

    public void setManufactureCost(Double manufactureCost) {
        this.manufactureCost = manufactureCost;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        if (unitOfMeasure == null) throw new app.exceptions.UnexceptableValue("unitOfMasure");
        else this.unitOfMeasure = unitOfMeasure;
    }



    public void setOwnerName(String ownerName) {
        if (ownerName == null) throw new app.exceptions.UnexceptableValue("owner");
        else this.personName = ownerName;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
                + ", coordinates=[" + x + ", " +y + "]"
                + ", creation date=" + creationDate
                + ", price=" + price
                + ", part number=" + partNumber
                + ", manufacture cost=" + manufactureCost
                + ", unit of measure=" + unitOfMeasure
                + ", owner=" + personName +
                "[" + personEyeColor + ", " + personHeight + ", " + personEyeColor + ", " + nationality + "]"
                + "]";
    }

    @Override
    public String print() {
        return name + "\n" +
                "---------------------------------------------" +
                "\nid: " + id +
                "\nname: " + name
                + "\ncoordinates: [" + x + ", "  + y + "]"
                + "\ncreation date: " + creationDate
                + "\nprice: " + price
                + "\npart number: " + partNumber
                + "\nmanufacture cost: " + manufactureCost
                + "\nunit of measure: " + unitOfMeasure
                + "\nowner:\n++++++++++++++++" +
                "\nname: " + personName
                + "\nheight: " + personHeight
                + "\neyes color: " + personEyeColor
                + "\nnationality: " + personName
                + "\n++++++++++++++++"
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





