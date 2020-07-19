package autonomouscarproject.raspividj.attributes.enums;

import autonomouscarproject.raspividj.attributes.Attribute;

public enum Codec implements Attribute<Codec.CodecMode> {
  CODEC(CodecMode.H264, "cd");

  public final String shortname;
  CodecMode v;

  Codec(CodecMode defaultV, String shortname) {
    this.v = defaultV;
    this.shortname = shortname;
  }

  @Override
  public CodecMode[] get() {
    return new CodecMode[]{v};
  }

  @Override
  public Attribute<CodecMode> set(CodecMode[] v) {
    this.v = v[0];
    return this;
  }

  @Override
  public String getInvocationName() {
    return shortname;
  }

  public enum CodecMode {
    H264, MJPEG;

    @Override
    public String toString() {
      return this.name().toLowerCase();
    }
  }
}


