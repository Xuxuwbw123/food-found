package com.freshtrace.unified.common;

public class UserContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Integer> USER_TYPE = new ThreadLocal<>();

    public static void setUserId(Long id) { USER_ID.set(id); }
    public static Long getUserId() { return USER_ID.get(); }
    public static void setUserType(Integer type) { USER_TYPE.set(type); }
    public static Integer getUserType() { return USER_TYPE.get(); }
    public static void clear() { USER_ID.remove(); USER_TYPE.remove(); }
}
