package com.budwhite.studying.framework.web.model.message;

public class Message {
    private Message() {}
    public static class Error {
        private Error() {}
        public static final String GENERIC_INTERNAL_ERROR_MESSAGE = "Errore interno nel processamento della richiesta";
        public static final String GENERIC_BAD_CLIENT_DATA_ERROR_MESSAGE = "Dati mancanti o errati nella richiesta";
        public static final String GENERIC_INCONSISTENT_CLIENT_DATA_ERROR_MESSAGE = "Dati non consistenti nella richiesta";
        public static final String GENERIC_MISSING_REQUEST_PARAMETERS_ERROR_MESSAGE = "Parametri mancanti nella richiesta";
        public static final String GENERIC_MISSING_DATA_ERROR_MESSAGE = "Dati mancanti o nessun dato trovato";
        public static final String API_REQUEST_BODY_VALIDATION_ERROR_MESSAGE = "invalid request body";
    }

    public static class Info {
        private Info() {}
        public static final String OK = "Ok";
        public static final String GENERIC_CONFIRMATION_MESSAGE = "Operazione eseguita correttamente";
    }
}
