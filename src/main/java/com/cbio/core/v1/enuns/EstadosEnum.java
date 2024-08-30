package com.cbio.core.v1.enuns;

public enum EstadosEnum {


    RONDONIA(11, "RO", "Rondônia", Regiao.NORTE),
    ACRE(12, "AC", "Acre", Regiao.NORTE),
    AMAZONAS(13, "AM", "Amazonas", Regiao.NORTE),
    RORAIMA(14, "RR", "Roraima", Regiao.NORTE),
    PARA(15, "PA", "Pará", Regiao.NORTE),
    AMAPA(16, "AP", "Amapá", Regiao.NORTE),
    TOCANTINS(17, "TO", "Tocantins", Regiao.NORTE),
    MARANHAO(21, "MA", "Maranhão", Regiao.NORDESTE),
    PIAUI(22, "PI", "Piauí", Regiao.NORDESTE),
    CEARA(23, "CE", "Ceará", Regiao.NORDESTE),
    RIO_GRANDE_DO_NORTE(24, "RN", "Rio Grande do Norte", Regiao.NORDESTE),
    PARAIBA(25, "PB", "Paraíba", Regiao.NORDESTE),
    PERNAMBUCO(26, "PE", "Pernambuco", Regiao.NORDESTE),
    ALAGOAS(27, "AL", "Alagoas", Regiao.NORDESTE),
    SERGIPE(28, "SE", "Sergipe", Regiao.NORDESTE),
    BAHIA(29, "BA", "Bahia", Regiao.NORDESTE),
    MINAS_GERAIS(31, "MG", "Minas Gerais", Regiao.SUDESTE),
    ESPIRITO_SANTO(32, "ES", "Espírito Santo", Regiao.SUDESTE),
    RIO_DE_JANEIRO(33, "RJ", "Rio de Janeiro", Regiao.SUDESTE),
    SAO_PAULO(35, "SP", "São Paulo", Regiao.SUDESTE),
    PARANA(41, "PR", "Paraná", Regiao.SUL),
    SANTA_CATARINA(42, "SC", "Santa Catarina", Regiao.SUL),
    RIO_GRANDE_DO_SUL(43, "RS", "Rio Grande do Sul", Regiao.SUL),
    MATO_GROSSO_DO_SUL(50, "MS", "Mato Grosso do Sul", Regiao.CENTRO_OESTE),
    MATO_GROSSO(51, "MT", "Mato Grosso", Regiao.CENTRO_OESTE),
    GOIAS(52, "GO", "Goiás", Regiao.CENTRO_OESTE),
    DISTRITO_FEDERAL(53, "DF", "Distrito Federal", Regiao.CENTRO_OESTE);

    private final int id;
    private final String sigla;
    private final String nome;
    private final Regiao regiao;

    EstadosEnum(int id, String sigla, String nome, Regiao regiao) {
        this.id = id;
        this.sigla = sigla;
        this.nome = nome;
        this.regiao = regiao;
    }

    public int getId() {
        return id;
    }

    public String getSigla() {
        return sigla;
    }

    public String getNome() {
        return nome;
    }

    public Regiao getRegiao() {
        return regiao;
    }

    public enum Regiao {
        NORTE(1, "N", "Norte"),
        NORDESTE(2, "NE", "Nordeste"),
        SUDESTE(3, "SE", "Sudeste"),
        SUL(4, "S", "Sul"),
        CENTRO_OESTE(5, "CO", "Centro-Oeste");

        private final int id;
        private final String sigla;
        private final String nome;

        Regiao(int id, String sigla, String nome) {
            this.id = id;
            this.sigla = sigla;
            this.nome = nome;
        }

        public int getId() {
            return id;
        }

        public String getSigla() {
            return sigla;
        }

        public String getNome() {
            return nome;
        }
    }
}