package autonomouscarproject.raspividj.attributes.enums;

import autonomouscarproject.raspividj.attributes.Attribute;

public enum Exposure implements Attribute<Exposure.ExposureMode> {
  EXPOSURE(ExposureMode.AUTO, "ex");

  public final String shortname;
  ExposureMode v;

  Exposure(ExposureMode defaultV, String shortname) {
    this.v = defaultV;
    this.shortname = shortname;
  }

  @Override
  public ExposureMode[] get() {
    return new ExposureMode[]{v};
  }

  @Override
  public Attribute<ExposureMode> set(ExposureMode[] v) {
    this.v = v[0];
    return this;
  }

  @Override
  public String getInvocationName() {
    return shortname;
  }

  public enum ExposureMode {
    AUTO, NIGHT, NIGHTPREVIEW, BACKLIGHT, SPOTLIGHT, SPORTS, SNOW, BEACH, VERYLONG, FIXEDFPS,
    ANTISHAKE, FIREWORKS;

    @Override
    public String toString() {
      return this.name().toLowerCase();
    }
  }
}


