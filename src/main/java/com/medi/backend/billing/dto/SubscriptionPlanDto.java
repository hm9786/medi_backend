package com.medi.backend.billing.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class SubscriptionPlanDto {

    public int id;
    public String planName;
    public int price;
    public int decimal;
    public int channel_limit;
    public Date createdAt;
    public Date updateAt;

}
