package com.tyme.solar;

import com.tyme.AbstractTyme;
import com.tyme.culture.Constellation;
import com.tyme.culture.Week;
import com.tyme.culture.dog.Dog;
import com.tyme.culture.dog.DogDay;
import com.tyme.culture.nine.Nine;
import com.tyme.culture.nine.NineDay;
import com.tyme.culture.phenology.Phenology;
import com.tyme.culture.phenology.PhenologyDay;
import com.tyme.festival.SolarFestival;
import com.tyme.holiday.LegalHoliday;
import com.tyme.jd.JulianDay;
import com.tyme.lunar.LunarDay;
import com.tyme.lunar.LunarMonth;

/**
 * 公历日
 *
 * @author 6tail
 */
public class SolarDay extends AbstractTyme {

  public static final String[] NAMES = {"1日", "2日", "3日", "4日", "5日", "6日", "7日", "8日", "9日", "10日", "11日", "12日", "13日", "14日", "15日", "16日", "17日", "18日", "19日", "20日", "21日", "22日", "23日", "24日", "25日", "26日", "27日", "28日", "29日", "30日", "31日"};

  /**
   * 公历月
   */
  protected SolarMonth month;

  /**
   * 日
   */
  protected int day;

  /**
   * 初始化
   *
   * @param year  年
   * @param month 月
   * @param day   日
   */
  protected SolarDay(int year, int month, int day) {
    this.month = SolarMonth.fromYm(year, month);
    if (day < 1) {
      throw new IllegalArgumentException(String.format("illegal solar day: %d-%d-%d", year, month, day));
    }
    if (1582 == year && 10 == month) {
      if (day > 4 && day < 15) {
        throw new IllegalArgumentException(String.format("illegal solar day: %d-%d-%d", year, month, day));
      } else if (day > 31) {
        throw new IllegalArgumentException(String.format("illegal solar day: %d-%d-%d", year, month, day));
      }
    } else if (day > SolarMonth.fromYm(year, month).getDayCount()) {
      throw new IllegalArgumentException(String.format("illegal solar day: %d-%d-%d", year, month, day));
    }
    this.day = day;
  }

  public static SolarDay fromYmd(int year, int month, int day) {
    return new SolarDay(year, month, day);
  }

  /**
   * 月
   *
   * @return 月
   */
  public SolarMonth getMonth() {
    return month;
  }

  /**
   * 日
   *
   * @return 日
   */
  public int getDay() {
    return day;
  }

  /**
   * 星期
   *
   * @return 星期
   */
  public Week getWeek() {
    return getJulianDay().getWeek();
  }

  /**
   * 星座
   *
   * @return 星座
   */
  public Constellation getConstellation() {
    int index = 11;
    int y = month.getMonth() * 100 + day;
    if (y >= 321 && y <= 419) {
      index = 0;
    } else if (y >= 420 && y <= 520) {
      index = 1;
    } else if (y >= 521 && y <= 621) {
      index = 2;
    } else if (y >= 622 && y <= 722) {
      index = 3;
    } else if (y >= 723 && y <= 822) {
      index = 4;
    } else if (y >= 823 && y <= 922) {
      index = 5;
    } else if (y >= 923 && y <= 1023) {
      index = 6;
    } else if (y >= 1024 && y <= 1122) {
      index = 7;
    } else if (y >= 1123 && y <= 1221) {
      index = 8;
    } else if (y >= 1222 || y <= 119) {
      index = 9;
    } else if (y <= 218) {
      index = 10;
    }
    return Constellation.fromIndex(index);
  }

  public String getName() {
    return NAMES[day - 1];
  }

  @Override
  public String toString() {
    return month + getName();
  }

  public SolarDay next(int n) {
    return getJulianDay().next(n).getSolarDay();
  }

  /**
   * 是否在指定公历日之前
   *
   * @param target 公历日
   * @return true/false
   */
  public boolean isBefore(SolarDay target) {
    int aYear = month.getYear().getYear();
    SolarMonth targetMonth = target.getMonth();
    int bYear = targetMonth.getYear().getYear();
    if (aYear == bYear) {
      int aMonth = month.getMonth();
      int bMonth = targetMonth.getMonth();
      return aMonth == bMonth ? day < target.getDay() : aMonth < bMonth;
    }
    return aYear < bYear;
  }

  /**
   * 是否在指定公历日之后
   *
   * @param target 公历日
   * @return true/false
   */
  public boolean isAfter(SolarDay target) {
    int aYear = month.getYear().getYear();
    SolarMonth targetMonth = target.getMonth();
    int bYear = targetMonth.getYear().getYear();
    if (aYear == bYear) {
      int aMonth = month.getMonth();
      int bMonth = targetMonth.getMonth();
      return aMonth == bMonth ? day > target.getDay() : aMonth > bMonth;
    }
    return aYear > bYear;
  }

  /**
   * 节气
   *
   * @return 节气
   */
  public SolarTerm getTerm() {
    SolarTerm term = SolarTerm.fromIndex(month.getYear().getYear() + 1, 0);
    while (isBefore(term.getJulianDay().getSolarDay())) {
      term = term.next(-1);
    }
    return term;
  }

  /**
   * 七十二候
   *
   * @return 七十二候
   */
  public PhenologyDay getPhenologyDay() {
    SolarTerm term = getTerm();
    int dayIndex = subtract(term.getJulianDay().getSolarDay());
    int index = dayIndex / 5;
    if (index > 2) {
      index = 2;
    }
    dayIndex -= index * 5;
    return new PhenologyDay(Phenology.fromIndex(term.getIndex() * 3 + index), dayIndex);
  }

  /**
   * 三伏天
   *
   * @return 三伏天
   */
  public DogDay getDogDay() {
    SolarTerm xiaZhi = SolarTerm.fromIndex(month.getYear().getYear(), 12);
    // 第1个庚日
    SolarDay start = xiaZhi.getJulianDay().getSolarDay();
    int add = 6 - start.getLunarDay().getSixtyCycle().getHeavenStem().getIndex();
    if (add < 0) {
      add += 10;
    }
    // 第3个庚日，即初伏第1天
    add += 20;
    start = start.next(add);
    int days = subtract(start);
    // 初伏以前
    if (days < 0) {
      return null;
    }
    if (days < 10) {
      return new DogDay(Dog.fromIndex(0), days);
    }
    // 第4个庚日，中伏第1天
    start = start.next(10);
    days = subtract(start);
    if (days < 10) {
      return new DogDay(Dog.fromIndex(1), days);
    }
    // 第5个庚日，中伏第11天或末伏第1天
    start = start.next(10);
    days = subtract(start);
    // 立秋
    if (xiaZhi.next(3).getJulianDay().getSolarDay().isAfter(start)) {
      if (days < 10) {
        return new DogDay(Dog.fromIndex(1), days + 10);
      }
      start = start.next(10);
      days = subtract(start);
    }
    if (days < 10) {
      return new DogDay(Dog.fromIndex(2), days);
    }
    return null;
  }

  /**
   * 数九天
   *
   * @return 数九天
   */
  public NineDay getNineDay() {
    int year = month.getYear().getYear();
    SolarDay start = SolarTerm.fromIndex(year + 1, 0).getJulianDay().getSolarDay();
    if (isBefore(start)) {
      start = SolarTerm.fromIndex(year, 0).getJulianDay().getSolarDay();
    }
    SolarDay end = start.next(81);
    if (isBefore(start) || !isBefore(end)) {
      return null;
    }
    int days = subtract(start);
    return new NineDay(Nine.fromIndex(days / 9), days % 9);
  }

  /**
   * 位于当年的索引
   *
   * @return 索引
   */
  public int getIndexInYear() {
    int m = month.getMonth();
    int y = month.getYear().getYear();
    int days = 0;
    for (int i = 1; i < m; i++) {
      days += SolarMonth.fromYm(y, i).getDayCount();
    }
    int d = day;
    if (1582 == y && 10 == m) {
      if (d >= 15) {
        d -= 10;
      }
    }
    return days + d - 1;
  }

  /**
   * 公历日期相减，获得相差天数
   *
   * @param target 公历
   * @return 天数
   */
  public int subtract(SolarDay target) {
    return (int) (getJulianDay().getDay() - target.getJulianDay().getDay());
  }

  /**
   * 儒略日
   *
   * @return 儒略日
   */
  public JulianDay getJulianDay() {
    return JulianDay.fromYmdHms(month.getYear().getYear(), month.getMonth(), day, 0, 0, 0);
  }

  /**
   * 农历日
   *
   * @return 农历日
   */
  public LunarDay getLunarDay() {
    LunarMonth m = LunarMonth.fromYm(month.getYear().getYear(), month.getMonth()).next(-3);
    int days = subtract(m.getFirstJulianDay().getSolarDay());
    while (days >= m.getDayCount()) {
      m = m.next(1);
      days = subtract(m.getFirstJulianDay().getSolarDay());
    }
    return LunarDay.fromYmd(m.getYear().getYear(), m.getMonthWithLeap(), days + 1);
  }

  /**
   * 法定假日，如果当天不是法定假日，返回null
   *
   * @return 法定假日
   */
  public LegalHoliday getLegalHoliday() {
    SolarMonth m = getMonth();
    return LegalHoliday.fromYmd(m.getYear().getYear(), m.getMonth(), day);
  }

  /**
   * 公历现代节日，如果当天不是公历现代节日，返回null
   *
   * @return 公历现代节日
   */
  public SolarFestival getFestival() {
    SolarMonth m = getMonth();
    return SolarFestival.fromYmd(m.getYear().getYear(), m.getMonth(), day);
  }

}
