package autonomouscarproject.raspividj.attributes;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public enum BoolAttribute implements Attribute<Boolean> {
  HELP(false, "?"),
  VERBOSE(false, "v"),
  DEMO(false, "d"),
  TIMED(false, "td"),
  SIGNAL(false, "s"),
  KEYPRESS(false, "k"),
  INLINE(false, "ih"),
  WRAP(false, "wr"),
  START(false, "sn"),
  SPLIT(false, "sp"),
  CIRCULAR(false, "c"),
  FLUSH(false, "fl"),
  SAVEPTS(false, "pts"),
  SETTINGS(false, "set"),
  FULLSCREEN(false, "f"),
  NOPREVIEW(true, "n"),
  ENCPREVIEW(false, "e"),
  VSTAB(false, "vs"),
  HORIZONTALFLIP(false, "hf"),
  VERTICALFLIP(false, "vf"),
  STATS(false, "st"),
  ANOTTATIONFLAGS(false, "a"),
  STEROSCOPIC(false, "3d"),
  DECIMATE(false, "dec"),
  STEROSWAP(false, "3dswap");

  @NotNull
  public final String shortname;
  @NotNull
  private Boolean v;

  BoolAttribute(Boolean defaultV, String shortname) {
    v = defaultV;
    this.shortname = shortname;
  }

  @Override
  public Boolean[] get() {
    return new Boolean[]{v};
  }

  @Override
  public Attribute<Boolean> set(Boolean[] v) {
    this.v = v[0];
    return this;
  }

  @Override
  public String getInvocationName() {
    return shortname;
  }

  @Override
  public Optional<String[]> getCommand() {
    if (v) {
      return Optional.of(new String[]{shortname});
    } else {
      return Optional.empty();
    }
  }
}
