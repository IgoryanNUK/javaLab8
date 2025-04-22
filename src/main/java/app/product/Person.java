package app.product;

import app.exceptions.UnexceptableValue;

import java.io.Serializable;

/**
 * Класс человека.
 */
public class Person implements Printable, Serializable {
    //private HashSet<Person> people = new HashSet<>();
    /** имя */
    private String name;//Поле не может быть null, Строка не может быть пустой
    /** рост */
    private float height; //Значение поля должно быть больше 0
    /** цвет глаз */
    private Color eyeColor; //Поле не может быть null
    /** национальность */
    private Country nationality; //Поле может быть null

    public Person() {}

    /** Основной конструктор. Поле имени и цвета глаз не может быть null. */
    public Person(String name, float height, Color eyeColor, Country nationality) {
        setName(name);
        setHeight(height);
        setEyeColor(eyeColor);

        this.nationality = nationality;
    }

    /*public static Person getByName() {
        people.
    }*/

    public String getName() {return name;}

    public float getHeight() {return height;}

    public Color getEyeColor() {return eyeColor;}

    public Country getNationality() {return nationality;}

    public void setName(String name) {
        if (name == null || name.equals("")) throw new UnexceptableValue("name");
        else this.name = name;
    }

    public void setHeight(float height) {
        if (height <= 0) throw new UnexceptableValue("height");
        else this.height = height;
    }

    public void setEyeColor(Color eyeColor) {
        if (eyeColor == null) throw new UnexceptableValue("eyeColor");
        else this.eyeColor = eyeColor;
    }

    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }

    @Override
    public String toString() {
        return "Person[name=" + name +
                ", height=" + height +
                ", eyeColor=" + eyeColor +
                ", nationality=" + nationality +
                "]";
    }

    @Override
    public String print() {
        return name +
                "\n+++++++++++++++++++++++++" +
                "\nheight: " + height +
                "\neye color: " + eyeColor +
                "\nnationality: " + (nationality!=null ? nationality.toString() : "unknown")
                + "\n+++++++++++++++++++++++++";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (o == null) return false;

        if (o.getClass() != getClass()) return false;

        Person p = (Person) o;
        return name.equals(p.name) && height == p.height && eyeColor.equals(p.eyeColor)
                && nationality.equals(p.nationality);
    }
}
