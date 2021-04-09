package leemos.orion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import leemos.orion.remoting.RandomSelectStrategy;

public class RandomSelectStrategyTest {

    @Test
    public void testRandomSelect() {
        List<Conn> conns = new ArrayList<Conn>();
        for (int i = 0; i < 1000; i++) {
            conns.add(new Conn(i));
        }

        RandomSelectStrategy<Conn> selector = new RandomSelectStrategy<Conn>();
        Conn conn = selector.select(conns);
        assertTrue(conns.contains(conn));

        conns = new ArrayList<Conn>();
        for (int i = 0; i < 1; i++) {
            conns.add(new Conn(i));
        }
        selector = new RandomSelectStrategy<Conn>();
        conn = selector.select(conns);
        assertEquals(conns.get(0), conn);
    }
}


class Conn {
    private int id;

    public Conn(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}