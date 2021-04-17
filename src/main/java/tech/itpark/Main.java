package tech.itpark;

import tech.itpark.http.annotation.RequestBody;
import tech.itpark.http.annotation.RequestHeader;
import tech.itpark.http.annotation.ResponseBody;
import tech.itpark.http.converter.JsonBodyConverter;
import tech.itpark.http.converter.XmlBodyConverter;
import tech.itpark.http.server.HandleMethodResolver;
import tech.itpark.http.model.Employee;
import tech.itpark.http.model.User;
import tech.itpark.http.server.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        final var server = new Server();
        // TODO: server.register("GET", "/api/users", handler);
        // TODO: method reference
        // 1. Class -> Static method
        // 2. Object.method -> Class::method
        // 3. ... ?
        // server.register((Object handler) -> HandleMethodResolver.handlerMethodResolver(handler));

        server.register(HandleMethodResolver::handlerMethodResolver);
        server.register(HandleMethodResolver::singlePublicMethodResolver);

//CONVERTERS+
        server.registerConverter(new JsonBodyConverter());
        server.registerConverter(new XmlBodyConverter());
//CONVERTERS-

//MODELS+

//Users+
        final var userMap = new HashMap<Integer, User>();

        server.registerHandlerForMethodAndPath("POST", "/api/users", new AdvancedHandlerUsersPOST() {
            @Override
            public void handle(@RequestHeader("Content-Type") String contentTypeHeader, @RequestBody User[] users) {
//                String[] contentTypes = contentTypeHeader.split("\\s*,\\s*",10);
                Arrays.stream(users).forEach(user -> userMap.put(user.getId(), user));
            }
        });

        server.registerHandlerForMethodAndPath("GET", "/api/users", new AdvancedHandlerUsersGET() {
            @Override
            @ResponseBody("xml")
            public User[] handle(@RequestHeader("Content-Type") String contentTypeHeader, @RequestHeader("IDS") String idsHeader) {
                String idsHeaderString = idsHeader.trim();
                if (idsHeaderString.isEmpty()){
                    return userMap.values().toArray(User[]::new);
                }
                String[] idsHeaderArray = idsHeaderString.split("\\s*,\\s*",100);
                int[] ids;
                try {
                    ids = Stream.of(idsHeaderArray).mapToInt(s -> Integer.parseInt(s)).filter(i -> userMap.containsKey(i)).toArray();
                }catch (NumberFormatException e){
                    return null;
                }
                List<User> users = new ArrayList<>();
                Arrays.stream(ids).forEach(i -> users.add(userMap.get(i)));
                return users.toArray(User[]::new);
            }
        });
//Users-

//Employees+
        final var employeeMap = new HashMap<Integer, Employee>();

        server.registerHandlerForMethodAndPath("POST", "/api/employees", new AdvancedHandlerEmployeesPOST() {
            @Override
            @ResponseBody
            public Employee[] handle(@RequestBody Employee[] employees) {
                Arrays.stream(employees).forEach(employee -> employeeMap.put(employee.getId(), employee));
                return employees;
            }
        });

        server.registerHandlerForMethodAndPath("GET", "/api/employees", new AdvancedHandlerEmployeesGET() {
            @Override
            @ResponseBody
            public Employee[] handle(@RequestHeader("IDS") String idsHeader) {
                String idsHeaderString = idsHeader.trim();
                if (idsHeaderString.isEmpty()){
                    return employeeMap.values().toArray(Employee[]::new);
                }
                String[] idsHeaderArray = idsHeaderString.split("\\s*,\\s*",100);
                int[] ids;
                try {
                    ids = Stream.of(idsHeaderArray).mapToInt(s -> Integer.parseInt(s)).filter(i -> employeeMap.containsKey(i)).toArray();
                }catch (NumberFormatException e){
                    return null;
                }
                List<Employee> employees = new ArrayList<>();
                Arrays.stream(ids).forEach(i -> employees.add(employeeMap.get(i)));
                return employees.toArray(Employee[]::new);
            }
        });
//Employees -

//MODELS -

        server.start(8888);//server.start(9999);
    }
}


abstract class AdvancedHandlerUsersPOST {
    public abstract void handle(String contentTypes, User[] users);
}

abstract class AdvancedHandlerUsersGET {
    public abstract User[] handle(String contentTypes, String ids);
}

abstract class AdvancedHandlerEmployeesPOST {
    public abstract Employee[] handle(Employee[] employees);
}

abstract class AdvancedHandlerEmployeesGET {
    public abstract Employee[] handle(String ids);
}
