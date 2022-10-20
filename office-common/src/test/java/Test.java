import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Test {
    static String[] strings = {"1"};
    static Set<String> stringSet = new HashSet<String>() {
        {
            add("morning");
//            add("afternoon");
            add("1");
            add("2");
            add("3");
        }
    };


    public static void main(String[] args) {
        System.out.println(stringSet.containsAll(Arrays.asList(strings)));
    }
}
