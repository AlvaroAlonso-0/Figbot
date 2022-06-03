package figbot;

import controller.Controller;

public class App{

    private static Controller controller;

    public static Controller getController(){
        return controller;
    }

    public static void main(String[] args) {
        controller = new Controller();
    }
}
