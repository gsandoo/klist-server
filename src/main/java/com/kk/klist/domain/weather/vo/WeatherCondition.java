package com.kk.klist.domain.weather.vo;

public enum WeatherCondition {

    SUNNY,
    RAINY,
    SLEET,
    SNOWY,
    SHOWER,
    WINDY;

    private static final double WINDY_THRESHOLD_WSD = 8.0;

    /**
     * 기상청 강수형태(PTY) 코드를 도메인 조건으로 변환한다.
     * 강수가 없을 때는 풍속을 봐서 강풍 여부를 추가로 판단한다.
     */
    public static WeatherCondition fromPty(int pty, double windSpeed) {
        return switch (pty) {
            case 1, 5 -> RAINY;
            case 2, 6 -> SLEET;
            case 3, 7 -> SNOWY;
            case 4 -> SHOWER;
            default -> windSpeed >= WINDY_THRESHOLD_WSD ? WINDY : SUNNY;
        };
    }
}
