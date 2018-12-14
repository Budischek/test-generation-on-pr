package kr.ac.kaist.testonpr.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class Serializer {

  public static String serialize(Object o) {

    try {
      ByteArrayOutputStream bo = new ByteArrayOutputStream();
      ObjectOutputStream so = new ObjectOutputStream(bo);
      so.writeObject(o);
      so.flush();
      so.close();
      return Base64.getEncoder().encodeToString(bo.toByteArray());
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "";
  }

  public static <T> T deserialize(String serialized, T o) {
    try {
      byte b[] = Base64.getDecoder().decode(serialized);;
      ByteArrayInputStream bi = new ByteArrayInputStream(b);
      ObjectInputStream si = new ObjectInputStream(bi);
      return (T) si.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }
}
