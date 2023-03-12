package com.eagletsoft.framework.demo.view;

import com.eagletsoft.framework.demo.repo.Membership;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataView;

import java.io.Serializable;

@DataView(Membership.class)
public class MembershipCreation implements Serializable {
    private Long membershipId;
    private Long userId;
    private String name;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
}
