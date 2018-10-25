package kr.ac.kaist.testonpr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.kaist.testonpr.logic.UptimeLogicBean;

@RestController
public class UptimeController {

  @Autowired
  UptimeLogicBean uptimeLogicBean;

  @GetMapping("/uptime")
  public long getUptime() {
    return uptimeLogicBean.getUptime();
  }
}
