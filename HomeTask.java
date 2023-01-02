
/**
 Напишите приложение, которое будет запрашивать у пользователя следующие данные в произвольном порядке, разделенные пробелом:
Фамилия Имя Отчество датарождения номертелефона пол
Форматы данных:
фамилия, имя, отчество - строки

дата_рождения - строка формата dd.mm.yyyy

номер_телефона - целое беззнаковое число без форматирования

пол - символ латиницей f или m.

Приложение должно проверить введенные данные по количеству. Если количество не совпадает с требуемым, вернуть код ошибки, 
обработать его и показать пользователю сообщение, что он ввел меньше и больше данных, чем требуется.

Приложение должно попытаться распарсить полученные значения и выделить из них требуемые параметры. 
Если форматы данных не совпадают, нужно бросить исключение, соответствующее типу проблемы. 
Можно использовать встроенные типы java и создать свои. Исключение должно быть корректно обработано, 
пользователю выведено сообщение с информацией, что именно неверно.

Если всё введено и обработано верно, должен создаться файл с названием, равным фамилии, в него в одну строку должны записаться полученные данные, вида

<Фамилия><Имя><Отчество><датарождения> <номертелефона><пол>

Однофамильцы должны записаться в один и тот же файл, в отдельные строки.

Не забудьте закрыть соединение с файлом.

При возникновении проблемы с чтением-записью в файл, исключение должно быть корректно обработано, пользователь должен увидеть стектрейс ошибки.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Pattern;

public class HomeTask {

    private static String fileName = null;

    public static void main(String[] args) {
        dataFile(checkUserInput(getUserInput()));
    }

    public static String[] getUserInput() {
        System.out.println("Input your data. Use the following formats:");
        System.out.println("1. 'Last name, First name, Midle name'");
        System.out.println("2.  Date of birth:'dd.mm.yyyy'");
        System.out.println("3.  Phone:'111111111111'");
        System.out.println("4.  Sex:'f or m'");
        System.out.println("Use whitespace for separation:");
        String[] userInputArray = null;
        boolean status = true;
        while (status) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
                String userInput = bufferedReader.readLine();
                if (userInput.isBlank()) {
                    throw new IllegalArgumentException(
                            "Wrong input format! You entered empty or blank string.Try again!");
                }
                userInputArray = userInput.split(" ");
            } catch (IOException ex) {
                System.out.println("There are some IO problems.");
                ex.printStackTrace();
            }
            if (userInputArray.length != 4) {
                throw new IllegalArgumentException(
                        "Wrong input format! You entered wrong numbers of the parameters. Try again!");
            } else {
                status = false;
            }

        }
        return userInputArray;
    }

    public static HashMap<String, String> checkUserInput(String[] arrayStr) {// проводим грубую оценку введенных данных,
                                                                             // далее углубленнцю и формрует мапу как
                                                                             // результат для вывода в файл
        int sexLen = 1;
        int dateLen = 10;
        HashMap<String, String> resultData = new HashMap<String, String>();
        for (int i = 0; i < arrayStr.length; i++) {
            if (arrayStr[i].length() == sexLen
                    && (arrayStr[i].equalsIgnoreCase("f") || arrayStr[i].equalsIgnoreCase("m"))) {
                resultData.put("Sex", arrayStr[i]);
            } else if (arrayStr[i].length() == dateLen && arrayStr[i].indexOf(".") == 2) {
                if (checkDate(arrayStr[i])) {
                    resultData.put("Date of birth", arrayStr[i]);
                }
            } else if (arrayStr[i].contains(",")) {
                if (checkFullName(arrayStr[i])) {
                    resultData.put("Full name", arrayStr[i]);
                }
            } else if (!arrayStr[i].contains(".") && !arrayStr[i].contains(",")) {
                if (checkPhone(arrayStr[i])) {
                    resultData.put("Phone number", arrayStr[i]);
                }
            } else {
                throw new IllegalArgumentException("Wrong input format. Check your input");
            }
        }
        return resultData;
    }

    public static boolean checkDate(String dateStr) {
        boolean result = false;
        String[] tempArray = dateStr.split("\\.");
        if (tempArray.length != 3) {
            throw new IllegalArgumentException(
                    "Wrong DATE OF BIRTH format. You should use 'dd.mm.yyyy' format. Check please " + dateStr);
        } else {
            if (Integer.parseInt(tempArray[2]) > 1900 && Integer.parseInt(tempArray[2]) < 2100) {// проверяем диапазон
                                                                                                 // годов
                if (Integer.parseInt(tempArray[1]) > 0 && Integer.parseInt(tempArray[1]) < 13) {// проверяем диапазон
                                                                                                // месяцев
                    if (Integer.parseInt(tempArray[1]) == 4 || Integer.parseInt(tempArray[1]) == 6
                            || Integer.parseInt(tempArray[1]) == 9 || Integer.parseInt(tempArray[1]) == 11) {
                        if (Integer.parseInt(tempArray[0]) < 1 || Integer.parseInt(tempArray[0]) > 30) {
                            throw new IllegalArgumentException(
                                    "Wrong DAY format. Check " + tempArray[0] + " ,should be in range [1..30].");
                        }
                    } else if (Integer.parseInt(tempArray[1]) == 2) {
                        if (Integer.parseInt(tempArray[0]) < 1 || Integer.parseInt(tempArray[0]) > 28) {
                            throw new IllegalArgumentException(
                                    "Wrong DAY format. Check " + tempArray[0] + " ,should be in range [1..28].");
                        }
                    } else {
                        if (Integer.parseInt(tempArray[0]) < 1 || Integer.parseInt(tempArray[0]) > 31) {
                            throw new IllegalArgumentException(
                                    "Wrong DAY format. Check " + tempArray[0] + " ,should be in range [1..31].");
                        }
                    }
                    result = true;
                } else {
                    throw new IllegalArgumentException(
                            "Wrong MONTH format. Check " + tempArray[1] + " ,should be in range [1..12].");
                }
            } else {
                throw new IllegalArgumentException(
                        "Wrong YEAR format. Check " + tempArray[2] + " ,should be in range [1900..2100].");
            }
        }
        return result;
    }

    public static boolean checkFullName(String nameStr) {
        boolean result = false;
        String pattern = "\\D+";
        String[] tempArray = nameStr.split(",");
        fileName = tempArray[0] + ".txt";// формируем название файла на основе фамилии
        if (tempArray.length != 3) {
            throw new IllegalArgumentException(
                    "Wrong FULL NAME format. You should use 'Last name, First name, Midle name' format. Check please "
                            + nameStr);
        } else {
            for (int i = 0; i < tempArray.length; i++) {// проверяем, что в ФИО нет цифр.
                if (!Pattern.matches(pattern, tempArray[i])) {// если строка содержит число,то ошибка
                    throw new IllegalArgumentException(
                            "Wrong '" + tempArray[i] + "' format. The FULL NAME cannot contain digits.");
                }
            }
            result = true;
        }
        return result;
    }

    public static boolean checkPhone(String phoneStr) {
        boolean result = false;
        try {
            Double.parseDouble(phoneStr);
            result = true;
        } catch (NumberFormatException exception) {
            System.out.println("Wrong PHONE NUMBER format. Only number is premised. Check please " + phoneStr);
        }
        return result;
    }

    public static void dataFile(HashMap<String, String> data) {
        String currPathName = System.getProperty("user.dir");
        File currUserFile = new File(currPathName, fileName);
        try (FileWriter userData = new FileWriter(currUserFile, true);) {
            for (HashMap.Entry<String, String> item : data.entrySet()) {
                userData.write(item.getKey() + " - ");
                userData.write(item.getValue() + ";" + "\n");
            }
            System.out.println(
                    "File '" + fileName + "' was created successfully " + " in the folder '" + currPathName + "'.");
        } catch (IOException e) {
            System.out.println("Cannot create file " + fileName + ".");
            e.printStackTrace();
        }

    }

}