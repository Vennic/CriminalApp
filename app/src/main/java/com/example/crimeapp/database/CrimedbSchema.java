package com.example.crimeapp.database;

public class CrimedbSchema {
    public static final class CrimeTable {
        public static final String NAME = "crimes";

        public static class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String REQ_POLICE = "req_police";
        }
    }


}
