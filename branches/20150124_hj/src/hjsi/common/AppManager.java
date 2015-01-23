package hjsi.common;

import hjsi.activity.Base;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * 애플리케이션의 시스템적인 부분을 총괄하는 싱글턴 클래스
 */
public class AppManager {
  private static String tag; // 로그 출력용 클래스 이름을 갖고 있음
  private static AppManager uniqueInstance; // 자신의 유일한 인스턴스를 가지고 있는다.

  private LinkedList<Base> arAct; // 실행 중인 액티비티 리스트
  private HashMap<String, Bitmap> arBitmap; // 게임에 로드된 비트맵 목록
  private volatile int logicFps;

  private AppManager() {
    AppManager.tag = getClass().getSimpleName();

    arAct = new LinkedList<Base>();
    arBitmap = new HashMap<String, Bitmap>();
  }

  /**
   * @return 유일한 인스턴스를 반환함
   */
  public static AppManager getInstance() {
    if (AppManager.uniqueInstance == null) {
      synchronized (AppManager.class) {
        if (AppManager.uniqueInstance == null) {
          AppManager.uniqueInstance = new AppManager();
        }
      }
    }

    return AppManager.uniqueInstance;
  }

  /**
   * 현재 함수명 리턴(1.4 이후에서만 사용가능)
   *
   * @author 2007.11.27(김준호)
   * @since 1.4
   */
  public static String getMethodName() {
    StackTraceElement[] elements = null;

    try {
      throw new Exception("getMethodName");
    } catch (Exception e) {
      elements = e.getStackTrace();
    }

    String methodName = ((StackTraceElement) elements[1]).getMethodName();
    return methodName;
  }

  /**
   * 간단하게 클래스 이름을 tag로 하고 메소드 이름을 메시지로 하는 로그를 출력한다. (메소드가 제대로 호출되는지 판단하려고)
   */
  public static void printSimpleLog() {
    StackTraceElement[] elements = null;

    try {
      throw new Exception("getMethodName");
    } catch (Exception e) {
      elements = e.getStackTrace();
    }

    // return "getMethodName invalid parameter value of depth: expected < " + elements.length +
    // ", current = " +
    // depth;

    String methodName = elements[1].getMethodName();
    String className = elements[1].getClassName();
    className = className.substring(className.lastIndexOf(".") + 1);

    Log.d(className, methodName);
  }

  public void addActivity(Base act) {
    if (act != null) {
      if (arAct.contains(act) == false) {
        arAct.addFirst(act);
      }
    }

    Log.d(AppManager.tag, AppManager.getMethodName() + "() " + act.toString());
  }

  public void removeActivity(Base act) {
    if (act != null) {
      arAct.remove(act);
    }

    Log.d(AppManager.tag, AppManager.getMethodName() + "() " + act.toString());
  }

  /**
   * 현재 실행 중(arAct에 들어있음)인 Activity들을 종료시킴.
   */
  public void quitApp() {
    for (Activity act : arAct) {
      act.finish();
      Log.d(AppManager.tag, act.toString() + ".finish();");
    }
  }

  /**
   * 리소스 관리 대상으로 비트맵을 추가함
   *
   * @param key
   * @param bitmap
   */
  public void addBitmap(String key, Bitmap bitmap) {
    String msg = AppManager.getMethodName() + "(";
    msg += "\"" + key + "\", " + bitmap.getClass().getCanonicalName() + ")";
    if (arBitmap.containsKey(key) == false) {
      arBitmap.put(key, bitmap);
      msg += " added.";
    } else {
      msg += " already exists.";
    }
    Log.d(AppManager.tag, msg);
  }

  public Bitmap getBitmap(String key) throws NullPointerException {
    if (arBitmap.containsKey(key) == false) {
      String msg = AppManager.getMethodName() + "(";
      msg += "\"" + key + "\") returned null";
      Log.d(AppManager.tag, msg);

      throw new NullPointerException("이 메소드는 반드시 제대로된 객체를 반환해야함.");
    }
    return arBitmap.get(key);
  }

  /**
   * 모든 리소스를 반환 (지금은 비트맵만)
   */
  public void allRecycle() {
    String msg = AppManager.getMethodName() + "()";

    Set<String> arKey = arBitmap.keySet();
    synchronized (arBitmap) {
      for (String key : arKey) {
        msg += " " + key + ",";
        arBitmap.get(key).recycle();
      }
    }
    arBitmap.clear();
    msg = msg.substring(0, msg.length() - 1);
    msg += " cleared";

    Log.d(AppManager.tag, msg);
  }
  

  public void recycleBitmap(String key)
  {
      String msg = AppManager.getMethodName() + "()";

      arBitmap.get(key).recycle();

      msg += key + " recycled";

      Log.d(tag, msg);
  }


  public int getLogicFps() {
    return logicFps;
  }

  public void setLogicFps(int fps) {
    logicFps = fps;
  }
}
