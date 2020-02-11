import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 主面板。
 *
 * @ClassName: MyPanel
 * @Description: 主面板
 * @author: Bruce Young
 * @date: 2020年02月02日 17:03
 */
public class MyPanel extends JPanel implements Runnable {

    /**
     *
     */
    private static final long serialVersionUID = -5865496544871400953L;

    public MyPanel() {
        super();
        this.setBackground(new Color(0x444444));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(new Color(0x00ff00));// 设置医院边界颜色
        // 绘制医院边界
        g.drawRect(Hospital.getInstance().getX(), Hospital.getInstance().getY(), Hospital.getInstance().getWidth(),
                Hospital.getInstance().getHeight());
        g.setFont(new Font("等线", Font.BOLD, 16));
        g.setColor(new Color(0x00ff00));
        g.drawString("医院", Hospital.getInstance().getX() + Hospital.getInstance().getWidth() / 2 - 16,
                Hospital.getInstance().getY() - 16);
        // 绘制代表人类的圆点
        List<Person> people = PersonPool.getInstance().getPersonList();
        if (people == null) {
            return;
        }
        for (Person person : people) {
            switch (person.getState()) {
            case NORMAL:
                // 健康人
                g.setColor(new Color(0xdddddd));
                break;

            case SHADOW:
                // 潜伏期感染者
                g.setColor(new Color(0xffee00));
                break;

            case CONFIRMED:
                // 确诊患者
                g.setColor(new Color(0xff0000));
                break;

            case FREEZE:
                // 已隔离者
                g.setColor(new Color(0x48FFFC));
                break;

            case DEATH:
                // 死亡患者
                g.setColor(new Color(0x000000));
                break;

            default:
                break;
            }
            person.update();// 对各种状态的市民进行不同的处理
            g.fillOval(person.getX(), person.getY(), 3, 3);

        }

        int captionStartOffsetX = 700 + Hospital.getInstance().getWidth() + 40;
        int captionStartOffsetY = 40;
        int captionSize = 24;

        // 显示数据信息
        g.setColor(Color.WHITE);
        g.setColor(new Color(0xffffff));
        g.drawString("世界时间（天）：" + (int) (worldTime / 10.0), captionStartOffsetX, captionStartOffsetY);
        g.drawString("城市总人数：" + Constants.POPULATION, captionStartOffsetX, captionStartOffsetY + captionSize);
        g.setColor(new Color(0xdddddd));
        g.drawString("健康者人数：" + PersonPool.getInstance().getPeopleSize(Person.State.NORMAL), captionStartOffsetX,
                captionStartOffsetY + 2 * captionSize);
        g.setColor(new Color(0xffee00));
        g.drawString("潜伏期人数：" + PersonPool.getInstance().getPeopleSize(Person.State.SHADOW), captionStartOffsetX,
                captionStartOffsetY + 3 * captionSize);
        g.setColor(new Color(0xff0000));
        g.drawString("发病者人数：" + PersonPool.getInstance().getPeopleSize(Person.State.CONFIRMED), captionStartOffsetX,
                captionStartOffsetY + 4 * captionSize);
        g.setColor(new Color(0x48FFFC));
        g.drawString("已隔离人数：" + PersonPool.getInstance().getPeopleSize(Person.State.FREEZE), captionStartOffsetX,
                captionStartOffsetY + 5 * captionSize);
        g.setColor(new Color(0x00a1ff));
        g.drawString("空余病床："
                + Math.max(Constants.BED_COUNT - PersonPool.getInstance().getPeopleSize(Person.State.FREEZE), 0),
                captionStartOffsetX, captionStartOffsetY + 6 * captionSize);

        g.setColor(new Color(0xE39476));
        // 暂定急需病床数量为 NEED = 确诊发病者数量 - 已隔离住院数量
        //
        int needBeds = PersonPool.getInstance().getPeopleSize(Person.State.CONFIRMED)
                - PersonPool.getInstance().getPeopleSize(Person.State.FREEZE);

        g.drawString("需要病床：" + (needBeds > 0 ? needBeds : 0), captionStartOffsetX,
                captionStartOffsetY + 7 * captionSize);
        g.setColor(new Color(0xccbbcc));
        g.drawString("死亡人数：" + PersonPool.getInstance().getPeopleSize(Person.State.DEATH), captionStartOffsetX,
                captionStartOffsetY + 8 * captionSize);
        g.setColor(new Color(0x00ff23));
        g.drawString("治愈人次：" + PersonPool.RECOVERED, captionStartOffsetX, captionStartOffsetY + 9 * captionSize);

    }

    public static int worldTime = 0;// 世界时间
    private JButton closeBtn = new JButton("点击退出");
    {
        closeBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }

        });
        this.add(closeBtn);
        closeBtn.setVisible(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    public Timer timer = new Timer();

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (PersonPool.getInstance().getPeopleSize(Person.State.SHADOW)
                    + PersonPool.getInstance().getPeopleSize(Person.State.CONFIRMED)
                    + PersonPool.getInstance().getPeopleSize(Person.State.FREEZE) == 0) {
                timer.cancel();
                closeBtn.setVisible(true);
            }
            MyPanel.this.repaint();
            worldTime++;
        }
    }

    @Override
    public void run() {
        timer.schedule(new MyTimerTask(), 0, 100);//启动世界计时器，时间开始流动
    }


}