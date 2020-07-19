package autonomouscarproject.raspividj.attributes.enums;

import autonomouscarproject.raspividj.attributes.Attribute;

public enum RawFormat implements Attribute<RawFormat.RawFormatMode> {
  RAW_FORMAT(RawFormatMode.RGB, "rf");

  public final String shortname;
  RawFormatMode v;

  RawFormat(RawFormatMode defaultV, String shortname) {
    this.v = defaultV;
    this.shortname = shortname;
  }

  @Override
  public RawFormatMode[] get() {
    return new RawFormatMode[]{v};
  }

  @Override
  public Attribute<RawFormatMode> set(RawFormatMode[] v) {
    this.v = v[0];
    return this;
  }

  @Override
  public String getInvocationName() {
    return shortname;
  }

  public enum RawFormatMode {
    YUV, RGB, GRAY;

    @Override
    public String toString() {
      return this.name().toLowerCase();
    }
  }
}


