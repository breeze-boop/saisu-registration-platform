package com.mishi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mishi.domain.Voucher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface VoucherMapper extends BaseMapper<Voucher> {
    @Update("""
            UPDATE voucher
               SET stock = stock - 1, version = version + 1
             WHERE id = #{voucherId}
               AND stock > 0
               AND version = #{version}
            """)
    int decreaseStockWithOptimisticLock(@Param("voucherId") Long voucherId, @Param("version") Integer version);

    @Update("""
            UPDATE voucher
               SET stock = stock + 1, version = version + 1
             WHERE id = #{voucherId}
            """)
    int releaseStock(@Param("voucherId") Long voucherId);
}
