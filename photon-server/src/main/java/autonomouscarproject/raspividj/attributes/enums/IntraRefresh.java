package autonomouscarproject.raspividj.attributes.enums;

import autonomouscarproject.raspividj.attributes.Attribute;

public enum IntraRefresh implements Attribute<IntraRefresh.IntraRefreshMode> {
  INTRA_REFRESH(IntraRefreshMode.CYCLIC, "if");

  public final String shortname;
  IntraRefreshMode v;

  IntraRefresh(IntraRefreshMode defaultV, String shortname) {
    this.v = defaultV;
    this.shortname = shortname;
  }

  @Override
  public IntraRefreshMode[] get() {
    return new IntraRefreshMode[]{v};
  }

  @Override
  public Attribute<IntraRefreshMode> set(IntraRefreshMode[] v) {
    this.v = v[0];
    return this;
  }

  @Override
  public String getInvocationName() {
    return shortname;
  }

  public enum IntraRefreshMode {
    CYCLIC, ADAPTIVE, BOTH, CYCLICROWS;

    @Override
    public String toString() {
      return this.name().toLowerCase();
    }
  }
}


