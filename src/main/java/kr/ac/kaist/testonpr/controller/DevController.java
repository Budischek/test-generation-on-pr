package kr.ac.kaist.testonpr.controller;

import org.junit.runner.JUnitCore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DevController {

  //Use only for development (e.g. call w/e you are currently working on)
  @RequestMapping("/dev")
  public String devController() {
    JUnitCore junit = new JUnitCore();
    return "";
  }
}
