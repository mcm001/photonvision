package autonomouscarproject.raspividj.attributes;

import java.util.Optional;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum IntAttribute implements Attribute<Integer> {

  WIDTH(new Integer[]{1920}, "w", forceToRange(1, null)),
  HEIGHT(new Integer[]{1080}, "h", forceToRange(1, null)),
  BITRATE(null, "b", forceToRange(0, null)),
  TIMEOUT(new Integer[]{5000}, "t", forceToRange(0, null)),
  FRAMERATE(new Integer[]{30}, "fps", forceToRange(0, 60)),
  INTRA(null, "g", null),
  QUANTITIZAION(null, "qp", forceToRange(0, null)),
  SEGMENT(null, "sg", forceToRange(0, null)),
  LEVEL(null, "lev", forceToRange(0, null)),
  CAMERASELECT(new Integer[]{0}, "cs", forceToRange(0, null)),
  PREVIEW(null, "p", null),
  OPACITY(new Integer[]{255}, "op", forceToRange(0, 255)),
  SHARPNESS(null, "sh", forceToRange(-100, 100)),
  CONTRAST(null, "co", forceToRange(-100, 100)),
  BRIGHTNESS(null, "br", forceToRange(0, 100)),
  SATURATION(null, "sa", forceToRange(-100, 100)),
  ISO(null, "ISO", forceToRange(100, 800)),
  EV(null, "ev", forceToRange(-10, 10)),
  ROTATION(null, "rot", forceToRange(0, 359)),
  SHUTTERSPEED(null, "ss", forceToRange(0, null));

  @NotNull
  public final String shortname;
  @Nullable
  private final Consumer<Integer[]> handler;
  private Integer[] vals;

  IntAttribute(Integer[] defaultV, @NotNull String shortname, @Nullable Consumer<Integer[]>
      handler) {
    this.handler = handler;
    this.shortname = shortname;
    set(defaultV);
  }

  private static Consumer<Integer[]> forceToRange(@Nullable Integer low, @Nullable Integer high) {
    return (ints) -> {
      for (int i = 0; i < ints.length; i++) {
        if (Optional.ofNullable(low).isPresent() && ints[i] < low) {
          ints[i] = low;
        } else if (Optional.ofNullable(high).isPresent() && ints[i] > high) {
          ints[i] = high;
        }
      }
    };
  }

  @Override
  public Integer[] get() {
    return vals;
  }

  @Override
  public Attribute<Integer> set(Integer[] vs) {
    Optional.ofNullable(vs).ifPresent(Optional.ofNullable(this.handler).orElse((v) -> {
    }));
    this.vals = vs;
    return this;
  }

  @Override
  public String getInvocationName() {
    return shortname;
  }

}
