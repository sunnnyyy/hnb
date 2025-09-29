package com.hnb.enums;

public enum RoomNumber {
    ROOM_101(101),
    ROOM_102(102),
    ROOM_103(103),
    ROOM_104(104),
    ROOM_105(105);

    private final int number;

    RoomNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
