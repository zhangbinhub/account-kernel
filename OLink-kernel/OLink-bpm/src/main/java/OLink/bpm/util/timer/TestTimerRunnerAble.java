package OLink.bpm.util.timer;

import java.util.Date;

public class TestTimerRunnerAble extends TimeRunnerAble{
  public String text;
  public TestTimerRunnerAble(String text) {
    this.text = text;
  }
  public void run(){
  }

  public static void main(String[] args) {
    TimerRunner.registerTimerTask(new TestTimerRunnerAble("hello world!"), new Date(), 10000);
    TimerRunner.registerTimerTask(new TestTimerRunnerAble("hello Zhouty!"), new Date(), 20000);
  }
}
