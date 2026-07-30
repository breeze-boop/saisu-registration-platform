package com.mishi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mishi.domain.VoucherOrder;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface VoucherOrderMapper extends BaseMapper<VoucherOrder> {
    @Select("SELECT COUNT(1) FROM voucher_order WHERE voucher_id = #{voucherId} AND user_id = #{userId}")
    int countUserVoucherOrders(@Param("voucherId") Long voucherId, @Param("userId") Long userId);

    @Update("""
            UPDATE voucher_order
               SET status = 'PAID', paid_at = #{paidAt}, version = version + 1
             WHERE id = #{orderId}
               AND status = 'PENDING_PAYMENT'
            """)
    int payPendingOrder(@Param("orderId") Long orderId, @Param("paidAt") LocalDateTime paidAt);

    @Update("""
            UPDATE voucher_order
               SET status = 'CLOSED', closed_at = #{closedAt}, version = version + 1
             WHERE id = #{orderId}
               AND status = 'PENDING_PAYMENT'
            """)
    int closePendingOrder(@Param("orderId") Long orderId, @Param("closedAt") LocalDateTime closedAt);
}
