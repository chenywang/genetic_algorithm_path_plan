package domain;

import lombok.Data;

@Data
public class Order {

    private boolean picked;

    private Location start;

    private Location end;

    private TimeWindow fetchTimeWindow;

    private TimeWindow deliverTimeWindow;

    private long expectFetchTime;

    private long expectDeliverTime;

    private double supplierServiceTime;

    private double receiverServiceTime;

    private long cityId;

    private long supplierId;

    @Override
    public String toString() {
        return "Order{" +
                "picked=" + picked +
                ", start=" + start +
                ", end=" + end +
                ", expectFetchTime=" + expectFetchTime +
                ", expectDeliverTime=" + expectDeliverTime +
                ", supplierServiceTime=" + supplierServiceTime +
                ", receiverServiceTime=" + receiverServiceTime +
                ", cityId=" + cityId +
                ", supplierId=" + supplierId+
                '}';
    }

    public Order() {

    }

    public Order(Location start, Location end, boolean picked) {
        this.start = start;
        this.end = end;
        this.picked = picked;
        this.fetchTimeWindow = new TimeWindow();
        this.deliverTimeWindow = new TimeWindow();
    }

    public Order(Location start, Location end, boolean picked, long fetchEndTime
            , long deliverEndTime, double supplierServiceTime, double receiverServiceTime
            , long cityId, long supplierId) {
        this.start = start;
        this.end = end;
        this.picked = picked;
        this.fetchTimeWindow = new TimeWindow(fetchEndTime);
        this.deliverTimeWindow = new TimeWindow(deliverEndTime);
        this.supplierServiceTime = supplierServiceTime;
        this.receiverServiceTime = receiverServiceTime;
        this.cityId = cityId;
        this.supplierId = supplierId;
    }


    @Override
    public Order clone() {
        return new Order(start.clone(), end.clone(), picked
                , fetchTimeWindow.getEndTime(), deliverTimeWindow.getEndTime()
                , supplierServiceTime, receiverServiceTime, cityId, supplierId);
    }
}
