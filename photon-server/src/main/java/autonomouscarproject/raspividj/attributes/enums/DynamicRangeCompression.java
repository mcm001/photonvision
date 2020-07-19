package autonomouscarproject.raspividj.attributes.enums;

import autonomouscarproject.raspividj.attributes.Attribute;

public enum DynamicRangeCompression implements Attribute<DynamicRangeCompression.DynamicRangeCompressionMode> {
  DYNAMIC_RANGE_COMPRESSION(DynamicRangeCompressionMode.OFF, "drc");

  public final String shortname;
  DynamicRangeCompressionMode v;

  DynamicRangeCompression(DynamicRangeCompressionMode defaultV, String shortname) {
    this.v = defaultV;
    this.shortname = shortname;
  }

  @Override
  public DynamicRangeCompressionMode[] get() {
    return new DynamicRangeCompressionMode[]{v};
  }

  @Override
  public Attribute<DynamicRangeCompressionMode> set(DynamicRangeCompressionMode[] v) {
    this.v = v[0];
    return this;
  }

  @Override
  public String getInvocationName() {
    return shortname;
  }

  public enum DynamicRangeCompressionMode {
    OFF, LOW, MED, HIGH;

    @Override
    public String toString() {
      return this.name().toLowerCase();
    }
  }
}


