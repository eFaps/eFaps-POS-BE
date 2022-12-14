package org.efaps.pos.dto;

public enum WorkspaceFlag {


  ROUNDPAYABLE(2);

  public int bitIndex;

  WorkspaceFlag(int bitIndex) {
    this.bitIndex = bitIndex;
  }
}
