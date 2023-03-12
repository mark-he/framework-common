package com.eagletsoft.framework.demo.repo;

import com.eagletsoft.boot.framework.data.entity.guid.GuidEntity;
import com.eagletsoft.boot.framework.data.json.meta.ExtView;
import com.eagletsoft.boot.framework.data.json.meta.ExtViewport;
import com.eagletsoft.framework.demo.view.MemberPort1;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataRule;
import com.eagletsoft.framework.plugin.dataview.def.meta.DataView;
import com.eagletsoft.framework.plugin.dataview.def.meta.Dependency;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@DataView
@Table(name = "membership")
@DataRule(value="1 == membershipId && 2 == userId", message="membershipId 为 1 时 userId 必须为 2")
@ExtView
@ExtViewport(value = MemberPort1.class, groups = {"test1"})
public class Membership extends GuidEntity {
    private Long membershipId;
    private Long userId;
    private String nick;

    //@DataField(value = "name", required = true, onCreated = "#uuid()")

    private String name;
    @Dependency(on = "name", where = "C", values = {"DDDD"})
    private String mobile;
    private String gender;
    private String bizCode;
    private Long unitId;
    private String unitName;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
