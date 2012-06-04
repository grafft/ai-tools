package ru.isa.ai.linguistic.utils;

import org.jawin.DispatchPtr;

public class FormatUtils {
   public static final long MIL_IN_DAY = 24 * 60 * 60 * 1000;
   public static final long MIL_IN_HOUR = 60 * 60 * 1000;
   public static final long MIL_IN_MIN = 60 * 1000;
   public static final long MIL_IN_SEC = 1000;

   public static int getIdentifier(DispatchPtr dis) {
      if (dis == null) {
         return 0;
      }
      return Math.max(dis.getPeer(), dis.getUnknown());
   }

   public static String getFormattedIntervalFromCurrent(long fromMillis) {
      long fromDate = System.currentTimeMillis() - fromMillis;
      StringBuilder builder = new StringBuilder();
      long day = fromDate / MIL_IN_DAY;
      long hour = (fromDate - day * MIL_IN_DAY) / MIL_IN_HOUR;
      long min = (fromDate - day * MIL_IN_DAY - hour * MIL_IN_HOUR) / MIL_IN_MIN;
      long sec = (fromDate - day * MIL_IN_DAY - hour * MIL_IN_HOUR - min * MIL_IN_MIN) / MIL_IN_SEC;
      long mil = (fromDate - day * MIL_IN_DAY - hour * MIL_IN_HOUR - min * MIL_IN_MIN - sec * MIL_IN_SEC);
      builder.append(day).append(" day ").append(hour).append(" hour ")
              .append(min).append(" min ").append(sec).append(".")
              .append(mil).append(" sec");
      return builder.toString();
   }
}
