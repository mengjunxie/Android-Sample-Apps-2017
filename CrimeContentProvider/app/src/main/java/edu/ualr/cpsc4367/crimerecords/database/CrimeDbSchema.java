package edu.ualr.cpsc4367.crimerecords.database;

public class CrimeDbSchema {
    public static final class CrimeTable {
        public static final String NAME = "crimes";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_UUID = "uuid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_SOLVED = "solved";
        public static final String COLUMN_SUSPECT = "suspect";
    }
}
