package autonomouscarproject.raspividj.attributes.enums;

import autonomouscarproject.raspividj.attributes.Attribute;

public enum Metering implements Attribute<Metering.MeteringMode> {
  METERING(MeteringMode.AVERAGE, "mm");

  public final String shortname;
  MeteringMode v;

  Metering(MeteringMode defaultV, String shortname) {
    this.v = defaultV;
    this.shortname = shortname;
  }

  @Override
  public MeteringMode[] get() {
    return new MeteringMode[]{v};
  }

  @Override
  public Attribute<MeteringMode> set(MeteringMode[] v) {
    this.v = v[0];
    return this;
  }

  @Override
  public String getInvocationName() {
    return shortname;
  }

  public enum MeteringMode {
    AVERAGE, SPOT, BACKLIT, MATRIX;

    @Override
    public String toString() {
      return this.name().toLowerCase();
    }
  }
}


