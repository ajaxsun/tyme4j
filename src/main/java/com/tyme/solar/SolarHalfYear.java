package com.tyme.solar;

import com.tyme.AbstractTyme;

import java.util.ArrayList;
import java.util.List;

/**
 * 公历半年
 *
 * @author 6tail
 */
public class SolarHalfYear extends AbstractTyme {

  public static final String[] NAMES = {"上半年", "下半年"};

  /**
   * 年
   */
  protected SolarYear year;

  /**
   * 索引，0-1
   */
  protected int index;

  /**
   * 初始化
   *
   * @param year  年
   * @param index 索引，0-1
   */
  protected SolarHalfYear(int year, int index) {
    this.year = SolarYear.fromYear(year);
    if (index < 0 || index > 1) {
      throw new IllegalArgumentException(String.format("illegal solar half year index: %d", index));
    }
    this.index = index;
  }

  public static SolarHalfYear fromIndex(int year, int index) {
    return new SolarHalfYear(year, index);
  }

  /**
   * 年
   *
   * @return 年
   */
  public SolarYear getYear() {
    return year;
  }

  /**
   * 索引
   *
   * @return 索引，0-1
   */
  public int getIndex() {
    return index;
  }

  public String getName() {
    return NAMES[index];
  }

  @Override
  public String toString() {
    return year + getName();
  }

  public SolarHalfYear next(int n) {
    int m = index + n;
    return fromIndex(year.getYear() + m / 2, Math.abs(m % 2));
  }

  /**
   * 月份列表
   *
   * @return 月份列表，半年有6个月。
   */
  public List<SolarMonth> getMonths() {
    List<SolarMonth> l = new ArrayList<>(6);
    int y = year.getYear();
    for (int i = 0; i < 6; i++) {
      l.add(SolarMonth.fromYm(y, index * 6 + i + 1));
    }
    return l;
  }

  /**
   * 季度列表
   *
   * @return 季度列表，半年有2个季度。
   */
  public List<SolarSeason> getSeasons() {
    List<SolarSeason> l = new ArrayList<>(2);
    int y = year.getYear();
    for (int i = 0; i < 2; i++) {
      l.add(SolarSeason.fromIndex(y, index * 2 + i));
    }
    return l;
  }

}
