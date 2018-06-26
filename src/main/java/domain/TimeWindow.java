package domain;


import lombok.Data;

@Data
public class TimeWindow {

    private long startTime;

    private long endTime;

    public TimeWindow() {
        this.startTime = 0;
        this.endTime = Integer.MAX_VALUE;
    }

    public TimeWindow(long endTime) {
        this.startTime = 0;
        this.endTime = endTime;
    }

    public TimeWindow(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TimeWindow(TimeWindow tw) {
        this.startTime = tw.startTime;
        this.endTime = tw.endTime;
    }

    @Override
    public TimeWindow clone() {
        return new TimeWindow(startTime, endTime);
    }
}
