package app.client;

import app.exceptions.UnknownException;
import app.product.*;

import java.io.IOException;

/**
 * Класс, реализующий ввод значений полей объектов.
 */
public class EnterManager {
    /**
     * Реализует ввод имён объектов.
     * При вводе пустой строки выводит сообщение о некорректности значения имени и повторяет ввод.
     *
     * @param ioManager менеджер ввода-вывода
     * @param fieldName название вводимого значения
     * @return имя объекта
     * @throws IOException
     */
    public static String nameEnter(UserIOManager ioManager, String fieldName, boolean isScript) throws IOException {
        String name;
        do {
            name = ioManager.ask("Введите " + fieldName + ": ");
            if (name != null && !name.equals("")) {
                if (isScript) System.out.println(name);
                return name;
            }
            else
                ioManager.println("(⊙ _ ⊙) " + fieldName + " не может являться пустой строкой. Пожалуйста, повторите ввод");
        } while ((!isScript || ioManager.getInput().ready()) && name.equals(""));
        return null;
    }

    /**
     * Реализет ввод координат.
     * Требует, чтобы координаты являлись вещественным числом.
     *
     * @param ioManager менеджер ввода-вывода
     * @return объект кординат
     */
    public static Coordinates coordinatesEnter(UserIOManager ioManager, boolean isScript) throws IOException{
        String wem = "(⊙ _ ⊙) Координата может принимать только вещественное число (большее 0). Пожалуйста, повторите попытку.";
        boolean isEntered = false;
        Double x = 0D;
        do {
            try {
                x = Double.valueOf(ioManager.ask("Введите координату x: "));
                if (isScript) ioManager.println(x.toString());
                isEntered = true;
            } catch (NumberFormatException e) {
                ioManager.println(wem);
            }
        } while (!isEntered && (!isScript || ioManager.getInput().ready()));


        isEntered = false;
        Double y = 0D;
        do {
            try {
                y = Double.valueOf(ioManager.ask("Введите координату y: "));
                if (isScript) ioManager.println(y.toString());
                isEntered = true;
            } catch (NumberFormatException e) {
                ioManager.println(wem);
            }
        } while (!isEntered && (!isScript || ioManager.getInput().ready()));

        return new Coordinates(x, y);
    }

    /**
     * Реализует ввод вещественного значения.
     * Требует, чтобы значение являлось вещественным числом, большим 0.
     *
     * @param io менеджер ввода-вывода
     * @param fieldName назвние параметра, для которого вводится значение
     * @return значение
     */
    public static float floatEnter(UserIOManager io, String fieldName, boolean isScript) throws IOException{
        String wem = "(⊙ _ ⊙) " + fieldName + " может принимать только вещественное число, большее 0. Пожалуйста, повторите попытку.";
        float fl = 0;
        while (fl == 0 && (!isScript || io.getInput().ready())) {
            try {
                fl = Float.parseFloat(io.ask("Введите " + fieldName + ": "));
                fl = fl > 0 ? fl : 0;
            } catch (NumberFormatException e) {}
            if (fl == 0) io.println(wem);
            else if (isScript) io.println(String.valueOf(fl));
        }

        return fl;
    }

    /**
     * Реализует ввод партийного номера продукта.
     * Требует, чтобы номер являлся строкой длины от 23 до 51, при вводе пустой строки возвращает null.
     *
     * @param io менеджер ввода-вывода
     * @return партийный номер продукта
     */
    public static String partNumberEnter(UserIOManager io, boolean isScript) {
        try {
            String pN = null;
            boolean isEntered = false;
            String wem = "(⊙ _ ⊙) Партийный номер должен быть уникальным, а также не должен бать длиннее 51 символа и короче 23 символов. Пожалуйста, повторите попытку.";

            while (!isEntered && (!isScript || io.getInput().ready())) {
                pN = io.ask("Введите ПАРТИЙНЫЙ номер ([ENTER] пропустить поле): ");
                if (pN.equals("")) {
                    pN = null;
                    isEntered = true;
                } else if (pN.length() >= 23 && pN.length() <= 51) {
                    isEntered = true;
                } else {
                    io.println(wem);
                }
                if (isEntered && isScript) io.println(pN);
            }

            return pN;
        } catch (Exception e) {
            throw new UnknownException(e);
        }
    }

    /**
     * Реализует ввод стоимости производства продукта.
     * Повторяет ввод, пока значение не будет удовлетворять ограничениям
     *
     * @param io менеджер ввода-вывода
     * @return
     * @throws IOException
     */
    public static Double manufactureCostEnter(UserIOManager io, boolean isScript) throws IOException {
        Double mC = null;

        String s = null;
        while (s == null && (!isScript || io.getInput().ready())) {
            s = io.ask("Введите стоимость производства продукта ([ENTER] пропустить поле): ");
            if (!s.equals("")) {
                try {
                    mC = Double.valueOf(s);
                } catch (NumberFormatException e) {
                    io.println("(⊙ _ ⊙) Неверный формат. Пожалуйста, введите вещественное число, или пропустите это поле.");
                    s = null;
                }
            }
            if (s != null && isScript) io.println(mC != null ? mC.toString() : null);
        }

        return mC;
    }

    /**
     * Реализует ввод пользователя перечисляемого типа.
     * Если не запущен скрипт, то ввод будет запрашиваться до тех пор, пока пользователь не введёт валидное значение.
     *
     * @param io менеджер ввода-вывода
     * @param values значения, которые может принимать перечисляемый тип
     * @param fieldName название поля, для которого вводится значение
     * @param isNullExceptable флаг, отвечающий за то, приемлемо ли значение null для вводимого поля
     * @param isScript обозначает, читается ли сейчас скрипт
     * @return значение перечисляемого типа, введённое пользователеем, или null (если приемлем)
     * @param <E> перечисляемый тип
     * @throws IOException
     */
    public static <E extends Enum<E>> E enumEnter(UserIOManager io, E[] values, String fieldName, boolean isNullExceptable, boolean isScript) throws IOException{
        while (!isScript || io.getInput().ready()) {
            io.printf("Введите " + fieldName + " (");
            int i = 0;
            for (E u : values) {
                io.printf(u.toString());
                i++;
                if (values.length != i) {
                    io.printf(" / ");
                }
            }
            io.printf("): ");

            String s = io.getInput().readLine().trim().toUpperCase();
            if (isNullExceptable && s.equals("")) return null;
            for (E u : values) {
                if (s.equals(u.toString())) {
                    if (isScript) System.out.println(u);
                    return u;
                }
            }
            io.println("(⊙ _ ⊙) Неверный формат. Пожалуйста, введите один из предложенных значений.");
        }
        return null;
    }

    /**
     * Реализует ввод поля, являющимся классом человека.
     * Поочередно зпрашивает ввод имени, роста, цвета глаз, национальности (необязательное поле).
     *
     * @param ioManager менеджер ввода-вывода
     * @param isScript обозначет то, читается ли сейчас скрипт
     * @return объект человека, введённого пользователем
     * @throws IOException
     */
    public static Person personEnter(UserIOManager ioManager, boolean isScript) throws IOException {

        String name = nameEnter(ioManager, "имя владельца", isScript);
        float height = floatEnter(ioManager, "рост владельца", isScript);
        Color c = enumEnter(ioManager, Color.values(), "цвет глаз владельца", false, isScript);
        Country country = enumEnter(ioManager, Country.values(), "национальность владельца", true, isScript);

        return new Person(name, height, c, country);
    }

}
