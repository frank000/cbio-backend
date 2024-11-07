package com.cbio.app.service.utils;

public class ModelUtil {
    public static class Validators{
        public static boolean isValorParamentroVariavel(String valorParamentro){
            return valorParamentro.matches("\\#\\{([a-zA-Z0-9_.]+)\\}$");//valida se o valor informado se encotra no padrão #{}
        }
        public static String normalizeValorParamentroVariavel(String valorParamentro){
            return valorParamentro.trim()                        //retira do padrão variavel para string comum
                    .replace("#{", "")
                    .replace("}", "");
        }
    }
}
