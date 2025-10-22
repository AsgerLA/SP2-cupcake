package app;

public class Path {

    public static class Web {
        public static final String INDEX = "/";
        public static final String LOGIN = "/login";
        public static final String LOGOUT = "/logout";
        public static final String REGISTER = "/register";
        public static final String ORDERS = "/orders";
        public static final String ORDER = "/order";
        public static final String REMOVE_ORDER = "/remove-order";
        public static final String BASKET = "/indkobskurv";
        public static final String REMOVE_BASKET = "/remove-basket/{id}";
    }

    public static class Template {
        public static final String INDEX = "/index.html";
        public static final String LOGIN = "/login.html";
        public static final String BASKET = "/indkobskurv.html";
    }
}
