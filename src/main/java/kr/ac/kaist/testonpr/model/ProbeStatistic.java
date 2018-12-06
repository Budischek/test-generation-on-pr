package kr.ac.kaist.testonpr.model;

import java.io.Serializable;

public class ProbeStatistic implements Serializable {
  public boolean[][] activation;

  public ProbeStatistic() {
    this.activation = null;
  }

  public ProbeStatistic(boolean[][] activation) {
    this.activation = activation;
  }

  public String serialize() {
    StringBuilder sb = new StringBuilder();;
    sb.append(activation.length + ";" + activation[0].length + ";");

    for(int i = 0; i < activation.length; i++) {
      for(int j = 0; j < activation[0].length; j++) {
        if(activation[i][j]) {
          sb.append(1);
        } else {
          sb.append(0);
        }
      }
      sb.append(";");
    }

    return sb.toString();
  }

  public void deserialize(String serialized) {
    String[] parts = serialized.split(";");
    this.activation = new boolean[Integer.valueOf(parts[0])][Integer.valueOf(parts[1])];

    for(int i = 0; i < activation.length; i++) {
      char[] chars = parts[i + 2].toCharArray();
      for(int j = 0; j < chars.length; j++) {
        activation[i][j] = chars[j] == '1';
      }
    }
  }
}
