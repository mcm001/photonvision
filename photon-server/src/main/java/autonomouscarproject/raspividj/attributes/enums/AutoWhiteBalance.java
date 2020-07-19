package autonomouscarproject.raspividj.attributes.enums;

import autonomouscarproject.raspividj.attributes.Attribute;

public enum AutoWhiteBalance implements Attribute<AutoWhiteBalance.AutoWhiteBalanceMode> {
  AUTO_WHITE_BALANCE(AutoWhiteBalanceMode.OFF, "awb");

  public final String shortname;
  AutoWhiteBalanceMode v;

  AutoWhiteBalance(AutoWhiteBalanceMode defaultV, String shortname) {
    this.v = defaultV;
    this.shortname = shortname;
  }

  @Override
  public AutoWhiteBalanceMode[] get() {
    return new AutoWhiteBalanceMode[]{v};
  }

  @Override
  public Attribute<AutoWhiteBalanceMode> set(AutoWhiteBalanceMode[] v) {
    this.v = v[0];
    return this;
  }

  @Override
  public String getInvocationName() {
    return shortname;
  }

  public enum AutoWhiteBalanceMode {
    OFF, AUTO, SUN, CLOUD, SHADE, TUNGSTEN, FLUORESCENT, INCANDESCENT, FLASH, HORIZON;

    @Override
    public String toString() {
      return this.name().toLowerCase();
    }
  }
}


