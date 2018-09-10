package util;

import javax.security.auth.callback.*;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;

public class PrototypUtil {


    public static final CallbackHandler CALLBACK_HANDLER_SERVER = callbacks -> {
        for (Callback callback : callbacks) {
            if (callback instanceof TextOutputCallback) {
                TextOutputCallback outputCallback = (TextOutputCallback) callback;
                System.out.println(outputCallback.getMessage());
            } else if (callback instanceof PasswordCallback) {
                PasswordCallback passwordCallback = (PasswordCallback) callback;
                passwordCallback.setPassword("secret".toCharArray());
            } else if (callback instanceof NameCallback) {
                ((NameCallback) callback).setName("client");
            }else if(callback instanceof RealmCallback){
                RealmCallback realmCallback = (RealmCallback) callback;
                realmCallback.setText("127.0.0.1");
            }else if(callback instanceof AuthorizeCallback){
                AuthorizeCallback authorizeCallback = (AuthorizeCallback) callback;
                authorizeCallback.setAuthorized(true);

            }
        }
    };
    public static final CallbackHandler CALLBACK_HANDLER_CLIENT = callbacks -> {
        for (Callback callback : callbacks) {
            if (callback instanceof TextOutputCallback) {
                TextOutputCallback outputCallback = (TextOutputCallback) callback;
                System.out.println(outputCallback.getMessage());
            } else if (callback instanceof PasswordCallback) {
                PasswordCallback passwordCallback = (PasswordCallback) callback;
                passwordCallback.setPassword("secret".toCharArray());
            } else if (callback instanceof NameCallback) {
                ((NameCallback) callback).setName("client");
            }else if(callback instanceof RealmCallback){
                RealmCallback realmCallback = (RealmCallback) callback;
                realmCallback.setText("127.0.0.1");
            }
        }
    };

}
