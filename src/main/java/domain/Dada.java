package domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Dada {

    private Location current;

    private List<Order> orders;

    private long cityId;


    public Dada(Location current, List<Order> orders) {
        this.current = current;
        this.orders = orders;
    }

    public Dada(Location current, List<Order> orders, double velocity) {
        this.current = current;
        this.orders = orders;
    }

    @Override
    public Dada clone() {
        Location current = this.current.clone();
        List<Order> orders = new ArrayList<>(this.orders.size());
        for (Order o : this.orders) {
            orders.add(o.clone());
        }
        return new Dada(current, orders);
    }

    @Override
    public String toString() {
        return "Dada{" +
                "current=" + current +
                ",orders=" + orders +
                '}';
    }
}
