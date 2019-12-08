package com.vietnamproject.accesscontrol.config;

public class Define {

    public class Error {

        public static final int NONE = 0;

        /** 서버 DB 저장 실패 */
        public static final int DB_INSERT = 1;

        /** 데이터 파싱 에러 */
        public static final int DATA_PARSE = 2;

        /** 잘못된 파라메터 */
        public static final int INVALID_PARAM = 3;

        /** 파라메터 없음 */
        public static final int EMPTY_PARAM = 4;

        /** 시스템 오류 */
        public static final int SYSTEM_ERROR = 5;

        /** 지원안함 */
        public static final int NOT_SUPPORTED = 6;

        /** 기기정보 없음 */
        public static final int EMPTY_DEVICE_INFO = 7;

        /** 등록안된 사용자(미가입 사용자) */
        public static final int EMPTY_USER_INFO = 8;

    }

    public class CMD {

        /** 카메라잠금 */
        public static final String CAMERA_LOCK = "camera_lock";

        /** 카메라잠금해제 */
        public static final String CAMERA_UNLOCK = "camera_unlock";

        /** 와이파이잠금 */
        public static final String WIFI_LOCK = "wifi_lock";

        /** 와이파이잠금해제 */
        public static final String WIFI_UNLOCK = "wifi_unlock";

        /** 블루투스잠금 */
        public static final String BLUETOOTH_LOCK = "bluetooth_lock";

        /** 블루투스잠금해제 */
        public static final String BLUETOOTH_UNLOCK = "bluetooth_unlock";

        /** 상태확인 */
        public static final String ALIVE = "alive";

        /** 작동중지 */
        public static final String STOP = "stop";

    }
}