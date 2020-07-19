package autonomouscarproject.raspividj.attributes.enums;

import autonomouscarproject.raspividj.attributes.Attribute;

public enum Mode implements Attribute<Mode.ModeMode> {
  MODE(ModeMode.AUTO, "ex");

  public final String shortname;
  ModeMode v;

  Mode(ModeMode defaultV, String shortname) {
    this.v = defaultV;
    this.shortname = shortname;
  }

  @Override
  public ModeMode[] get() {
    return new ModeMode[]{v};
  }

  @Override
  public Attribute<ModeMode> set(ModeMode[] v) {
    this.v = v[0];
    return this;
  }

  @Override
  public String getInvocationName() {
    return shortname;
  }

  public enum ModeMode {
    AUTO(0);

    Integer code;

    ModeMode(int code) {
      this.code = code;
    }

    @Override
    public String toString() {
      return this.code.toString();
    }
  }
}


