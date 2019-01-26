import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Process {  // 进程类

    private String name;    // 进程名
    private int size;       // 所需内存大小
    private int disk;       // 所需磁盘数量
    private int firstpoint; // 分配的内存起始地址
    private int ntime;      // 服务时间
    private String rtime;      // 到达时间
    private String stime;       // 进入内存时间
    private int remainServiceTime; //还需要服务的时间
    private String ftime;      // 完成时间
    private int T;          // 周转时间 = 到达时间 - 完成时间
    DateFormat df = new SimpleDateFormat("HH:mm");  // 时间格式

    public int getSize() {
        return size;
    }

    public int getDisk() {
        return disk;
    }

    public int getFirstpoint() {
        return firstpoint;
    }

    public void setFirstpoint(int firstpoint) {
        this.firstpoint = firstpoint;
    }

    public String getStime() {
        return stime;
    }

    public void setStime(String stime) {
        this.stime = stime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNtime() {
        return ntime;
    }

    public String getRtime() {
        return rtime;
    }

    public int getRemainServiceTime() {
        return remainServiceTime;
    }

    public void setRemainServiceTime(int remainServiceTime) {
        this.remainServiceTime = remainServiceTime;
    }

    public String getFtime() {
        return ftime;
    }

    public void setFtime(String ftime) {
        this.ftime = ftime;
    }

    public int getT() throws ParseException {
        Date finish = df.parse(ftime);
        Date arrive = df.parse(rtime);
        long diff = finish.getTime()-arrive.getTime();
        T = (int) (diff/(1000 * 60));
        return T;
    }

    public Process(String name, String rtime, int ntime, int remainServiceTime, int size, int disk) {
        this.name = name;
        this.rtime = rtime;
        this.ntime = ntime;
        this.size = size;
        this.disk = disk;
        this.remainServiceTime = remainServiceTime;
    }
}
