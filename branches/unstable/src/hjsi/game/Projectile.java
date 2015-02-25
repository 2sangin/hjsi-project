package hjsi.game;

import hjsi.common.Timer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * 투사체
 * 
 * @author Administrator
 *
 */
public class Projectile extends Unit implements Movable {
  /**
   * 타겟
   */
  Unit target;
  /**
   * 투사체 속도
   */
  private int moveSpeed;
  /**
   * 투사체 종류
   */
  private int type;
  /**
   * 타워의 공격 데미지
   */
  private int damage;

  private Timer timerMovement;

  private Vector2d vector;

  private static final int NORMAL = 1;
  private static final int SLOW = 2;
  private static final int SPLASH = 3;
  private static final int POISON = 4;
  private static final int ICED = 5;
  private static final int CHAIN = 6;

  public Projectile(float x, float y, int damage, Unit target, Bitmap face) {
    super(Unit.TYPE_ETC, 0, x, y, face);
    this.target = target;

    moveSpeed = 3;
    type = NORMAL;
    this.damage = damage;
    setHitRect();

    vector = new Vector2d();

    timerMovement = Timer.create("투사체", 10);
    timerMovement.start();
  }

  @Override
  public void draw(Canvas canvas, float screenRatio) {
    super.draw(canvas, screenRatio);
    Paint pnt = new Paint();
    pnt.setStyle(Paint.Style.STROKE);
    pnt.setColor(Color.RED);
    canvas.drawRect(new RectF(x * screenRatio, y * screenRatio, x * screenRatio + width, y
        * screenRatio + height), pnt);
  }

  @Override
  public void move() {
    if (timerMovement.isUsable()) {
      timerMovement.consumeTimer();

      /* 충돌검사 */
      if (target.hitRect.contains(hitRect)) {
        destroyed = true;
        ((Hittable) target).hit(damage);
      }

      /* 투사체에서 몹까지의 벡터 */
      vector.set(target.cntrX - x, target.cntrY - y);
      /* 벡터 정규화 */
      vector.nor();
      /* 투사체 이동속도 스칼라 곱 */
      vector.mul(moveSpeed);

      /* 유도 알고리즘 */
      if (target instanceof Mob) {
        Vector2d desired =
            new Vector2d(((Mob) target).vector.x + vector.x, ((Mob) target).vector.y + vector.y);

        x += desired.x;
        y += desired.y;
        cntrX += desired.x;
        cntrY += desired.y;
      } else if (target instanceof Statue) {
        x += vector.x;
        y += vector.y;
        cntrX += vector.x;
        cntrY += vector.y;
      }
      setHitRect();
    }
  }

  @Override
  public void action() {
    // TODO Auto-generated method stub

  }

  @Override
  public void touch() {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   * 
   * @see hjsi.game.Unit#unfreeze()
   */
  @Override
  public void unfreeze() {
    timerMovement.resume();
  }

  /*
   * (non-Javadoc)
   * 
   * @see hjsi.game.Unit#freeze()
   */
  @Override
  public void freeze() {
    timerMovement.pause();
  }
}
