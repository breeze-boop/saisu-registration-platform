package com.mishi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("voucher")
public class Voucher {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long shopId;
    private String title;
    private BigDecimal payValue;
    private BigDecimal actualValue;
    private Integer stock;
    private LocalDateTime beginAt;
    private LocalDateTime endAt;
    private Integer version;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getPayValue() { return payValue; }
    public void setPayValue(BigDecimal payValue) { this.payValue = payValue; }
    public BigDecimal getActualValue() { return actualValue; }
    public void setActualValue(BigDecimal actualValue) { this.actualValue = actualValue; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public LocalDateTime getBeginAt() { return beginAt; }
    public void setBeginAt(LocalDateTime beginAt) { this.beginAt = beginAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
