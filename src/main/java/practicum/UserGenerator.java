package practicum;
import com.github.javafaker.Faker;

public class UserGenerator {

    private static String getRandomName(){
        Faker faker = new Faker();
        String name = faker.name().lastName();
        return name;
    }

    private static String getRandomPass(){
        Faker faker = new Faker();
        String password = faker.animal().name();
        return password;
    }

    private static String getEmailFromName(String name){
        String email = name.toLowerCase() + "@bk.ru";
        return email;
    }



    public static User getRandomUser(){
        String name = getRandomName();
        String email = getEmailFromName(name);
        String password = getRandomPass();

        return new User(email, password,name);
    }


    public static User getUserWithoutEmail(){
        String name = getRandomName();
        String email = null;
        String password = getRandomPass();

        return new User(email, password, name);
    }

}
