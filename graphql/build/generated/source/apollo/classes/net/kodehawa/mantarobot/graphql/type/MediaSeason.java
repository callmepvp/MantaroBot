/*
 * Copyright (C) 2016-2020 David Alejandro Rubio Escares / Kodehawa
 *
 *  Mantaro is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Mantaro is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Mantaro.  If not, see http://www.gnu.org/licenses/
 *
 */

package net.kodehawa.mantarobot.graphql.type;

import java.lang.String;
import javax.annotation.Generated;

@Generated("Apollo GraphQL")
public enum MediaSeason {
  /**
   * Months December to February
   */
  WINTER("WINTER"),

  /**
   * Months March to May
   */
  SPRING("SPRING"),

  /**
   * Months June to August
   */
  SUMMER("SUMMER"),

  /**
   * Months September to November
   */
  FALL("FALL"),

  /**
   * Auto generated constant for unknown enum values
   */
  $UNKNOWN("$UNKNOWN");

  private final String rawValue;

  MediaSeason(String rawValue) {
    this.rawValue = rawValue;
  }

  public String rawValue() {
    return rawValue;
  }

  public static MediaSeason safeValueOf(String rawValue) {
    for (MediaSeason enumValue : values()) {
      if (enumValue.rawValue.equals(rawValue)) {
        return enumValue;
      }
    }
    return MediaSeason.$UNKNOWN;
  }
}
