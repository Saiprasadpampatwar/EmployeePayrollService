import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class HamcrestTest {
    @Test
    public void coreMathersTest() {
        Assert.assertThat(Long.valueOf(1), CoreMatchers.instanceOf(Long.class));
        Assert.assertThat(Long.valueOf(1), CoreMatchers.isA(Long.class));
    }

    @Test
    public void listMatchersTest() {
        List<Integer> list = Arrays.asList(5,2,4);
        MatcherAssert.assertThat(list, Matchers.hasSize(3));
        MatcherAssert.assertThat(list,Matchers.contains(5,2,4));
        MatcherAssert.assertThat(list,Matchers.containsInAnyOrder(2,4,5));
        MatcherAssert.assertThat(list,Matchers.everyItem(Matchers.greaterThan(1)));

    }

    @Test
    public void arrayMatchersTest() {
        Integer [] ints = new Integer[] {7,5,12,16};
        MatcherAssert.assertThat(ints,Matchers.arrayWithSize(4));
        MatcherAssert.assertThat(ints,Matchers.arrayContaining(7,5,12,16));
    }

    @Test
    public void objectMatchersTest() {
        ToDo toDo = new ToDo(1,"learnHamCrest","Important");
        ToDo toDo1 = new ToDo(1,"learnHamCrest","Important");
        MatcherAssert.assertThat(toDo,Matchers.hasProperty("summary"));
        MatcherAssert.assertThat(toDo,Matchers.samePropertyValuesAs(toDo1));
    }
}
