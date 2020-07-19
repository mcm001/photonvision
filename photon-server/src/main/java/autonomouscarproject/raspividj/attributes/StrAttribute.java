package autonomouscarproject.raspividj.attributes;

import org.jetbrains.annotations.NotNull;

public enum StrAttribute implements Attribute<String> {

  OUTPUT(new String[]{"-"}, "o"),
  VECTORS("x"),
  RAWOUTPUT("r");

  @NotNull
  public final String shortname;
  private String[] vals;

  StrAttribute(String shortname) {
    vals = new String[]{"/dev/null"};
    this.shortname = shortname;
  }

  StrAttribute(String[] defaultV, String shortname) {
    vals = defaultV;
    this.shortname = shortname;
  }

  @Override
  public String[] get() {
    return vals;
  }

  @Override
  public Attribute<String> set(String[] v) {
    vals = v;
    return this;
  }

  @Override
  public String getInvocationName() {
    return shortname;
  }
}
