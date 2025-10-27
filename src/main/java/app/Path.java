package app;

public class Path {

    public static class Web {
        public static final String INDEX = "/";
        public static final String LOGIN = "/login";
        public static final String LOGOUT = "/logout";
        public static final String REGISTER = "/register";
        public static final String ORDERS = "/orders";
        public static final String ORDER = "/order";
        public static final String BASKET = "/indkobskurv";
        public static final String REMOVE_BASKET = "/remove-basket/{id}";

        public static final String ADMIN_BEFORE = "/admin*";
        public static final String ADMIN = "/admin";
        public static final String ADMIN_ORDERS = "/admin/orders";
        public static final String ADMIN_SPEC_ORDERS = "/admin/user-orders/{id}";
        public static final String ADMIN_REMOVE_ORDER = "/admin/remove-order/{id}";
        public static final String ADMIN_UPDATE_BALANCE = "/admin/update-balance/{id}";

    }

    public static class Template {
        public static final String ERROR = "/error.html";
        public static final String INDEX = "/index.html";
        public static final String LOGIN = "/login.html";
        public static final String ORDERS = "/ordrer.html";
        public static final String BASKET = "/indkobskurv.html";
        public static final String ADMIN = "/admin.html";
        public static final String ADMIN_ORDERS = "/adminAlleOrdrer.html";
        public static final String ADMIN_SPEC_ORDERS = "/adminSpecifikBrugerOrdrer.html";

    }
}
