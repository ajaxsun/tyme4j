package com.tyme.solar;

import com.tyme.AbstractTyme;

import java.util.ArrayList;
import java.util.List;

/**
 * 公历季度
 *
 * @author 6tail
 */
public class SolarSeason extends AbstractTyme {

  public static final String[] NAMES = {"一季度", "二季度", "三季度", "四季度"};

  /**
   * 年
   */
  protected SolarYear year;

  /**
   * 索引，0-3
   */
  protected int index;

  /**
   * 初始化
   *
   * @param year  年
   * @param index 索引，0-3
   */
  protected SolarSeason(int year, int index) {
    this.year = SolarYear.fromYear(year);
    if (index < 0 || index > 3) {
      throw new IllegalArgumentException(String.format("illegal solar season index: %d", index));
    }
    this.index = index;
  }

  public static SolarSeason fromIndex(int year, int index) {
    return new SolarSeason(year, index);
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
   * @return 索引，0-3
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

  public SolarSeason next(int n) {
    int m = index + n;
    return fromIndex(year.getYear() + m / 4, Math.abs(m % 4));
  }

  /**
   * 月份列表
   *
   * @return 月份列表，1季度有3个月。
   */
  public List<SolarMonth> getMonths() {
    List<SolarMonth> l = new ArrayList<>(3);
    int y = year.getYear();
    for (int i = 0; i < 3; i++) {
      l.add(SolarMonth.fromYm(y, index * 3 + i + 1));
    }
    return l;
  }

}
