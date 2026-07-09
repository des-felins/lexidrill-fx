package dev.cyberjar.springfx.config;

public enum FxmlView {


    LOGIN {

        @Override
        public String getFxmlPath() {
            return "/fxml/login.fxml";
        }
    },

    ADD_WORDS {

        @Override
        public String getFxmlPath() {
            return "/fxml/add-words.fxml";
        }
    },

    PRACTICE_WORDS {
        @Override
        public String getFxmlPath() {
            return "/fxml/practice-words.fxml";
        }
    },

    HOME {

        @Override
        public String getFxmlPath() {
            return "/fxml/home.fxml";
        }
    };

    public abstract String getFxmlPath();
}
