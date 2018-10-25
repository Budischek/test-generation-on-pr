package kr.ac.kaist.testonpr.logic;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.springframework.stereotype.Component;

@Component
public class UptimeLogicBean {
  RuntimeMXBean mxBean;

  public UptimeLogicBean() {
    mxBean = ManagementFactory.getRuntimeMXBean();
  }

  public long getUptime() {
    return mxBean.getUptime();
  }
}
