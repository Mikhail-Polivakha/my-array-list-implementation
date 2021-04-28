import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CustomArrayListTest {

    @Test
    @DisplayName("Testing size related during object construction")
    public void testSize() {
        CustomArrayList<Integer> customArrayList = new CustomArrayList<>();
        Assertions.assertEquals(0, customArrayList.size());

        customArrayList = new CustomArrayList<>(100);
        Assertions.assertEquals(0, customArrayList.size());

        customArrayList = new CustomArrayList<>(new Integer[]{1, 7, 0, 12, 55});
        Assertions.assertEquals(5, customArrayList.size());

        AtomicReference<CustomArrayList<String>> illegallyCreatedList = new AtomicReference<>();
        Assertions.assertThrows(IllegalArgumentException.class, () -> illegallyCreatedList.set(new CustomArrayList<>(-1)));
    }

    @Nested
    @DisplayName("'Mutations' related test")
    class AddTest {

        @Test
        public void add() {
            CustomArrayList<String> customArrayList = new CustomArrayList<>(new String[]{"ROAD", "TO", "SOFTWARE", "ARCHITECT"});
            Assertions.assertEquals(4, customArrayList.size());
            final String seniorSoftwareArchitect = "SENIOR SOFTWARE ARCHITECT";
            customArrayList.add(seniorSoftwareArchitect);
            Assertions.assertArrayEquals(
                    List.of("ROAD", "TO", "SOFTWARE", "ARCHITECT", "SENIOR SOFTWARE ARCHITECT").toArray(),
                    customArrayList.toArray()
            );
        }

        @Test
        void addOneToSpecificPosition() {
            CustomArrayList<DayOfWeek> customArrayList = new CustomArrayList<>(new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY, DayOfWeek.SUNDAY});
            Assertions.assertEquals(3, customArrayList.size());

            customArrayList.add(1, DayOfWeek.TUESDAY);

            Assertions.assertArrayEquals(
                    List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.FRIDAY, DayOfWeek.SUNDAY).toArray(),
                    customArrayList.toArray()
            );
        }

        @Test
        void addAll() {
            CustomArrayList<Charset> customArrayList = new CustomArrayList<>(new Charset[]{StandardCharsets.ISO_8859_1, StandardCharsets.US_ASCII});
            Assertions.assertEquals(2, customArrayList.size());
            customArrayList.addAll(Collections.emptyList());
            Assertions.assertEquals(2, customArrayList.size());
            customArrayList.addAll(Collections.singletonList(StandardCharsets.US_ASCII));
            Assertions.assertEquals(3, customArrayList.size());
            Assertions.assertEquals(StandardCharsets.US_ASCII, customArrayList.get(customArrayList.size() - 1));
            Assertions.assertEquals(StandardCharsets.US_ASCII, customArrayList.get(customArrayList.size() - 2));
            Assertions.assertEquals(StandardCharsets.ISO_8859_1, customArrayList.get(customArrayList.size() - 3));

            customArrayList.addAll(List.of(StandardCharsets.UTF_8, StandardCharsets.UTF_8, StandardCharsets.UTF_16BE));
            Assertions.assertEquals(6, customArrayList.size());
            Assertions.assertEquals(StandardCharsets.UTF_16BE, customArrayList.get(customArrayList.size() - 1));
            Assertions.assertEquals(StandardCharsets.UTF_8, customArrayList.get(customArrayList.size() - 2));
            Assertions.assertEquals(StandardCharsets.ISO_8859_1, customArrayList.get(0));
        }

        @Test
        void addAllFromSpecificIndex() {
            final CustomArrayList<Object> customArrayList = new CustomArrayList<>(new System.Logger.Level[]{
                    System.Logger.Level.ALL,
                    System.Logger.Level.DEBUG,
                    System.Logger.Level.TRACE
            });

            Assertions.assertEquals(3, customArrayList.size());
            customArrayList.addAll(0, List.of(System.Logger.Level.WARNING, System.Logger.Level.WARNING, System.Logger.Level.ALL));
            Assertions.assertEquals(6, customArrayList.size());
            Assertions.assertEquals(System.Logger.Level.WARNING, customArrayList.get(0));
            Assertions.assertEquals(System.Logger.Level.WARNING, customArrayList.get(1));
            Assertions.assertEquals(System.Logger.Level.ALL, customArrayList.get(2));
            Assertions.assertEquals(System.Logger.Level.TRACE, customArrayList.get(customArrayList.size() - 1));


            final CustomArrayList<Object> seniorSoftwareArchitectArrayList = new CustomArrayList<>(new String[]{"First", "Second", "Third", "Fourth"});
            Assertions.assertEquals(4, seniorSoftwareArchitectArrayList.size());
            seniorSoftwareArchitectArrayList.addAll(2, List.of("Google", "Tesla", "SpaceX"));
            Assertions.assertArrayEquals(
                    List.of("First", "Second", "Google", "Tesla", "SpaceX", "Third", "Fourth").toArray(),
                    seniorSoftwareArchitectArrayList.toArray()
            );
        }

        @Test
        void addAllHuge() {
            final CustomArrayList<Integer> customArrayList = new CustomArrayList<>(
                    List.of(13, 41, 6, 711, 44, 98, 12, 66, 13, 8, 44, 2, 19, 17, 87, 62, 819, 812, 144, 511, 31, 71).toArray()
            );

            Assertions.assertEquals(22, customArrayList.size());
            customArrayList.addAll(5, List.of(1, 2, 3, 4, 5));
            Assertions.assertEquals(27, customArrayList.size());
            Assertions.assertArrayEquals(
                    List.of(13, 41, 6, 711, 44, 1, 2, 3, 4, 5, 98, 12, 66, 13, 8, 44, 2, 19, 17, 87, 62, 819, 812, 144, 511, 31, 71).toArray(),
                    customArrayList.toArray()
            );
        }

        @Test
        void copy() {
            final CustomArrayList<String> customArrayList = new CustomArrayList<>(List.of("Spring Cloud", "Amazon S3", "Amazon EKS").toArray());
            final ArrayList<String> strings = new ArrayList<>(List.of("Some", "Dummy", "Strings"));
            Collections.copy(strings, customArrayList);

            Assertions.assertEquals(strings, List.of("Spring Cloud", "Amazon S3", "Amazon EKS"));
        }

        @Test
        void sort() {
            final CustomArrayList<Integer> sortedList = new CustomArrayList<>(List.of(8, 4, 10, 5, 14, 19, 1).toArray());
            Collections.sort(sortedList);
            Assertions.assertArrayEquals(List.of(1, 4, 5, 8, 10, 14, 19).toArray(), sortedList.toArray());
        }
    }
}
