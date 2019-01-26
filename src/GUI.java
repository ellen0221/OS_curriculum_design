import javax.swing.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/*
    题目: 多道批处理系统的两级调度-2

    测试数据:
    作业      到达时间   估计运行时间        内存需要    磁带机需要
    JOB1      10：00      25分钟            15K         2台
    JOB2      10：20      30分钟            60K         1台
    JOB3      10：30      10分钟            50K         3台
    JOB4      10：35      20分钟            10K         2台
    JOB5      10：40      15分钟            30K         2台

    显示被选中作业、内存空闲区和磁带机的情况。比较不同算法作业的选中次序及作业平均周转时间。
 */

public class GUI extends JFrame {
    int memory = 100;
    int disk = 4;       // 磁盘数量
    String nowtime;   // 当前时间
    private List<Point> points = new LinkedList<>();   // 空闲内存分区
    private List<Process> process = new LinkedList<>();     // 输入井
    private List<Process> prepared = new LinkedList<>();    // 就绪队列
    private List<Process> runed = new LinkedList<>();         // 运行队列
    private List<Process> finished = new LinkedList<>();    // 运行完成队列
    private JLabel time;
    public DateFormat df = new SimpleDateFormat("HH:mm");  // 时间格式

    public static NumberFormat nbf=NumberFormat.getInstance();

    public static void main(String[] args) throws ParseException, InterruptedException {
        new GUI();

    }

    private JPanel gui;
    private JList prepare;
    private JList finish;
    private JList run;
    private JList disksize;
    private JList memorysize;
    private JLabel avg_t;
    private JLabel t_a;
    private JLabel t_b;
    private JLabel t_c;
    private JLabel t_d;
    private JLabel t_e;
    private JLabel ftime_a;
    private JLabel ftime_b;
    private JLabel ftime_c;
    private JLabel ftime_d;
    private JLabel ftime_e;
    private JLabel stime_a;
    private JLabel stime_b;
    private JLabel stime_c;
    private JLabel stime_d;
    private JLabel stime_e;
    private JLabel rt_a;
    private JLabel rt_b;
    private JLabel rt_c;
    private JLabel rt_d;
    private JLabel rt_e;


    public GUI() throws ParseException, InterruptedException {
        super("多道批处理系统的两级调度-2 ");
        this.add(gui);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        time.setText(nowtime);

        points.add(new Point(0, memory));

        // 初始化各个作业
        Process a = new Process("JOB1","10:00",25,25,15,2);
        Process b = new Process("JOB2","10:20",30,30,60,1);
        Process c = new Process("JOB3","10:30",10,10,50,3);
        Process d = new Process("JOB4","10:35",20,20,10,2);
        Process e = new Process("JOB5","10:40",15,15,30,2);

        // 将各作业加入输入井
        process.add(a);
        process.add(b);
        process.add(c);
        process.add(d);
        process.add(e);

        // 刷新界面
        refreshprepare(prepared);
        refreshfinish(finished);
        refreshruned(runed);
        refreshmemory(points);
        refreshdisk(disk);

        nbf.setMinimumFractionDigits(2);

        List<Process> f = Work();
        // 输出最终调度结果
        for (int i=0; i<f.size(); i++) {
            Process p = f.get(i);
            switch (p.getName()) {
                case "JOB1":
                    rt_a.setText(p.getRtime());
                    stime_a.setText(p.getStime());
                    ftime_a.setText(p.getFtime());
                    t_a.setText(Integer.toString(p.getT()));
                    break;
                case "JOB2":
                    rt_b.setText(p.getRtime());
                    stime_b.setText(p.getStime());
                    ftime_b.setText(p.getFtime());
                    t_b.setText(Integer.toString(p.getT()));
                    break;
                case "JOB3":
                    rt_c.setText(p.getRtime());
                    stime_c.setText(p.getStime());
                    ftime_c.setText(p.getFtime());
                    t_c.setText(Integer.toString(p.getT()));
                    break;
                case "JOB4":
                    rt_d.setText(p.getRtime());
                    stime_d.setText(p.getStime());
                    ftime_d.setText(p.getFtime());
                    t_d.setText(Integer.toString(p.getT()));
                    break;
                case "JOB5":
                    rt_e.setText(p.getRtime());
                    stime_e.setText(p.getStime());
                    ftime_e.setText(p.getFtime());
                    t_e.setText(Integer.toString(p.getT()));
                    break;
            }
        }
        // 计算平均周转时间
        avg_t.setText(nbf.format(AVG_T(f)));
    }

    // 作业、进程调度
    public List<Process> Work() throws ParseException, InterruptedException {
        // 按到达顺序排序
        sortByRtime1(0, process.size()-1);

        // 设置当前时间
        nowtime = "9:55";
        while (!nowtime.equals(process.get(process.size()-1).getRtime())) {
            Thread.sleep(500);
            // 更新当前时间，每次增加1分钟
            Date dd = df.parse(nowtime);
            Date d = new Date(dd.getTime() + 60000);
            nowtime = df.format(d);
            time.setText(nowtime);
        }

        // 按作业到达时间排序(先到的在队列尾部)  先来先服务
        sortByRtime1(0, process.size()-1);
        while (process.size() > 0 || runed.size()>0) {
            // 从输入井中选出作业加入就绪队列  先来先服务
            for (int i = process.size()-1; i>=0; i--) {
                if (comparetime(process.get(i).getRtime(),nowtime)>1) {   // 到达时间<=当前时间
                    Process p = process.get(i);
                    // 有足够的磁盘机
                    if (p.getDisk()<=disk) {  // 系统现有的磁盘机允许分配
                        sortByAddress(0, points.size()-1);  // 首次适应
//                        sortBySize(0, points.size()-1);     // 最佳适应
//                        Collections.reverse(points);      // 最差适应
                        // 遍历空闲分区列表，进行内存和磁盘机分配
                        for (int x=0; x<points.size(); x++) {
                            if (p.getSize()<=points.get(x).getSize()) { // 系统现有的内存允许分配，则进行资源分配
                                p.setFirstpoint(points.get(x).getHead());
                                p.setStime(nowtime);
                                points.get(x).setHead(p.getFirstpoint()+p.getSize());
                                points.get(x).setSize(points.get(x).getSize()-p.getSize());
                                disk -= p.getDisk();
                                prepared.add(p);
                                process.remove(i);
                                refreshprepare(prepared);
                                refreshmemory(points);
                                refreshdisk(disk);
                                break;
                            }
                        }
                    }
                } else {
                    break;
                }
            }

            // 从就绪队列中选出作业加入运行队列 抢占式短作业优先
            if (prepared.size()>0 && runed.size()==0) { // 就绪队列不空且运行队列为空
                // 将就绪队列按服务时间排序
                sortByServertime(0, prepared.size()-1);

                runed.add(prepared.get(prepared.size()-1));
                prepared.remove(prepared.size()-1);
                refreshprepare(prepared);
                refreshruned(runed);
            } else if (prepared.size()>0 && runed.size()==1) {
                // 比较就绪队列中作业的服务时间是否比正在运行的作业的剩余服务时间短，是则抢占，否则等待
                if (prepared.get(prepared.size()-1).getNtime()<runed.get(0).getRemainServiceTime()) {
                    // 抢占后，被抢占的作业重新加入就绪列表
                    Process p = runed.get(0);
                    runed.remove(0);
                    runed.add(prepared.get(prepared.size()-1));
                    prepared.remove(prepared.size()-1);
                    prepared.add(p);
                    refreshruned(runed);
                }
            }

            // 运行作业
            if (runed.size()==1) {
                Thread.sleep(300);
                // 更新当前时间，每次增加1分钟
                Date dd = df.parse(nowtime);
                Date d = new Date(dd.getTime() + 60000);
                nowtime = df.format(d);
                time.setText(nowtime);
                // 判断作业是否运行完成
                if ((runed.get(0).getRemainServiceTime()-1) == 0) {
                    runed.get(0).setFtime(nowtime);
                    // 回收内存和磁盘机
                    collectMemory();
                    refreshmemory(points);
                    disk = collectDisk();
                    refreshdisk(disk);
                    finished.add(runed.get(0));
                    runed.remove(0);
                    refreshruned(runed);
                    refreshfinish(finished);
                } else {
                    runed.get(0).setRemainServiceTime(runed.get(0).getRemainServiceTime()-1);
                }
            }
        }
        return finished;
    }

    // 回收内存
    public List<Point> collectMemory() {
        int start = runed.get(0).getFirstpoint();
        int end = start+runed.get(0).getSize();
        int size = runed.get(0).getSize();
        boolean iscollect = false;  // 标记是否回收
        sortByAddress(0, points.size()-1);
        for (int i=0; i<points.size(); i++) {
            // 合并空闲分区
            if (points.get(i).getHead() == end) {
                points.get(i).setHead(start);
                points.get(i).setSize(points.get(i).getSize()+size);
                iscollect = true;
                break;
            } else if ((points.get(i).getHead()+points.get(i).getSize()) == start) {
                points.get(i).setSize(points.get(i).getSize()+size);
                iscollect = true;
                break;
            }
        }
        if (!iscollect) {
            points.add(new Point(start, size));
        }
        sortByAddress(0, points.size() - 1);
        Collections.reverse(points);
        for (int i = points.size() - 1; i > 0; i--) {
            if ((points.get(i).getHead() + points.get(i).getSize()) == points.get(i - 1).getHead()) {
                points.get(i - 1).setSize(points.get(i).getSize() + points.get(i - 1).getSize());
                points.get(i - 1).setHead(points.get(i).getHead());
                points.remove(i);
            }
        }
        sortByAddress(0,points.size()-1);   // 首次适应算法
//        sortBySize(0, points.size()-1);     // 最佳适应算法
//        Collections.reverse(points);    // 最差适应算法
        return points;
    }

    // 回收磁盘
    public int collectDisk() {
        int d = runed.get(0).getDisk();
        return disk+d;
    }

    // 刷新就绪队列
    public void refreshprepare(List<Process> prepared) {
        prepare.removeAll();
        DefaultListModel dlm = new DefaultListModel();
        for(Process p : prepared) {
            dlm.addElement(p.getName());
        }
        prepare.setModel(dlm);
    }

    // 刷新运行队列
    public void refreshruned(List<Process> runed) {
        run.removeAll();
        DefaultListModel dlm = new DefaultListModel();
        for(Process p : runed) {
            dlm.addElement(p.getName());
        }
        run.setModel(dlm);
    }

    // 刷新完成队列
    public void refreshfinish(List<Process> finished) {
        finish.removeAll();
        DefaultListModel dlm = new DefaultListModel();
        for(Process p : finished) {
            dlm.addElement(p.getName());
        }
        finish.setModel(dlm);
    }

    // 刷新内存空闲分区
    public void refreshmemory(List<Point> points) {
        memorysize.removeAll();
        DefaultListModel dlm = new DefaultListModel();
        for(Point p : points) {
            dlm.addElement(p.getHead() + "~" + (p.getHead()+p.getSize()-1));
        }
        memorysize.setModel(dlm);
    }

    // 刷新磁盘情况
    public void refreshdisk(int disk) {
        disksize.removeAll();
        DefaultListModel dlm = new DefaultListModel();
        dlm.addElement("剩余磁带机数：" + disk);
        disksize.setModel(dlm);
    }

    // 比较两个时间t1，t2的大小，t1>=t2则返回true
    public int comparetime(String t1, String t2) throws ParseException {
        Date d1 = df.parse(t1);
        Date d2 = df.parse(t2);
        long diff = d1.getTime()-d2.getTime();
        int t = (int) (diff/(1000 * 60));
        if (t>0) {
            // t1>t2
            return 1;
        } else if (t==0) {
            // t1==t2
            return 2;
        } else {
            // t1<t2
            return 3;
        }
    }

    /*
        按到达时间排序（后到的在前）
        @parameter low
        @parameter high
     */
    public void sortByRtime1(int low, int high) throws ParseException {
        int start = low;
        int end = high;
        String key = process.get(start).getRtime();


        while (end > start) {
            // 从后往前 end <= key 则 end--
            while (end > start && comparetime(process.get(end).getRtime(),key)>1) {
                end--;
            }
            // 若end到达时间>=key，即end进程后到达，则交换end和start，将到达时间最大的进程放在队列的最前面，且此时key所对应的进程以后的进程到达时间都比其早
            if (comparetime(process.get(end).getRtime(),key)<3) {
                Process p = process.get(end);
                process.set(end, process.get(start));
                process.set(start, p);
            }

            // 从前往后
            while (end > start && comparetime(process.get(start).getRtime(),key)<3) {
                start++;
            }
            // 若在start进程和key所对应的进程间有比key更小的，则交换二者
            if (comparetime(process.get(start).getRtime(),key)>1) {
                Process p = process.get(end);
                process.set(end, process.get(start));
                process.set(start, p);
            }
        }

        // 递归排序
        if (low < start) {
            sortByRtime1(low, start - 1);
        }
        if (high > end) {
            sortByRtime1(end + 1, high);
        }
    }

    /*
       按服务时间长短排序(服务时间短的在后)
       @parameter low
       @parameter high
    */
    public void sortByServertime(int low, int high) {
        int start = low;
        int end = high;
        int servertime = prepared.get(start).getNtime();

        while (end > start) {

            while (end > start && prepared.get(end).getNtime() <= servertime) {
                end--;
            }
            // 若end进程的服务时间更短，则交换end和start
            if (prepared.get(end).getNtime() >= servertime) {
                Process p = prepared.get(end);
                prepared.set(end, prepared.get(start));
                prepared.set(start, p);
            }

            while (end > start && prepared.get(start).getNtime() >= servertime) {
                start++;
            }
            // 若start比servertime对应的进程的服务时间更长，则交换
            if (prepared.get(start).getNtime() <= servertime) {
                Process p = prepared.get(end);
                prepared.set(end, prepared.get(start));
                prepared.set(start, p);
            }
        }

        if (low < start) {
            sortByServertime(low, start - 1);
        }
        if (high > end) {
            sortByServertime(end + 1, high);
        }
    }

    /*
        根据分区始址排序(小的在前)   首次适应
        @parameter low
        @parameter high
     */
    public void sortByAddress(int low, int high) {
        int start = low;
        int end = high;
        int key = points.get(start).getHead();

        while (end > start) {

            // 从后往前
            while (end > start && points.get(end).getHead() >= key) {
                end--;
            }
            if (points.get(end).getHead() <= key) {
                Point p = points.get(end);
                points.set(end, points.get(start));
                points.set(start, p);
            }

            // 从前往后
            while (end > start && points.get(start).getHead() <= key) {
                start++;
            }
            if (points.get(start).getHead() >= key) {
                Point p = points.get(end);
                points.set(end, points.get(start));
                points.set(start, p);
            }
        }

        // 递归排序
        if (low < start) {
            sortByAddress(low, start - 1);
        }
        if (high > end) {
            sortByAddress(end + 1, high);
        }

    }

    /*
        按照空闲区大小排序（小的在前）    最佳适应
        @parameter low
        @parameter high
     */
    public void sortBySize(int low, int high) {
        int start = low;
        int end = high;
        int size = points.get(start).getSize();

        while (end > start) {

            while (end > start && points.get(end).getSize() >= size) {
                end--;
            }

            if (points.get(end).getSize() <= size) {
                Point p = points.get(end);
                points.set(end, points.get(start));
                points.set(start, p);
            }

            while (end > start && points.get(start).getSize() <= size) {
                start++;
            }

            if (points.get(start).getSize() >= size) {
                Point p = points.get(end);
                points.set(end, points.get(start));
                points.set(start, p);
            }
        }

        if (low < start) {
            sortBySize(low, start - 1);
        }
        if (high > end) {
            sortBySize(end + 1, high);
        }
    }

    /*
        计算一个作业列表的平均周转时间
        @parameter: listOfProcess
     */
    public double AVG_T(List<Process> listOfProcess) throws ParseException {
        double t = 0;
        for (int i = 0; i < listOfProcess.size(); i++) {
            t += listOfProcess.get(i).getT();
        }
        return t / listOfProcess.size();
    }

}
