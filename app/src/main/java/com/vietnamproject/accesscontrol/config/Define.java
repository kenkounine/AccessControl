package com.vietnamproject.accesscontrol.config;

public class Define {

    public static final String TAG = "WHKIM";

    public static final String PREFIX = "/S0u";

    public static final String DEFAULT_PASSWORD = "0000";

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

        /** 미로그인 사용자 */
        public static final int NOT_LOGIN = 100;

        /** 권한 해제됨 */
        public static final int PERMISSION_DENIED = 101;

        /** 기기관리자 해제됨 */
        public static final int ADMIN_DISABLED = 102;

    }

    public class CMD {

        public static final String COMMAND = "cmd";

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

    public class CMD_CODE {

        public static final int NONE = 0;

        /** 카메라잠금 */
        public static final int CAMERA_LOCK = 1;

        /** 카메라잠금해제 */
        public static final int CAMERA_UNLOCK = 2;

        /** 와이파이잠금 */
        public static final int WIFI_LOCK = 3;

        /** 와이파이잠금해제 */
        public static final int WIFI_UNLOCK = 4;

        /** 블루투스잠금 */
        public static final int BLUETOOTH_LOCK = 5;

        /** 블루투스잠금해제 */
        public static final int BLUETOOTH_UNLOCK = 6;

        /** 상태확인 */
        public static final int ALIVE = 7;

        /** 작동중지 */
        public static final int STOP = 8;

    }

    public class SharedKey {

        public static final String USER_ID = "user_id";

        public static final String LOCATION = "location";

        public static final String LAST_CMD = "last_cmd";

    }
}