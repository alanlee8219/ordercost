package com.example.jcs.orderassistant; /**
 * Created by JCS on 2015/9/30.
 */

import android.provider.BaseColumns;

public class DatabaseSchema {


    static final int DATABASE_VERSION = 1;

    public static abstract class OrderEntry implements BaseColumns {

        public static final String TABLE_NAME = "order_table";

        public static final String COLUMN_ORDERID = "order_id";

        public static final String COLUMN_DATE = "order_date";

        public static final String COLUMN_return = "cash_back";

    };

    public static abstract class SubOrderEntry implements BaseColumns {

        public static final String TABLE_NAME = "suborder";

        public static final String COLUMN_ORDERID = "suborder_id";

        public static final String COLUMN_SUBORDERID = "suborder_id";

        public static final String COLUMN_SUM = "sum_money";

        public static final String COLUMN_MEMBER = "member_id";
    };

    public static abstract class MemberEntry implements BaseColumns {

        public static final String TABLE_NAME = "member";

        public static final String COLUMN_ID = "member_id";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_MONEY = "money";
    };

}
