package com.eagletsoft.framework.demo.view;

import com.eagletsoft.boot.framework.data.json.meta.Formula;
import com.eagletsoft.boot.framework.data.json.meta.Many;
import com.eagletsoft.framework.demo.repo.Membership;

@Formula(expression = "membershipId + userId")
public class MemberPort1 {
    @Many(value="a", ref = "membershipId", target = Membership.class, batch = true, option = {"gender", "A"})
    @Many(value="a", ref = "membershipId", target = Membership.class, batch = true, option = {"gender", "B"})
    //@One(ref = "membershipId", target = Membership.class, batch = true)
    //@Many2Many(value = "positions", src="id", ref = "memberId", target = Position.class, targetRef = "positionId", batch = true, mediator = MemberPosition.class)
    private Long membershipId;
    private Long userId;
    private String nick;

    public Long getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(Long membershipId) {
        this.membershipId = membershipId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
