package com.qqun.user;

public class Role {
    public enum Group {RADIANTS, DEADS, INSCRIBERS, KILLER, ADMIN}
    private Group group;
    private int person;

    public Role(String group, int person) throws IllegalArgumentException {
        this.group = Group.valueOf(group);
        this.person = person;
    }

    public final Group getGroup() {
        return group;
    }

    public final int getPerson() {
        return person;
    }
}
