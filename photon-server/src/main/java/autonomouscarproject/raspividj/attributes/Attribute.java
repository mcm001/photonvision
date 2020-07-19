package autonomouscarproject.raspividj.attributes;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public interface Attribute<T> {

  T[] get();

  Attribute<T> set(T[] v);

  String getInvocationName();

  default Optional<String[]> getCommand() {
    // Add the shortname together with the data
    return Optional.of(Stream.concat(Arrays.stream(new String[]{getInvocationName()}), Arrays.stream
        (get())).map(Object::toString).toArray(String[]::new));
  }

}
